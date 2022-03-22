package com.example.socialfitnessapp.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialfitnessapp.Home.MainActivity;
import com.example.socialfitnessapp.MyDiary.DiaryTracking;
import com.example.socialfitnessapp.Profile.ProfileActivity;
import com.example.socialfitnessapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegInfoOneActivity extends AppCompatActivity {

    EditText name, surname, date, weight, height;
    TextView heightTxt, weightTxt;
    Button backBtn, signUpBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_info_one);

        initialise();
        signUpUser();

    }


    protected void signUpUser() {

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uEmail = getIntent().getStringExtra("keyEmail");
                String uPassword = getIntent().getStringExtra("keyPassword");
                String uUsername = getIntent().getStringExtra("keyUsername");
                String uName = name.getText().toString().trim();
                String uSurname = surname.getText().toString().trim();
                String uWeight = weight.getText().toString().trim();
                String uHeight = height.getText().toString().trim();

                // Creates the account for the user
                fAuth.createUserWithEmailAndPassword(uEmail, uPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegInfoOneActivity.this, "User Crreated.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            // Creates document reference
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            //Stores information about the user in hashmap then uploads to db
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", uUsername);
                            user.put("name", uName);
                            user.put("surname", uSurname);
                            user.put("bio", "Hello! I am " + uUsername + " !");
                            user.put("weight", uWeight);
                            user.put("height", uHeight);
                            user.put("goalCalories", "2000" );
                            user.put("goalWater", "2500");
                            user.put("goalExercise", "30");

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "onSuccess: User profile is created for " + userID);
                                }
                            });

                            // Document reference that will store user diary is created with todays instance
                            DocumentReference myDiaryReference = fStore.collection("diaries").document(userID);
                            Map<String, DiaryTracking> diary = new HashMap<>();
                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            diary.put(date, new DiaryTracking(date));
                            myDiaryReference.set(diary);

                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intent);
                            finish();


                        }else {
                            // When user fails to sign up i.e. email already taken
                            Toast.makeText(RegInfoOneActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    protected void initialise() {
        name = findViewById(R.id.regOne_name);
        surname = findViewById(R.id.regOne_surname);
        date = findViewById(R.id.regOne_DOB);
        heightTxt = findViewById(R.id.regOne_heightText);
        weightTxt = findViewById(R.id.regOne_weightText);
        weight = findViewById(R.id.regOne_weightInput);
        height = findViewById(R.id.regOne_heightInput);
        backBtn = findViewById(R.id.regOne_backButton);
        signUpBtn = findViewById(R.id.regOne_signUpButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }
}