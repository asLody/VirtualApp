package com.lody.virtual.service.am;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.SparseArray;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.stub.StubManifest;
import com.lody.virtual.helper.proto.AppTaskInfo;
import com.lody.virtual.helper.proto.StubActivityRecord;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.ClassUtils;
import com.lody.virtual.helper.utils.ComponentUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import mirror.android.app.ActivityManagerNative;
import mirror.android.app.ActivityThread;
import mirror.android.app.IApplicationThread;
import mirror.com.android.internal.R_styleable;

import static android.content.pm.ActivityInfo.LAUNCH_SINGLE_INSTANCE;
import static android.content.pm.ActivityInfo.LAUNCH_SINGLE_TASK;
import static android.content.pm.ActivityInfo.LAUNCH_SINGLE_TOP;

/**
 * @author Lody
 */

/* package */ class ActivityStack {

	private final ActivityManager mAM;
	private final VActivityManagerService mService;

	/**
	 * [Key] = TaskId [Value] = TaskRecord
	 */
	private final SparseArray<TaskRecord> mHistory = new SparseArray<>();

	private final Runnable mCleanTaskScheduler = new Runnable() {
		@Override
		public void run() {
			synchronized (mHistory) {
				int N = mHistory.size();
				while (N-- > 0) {
					final TaskRecord task = mHistory.valueAt(N);
					List<ActivityRecord> activities = Collections.unmodifiableList(task.activities);
					for (ActivityRecord r : activities) {
						if (!r.marked) {
							continue;
						}
						try {
							r.process.client.finishActivity(r.token);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	};

	ActivityStack(VActivityManagerService mService) {
		this.mService = mService;
		mAM = (ActivityManager) VirtualCore.get().getContext().getSystemService(Context.ACTIVITY_SERVICE);
	}

	private static void removeFlags(Intent intent, int flags) {
		intent.setFlags(intent.getFlags() & ~flags);
	}

	private static boolean containFlags(Intent intent, int flags) {
		return (intent.getFlags() & flags) != 0;
	}

	private static ActivityRecord topActivityInTask(TaskRecord task) {
		synchronized (task.activities) {
			for (int size = task.activities.size() - 1; size >= 0; size--) {
				ActivityRecord r = task.activities.get(size);
				if (!r.marked) {
					return r;
				}
			}
			return null;
		}
	}

	private void deliverNewIntentLocked(ActivityRecord sourceRecord, ActivityRecord targetRecord, Intent intent) {
		String creator = sourceRecord != null ? sourceRecord.component.getPackageName() : "android";
		try {
			targetRecord.process.client.scheduleNewIntent(creator, targetRecord.token, intent);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private TaskRecord findTaskByAffinityLocked(int userId, String affinity) {
		for (int i = 0; i < this.mHistory.size(); i++) {
			TaskRecord r = this.mHistory.valueAt(i);
			if (userId == r.userId && affinity.equals(r.affinity)) {
				return r;
			}
		}
		return null;
	}

	private TaskRecord findTaskByIntentLocked(int userId, Intent intent) {
		for (int i = 0; i < this.mHistory.size(); i++) {
			TaskRecord r = this.mHistory.valueAt(i);
			if (userId == r.userId && r.taskRoot != null && intent.getComponent().equals(r.taskRoot.getComponent())) {
				return r;
			}
		}
		return null;
	}

	private ActivityRecord findActivityByToken(int userId, IBinder token) {
		ActivityRecord target = null;
		if (token != null) {
			for (int i = 0; i < this.mHistory.size(); i++) {
				TaskRecord task = this.mHistory.valueAt(i);
				if (task.userId != userId) {
					continue;
				}
				synchronized (task.activities) {
					for (ActivityRecord r : task.activities) {
						if (r.token == token) {
							target = r;
						}
					}
				}
			}
		}
		return target;
	}

	private boolean markTaskByClearTarget(TaskRecord task, ClearTarget clearTarget, ComponentName component) {
		boolean marked = false;
		switch (clearTarget) {
			case FULL_TASK : {
				synchronized (task.activities) {
					for (ActivityRecord r : task.activities) {
						r.marked = true;
						marked = true;
					}
				}
			} break;
			case SPEC_ACTIVITY : {
				synchronized (task.activities) {
					for (ActivityRecord r : task.activities) {
						if (r.component.equals(component)) {
							r.marked = true;
							marked = true;
						}
					}
				}
			} break;
			case AFTER_TOP : {
				synchronized (task.activities) {
					int N = task.activities.size();
					while (N-- > 0) {
						ActivityRecord r = task.activities.get(N);
						if (r.component.equals(component)) {
							marked = true;
							break;
						}
					}
					if (marked) {
						while (N++ < task.activities.size() - 1) {
							task.activities.get(N).marked = true;
						}
					}
				}
			} break;
			case AFTER_TOP_WITH_TOP: {
				synchronized (task.activities) {
					int N = task.activities.size();
					while (N-- > 0) {
						ActivityRecord r = task.activities.get(N);
						if (r.component.equals(component)) {
							marked = true;
							break;
						}
					}
					N--;
					if (marked) {
						while (N++ < task.activities.size() - 1) {
							task.activities.get(N).marked = true;
						}
					}
				}
			} break;
		}
		return marked;
	}

	/**
	 * App started in VA may be removed in OverView screen, then AMS.removeTask
	 * will be invoked, all data struct about the task in AMS are released,
	 * while the client's process is still alive. So remove related data in VA
	 * as well. A new TaskRecord will be recreated in `onActivityCreated`
	 *
	 */
	private void optimizeTasksLocked() {
		// noinspection deprecation
		ArrayList<ActivityManager.RecentTaskInfo> recentTask = new ArrayList<>(mAM.getRecentTasks(Integer.MAX_VALUE,
				ActivityManager.RECENT_WITH_EXCLUDED | ActivityManager.RECENT_IGNORE_UNAVAILABLE));
		int N = mHistory.size();
		while (N-- > 0) {
			TaskRecord task = mHistory.valueAt(N);
			ListIterator<ActivityManager.RecentTaskInfo> iterator = recentTask.listIterator();
			boolean taskAlive = false;
			while (iterator.hasNext()) {
				ActivityManager.RecentTaskInfo info = iterator.next();
				if (info.id == task.taskId) {
					taskAlive = true;
					iterator.remove();
					break;
				}
			}
			if (!taskAlive) {
				mHistory.removeAt(N);
			}
		}
	}

	int startActivityLocked(int userId, Intent intent, ActivityInfo info, IBinder resultTo, Bundle options,
			String resultWho, int requestCode) {

		optimizeTasksLocked();

		Intent destIntent;
		ActivityRecord sourceRecord = findActivityByToken(userId, resultTo);
		TaskRecord sourceTask = sourceRecord != null ? sourceRecord.task : null;

		ReuseTarget reuseTarget = ReuseTarget.CURRENT;
		ClearTarget clearTarget = ClearTarget.NOTHING;
		boolean clearTop = containFlags(intent, Intent.FLAG_ACTIVITY_CLEAR_TOP);

		if (intent.getComponent() == null) {
			intent.setComponent(new ComponentName(info.packageName, info.name));
		}
		if (sourceRecord != null && sourceRecord.launchMode == LAUNCH_SINGLE_INSTANCE) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		if (clearTop) {
			removeFlags(intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			clearTarget = ClearTarget.AFTER_TOP_WITH_TOP;
		}
		if (containFlags(intent, Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
			if (containFlags(intent, Intent.FLAG_ACTIVITY_NEW_TASK)) {
				clearTarget = ClearTarget.FULL_TASK;
			} else {
				removeFlags(intent, Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			switch (info.documentLaunchMode) {
				case ActivityInfo.DOCUMENT_LAUNCH_INTO_EXISTING :
					clearTarget = ClearTarget.FULL_TASK;
					reuseTarget = ReuseTarget.DOCUMENT;
					break;
				case ActivityInfo.DOCUMENT_LAUNCH_ALWAYS :
					reuseTarget = ReuseTarget.MULTIPLE;
					break;
			}
		}

		switch (info.launchMode) {
			case LAUNCH_SINGLE_TOP : {
				clearTarget = ClearTarget.AFTER_TOP;
				if (containFlags(intent, Intent.FLAG_ACTIVITY_NEW_TASK)) {
					reuseTarget = containFlags(intent, Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
							? ReuseTarget.MULTIPLE
							: ReuseTarget.AFFINITY;
				}
			}
				break;
			case LAUNCH_SINGLE_TASK : {
				clearTarget = ClearTarget.AFTER_TOP;
				reuseTarget = containFlags(intent, Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
						? ReuseTarget.MULTIPLE
						: ReuseTarget.AFFINITY;
			}
				break;
			case LAUNCH_SINGLE_INSTANCE : {
				clearTarget = ClearTarget.AFTER_TOP;
				reuseTarget = ReuseTarget.AFFINITY;
			}
				break;
			default : {
				if (containFlags(intent, Intent.FLAG_ACTIVITY_SINGLE_TOP)) {
					clearTarget = ClearTarget.AFTER_TOP;
				}
			}
				break;
		}
		if (clearTarget == ClearTarget.NOTHING) {
			if (containFlags(intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)) {
				clearTarget = ClearTarget.SPEC_ACTIVITY;
			}
		}
		if (sourceTask == null && reuseTarget == ReuseTarget.CURRENT) {
			reuseTarget = ReuseTarget.AFFINITY;
		}

		String affinity = ComponentUtils.getTaskAffinity(info);
		TaskRecord reuseTask = null;
		switch (reuseTarget) {
			case AFFINITY :
				reuseTask = findTaskByAffinityLocked(userId, affinity);
				break;
			case DOCUMENT :
				reuseTask = findTaskByIntentLocked(userId, intent);
				break;
			case CURRENT :
				reuseTask = sourceTask;
				break;
			default :
				break;
		}

		boolean taskMarked = false;
		if (reuseTask == null) {
			destIntent = startActivityProcess(userId, null, intent, info);
			if (destIntent != null) {
				destIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				destIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				destIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
					// noinspection deprecation
					destIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				} else {
					destIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					VirtualCore.get().getContext().startActivity(destIntent, options);
				} else {
					VirtualCore.get().getContext().startActivity(destIntent);
				}

			}
		} else if (!clearTarget.deliverIntent && ComponentUtils.isSameIntent(intent, reuseTask.taskRoot)) {
			// In this case, we only need to move the task to front.
			mAM.moveTaskToFront(reuseTask.taskId, 0);
		} else {
			boolean delivered = false;
			mAM.moveTaskToFront(reuseTask.taskId, 0);
			if (clearTarget.deliverIntent) {
				taskMarked = markTaskByClearTarget(reuseTask, clearTarget, intent.getComponent());
				ActivityRecord topRecord = topActivityInTask(reuseTask);
				// Target activity is on top
				if (topRecord != null && topRecord.component.equals(intent.getComponent())) {
					deliverNewIntentLocked(sourceRecord, topRecord, intent);
					delivered = true;
				}
			}
			if (taskMarked) {
				scheduleFinishMarkedActivity();
			}
			if (!delivered) {
				destIntent = startActivityProcess(userId, sourceRecord, intent, info);
				if (destIntent != null) {
					startActivityFromSourceTask(reuseTask, destIntent, info, resultWho, requestCode, options);
				}
			}
		}

		return 0;
	}

	private void scheduleFinishMarkedActivity() {
		VirtualRuntime.getUIHandler().removeCallbacks(mCleanTaskScheduler);
		VirtualRuntime.getUIHandler().post(mCleanTaskScheduler);
	}

	private boolean startActivityFromSourceTask(TaskRecord task, Intent intent, ActivityInfo info, String resultWho,
			int requestCode, Bundle options) {
		ActivityRecord top = topActivityInTask(task);
		if (top != null) {
			if (startActivityProcess(task.userId, top, intent, info) != null) {
				realStartActivityLocked(top.token, intent, resultWho, requestCode, options);
			}
		}
		return false;
	}

	private void realStartActivityLocked(IBinder resultTo, Intent intent, String resultWho, int requestCode,
			Bundle options) {
		Class<?>[] types = mirror.android.app.IActivityManager.startActivity.paramList();
		Object[] args = new Object[types.length];
		if (types[0] == IApplicationThread.TYPE) {
			args[0] = ActivityThread.getApplicationThread.call(VirtualCore.mainThread());
		}
		int intentIndex = ArrayUtils.protoIndexOf(types, Intent.class);
		int resultToIndex = ArrayUtils.protoIndexOf(types, IBinder.class, 2);
		int optionsIndex = ArrayUtils.protoIndexOf(types, Bundle.class);
		int resultWhoIndex = resultToIndex + 1;
		int requestCodeIndex = resultToIndex + 2;

		args[intentIndex] = intent;
		args[resultToIndex] = resultTo;
		args[resultWhoIndex] = resultWho;
		args[requestCodeIndex] = requestCode;
		if (optionsIndex != -1) {
			args[optionsIndex] = options;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			args[intentIndex - 1] = VirtualCore.get().getHostPkg();
		}
		ClassUtils.fixArgs(types, args);

		mirror.android.app.IActivityManager.startActivity.call(ActivityManagerNative.getDefault.call(),
				(Object[]) args);
	}

	private String fetchStubActivity(int vpid, ActivityInfo targetInfo) {

		boolean isFloating = false;
		boolean isTranslucent = false;
		boolean showWallpaper = false;
		try {
			int[] R_Styleable_Window = R_styleable.Window.get();
			int R_Styleable_Window_windowIsTranslucent = R_styleable.Window_windowIsTranslucent.get();
			int R_Styleable_Window_windowIsFloating = R_styleable.Window_windowIsFloating.get();
			int R_Styleable_Window_windowShowWallpaper = R_styleable.Window_windowShowWallpaper.get();

			AttributeCache.Entry ent = AttributeCache.instance().get(targetInfo.packageName, targetInfo.theme,
					R_Styleable_Window);
			if (ent != null && ent.array != null) {
				showWallpaper = ent.array.getBoolean(R_Styleable_Window_windowShowWallpaper, false);
				isTranslucent = ent.array.getBoolean(R_Styleable_Window_windowIsTranslucent, false);
				isFloating = ent.array.getBoolean(R_Styleable_Window_windowIsFloating, false);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		boolean isDialogStyle = isFloating || isTranslucent || showWallpaper;
		if (isDialogStyle) {
			return StubManifest.getStubDialogName(vpid);
		} else {
			return StubManifest.getStubActivityName(vpid);
		}
	}

	private Intent startActivityProcess(int userId, ActivityRecord sourceRecord, Intent intent, ActivityInfo info) {
		intent = new Intent(intent);
		ProcessRecord targetApp = mService.startProcessIfNeedLocked(info.processName, userId, info.packageName);
		if (targetApp == null) {
			return null;
		}
		Intent targetIntent = new Intent();
		targetIntent.setClassName(VirtualCore.get().getHostPkg(), fetchStubActivity(targetApp.vpid, info));
		ComponentName component = intent.getComponent();
		if (component == null) {
			component = ComponentUtils.toComponentName(info);
		}
		targetIntent.setType(component.flattenToString());
		StubActivityRecord saveInstance = new StubActivityRecord(intent, info,
				sourceRecord != null ? sourceRecord.component : null, userId);
		saveInstance.saveToIntent(targetIntent);
		return targetIntent;
	}

	void onActivityCreated(ProcessRecord targetApp, ComponentName component, ComponentName caller, IBinder token,
			Intent taskRoot, String affinity, int taskId, int launchMode, int flags) {
		synchronized (mHistory) {
			optimizeTasksLocked();
			TaskRecord task = mHistory.get(taskId);
			if (task == null) {
				task = new TaskRecord(taskId, targetApp.userId, affinity, taskRoot);
				mHistory.put(taskId, task);
			}
			ActivityRecord record = new ActivityRecord(task, component, caller, token, targetApp.userId, targetApp,
					launchMode, flags, affinity);
			synchronized (task.activities) {
				task.activities.add(record);
			}
		}
	}

	void onActivityResumed(int userId, IBinder token) {
		synchronized (mHistory) {
			optimizeTasksLocked();
			ActivityRecord r = findActivityByToken(userId, token);
			if (r != null) {
				synchronized (r.task.activities) {
					r.task.activities.remove(r);
					r.task.activities.add(r);
				}
			}
		}
	}

	boolean onActivityDestroyed(int userId, IBinder token) {
		synchronized (mHistory) {
			optimizeTasksLocked();
			ActivityRecord r = findActivityByToken(userId, token);
			if (r != null) {
				synchronized (r.task.activities) {
					r.task.activities.remove(r);
					if (r.task.activities.isEmpty()) {
						mHistory.remove(r.task.taskId);
						return true;
					}
				}
			}
			return false;
		}
	}

	void processDied(ProcessRecord record) {
		synchronized (mHistory) {
			optimizeTasksLocked();
			int N = mHistory.size();
			while (N-- > 0) {
				TaskRecord task = mHistory.valueAt(N);
				synchronized (task.activities) {
					Iterator<ActivityRecord> iterator = task.activities.iterator();
					while (iterator.hasNext()) {
						ActivityRecord r = iterator.next();
						if (r.process.pid == record.pid) {
							iterator.remove();
							if (task.activities.isEmpty()) {
								mHistory.remove(task.taskId);
							}
						}
					}
				}
			}

		}
	}

	String getPackageForToken(int userId, IBinder token) {
		synchronized (mHistory) {
			ActivityRecord r = findActivityByToken(userId, token);
			if (r != null) {
				return r.component.getPackageName();
			}
			return null;
		}
	}

	ComponentName getCallingActivity(int userId, IBinder token) {
		synchronized (mHistory) {
			ActivityRecord r = findActivityByToken(userId, token);
			if (r != null) {
				return r.component;
			}
			return null;
		}
	}

	AppTaskInfo getTaskInfo(int taskId) {
		synchronized (mHistory) {
			TaskRecord task = mHistory.get(taskId);
			if (task != null) {
				return task.getAppTaskInfo();
			}
			return null;
		}
	}

	private enum ClearTarget {
		NOTHING,
		SPEC_ACTIVITY,
		FULL_TASK(true),
		AFTER_TOP(true),
		AFTER_TOP_WITH_TOP(true);

		boolean deliverIntent;

		ClearTarget() {
			this(false);
		}
		ClearTarget(boolean deliverIntent) {
			this.deliverIntent = deliverIntent;
		}
	}

	private enum ReuseTarget {
		CURRENT, AFFINITY, DOCUMENT, MULTIPLE
	}
}
