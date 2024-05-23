package com.peerpal.peerpalapp;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.FileInputStream;
import java.io.IOException;


public class NotificationService extends FirebaseMessagingService {



        //FileInputStream serviceAccount = new FileInputStream("path/to/your-service-account-file.json");

       //FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl("https://<YOUR_PROJECT_ID>.firebaseio.com/").build();
       //FirebaseApp.initializeApp(options);

       //String registrationToken = "YOUR_REGISTRATION_TOKEN";


       //Message message = Message.builder().setNotification(new Notification("Test Notification", "This is a test notification")).setToken(registrationToken).build();

    //String response = FirebaseMessaging.getInstance().send(message);
    // System.out.println("Successfully sent message: " + response);

}
