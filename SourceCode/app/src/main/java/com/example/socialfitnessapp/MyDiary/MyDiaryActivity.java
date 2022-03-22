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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyDiaryActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button addCaloriesBtn;
    String userID, date;
    TextView testBox, calorieGoal, currentCalories, caloriesLeft, waterGoal, currentWater, waterLeft
               , exerciseGoal, currentExercise, exerciseLeft, heightInfo, weightInfo, bmiInfo;
    ImageView homeBtn, socialBtn, myProfileBtn, diaryBtn;
    DocumentReference diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_diary);

        initialise(); // Initialises all the views on the screen
        buttons(); //contain all buttons and their functionality
        fetchData(); // fetches the data from the db


    }

    // Method to calculate the BMI of a user
    private double calculateBMI(double height, double weight) {
        DecimalFormat df = new DecimalFormat("0.0");
        double value = (weight / ((height /100) * (height/ 100)));
        return Double.parseDouble(df.format(value));
    }

    // Function to calculate how far the user is from the goal
    private int calculateChange(int goal, int current) {
        return goal - current;
    }

    // Initialises all the views and firebase components
    protected void initialise() {
        homeBtn = findViewById(R.id.diary_homeButton);
        socialBtn = findViewById(R.id.diary_socialButton);
        myProfileBtn = findViewById(R.id.diary_profileButton);
        diaryBtn = findViewById(R.id.diary_diaryButton);
        testBox = findViewById(R.id.diary_testBox);
        calorieGoal = findViewById(R.id.diary_goalCalories);
        currentCalories = findViewById(R.id.diary_consumedCalories);
        caloriesLeft = findViewById(R.id.diary_leftCalories);
        waterGoal = findViewById(R.id.diary_waterGoal);
        currentWater = findViewById(R.id.diary_waterConsumed);
        waterLeft = findViewById(R.id.diary_waterLeft);
        exerciseGoal = findViewById(R.id.diary_exerciseGoal);
        currentExercise = findViewById(R.id.diary_exerciseDone);
        exerciseLeft = findViewById(R.id.diary_exerciseLeft);
        heightInfo = findViewById(R.id.diary_heightInfo);
        weightInfo = findViewById(R.id.diary_weightInfo);
        bmiInfo = findViewById(R.id.diary_bmiInfo);
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        fAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

    }


    // Fetches the data from the data base and then displays it
    private void fetchData() {
        // Fetches the diary document reference
        final String[] caloriesConsumed = new String[1];
        final String[] waterConsumed = new String[1];
        final String[] exerciseDone = new String[1];
        DocumentReference documentReference = fStore.collection("diaries").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                // This is how you access the users diary info
                HashMap<String, DiaryTracking> book = (HashMap) value.get(date);
                testBox.setText((CharSequence) book.get("id"));
                currentCalories.setText((CharSequence) book.get("calories"));
                currentWater.setText((CharSequence) book.get("water"));
                currentExercise.setText((CharSequence) book.get("exercise"));

                caloriesConsumed[0] = currentCalories.getText().toString().trim();
                waterConsumed[0] =  currentWater.getText().toString().trim();
                exerciseDone[0] = currentExercise.getText().toString().trim();


            }
        });

        // Fetches the users information
        DocumentReference userReference = fStore.collection("users").document(userID);
        userReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String height = value.getString("height");
                String weight = value.getString("weight");
                String goalExercise = value.getString("goalExercise");
                String goalWater = value.getString("goalWater");
                String goalCalories = value.getString("goalCalories");
                double bmi = calculateBMI(Double.parseDouble(height), Double.parseDouble(weight));
                int caloriesToGo = calculateChange(Integer.parseInt(goalCalories), Integer.parseInt(caloriesConsumed[0]));
                int waterToGo = calculateChange(Integer.parseInt(goalWater), Integer.parseInt(waterConsumed[0]));
                int exerciseToGo = calculateChange(Integer.parseInt(goalExercise), Integer.parseInt(exerciseDone[0]));

                heightInfo.setText(height);
                weightInfo.setText(weight);
                bmiInfo.setText(Double.toString(bmi));
                calorieGoal.setText(goalCalories);
                waterGoal.setText(goalWater);
                exerciseGoal.setText(goalExercise);
                caloriesLeft.setText( Integer.toString(caloriesToGo));
                waterLeft.setText( Integer.toString(waterToGo));
                exerciseLeft.setText( Integer.toString(exerciseToGo));
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

    //Updates the information provided
    protected void updateIntake(int amount, int code) {
        DocumentReference documentReference = fStore.collection("diaries").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                // This is how you access the users diary info
                HashMap<String, Object> book = (HashMap) value.get(date);
                //book.replace("calories", Integer.toString(cals));
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