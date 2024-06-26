package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.peerpal.peerpalapp.ui.home.HomeFragment;
import com.peerpal.peerpalapp.ui.messages.MessagesFragment;
import com.peerpal.peerpalapp.ui.peers.PeersFragment;

// Main activity of the application, responsible for managing navigation and fragments
public class MainActivity extends AppCompatActivity {

    // UI elements
    BottomNavigationView bottomNavigationView;
    ImageButton menuButton;
    ImageButton surveyButton;
    ImageButton reviewButton;

    // Fragments
    HomeFragment homeFragment;
    PeersFragment peersFragment;
    MessagesFragment messagesFragment;

    // Called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); // Set the layout for this activity

        // Initialize fragments
        homeFragment = new HomeFragment();
        peersFragment = new PeersFragment();
        messagesFragment = new MessagesFragment();

        // Initialize UI elements
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        menuButton = findViewById(R.id.main_menu);
        reviewButton = findViewById(R.id.review_button);
        surveyButton = findViewById(R.id.survey_button);

        // Set onClickListener for menu button
        menuButton.setOnClickListener(v -> {
            // Redirect to view profile activity
            Intent mainIntent = new Intent(MainActivity.this, ViewProfile.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        });

        reviewButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(MainActivity.this, ReviewActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        });

        surveyButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(MainActivity.this, SurveyActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        });

        // Set listener for bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Replace fragment based on selected item
            if(item.getItemId()==R.id.navigation_home){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,homeFragment).commit();
            }
            if(item.getItemId()==R.id.navigation_peers){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,peersFragment).commit();
            }
            if(item.getItemId()==R.id.navigation_messages){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,messagesFragment).commit();
            }
            return true;
        });

        // Set default selected item in bottom navigation view
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}