package com.peerpal.peerpalapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Activity responsible for displaying SplashScreen and redirecting user to appropriate activity
@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        // Get the current user
        currentUser = mAuth.getCurrentUser();

        // Delay execution for 1 second to display splash screen
        new Handler().postDelayed(() -> {
            // Check if a user is logged in
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                // If no user is logged in, redirect to the login activity
                Intent intent = new Intent(SplashScreen.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                // If a user is logged in, redirect to the main activity
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                // Add flags to clear the task stack and start a new task
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        }, 1000); // 1000 milliseconds = 1 second delay
    }
}