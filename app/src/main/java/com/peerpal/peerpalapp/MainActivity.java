package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.peerpal.peerpalapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.peerpal.peerpalapp.ui.home.HomeFragment;
import com.peerpal.peerpalapp.ui.messages.MessagesFragment;
import com.peerpal.peerpalapp.ui.peers.PeersFragment;

// Main activity of the application, responsible for managing navigation and fragments
public class MainActivity extends AppCompatActivity {

    // UI elements
    BottomNavigationView bottomNavigationView;
    ImageButton menuButton;

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

        // Set onClickListener for menu button
        menuButton.setOnClickListener(v -> {
            // Redirect to view profile activity
            Intent mainIntent = new Intent(MainActivity.this, ViewProfile.class);
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


        getFCMToken();
    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.i("My token", token);
                System.out.println("My Token: "+token);
                //FirebaseFirestore.getInstance().collection("peers").document(FirebaseAuth.getInstance().getUid()).update("fcmtoken", token);

            }

        });

    }

}