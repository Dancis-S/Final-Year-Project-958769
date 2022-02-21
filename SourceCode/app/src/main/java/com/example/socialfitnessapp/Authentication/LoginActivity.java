package com.example.socialfitnessapp.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.socialfitnessapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText uEmail, uPassword;
    TextView registerBtn;
    Button loginBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get firebase authentication instance
        fAuth = FirebaseAuth.getInstance();

        uEmail = findViewById(R.id.login_email);
        uPassword = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_loginButton);
        registerBtn = findViewById(R.id.login_forgotPasswordButton);

        // Takes user to register activity if they click the button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish(); // Kills current activity
            }
        });

    }
}