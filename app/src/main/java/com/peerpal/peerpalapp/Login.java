package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

// Activity for user login
public class Login extends AppCompatActivity {
    private EditText email, password;
    private FirebaseAuth firebaseAuth;

    // Called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_login); // Set the layout for this activity



        // Initialize layout items
        email = findViewById(R.id.login_Email);
        password = findViewById(R.id.login_Password);
        Button loginBtn = findViewById(R.id.loginBtnAccept);
        TextView signupText = findViewById(R.id.login_signup_redirect);
        firebaseAuth = FirebaseAuth.getInstance();

        // Set onClickListener for login button
        loginBtn.setOnClickListener(v -> {
            // Get email and password input
            String emailString = email.getText().toString().trim();
            String passwordString = password.getText().toString().trim();

            // Validate email format
            if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                email.setError("Invalid Email");
                email.requestFocus(); // Request focus to highlight the email field
            } else {
                // If email format is correct, check domain
                if (!emailString.endsWith("@autuni.ac.nz")) {
                    email.setError("Invalid domain. Use your AUT student domain to Login");
                    email.requestFocus(); // Request focus to highlight the email field
                } else {
                    // If domain is correct, attempt login
                    loginUser(emailString, passwordString);
                }
            }
        });

        // Set onClickListener for signup text
        signupText.setOnClickListener(v -> {
            // Redirect to sign-up activity
            startActivity(new Intent(Login.this, SignUpActivity.class));
        });
    }

    // Method to authenticate user login
    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // On successful login
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // If user is new, add to database
                if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()) {
                    assert user != null;
                    String email1 = user.getEmail();
                    String uid = user.getUid();
                    // Create user data map
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("email", email1);
                    userData.put("uid", uid);
                    userData.put("Status", "");
                    userData.put("image", "");
                    userData.put("degree", "");
                    // Add user data to database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("NewUsers");
                    reference.child(uid).setValue(userData);
                }
                // Display success message
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();
                // Redirect to main activity
                Intent mainIntent = new Intent(Login.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            } else {
                // Display failure message
                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            // Display error message
            Toast.makeText(Login.this, "Error Occurred", Toast.LENGTH_LONG).show();
        });
    }
}