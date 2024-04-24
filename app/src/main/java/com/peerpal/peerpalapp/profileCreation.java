package com.peerpal.peerpalapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class profileCreation extends AppCompatActivity {

    EditText degree;

    private List<Button> hobbyButtons;
    private List<String> selectedHobbies;

    FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();


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


}