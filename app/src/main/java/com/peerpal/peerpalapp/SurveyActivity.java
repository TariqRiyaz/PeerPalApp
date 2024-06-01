package com.peerpal.peerpalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.peerpal.peerpalapp.ui.peers.PeersClass;

import java.util.HashMap;

public class SurveyActivity extends AppCompatActivity {
    Button Submit_button;
    TextInputEditText food_text;
    TextInputEditText superpowers_text;
    TextInputEditText movie_text;
    PeersClass peersClass;
    ImageButton BackButton;

    public SurveyActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_survey);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Submit_button = findViewById(R.id.submit_survey);
        food_text  = findViewById(R.id.food_text);
        superpowers_text  = findViewById(R.id.power_text);
        movie_text = findViewById(R.id.movie_text);
        BackButton = findViewById(R.id.back_home);

        Submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(SurveyActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);

                String superpower = String.valueOf(superpowers_text.getText());
                String food = String.valueOf(food_text.getText());
                String movie = String.valueOf(movie_text.getText());

                //Store in firebase
                UploadSurvey(food, movie,superpower);
                String respond = "Thanks you!";
                Toast.makeText(SurveyActivity.this, respond, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SurveyActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Redirect back to main activity when back button is pressed
                Intent mainIntent = new Intent(SurveyActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void UploadSurvey(String food, String movie, String superpower)
    {
        // Create user data hashmap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Food", food);
        hashMap.put("Movie", movie);
        hashMap.put("Superpower", superpower);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("survey").document();
        reference.set(hashMap);
        finish();
    }
}