package com.example.socialfitnessapp.Social;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialfitnessapp.R;
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

public class ViewProfileActivity extends AppCompatActivity {
    ImageView profilePicture, cancelBtn;
    Button addFriendBtn;
    TextView username, name, surname, bio;
    String userID;
    FirebaseStorage fStorage;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        userID = getIntent().getStringExtra("userKey"); // gets the users ID
        initialise();
        buttons();
        downloadProfilePic();
        fetchInformation();

    }

    //Downloads the users profile picture and sets it
    protected void downloadProfilePic() {
        StorageReference storageRef = fStorage.getReference();
        StorageReference profileRef = storageRef.child("users/" + userID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePicture);
            }
        });
    }

    // Method that fetches the users information and then displays it
    private void fetchInformation() {
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

    // Initialises all the views and objects in the activity
    private void buttons() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Initialises all the views and objects in the activity
    private void initialise() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        profilePicture = findViewById(R.id.viewProfile_profilePicture);
        cancelBtn = findViewById(R.id.viewProfile_backBtn);
        addFriendBtn = findViewById(R.id.viewProfile_addFriendBtn);
        username = findViewById(R.id.viewProfile_username);
        name = findViewById(R.id.viewProfile_name);
        surname = findViewById(R.id.viewProfile_surname);
        bio = findViewById(R.id.viewProfile_bio);

    }
}