package com.example.socialfitnessapp.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialfitnessapp.Home.MainActivity;
import com.example.socialfitnessapp.MyDiary.MyDiaryActivity;
import com.example.socialfitnessapp.R;
import com.example.socialfitnessapp.Social.SocialActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ProfileActivity extends AppCompatActivity {

    ImageView  profilePic, homeBtn, socialBtn, myProfileBtn, diaryBtn;
    TextView name, surname, username, bio;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    StorageReference storageRef;
    String userID;
    Button editProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialise();
        downloadProfilePic();
        downloadInformation();
        buttons();
    }

    //Downloads the users profile picture and sets it
    protected void downloadProfilePic() {
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageRef.child("users/" + userID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                Picasso.get().load(uri).into(profilePic);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    // Fetches user info from db and then displays it onto their profile
    protected void downloadInformation() {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText(value.getString("username"));
                name.setText(value.getString("name"));
                surname.setText(value.getString("surname"));
                bio.setText(value.getString("bio"));
            }
        });
    }

    // Initialises all the views
    protected void initialise() {
        profilePic = findViewById(R.id.profile_profilePicture);
        homeBtn = findViewById(R.id.profile_homeButton);
        socialBtn = findViewById(R.id.profile_socialButton);
        myProfileBtn = findViewById(R.id.profile_myProfileButton);
        diaryBtn = findViewById(R.id.profile_myDiaryButton);
        name = findViewById(R.id.profile_nameTextfield);
        surname = findViewById(R.id.profile_surnameTextfield);
        username = findViewById(R.id.profile_usernameTextfield);
        bio = findViewById(R.id.profile_bioTextfield);
        editProfileBtn = findViewById(R.id.profile_editProfileButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
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

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
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
