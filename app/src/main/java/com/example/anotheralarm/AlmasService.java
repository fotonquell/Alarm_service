package com.example.anotheralarm;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class AlmasService extends IntentService {

    // Must create a default constructor
    public AlmasService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    @Override

    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        if (NLService.notifications > 0){
            task();
        }
        System.out.println(NLService.notifications);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    public void task(){
        try {
            System.out.println("INSIDE TASK");
            File dir = new File("/storage/emulated/0/documents/Chiara/");
            int min = 1;
            int max = Objects.requireNonNull(dir.listFiles()).length;
            String filename = "";
            Random rand = new Random();
            int voiceN = rand.nextInt((max - min) + 1) + min;
            int i = 0;
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                if (f.isFile()) {
                    if (i == voiceN) {
                        break;
                    }
                    filename = f.getName();
                    i++;
                }
            }
            audioPlayer("/storage/emulated/0/documents/Chiara/", filename);
        } catch (Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public void audioPlayer(String path, String fileName){
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();
        try {
            SystemClock.sleep(5000);
            mp.setDataSource(path + File.separator + fileName);
            mp.prepare();
            mp.start();
            SystemClock.sleep(15000);
            mp.reset();
            mp.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

