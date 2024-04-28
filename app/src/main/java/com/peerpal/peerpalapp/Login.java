package com.peerpal.peerpalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private EditText email, password;

    private TextView signupText;
    private Button loginBtn;
    FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        //initiating the layout items
        email = findViewById(R.id.login_Email);
        password = findViewById(R.id.login_Password);
        loginBtn = findViewById(R.id.loginBtnAccept);
        signupText = findViewById(R.id.login_signup_redirect);
        firebaseAuth =FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString().trim();
                String passwordString = password.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                    email.setError("Invalid Email");
                    email.requestFocus(); // Request focus to highlight the email field
                } else {
                    // If the email matches the standard pattern, check if it belongs to the specified domain
                    if (!emailString.endsWith("@autuni.ac.nz")) {
                        email.setError("Invalid domain. Use your AUT student domain to Login");
                        email.requestFocus(); // Request focus to highlight the email field
                    } else{
                        loginUser(emailString, passwordString);
                    }
                }
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, signUpActivity.class));
            }
        });


    }



    private void loginUser(String email, String passwrd) {

        firebaseAuth.signInWithEmailAndPassword(email, passwrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if(task.getResult().getAdditionalUserInfo().isNewUser()){
                        String email = user.getEmail();
                        String uid = user.getUid();
                        HashMap<String, Object> hashMap =new HashMap<>();
                        hashMap.put("email", email);
                        hashMap.put("uid", uid);
                        hashMap.put("Status", "");
                        hashMap.put("image","");
                        hashMap.put("degree","");
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference =database.getReference("Users");
                        reference.child(uid).setValue(hashMap);
                    }
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(Login.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Error Occurred", Toast.LENGTH_LONG).show();
            }
        });
    }

}