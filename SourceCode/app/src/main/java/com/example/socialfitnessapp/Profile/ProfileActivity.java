package com.example.socialfitnessapp.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.socialfitnessapp.Home.MainActivity;
import com.example.socialfitnessapp.MyDiary.MyDiaryActivity;
import com.example.socialfitnessapp.R;
import com.example.socialfitnessapp.Social.SocialActivity;

public class ProfileActivity extends AppCompatActivity {

    ImageView homeBtn, socialBtn, myProfileBtn, diaryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        homeBtn = findViewById(R.id.profile_homeButton);
        socialBtn = findViewById(R.id.profile_socialButton);
        myProfileBtn = findViewById(R.id.profile_myProfileButton);
        diaryBtn = findViewById(R.id.profile_myDiaryButton);

        buttons();
    }

    // Method that is responsible for all the buttons on the activity
    protected void buttons() {
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        socialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SocialActivity.class);
                startActivity(intent);
                finish();
            }
        });

        myProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        diaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyDiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}