package com.example.anotheralarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class NLService extends NotificationListenerService {
    private static final String REBIND_ACTION = "REBIND";
    public static int notifications;


    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver nlservicereciver;


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("ON CREATE NLService");
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.anotheralarm.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver,filter);
    }

    @Override
    public void onDestroy() {
    //    super.onDestroy();
        System.out.println("ON DESTROY NLService");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        System.out.println("onNotificationPosted");
        int i = 0;
        for (StatusBarNotification sb : NLService.this.getActiveNotifications()) {
            if (!sb.getPackageName().equals("android")){
                if (!sb.getPackageName().equals("com.example.anotheralarm")){
                    System.out.println(sb.getPackageName() + "\n");
                    i++;
                }
            }
        }
        notifications = i;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        System.out.println("onNotificationRemoved");
        int i = 0;
        for (StatusBarNotification sb : NLService.this.getActiveNotifications()) {
            if (!sb.getPackageName().equals("android")){
                if (!sb.getPackageName().equals("com.example.anotheralarm")){
                    System.out.println(sb.getPackageName() + "\n");
                    i++;
                }
            }
        }
        notifications = i;
    }
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();


        String NOTIFICATION_CHANNEL_ID = "com.example.andy.myapplication";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);

        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentIntent(pendingIntent).build();
        startForeground(1337, notification);
        //Alarm to auto - send Intents to Service to reconnect, you can ommit next line.
        alarmIt();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Notification service onStartCommandCalled");
        if (intent!=null && intent.getAction().equals(REBIND_ACTION)){
            Log.d(TAG, "Notification service REBIND ACTION");
            tryReconnectService();//switch on/off component and rebind
        }
        //START_STICKY  to order the system to restart your service as soon as possible when it was killed.
        return START_STICKY;
    }

    public void tryReconnectService() {
        Log.d(TAG, "Notification service RECONNECT");
        toggleNotificationListenerService();
        ComponentName componentName =
                new ComponentName(getApplicationContext(), NLService.class);

        //It say to Notification Manager RE-BIND your service to listen notifications again inmediatelly!
        Log.d(TAG, "Notification service REBINDING");
        requestRebind(componentName);
    }
    private void alarmIt() {

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + 1);

        Intent intent = new Intent(this, NLService.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.setAction(REBIND_ACTION);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                intent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //The alarms that are repeated are inaccurate by default, use RTC_WAKE_UP at your convenience.
        //Alarm will fire every minute, CHANGE THIS iF DO YOU CAN, you can't use less than 1 minute to repeating alarms.
        manager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), 1000 * 60 * 1, pendingIntent);
    }


    /**
     * Try deactivate/activate your component service
     */
    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, NLService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, NLService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

}

class NLServiceReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}