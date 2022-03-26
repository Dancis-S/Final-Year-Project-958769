package com.example.socialfitnessapp.Home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.socialfitnessapp.Authentication.LoginActivity;
import com.example.socialfitnessapp.MyDiary.DiaryTracking;
import com.example.socialfitnessapp.MyDiary.MyDiaryActivity;
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

public class MainActivity extends AppCompatActivity {
    Button mainBtn, socialBtn, myProfileBtn, diaryBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise(); // Initialises all the views on the screen
        buttons(); //contain all buttons and their functionality
        checkDay(); // Checks that the documents for the day exists, if not creates new ones

    }

    // Initialises all the views and variables in the activity
    private void initialise() {
        mainBtn = findViewById(R.id.main_logoutButton);
        socialBtn = findViewById(R.id.main_socialButton);
        myProfileBtn = findViewById(R.id.main_profileButton);
        diaryBtn = findViewById(R.id.main_diaryButton);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid().toString();
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Checks if the document exists, if not creates new one then calls fetch data
    private void checkDay() {
        DocumentReference documentReference = fStore.collection("users/" + userID + "/myDiary").document(date);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.exists()) {
                    addNewDay();
                }
            }
        });

    }

    // Method that is called when there is a new day and creates a new day object
    protected void addNewDay() {
        DiaryTracking today = new DiaryTracking(date);
        DocumentReference documentReference = fStore.collection("users/" + userID + "/myDiary").document(date);
        HashMap<String, DiaryTracking> diary = new HashMap<>();
        diary.put(date, today);
        documentReference.set(diary);
    }

    // Method that is responsible for all the buttons on the activity
    protected void buttons() {

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

        // Button to logout
        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // logs out the user
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}