package com.peerpal.peerpalapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class profileCreation extends AppCompatActivity {

    private List<Button> hobbyButtons;
    private List<String> selectedHobbies;

    EditText degree;
    ImageView imageProfileEdit;

    private ProgressDialog pd;
    private Uri imageUri;
    Button saveProfile, update_image;
    UploadTask uploadtasks;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference  databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    private static final int PICK_IMAGE = 1;


    String cameraPermission[];
    String storagePermission[];

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String currentUserId;

    private String storagePath = "Users_Profile_Cover_image/";

    private String profileOrCoverPhoto;

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_creation);
        hobbyButtons = new ArrayList<>();
        selectedHobbies = new ArrayList<>();

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
        degree = findViewById(R.id.profile_creation_degree);
        imageProfileEdit = findViewById(R.id.imageViewEdit);
        saveProfile = findViewById(R.id.saveProfileBtn);
        update_image = findViewById(R.id.update_image_button);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();
        documentReference = db.collection("Users").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("profile_Images");
        databaseReference =  database.getReference("NewUsers");

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        update_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });


    }

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

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void uploadData() {
        String degreeInfo = degree.getText().toString();

        if(!TextUtils.isEmpty(degreeInfo) || imageUri != null || selectedHobbies != null){
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
            Log.d("uploadtasks", "uploading file...");
            uploadtasks = reference.putFile(imageUri);

            Task<Uri> urlTask = uploadtasks.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d("completor", "onComplete: sucessful");

                        Map<String, Object> profile = new HashMap<>();
                        profile.put("degree", degreeInfo);
                        profile.put("image",downloadUri.toString());
                        profile.put("hobbies", selectedHobbies);

                        Map<String, Object> realtimeDbUpdate  = new HashMap<>();
                        realtimeDbUpdate.put("image", downloadUri.toString());

                        databaseReference.child(currentUserId).setValue(profile);

                        documentReference.set(profile)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(profileCreation.this, "Profile Created", Toast.LENGTH_SHORT).show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(profileCreation.this, splashScreen.class);
                                                startActivity(intent);
                                            }
                                        }, 2000);
                                    }
                                });
                    }
                }
            });

        } else{
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}