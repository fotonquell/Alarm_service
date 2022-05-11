package com.example.anotheralarm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("aaaaaaaaaa");
        System.out.println("CONTEXT:" + context.toString());
        System.out.println("INTENT" + intent.getAction());
        if (intent.getAction().equals("Sound")){
            Intent i = new Intent(context, AlmasService.class);
            i.putExtra("foo", "AlarmReceiver");
            context.startService(i);
        }
    }
}
