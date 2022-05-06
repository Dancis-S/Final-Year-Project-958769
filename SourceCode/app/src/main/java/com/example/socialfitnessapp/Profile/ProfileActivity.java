package com.example.socialfitnessapp.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialfitnessapp.Authentication.LoginActivity;
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

public class  ProfileActivity extends AppCompatActivity {

    ImageView  profilePic, homeBtn, socialBtn, myProfileBtn, diaryBtn;
    TextView name, surname, username, bio;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    StorageReference storageRef;
    String userID;
    Button editProfileBtn, logoutBtn, testBtn;

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
        myProfileBtn = findViewById(R.id.profile_profileButton);
        diaryBtn = findViewById(R.id.profile_myDiaryButton);
        name = findViewById(R.id.profile_nameTextfield);
        surname = findViewById(R.id.profile_surnameTextfield);
        username = findViewById(R.id.profile_usernameTextfield);
        bio = findViewById(R.id.profile_bioTextfield);
        editProfileBtn = findViewById(R.id.profile_editProfileButton);
        logoutBtn = findViewById(R.id.profile_logout);
        testBtn = findViewById(R.id.profile_testButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

    }

    // Method that is responsible for all the buttons on the activity
    protected void buttons() {
        // Button to log user out
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               startActivity(intent);
               finish();
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

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
                finishAffinity();
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

        //Only for demo to show notification
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("Notification", "Notification", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Notification");
                builder.setContentTitle("Social Fitness App");
                builder.setContentText("Hi! Don't forget to login to keep your streak going!");
                builder.setSmallIcon(R.drawable.login_image);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
                managerCompat.notify(1,builder.build());

            }
        });

    }
    }
