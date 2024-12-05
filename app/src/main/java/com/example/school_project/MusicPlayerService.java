package com.example.school_project;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MusicPlayerService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer = MediaPlayer.create(this, R.raw.music_file); // Ensure you have your music file in res/raw
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Show a notification indicating the music is playing
        showMusicStatusNotification("Music Playing");

        return START_STICKY;
    }

    private void showMusicStatusNotification(String status) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For devices with Android Oreo (API level 26) or higher, create a Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "music_channel", // Channel ID
                    "Music Player",  // Channel Name
                    NotificationManager.IMPORTANCE_LOW // Channel Importance
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, "music_channel")
                .setContentTitle("Music Player")
                .setContentText(status)
                .setSmallIcon(android.R.drawable.ic_media_play) // You can use a system icon or your own icon
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)  // Ensures the notification cannot be dismissed
                .build();

        notificationManager.notify(2, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
