package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DURATION = 2000; // 2 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Handler to delay the transition to the main activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start the main activity after the splash screen duration
            Intent intent = new Intent(MainActivity.this, splashScreen.class);
            startActivity(intent);
            finish(); // Close the main activity
        }, SPLASH_SCREEN_DURATION);
    }
}