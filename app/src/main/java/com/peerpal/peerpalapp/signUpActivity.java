package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class signUpActivity extends AppCompatActivity {

    EditText firstName,lastname, email, password, confirmPassword;
    TextView acc_Creation_loginRedirect;
    Button signupButton;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
       firstName = findViewById(R.id.acc_Creation_First_Name);
       lastname = findViewById(R.id.acc_Creation_Last_Name);
       email = findViewById(R.id.acc_Creation_Email);
       password = findViewById(R.id.acc_Creation_Password);
       confirmPassword = findViewById(R.id.acc_Creation_Cofirm_Password);
       signupButton =findViewById(R.id.acc_Creation_signup_btn);
       mAuth =FirebaseAuth.getInstance();

       signupButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String trimFirstName = firstName.getText().toString().trim();
               String trimLastName = lastname.getText().toString().trim();
               String trimEmail = email.getText().toString().trim();
               String trimPassword = password.getText().toString().trim();
               String trimConfirmPassword = confirmPassword.getText().toString().trim();
               if (!Patterns.EMAIL_ADDRESS.matcher(trimEmail).matches()) {
                   email.setError("Invalid Email");
                   email.requestFocus(); // Request focus to highlight the email field
               } else {
                   // If the email matches the standard pattern, check if it belongs to the specified domain
                   if (!trimEmail.endsWith("@autuni.ac.nz")) {
                       email.setError("Invalid domain. Use your AUT student domain");
                       email.requestFocus(); // Request focus to highlight the email field
                   }
               }
               if (!trimPassword.equals(trimConfirmPassword)) {
                   // Passwords do not match
                   confirmPassword.setError("Passwords do not match");
                   confirmPassword.requestFocus(); // Request focus to highlight the confirm password field
               } else {
                   userRegister(trimFirstName,trimEmail, trimPassword);
                   // Passwords match
                   // Proceed with further validation or submission
               }
           }
       });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signUpActivity.this, MainActivity.class));
            }
        });
    }

    private void userRegister(String firstName,String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String email = currentUser.getEmail();
                    String uid = currentUser.getUid();
                    HashMap<Object, String> hashMap =new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("firstName", firstName);
                    hashMap.put("Status", "online");
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference =database.getReference("Users");
                    reference.child(uid).setValue(hashMap);
                }
            }
        });
    }
}