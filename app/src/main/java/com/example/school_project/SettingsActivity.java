package com.example.school_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // Replace with your actual layout name

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable navigation icon functionality
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Handle navigation icon click
        toolbar.setNavigationOnClickListener(v -> {
            // Finish the activity and return to the previous screen
            finish();
        });

        // Dark mode switch
        SwitchCompat switchDarkMode = findViewById(R.id.switch_dark_mode);

        // Set listener for dark mode toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Enable dark mode
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Disable dark mode
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Set up the buttons for notifications and music
        Button btnSendNotification = findViewById(R.id.btn_send_notification);
        Button btnPlayMusic = findViewById(R.id.btn_play_music);

        // Set onClickListeners for the buttons
        btnSendNotification.setOnClickListener(v -> {
            // Trigger the notification
            NotificationHelper notificationHelper = new NotificationHelper(this);
            notificationHelper.sendNotification("Internet Disconnected", "Your internet connection has been lost.");
        });

        btnPlayMusic.setOnClickListener(v -> {
            // Start the music service
            Intent musicIntent = new Intent(this, MusicPlayerService.class);
            startService(musicIntent);
        });
    }
}
