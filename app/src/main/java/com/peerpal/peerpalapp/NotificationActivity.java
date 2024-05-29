package com.peerpal.peerpalapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NotificationActivity  {
    String channelID = "CHANNEL_ID_NOTIFICATION";
    NotificationCompat.Builder builder =
            new NotificationCompat.Builder(getApplicationContext(), channelID);



}