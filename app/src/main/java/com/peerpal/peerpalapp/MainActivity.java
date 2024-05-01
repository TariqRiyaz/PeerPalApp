package com.peerpal.peerpalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.peerpal.peerpalapp.ui.home.HomeFragment;
import com.peerpal.peerpalapp.ui.peers.PeersFragment;
import com.peerpal.peerpalapp.ui.messages.MessagesFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton menuButton;
    HomeFragment homeFragment;
    PeersFragment peersFragment;
    MessagesFragment messagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        peersFragment = new PeersFragment();
        messagesFragment = new MessagesFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        menuButton = findViewById(R.id.main_menu);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, ViewProfile.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, ViewProfile.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}