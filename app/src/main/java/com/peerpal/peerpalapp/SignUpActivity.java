package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Activity responsible for creating user account
public class SignUpActivity extends AppCompatActivity {
    EditText firstName, lastname, email, phone, password, confirmPassword;
    TextView acc_Creation_loginRedirect;
    Button signupButton;
    FirebaseAuth mAuth;
    List<String> hobbies = new ArrayList<>();
    List<String> connections = new ArrayList<>();
    ProgressBar progressBar;

    Boolean empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        // Initialize views
        empty = true;
        firstName = findViewById(R.id.acc_Creation_First_Name);
        lastname = findViewById(R.id.acc_Creation_Last_Name);
        email = findViewById(R.id.acc_Creation_Email);
        phone = findViewById(R.id.acc_Creation_Phone);
        password = findViewById(R.id.acc_Creation_Password);
        confirmPassword = findViewById(R.id.acc_Creation_Cofirm_Password);
        signupButton = findViewById(R.id.acc_Creation_signup_btn);
        acc_Creation_loginRedirect = findViewById(R.id.acc_Creation_loginRedirect);
        mAuth = FirebaseAuth.getInstance();
        // Add default values to lists
        hobbies.add("No Hobbies");
        connections.add("");
        progressBar = findViewById(R.id.progressBar);


        try {
            signupButton.setOnClickListener(v -> {
                // Show loading wheel
                showLoading(true);
                // Get input values
                String trimFirstName = firstName.getText().toString().trim();
                String trimEmail = email.getText().toString().trim();
                String trimPhone = phone.getText().toString().trim();
                String trimPassword = password.getText().toString().trim();
                String trimConfirmPassword = confirmPassword.getText().toString().trim();
                if (firstName.getText().toString().equals("")) {
                    firstName.setError("Enter a First name");
                    firstName.requestFocus();
                }
                // Validate email format
                if (!Patterns.EMAIL_ADDRESS.matcher(trimEmail).matches()) {
                    email.setError("Invalid Email");
                    email.requestFocus(); // Request focus to highlight the email field
                    showLoading(false);
                } else {
                    // Check if the email belongs to the specified domain
                    if (!trimEmail.endsWith("@autuni.ac.nz")) {
                        email.setError("Invalid domain. Use your AUT student domain");
                        email.requestFocus(); // Request focus to highlight the email field
                        showLoading(false);
                    }
                }
                // Validate phone format
                try {
                    Integer.parseInt(trimPhone);

                    if ((phone.getText() == null) || (phone.getText().equals(""))) {
                        phone.setError("Invalid Phone Format");
                        phone.requestFocus(); // Request focus to highlight the phone field
                        showLoading(false);
                    }
                } catch (NumberFormatException e) {
                    phone.setError("Invalid Phone Format");
                    phone.requestFocus(); // Request focus to highlight the phone field
                    showLoading(false);
                }
                if ((password.getText().toString().equals(""))) {
                    password.setError("Password Field Cannot be Empty");
                    password.requestFocus(); // Request focus to highlight the phone field
                    showLoading(false);
                }
                // Validate password match
                if (!trimPassword.equals(trimConfirmPassword)) {
                    // Passwords do not match
                    confirmPassword.setError("Passwords do not match");
                    confirmPassword.requestFocus(); // Request focus to highlight the confirm password field
                    showLoading(false);
                } else {
                    if (!firstName.getText().toString().equals("") && Patterns.EMAIL_ADDRESS.matcher(trimEmail).matches() && trimEmail.endsWith("@autuni.ac.nz") && !phone.getText().toString().equals("") && !password.getText().toString().equals("")) {
                        empty = false;
                        // Passwords match, proceed with registration
                        userRegister(trimFirstName, trimEmail, trimPassword, trimPhone);
                    }
                }
            });
        } catch (Exception e){
            Toast.makeText(SignUpActivity.this, "Empty Fields", Toast.LENGTH_LONG).show();
        }

        acc_Creation_loginRedirect.setOnClickListener(v -> {

            // Redirect to login activity
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });
    }

    // Method to register a new user
    private void userRegister(String firstName, String email, final String password, String phone) {
        if (!empty) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Registration successful
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    assert currentUser != null;
                    String email1 = currentUser.getEmail();
                    String uid = currentUser.getUid();
                    // Create user data hashmap
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("email", email1);
                    hashMap.put("phone", phone);
                    hashMap.put("uid", uid);
                    hashMap.put("name", firstName);
                    hashMap.put("image", "https://firebasestorage.googleapis.com/v0/b/peerpalapp.appspot.com/o/profile_Images%2F1756.png?alt=media&token=3dd4d143-5635-4924-b598-c9138eb37e8c");
                    hashMap.put("degree", "No Degree");
                    hashMap.put("hobbies", hobbies);
                    hashMap.put("connections", connections);
                    // Add user data to Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference reference = db.collection("peers").document(uid);
                    reference.set(hashMap);
                    // Redirect to profile creation activity
                    Intent mainIntent = new Intent(SignUpActivity.this, VerifyUserActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    showLoading(false);
                    finish();
                } else {
                    // Registration failed
                    Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> {
                // Failure in user registration
                Toast.makeText(SignUpActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
            });
        } else{
            Toast.makeText(SignUpActivity.this, "Please enter all the fields", Toast.LENGTH_LONG).show();
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