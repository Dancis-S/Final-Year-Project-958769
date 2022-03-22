package com.example.socialfitnessapp.MyDiary;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialfitnessapp.Authentication.LoginActivity;
import com.example.socialfitnessapp.Home.MainActivity;
import com.example.socialfitnessapp.Profile.ProfileActivity;
import com.example.socialfitnessapp.R;
import com.example.socialfitnessapp.Social.SocialActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyDiaryActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button addCaloriesBtn;
    String userID, date;
    TextView testBox;
    ImageView homeBtn, socialBtn, myProfileBtn, diaryBtn;
    DocumentReference diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_diary);

        initialise();
        buttons();
        fetchData();

    }


    private void fetchData() {
        DocumentReference documentReference = fStore.collection("diaries").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                // This is how you access the users diary info
                HashMap<String, DiaryTracking> book = (HashMap) value.get(date);
                testBox.setText((CharSequence) book.get("id"));
                updateCalories(1000);

            }
        });

    }

    // If HashMap doesn't contain today's elements then create it and upload it
    protected void addNewDay() {
        DiaryTracking today = new DiaryTracking(date);
        DocumentReference documentReference = fStore.collection("diaries").document(userID);
        documentReference.update(date, today);
        fetchData();
    }

    // Initialises all the views and firebase components
    protected void initialise() {
        homeBtn = findViewById(R.id.diary_homeButton);
        socialBtn = findViewById(R.id.diary_socialButton);
        myProfileBtn = findViewById(R.id.diary_profileButton);
        diaryBtn = findViewById(R.id.diary_diaryButton);
        testBox = findViewById(R.id.diary_testBox);
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        fAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

    }

    protected void updateCalories(int cals) {
        DocumentReference documentReference = fStore.collection("diaries").document(userID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                // This is how you access the users diary info
                HashMap<String, Object> book = (HashMap) value.get(date);
                book.replace("calories", Integer.toString(cals));
                documentReference.update(date, book);

            }
        });


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