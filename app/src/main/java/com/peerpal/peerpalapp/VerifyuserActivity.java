package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyuserActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseUser user;
    Button resendCode;
    Button verifyBtn;
    ProgressBar progressBar;

    String userId;

    TextView verificationMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.verifyuser);
        fAuth  = FirebaseAuth.getInstance();

        verificationMsg = findViewById(R.id.verificationMsg);
        resendCode = findViewById(R.id.resendBtn);
        verifyBtn = findViewById(R.id.checkverifyBtn);
        progressBar = findViewById(R.id.progressBar);

        user = fAuth.getCurrentUser();

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.reload();
                if(!user.isEmailVerified()){
                    verificationMsg.setText("Not verfied, Please try again!");
                } else if (user.isEmailVerified()) {
                    Intent mainIntent = new Intent(VerifyuserActivity.this, ProfileCreation.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    showLoading(false);
                    finish();
                }
            }
        });

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sending verification link
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(v.getContext(), "Verification Email Has been sent", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(VerifyuserActivity.this, VerifyuserActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        showLoading(false);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("verifyuser", "Verification link failed");
                    }
                });
            }
        });
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