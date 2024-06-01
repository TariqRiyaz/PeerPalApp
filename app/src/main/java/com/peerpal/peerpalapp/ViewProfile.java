package com.peerpal.peerpalapp;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
        Button deleteAccountButton = findViewById(R.id.deleteAccountButton);


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

        deleteAccountButton.setOnClickListener(v -> {
            // Prompts user to verify deletion of account
            final EditText inputField = new EditText(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Account Deletion");
            builder.setMessage("Please enter email to verify deletion of account...");
            builder.setView(inputField);

            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String input = inputField.getText().toString().trim();
                    TextView email = findViewById(R.id.peersEmail);
                    if (input.contentEquals(email.getText())) {
                        //Deleting peers user document
                        String peerUID = firebaseAuth.getCurrentUser().getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference peerDocRef = db.collection("peers").document(peerUID);
                        peerDocRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                peerDocRef.delete();
                            }
                        });

                        //Deleting peer from other user connections
                        db.collection("peers").get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                    if (((ArrayList<String>) document.get("connections")).contains(peerUID)) {
                                        String tempUID = (String) document.get("uid");
                                        DocumentReference tempDocRef = db.collection("peers").document(tempUID);
                                        ArrayList<String> tempConnections = ((ArrayList<String>) document.get("connections"));
                                        assert tempConnections != null;
                                        tempConnections.remove(peerUID);
                                        tempDocRef.update("connections", tempConnections);
                                    }
                                }
                            }
                        });

                        //Deleting chatrooms that mention peer
                        db.collection("chatrooms").whereArrayContains("userIds", peerUID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                    }
                                }
                            }
                        });

                        // Deleting authentication of user and sending back to splashscreen
                        firebaseAuth.getCurrentUser().delete();
                        firebaseAuth.signOut();

                        Intent mainIntent = new Intent(ViewProfile.this, SplashScreen.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
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