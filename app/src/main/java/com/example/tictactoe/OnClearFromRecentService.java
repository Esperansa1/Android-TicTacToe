package com.example.tictactoe;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class OnClearFromRecentService extends Service {


    NotificationManager notificationManager;
    private final String CHANNEL_ID = "TicTacToeID";
    long startTime = 0;

    long elapsedTime;
    long elapsedSeconds;
    long secondsDisplay;
    long elapsedMinutes;

    @Override
    public String toString() {
        return "OnClearFromRecentService{" +
                "message='" + getMessage() + '\'' +
                '}';
    }

    //runs without a timer by reposting this handler at the end of the runnable
    @Override
    public void onCreate() {
        super.onCreate();
        startTime = System.currentTimeMillis();
        Log.d("Service", "Timer started");

    }


    public String getMessage() {
        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;
        secondsDisplay = elapsedSeconds % 60;
        elapsedMinutes = elapsedSeconds / 60;

        return ("You have played for a total time: " + String.format("%d:%02d", elapsedMinutes, secondsDisplay));
    }


    public void createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel(CHANNEL_ID,
                "TicTacToe", NotificationManager.IMPORTANCE_DEFAULT);

        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(chan);
    }

    public void createNotification() {

        System.out.println("SENDING NOTIFICATION");

        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.tictactoe)
                .setContentTitle(getMessage())
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "Bounding Service");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("Service", "Unbinding Service");

        createNotification();
        return super.onUnbind(intent);
    }


}