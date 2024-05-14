package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

// Activity responsible for creating user account
public class SignUpActivity extends AppCompatActivity {
    EditText firstName, lastname, email, phone, password, confirmPassword;
    TextView acc_Creation_loginRedirect;
    Button signupButton;
    FirebaseAuth mAuth;
    List<String> hobbies = new ArrayList<>();
    List<String> connections = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        // Initialize views
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
        hobbies.add("");
        hobbies.add("");
        hobbies.add("");
        connections.add("");

        signupButton.setOnClickListener(v -> {
            // Get input values
            String trimFirstName = firstName.getText().toString().trim();
            String trimEmail = email.getText().toString().trim();
            String trimPhone = phone.getText().toString().trim();
            String trimPassword = password.getText().toString().trim();
            String trimConfirmPassword = confirmPassword.getText().toString().trim();
            // Validate email format
            if (!Patterns.EMAIL_ADDRESS.matcher(trimEmail).matches()) {
                email.setError("Invalid Email");
                email.requestFocus(); // Request focus to highlight the email field
            } else {
                // Check if the email belongs to the specified domain
                if (!trimEmail.endsWith("@autuni.ac.nz")) {
                    email.setError("Invalid domain. Use your AUT student domain");
                    email.requestFocus(); // Request focus to highlight the email field
                }
            }
            // Validate phone format
            try {
                int i = Integer.parseInt(trimPhone);
            } catch (NumberFormatException e) {
                phone.setError("Phone number format is not correct");
                phone.requestFocus(); // Request focus to highlight the phone field
            }
            // Validate password match
            if (!trimPassword.equals(trimConfirmPassword)) {
                // Passwords do not match
                confirmPassword.setError("Passwords do not match");
                confirmPassword.requestFocus(); // Request focus to highlight the confirm password field
            } else {
                // Passwords match, proceed with registration
                userRegister(trimFirstName, trimEmail, trimPassword);
            }
        });

        acc_Creation_loginRedirect.setOnClickListener(v -> {
            // Redirect to login activity
            startActivity(new Intent(SignUpActivity.this, Login.class));
        });
    }

    // Method to register a new user
    private void userRegister(String firstName, String email, final String password) {
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
                hashMap.put("image", "");
                hashMap.put("degree", "");
                hashMap.put("hobbies", hobbies);
                hashMap.put("connections", connections);
                // Add user data to Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference reference = db.collection("peers").document(uid);
                reference.set(hashMap);
                // Redirect to profile creation activity
                Intent mainIntent = new Intent(SignUpActivity.this, ProfileCreation.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            } else {
                // Registration failed
                Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            // Failure in user registration
            Toast.makeText(SignUpActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
        });
    }
}