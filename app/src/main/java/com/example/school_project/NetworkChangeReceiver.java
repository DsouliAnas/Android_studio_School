package com.example.school_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork == null || !activeNetwork.isConnected()) {
            // No internet connection
            sendNotification(context);
        }
    }

    private void sendNotification(Context context) {
        // Create and show a notification
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.sendNotification("Internet Disconnected", "Your internet connection has been lost.");
    }
}
