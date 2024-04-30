package com.peerpal.peerpalapp;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class signUpActivity extends AppCompatActivity {

    EditText firstName,lastname, email, password, confirmPassword;
    TextView acc_Creation_loginRedirect;
    Button signupButton;

    private FirebaseAuth mAuth;

    List<String> hobbies = new ArrayList<>();
    List<String> connections = new ArrayList<>();
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
       acc_Creation_loginRedirect = findViewById(R.id.acc_Creation_loginRedirect);
       mAuth=FirebaseAuth.getInstance();
       hobbies.add("");
       hobbies.add("");
       hobbies.add("");
       connections.add("");

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
        acc_Creation_loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signUpActivity.this, Login.class));
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
                    HashMap<String, Object> hashMap =new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("firstName", firstName);
                    hashMap.put("Status", "online");
                    hashMap.put("image","");
                    hashMap.put("degree","");
                    hashMap.put("hobbies",hobbies);
                    hashMap.put("connections",connections);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference reference = db.collection("peers").document(uid);
                    reference.set(hashMap);
                    Intent mainIntent = new Intent(signUpActivity.this, profileCreation.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Toast.makeText(signUpActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(signUpActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
            }
        });
    }
}