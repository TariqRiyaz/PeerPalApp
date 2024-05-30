package com.peerpal.peerpalapp;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// Activity responsible for creating user profiles
public class ProfileCreation extends AppCompatActivity {
    private List<Button> hobbyButtons;
    private List<String> selectedHobbies;
    EditText degree;
    ImageView imageProfileEdit;
    private Uri imageUri;
    Button saveProfile, update_image;
    UploadTask uploadtasks;
    StorageReference storageReference;
    DocumentReference documentReference;
    private static final int PICK_IMAGE = 1;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    String currentuserEmail;
    ProgressBar progressBar;
    boolean newImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_creation);

        // Initialize lists and views
        hobbyButtons = new ArrayList<>();
        selectedHobbies = new ArrayList<>();

        // Initialize hobby buttons
        hobbyButtons.add(findViewById(R.id.HobbyButtonOne));
        hobbyButtons.add(findViewById(R.id.HobbyButtonTwo));
        hobbyButtons.add(findViewById(R.id.HobbyButtonThree));
        hobbyButtons.add(findViewById(R.id.HobbyButtonFour));
        hobbyButtons.add(findViewById(R.id.HobbyButtonFive));
        hobbyButtons.add(findViewById(R.id.HobbyButtonSix));
        hobbyButtons.add(findViewById(R.id.HobbyButtonSeven));
        hobbyButtons.add(findViewById(R.id.HobbyButtonEight));
        hobbyButtons.add(findViewById(R.id.HobbyButtonNine));
        hobbyButtons.add(findViewById(R.id.HobbyButtonTen));
        hobbyButtons.add(findViewById(R.id.HobbyButtonEleven));
        hobbyButtons.add(findViewById(R.id.HobbyButtonTwelve));
        degree = findViewById(R.id.profile_creation_degree);
        imageProfileEdit = findViewById(R.id.imageViewEdit);
        saveProfile = findViewById(R.id.saveProfileBtn);
        update_image = findViewById(R.id.update_image_button);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        // Get current user and Firestore reference
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        String peerUID = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference selfDocRef = db.collection("peers").document(peerUID);

        // Retrieve user data from Firestore
        selfDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                // Load profile image and degree
                imageUri = Uri.parse((String)document.get("image"));
            } else {
                Log.d(TAG, "Error getting user document: ", task.getException());
            }
        });

        // Set current user information
        currentUserId = user.getUid();
        currentuserEmail = user.getEmail();
        documentReference = db.collection("peers").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("profile_Images");

        // Handle save profile button click
        saveProfile.setOnClickListener(v -> uploadData());

        // Handle update image button click
        update_image.setOnClickListener(v -> {
            // Launch image picker
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_IMAGE);
            newImage = true;
        });
    }

    // Handle hobby button click
    public void onHobbyClicked(View view) {
        Button button = (Button) view;
        String hobby = button.getText().toString();

        // Check if the hobby is already selected
        if (selectedHobbies.contains(hobby)) {
            // Deselect the hobby
            selectedHobbies.remove(hobby);
            button.setBackgroundResource(android.R.drawable.btn_default); // Reset button background
        } else {
            // Check if maximum hobbies selected
            if (selectedHobbies.size() < 3) {
                // Select the hobby
                selectedHobbies.add(hobby);
            } else {
                for (Button button1 : hobbyButtons) {
                    button.setEnabled(false);
                }
            }
        }

        // Update button states
        updateButtonStates();
    }

    // Update button states based on selected hobbies
    private void updateButtonStates() {
        // Enable all buttons
        for (Button button : hobbyButtons) {
            button.setEnabled(true);
        }

        // Disable buttons for already selected hobbies
        for (String hobby : selectedHobbies) {
            for (Button button : hobbyButtons) {
                if (button.getText().toString().equals(hobby)) {
                    button.setEnabled(false);
                    button.setBackgroundColor(Color.RED);
                    button.setTextColor(Color.WHITE);// Set button background

                }
            }
        }
        // Disable remaining buttons if max selections reached
        if(selectedHobbies.size()>=3)

        {
            for (Button button : hobbyButtons) {
                if (!selectedHobbies.contains(button.getText().toString())) {
                    button.setEnabled(false);
                }
            }
        }
    }

    // Handle result from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
                imageUri = data.getData();

                Picasso.get().load(imageUri).into(imageProfileEdit);
            }
        }catch (Exception e){
            Toast.makeText(this, "Error"+e, Toast.LENGTH_SHORT).show();
        }

    }

    // Get file extension from URI
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    // Upload data to Firestore
    private void uploadData() {
        showLoading(true);
        String degreeInfo = degree.getText().toString();
        // Uploading new image
        if (degree.getText().length() > 0 && imageUri != null && !selectedHobbies.isEmpty()) {
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));
            uploadtasks = reference.putFile(imageUri);
            uploadtasks.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    documentReference.update("degree", degreeInfo);
                    documentReference.update("image", downloadUri.toString());
                    documentReference.update("hobbies", selectedHobbies);
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        Intent intent = new Intent(ProfileCreation.this, MainActivity.class);
                        startActivity(intent);
                    }, 2000);
                }
            });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            showLoading(false);
        }
    }

    // Function to toggle loading wheel on/off
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}