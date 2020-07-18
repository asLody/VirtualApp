package com.lody.virtual.client.stub;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.lody.virtual.server.notification.NotificationCompat;


/**
 * @author Lody
 *
 */
public class DaemonService extends Service {

    private static final int NOTIFY_ID = 1001;

	public static void startup(Context context) {
		context.startService(new Intent(context, DaemonService.class));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		startup(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
        startService(new Intent(this, InnerService.class));
	
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String channelId = "com.loy.1";
			NotificationChannel nc = new NotificationChannel(channelId, "Channel 1", android.app.NotificationManager.IMPORTANCE_DEFAULT);
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.createNotificationChannel(nc);
			
			Notification.Builder nb = new Notification.Builder(this, channelId);
			
			startForeground(NOTIFY_ID, nb.build());
		} else {
			startForeground(NOTIFY_ID, new Notification());
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	public static final class InnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				String channelId = "com.loy.2";
				NotificationChannel nc = new NotificationChannel(channelId, "Channel 2", android.app.NotificationManager.IMPORTANCE_DEFAULT);
				NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				manager.createNotificationChannel(nc);

				Notification.Builder nb = new Notification.Builder(this, channelId);

				startForeground(NOTIFY_ID, nb.build());
			} else {
				startForeground(NOTIFY_ID, new Notification());
			}
			
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
	}


}
