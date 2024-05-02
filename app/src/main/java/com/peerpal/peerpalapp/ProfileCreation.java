package com.peerpal.peerpalapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

    private List<String> selectedHobbies;
    EditText degree;
    ImageView imageProfileEdit;
    Button saveProfile, update_image;
    // Firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    DocumentReference documentReference;
    StorageReference storageReference;
    String currentUserId;
    String currentuserEmail;
    String username;
    // Constants for image selection
    private static final int PICK_IMAGE = 1;
    // URI for the selected image
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_creation);

        // Initialize lists and Firebase instances
        // UI elements
        selectedHobbies = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("profile_Images");

        // Get current user info
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        currentUserId = firebaseUser.getUid();
        currentuserEmail = firebaseUser.getEmail();
        documentReference = db.collection("peers").document(currentUserId);

        // Initialize UI elements
        degree = findViewById(R.id.profile_creation_degree);
        imageProfileEdit = findViewById(R.id.imageViewEdit);
        saveProfile = findViewById(R.id.saveProfileBtn);
        update_image = findViewById(R.id.update_image_button);

        // Set OnClickListener for save profile button
        saveProfile.setOnClickListener(v -> uploadData());

        // Set OnClickListener for update image button
        update_image.setOnClickListener(v -> {
            // Open gallery to select image
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_IMAGE);
        });
    }

    // Method called when a hobby button is clicked
    public void onHobbyClicked(View view) {
        // Logic for handling hobby button clicks
    }

    // Method called after selecting an image from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if image is selected successfully
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            imageUri = data.getData();
            // Load the selected image into ImageView
            Picasso.get().load(imageUri).into(imageProfileEdit);
        }
    }

    // Method to get the file extension of an image URI
    private String getFileExt(Uri uri){
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();
        // Get the mime type map
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Return the file extension of the image URI
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    // Method to upload user data to Firestore and profile image to Firebase Storage
    private void uploadData() {
        // Get the degree information entered by the user
        String degreeInfo = degree.getText().toString();

        // Check if degree information, image, and hobbies are selected
        if (imageUri != null && selectedHobbies != null) {
            // Create a storage reference for the image file
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
            // Upload the image file to Firebase Storage
            UploadTask uploadTask = reference.putFile(imageUri);

            // Continue with the task to get the download URL of the uploaded image
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                // Return the download URL of the uploaded image
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get the download URL of the uploaded image
                    Uri downloadUri = task.getResult();

                    // Create a profile data map with user information
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("uid", currentUserId);
                    profile.put("name", username);
                    profile.put("email", currentuserEmail);
                    profile.put("degree", degreeInfo);
                    profile.put("image", downloadUri.toString());
                    profile.put("hobbies", selectedHobbies);

                    // Set the user profile data in Firestore
                    documentReference.set(profile).addOnSuccessListener(unused -> {
                        // Show profile created toast message
                        Toast.makeText(ProfileCreation.this, "Profile Created", Toast.LENGTH_SHORT).show();

                        // Delay to show toast message and then navigate to MainActivity
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(ProfileCreation.this, MainActivity.class);
                            startActivity(intent);
                        }, 2000);
                    });
                }
            });
        } else {
            // Show error toast message if any field is empty
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}