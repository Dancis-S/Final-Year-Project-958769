package com.example.socialfitnessapp.MyDiary;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialfitnessapp.Home.MainActivity;
import com.example.socialfitnessapp.Profile.ProfileActivity;
import com.example.socialfitnessapp.R;
import com.example.socialfitnessapp.Social.SocialActivity;
import com.google.common.util.concurrent.ExecutionError;
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
    Button addCaloriesBtn, addWaterBtn, addExerciseBtn;
    String userID, date;
    TextView calorieGoal, currentCalories, caloriesLeft, waterGoal, currentWater, waterLeft
               , exerciseGoal, currentExercise, exerciseLeft, heightInfo, weightInfo, bmiInfo;
    ImageView homeBtn, socialBtn, myProfileBtn, diaryBtn;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_diary);

        initialise(); // Initialises all the views on the screen
        buttons(); //contain all buttons and their functionality
        fetchData(); // Fetches all the data and displays it


    }

    //Method that takes code given and then asks for the value to be updates (i.e. calories code=1)
    private void createNewDialog(int code) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.popup, null);

        EditText popupInput = (EditText) popupView.findViewById(R.id.popup_input);
        TextView popupHeading = (TextView) popupView.findViewById(R.id.popup_heading);
        Button cancelButton = (Button) popupView.findViewById(R.id.popup_cancelButton);
        Button saveButton = (Button) popupView.findViewById(R.id.popup_saveButton);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fetches the amount entered and the uploads it and closes
                int amount = Integer.parseInt(popupInput.getText().toString().trim());
                updateIntake(amount, code);
                dialog.dismiss();
                recreate();
                return;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // Method to calculate the BMI of a user
    public double calculateBMI(double height, double weight) {
        DecimalFormat df = new DecimalFormat("0.0");
        double value = (weight / ((height /100) * (height/ 100)));
        return Double.parseDouble(df.format(value));
    }

    // Function to calculate how far the user is from the goal
    public int calculateChange(int goal, int current) {
        return goal - current;
    }

    // Initialises all the views and firebase components
    protected void initialise() {
        homeBtn = findViewById(R.id.diary_homeButton);
        socialBtn = findViewById(R.id.diary_socialButton);
        myProfileBtn = findViewById(R.id.diary_profileButton);
        diaryBtn = findViewById(R.id.diary_diaryButton);
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

        addCaloriesBtn = findViewById(R.id.diary_addCalories);
        addWaterBtn = findViewById(R.id.diary_addWaterButton);
        addExerciseBtn = findViewById(R.id.diary_addExerciseButton);

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
        DocumentReference documentReference = fStore.collection("users/" + userID + "/myDiary").document(date);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                HashMap<String, DiaryTracking> book = (HashMap) value.get(date);
                currentCalories.setText(((CharSequence) book.get("calories")).toString());
                currentWater.setText(((CharSequence) book.get("water")).toString());
                currentExercise.setText(((CharSequence) book.get("exercise")).toString());

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

    //Updates the information provided depending on the code provided
    protected void updateIntake(int amount, int code) {
        DocumentReference documentReference = fStore.collection("users/" + userID + "/myDiary").document(date);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                HashMap<String, Object> book = (HashMap) value.get(date);
                if(code == 1) {
                    int cals = Integer.parseInt(currentCalories.getText().toString().trim()) + amount;
                    book.replace("calories", Integer.toString(cals));
                }
                documentReference.update(date, book);
                try {
                    Thread.sleep(2000);
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    // Method that is responsible for all the buttons on the activity
    protected void buttons() {

        addCaloriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewDialog(1);
            }
        });

        addWaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewDialog(2);
            }
        });

        addExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewDialog(3);
            }
        });


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