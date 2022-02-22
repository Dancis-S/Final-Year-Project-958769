package com.example.socialfitnessapp.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialfitnessapp.Home.MainActivity;
import com.example.socialfitnessapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText username, email, password, confirmPassword;
    Button continueBtn, backBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.register_username);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confirmPassword = findViewById(R.id.register_confirmPassword);
        continueBtn = findViewById(R.id.register_continueButton);
        backBtn = findViewById(R.id.register_backButton);

        fAuth = FirebaseAuth.getInstance();

        // If the user is already logged in then just take them into the app
        if(fAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Button to take user back to login screen
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish(); // Kills current activity
            }
        });

        // Button to take the user to the next registry screen to provide more information
        continueBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String uEmail = email.getText().toString().trim();
               String uPassword = password.getText().toString().trim();
               String uUsername = username.getText().toString().trim();
               String conPass = confirmPassword.getText().toString().trim();

               boolean issues = false;

               //Checks that there are no errors or empty inputs
               if(TextUtils.isEmpty(uEmail)) {
                   email.setError("Email is required");
                   issues = true;
               }
               if(uPassword.length() < 7) {
                   password.setError("Password length should be at least 7");
                   issues = true;
               }
               /*
               if(uPassword != conPass) {
                   confirmPassword.setError("Passwords do not match");
                   issues = true;
               }
               */

               if(TextUtils.isEmpty(uUsername)) {
                   username.setError("Username is required");
                   issues = true;
               }
               if(issues) {
                   return;
               }

               Intent intent = new Intent(getApplicationContext(), RegInfoOneActivity.class);
               intent.putExtra("keyUsername", uUsername);
               intent.putExtra("keyEmail", uEmail);
               intent.putExtra("keyPassword", uPassword);

               startActivity(intent);


           }
       }
        );

    }
}