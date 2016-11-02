package io.virtualapp.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.lody.virtual.os.VUserInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.helper.proto.AppSetting;
import com.lody.virtual.os.VUserHandle;
import com.lody.virtual.os.VUserManager;
import com.melnykov.fab.FloatingActionButton;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import io.virtualapp.R;
import io.virtualapp.VCommends;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.effects.ExplosionField;
import io.virtualapp.home.adapters.LaunchpadAdapter;
import io.virtualapp.home.models.AppModel;
import io.virtualapp.users.UserListActivity;
import io.virtualapp.widgets.PagerView;
import io.virtualapp.widgets.showcase.MaterialShowcaseView;

/**
 * @author Lody
 */
public class HomeActivity extends VActivity implements HomeContract.HomeView {

	String TAG = "HomeActivity";
	private HomeContract.HomePresenter mPresenter;
	private ProgressBar mLoadingBar;
	private PagerView mPagerView;
	private FloatingActionButton mAppFab;
	private FloatingActionButton mCrashFab;
	private ExplosionField mExplosionField;
	private LaunchpadAdapter mAdapter;
  private InstallerReceiver installerReceiver = new InstallerReceiver();

	public static void goHome(Context context) {
		if (context == null)
			return;
		Intent intent = new Intent(context, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mLoadingBar = (ProgressBar) findViewById(R.id.pb_loading_app);
		mPagerView = (PagerView) findViewById(R.id.home_launcher);
		mAppFab = (FloatingActionButton) findViewById(R.id.home_fab);
		mCrashFab = (FloatingActionButton) findViewById(R.id.home_del);
		mAdapter = new LaunchpadAdapter(this);
		mPagerView.setAdapter(mAdapter);
		new HomePresenterImpl(this, this);
		mPresenter.start();
		mAppFab.setOnClickListener(v -> mPresenter.wantAddApp());
		mExplosionField = ExplosionField.attachToWindow(this);
		mPagerView.setOnDragChangeListener(mPresenter::dragChange);
		mPagerView.setOnEnterCrashListener(mPresenter::dragNearCrash);
		mPagerView.setOnCrashItemListener(position -> {
			AppModel model = mAdapter.getItem(position);
			View v = mPagerView.getChildAt(position);
			mExplosionField.explode(v, null);
			mPresenter.deleteApp(model);
		});
		mPagerView.setOnItemClickListener((item, pos) -> {

			new AlertDialog.Builder(this)
					.setTitle("Choose an User")
					.setItems(getUsers(), (dialog, userId)
							-> mPresenter.launchApp((AppModel) item, userId))
					.setNegativeButton(android.R.string.cancel, null)
					.show();

		});
		findViewById(R.id.user_icon).setOnClickListener(v -> {
			startActivity(new Intent(this, UserListActivity.class));
        });
		mCrashFab.post(() -> {
			int[] location = new int[2];
			mAppFab.getLocationInWindow(location);
			mPagerView.setBottomLine(location[1]);
		});

		// 友盟统计 modify by young
		MobclickAgent.setDebugMode(true);
		// SD
		// K在统计Fragment时，需要关闭Activity自带的页面统计，
		// 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
		MobclickAgent.openActivityDurationTrack(false);
		if(Build.VERSION.SDK_INT>=23){
			MobclickAgent.setCheckDevice(false);
		}
		// MobclickAgent.setAutoLocation(true);
		// MobclickAgent.setSessionContinueMillis(1000);
		// MobclickAgent.startWithConfigure(
		// new UMAnalyticsConfig(mContext, "4f83c5d852701564c0000011", "Umeng",
		// EScenarioType.E_UM_NORMAL));
		MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
      /**
		 * 事件统计
		 */
		MobclickAgent.onEvent(this, "Hook", "xxxxxxxxxx");// Hook是标签，xxx是value
		registerInstallerReceiver();
	}

	public void registerInstallerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PACKAGE_ADDED);
		filter.addAction(Constants.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(installerReceiver, filter);
	}

	public void unregisterInstallerReceiver() {
		unregisterReceiver(installerReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();

		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
	}

	@Override
	public void setPresenter(HomeContract.HomePresenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void showLoading() {
		mLoadingBar.setVisibility(View.VISIBLE);
		mPagerView.setVisibility(View.GONE);
	}

	@Override
	public void loadFinish(List<AppModel> appModels) {
		mAdapter.setModels(appModels);
		mPagerView.refreshView();
		hideLoading();
	}

	@Override
	public void loadError(Throwable err) {
		hideLoading();
	}

	@Override
	public void showGuide() {
		new MaterialShowcaseView.Builder(this).setTarget(mAppFab).setDelay(700)
				.setContentText("Click this button to add an App ~").setDismissText("Got it")
				.setDismissTextColor(Color.parseColor("#03a9f4")).show();
	}

	@Override
	public void showFab() {
		mAppFab.show();
		mCrashFab.hide();
	}

	@Override
	public void hideFab() {
		mAppFab.hide();
		mCrashFab.setVisibility(View.VISIBLE);
		mCrashFab.show();
	}

	@Override
	public void setCrashShadow(boolean isShow) {
		mCrashFab.setShadow(isShow);
	}

	@Override
	public void waitingAppOpen() {
		ProgressDialog.show(this, "Please wait", "Opening the app...");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
	}

	@Override
	public void refreshPagerView() {
		mPagerView.refreshView();
	}

	@Override
	public void addAppToLauncher(AppModel model) {
		mAdapter.add(model);
		mPagerView.itemAdded();
	}

	private void hideLoading() {
		mLoadingBar.setVisibility(View.GONE);
		mPagerView.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == VCommends.REQUEST_SELECT_APP && data != null) {
			AppModel model = data.getParcelableExtra(VCommends.EXTRA_APP_MODEL);
			mPresenter.addApp(model);
			AppSetting info = VirtualCore.get().findApp(model.packageName);
			if (info != null) {
				if (info.dependSystem) {
					mPresenter.dataChanged();
					return;
				}
				model.context = this;
				ProgressDialog dialog = ProgressDialog.show(this, "Please wait", "Optimizing new Virtual App...");
				VUiKit.defer().when(() -> {
					try {
						model.loadData(info.getApplicationInfo(VUserHandle.USER_OWNER));
						VirtualCore.get().preOpt(info.packageName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).done((res) -> {
					dialog.dismiss();
					mPresenter.dataChanged();
				});
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterInstallerReceiver();
	}

	public String[] getUsers() {
		List<VUserInfo> userList = VUserManager.get().getUsers(false);
		List<String> users = new ArrayList<>(userList.size());
		for (VUserInfo info : userList) {
			users.add(info.name);
		}
		return users.toArray(new String[users.size()]);
	}

	public class InstallerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (HomeActivity.this.mPresenter == null)
				return;

			HomeActivity.this.mPresenter.dataChanged();
		}
	}
}
