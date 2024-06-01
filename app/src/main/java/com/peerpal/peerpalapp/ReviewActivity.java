package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ReviewActivity extends AppCompatActivity {

    Button Submit_button;
    RatingBar RatingStars;
    TextInputEditText Feedback;
    ImageButton BackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Submit_button = findViewById(R.id.Submit);
        RatingStars = findViewById(R.id.Star_rating);
        Feedback = findViewById(R.id.feedback_text);

        BackButton = findViewById(R.id.back_button);

        RatingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                float rate = rating;
            }

        });

        Submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ReviewActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                String message = String.valueOf(Feedback.getText());
                float rating = RatingStars.getRating();

                //Store in firebase
                UploadRating(rating, message);
                String respond = "Thanks for rating!";
                Toast.makeText(ReviewActivity.this, respond, Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ReviewActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }

        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Redirect back to main activity when back button is pressed
                Intent mainIntent = new Intent(ReviewActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void UploadRating(float rating, String feedback){
        // Create user data hashmap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Feedback", feedback);
        hashMap.put("Rating", rating);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("feedback").document();
        reference.set(hashMap);
        finish();
    }
}