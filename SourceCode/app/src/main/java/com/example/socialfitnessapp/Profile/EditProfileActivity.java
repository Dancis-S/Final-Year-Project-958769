package com.example.socialfitnessapp.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialfitnessapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {
    TextView changePP;
    EditText username, name, surname, bio;
    Button cancelBtn, saveBtn;
    ImageView profilePicture;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initialise();
        buttons();

    }

    // Method that initialises all the views
    protected void initialise() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        profilePicture = findViewById(R.id.editProfile_profilePicture);
        cancelBtn = findViewById(R.id.editProfile_cancelButton);
        saveBtn = findViewById(R.id.editProfile_saveButton);
        username = findViewById(R.id.editProfile_usernameInput);
        name = findViewById(R.id.editProfile_nameInput);
        surname = findViewById(R.id.editProfile_surnameInput);
        bio = findViewById(R.id.editProfile_bioInput);
        changePP = findViewById(R.id.editProfile_changePPButton);
    }

    // Method that contains all the buttons
    protected void buttons() {


        changePP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here for when user changes pp
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Saves all changes and uploads it
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }//end of buttons

}