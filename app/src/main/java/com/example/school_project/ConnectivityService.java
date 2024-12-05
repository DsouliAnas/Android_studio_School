package com.example.school_project;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class ConnectivityService extends Service {

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Define the network callback to handle connection events
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // Connection available, show notification
                showConnectionStatusNotification("Connected to the internet");
            }

            @Override
            public void onLost(Network network) {
                showConnectionStatusNotification("No internet connection");
            }
        };

        connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        return START_STICKY;
    }

    private void showConnectionStatusNotification(String status) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default_channel",
                    "Connectivity Status",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, "default_channel")
                .setContentTitle("Internet Connection Status")
                .setContentText(status)
                .setSmallIcon(R.drawable.ic_internet_status)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();

        notificationManager.notify(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
