package com.peerpal.peerpalapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

// Activity responsible for displaying user profile and displaying edit and logout options
public class ViewProfile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_profile);

        // Apply window insets to handle system UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore and FirebaseAuth instances
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String peersUID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        // Get reference to the current user's document
        DocumentReference docRef = db.collection("peers").document(peersUID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                // Initialize views to display user information
                ImageView peersImage = findViewById(R.id.peersImage);
                TextView peersName = findViewById(R.id.peersName);
                TextView peersDegree = findViewById(R.id.peersDegree);
                TextView peersEmail = findViewById(R.id.peersEmail);

                // Load user image using Picasso library
                Picasso.get().load((String) document.get("image")).into(peersImage);
                peersName.setText((String) document.get("name"));
                peersDegree.setText((String) document.get("degree"));
                peersEmail.setText((String) document.get("email"));
            } else {
                // Log error if fetching document fails
                Log.d(TAG, "Error getting user document: ", task.getException());
            }
        });

        // Initialize buttons for edit profile and logout
        Button editProfileButton = findViewById(R.id.editprofileButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set click listeners for buttons
        editProfileButton.setOnClickListener(v -> {
            // Redirect to profile edit activity
            Intent mainIntent = new Intent(ViewProfile.this, ProfileEdit.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        });

        logoutButton.setOnClickListener(v -> {
            // Sign out current user and redirect to splash screen
            firebaseAuth.signOut();
            Intent mainIntent = new Intent(ViewProfile.this, SplashScreen.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Redirect back to main activity when back button is pressed
                Intent mainIntent = new Intent(ViewProfile.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }
}