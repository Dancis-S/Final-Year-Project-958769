package com.example.socialfitnessapp.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.socialfitnessapp.R;

public class RegInfoOneActivity extends AppCompatActivity {
    EditText name, surname, date;
    TextView heightTxt, weightTxt, heightInfo, weightInfo;
    SeekBar heightBar, weightBar;
    Button backBtn, signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_info_one);


        name = findViewById(R.id.regOne_name);
        surname = findViewById(R.id.regOne_surname);
        date = findViewById(R.id.regOne_DOB);
        heightTxt = findViewById(R.id.regOne_heightText);
        weightTxt = findViewById(R.id.regOne_weightText);
        heightInfo = findViewById(R.id.regOne_heightInfo);
        weightInfo = findViewById(R.id.regOne_weightInfo);
        heightBar = findViewById(R.id.regOne_heightBar);
        weightBar = findViewById(R.id.regOne_weightBar);
        backBtn = findViewById(R.id.regOne_backButton);
        signUpBtn = findViewById(R.id.regOne_signUpButton);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}