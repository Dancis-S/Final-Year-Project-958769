package com.example.socialfitnessapp.Social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.socialfitnessapp.Home.MainActivity;
import com.example.socialfitnessapp.MyDiary.MyDiaryActivity;
import com.example.socialfitnessapp.Profile.ProfileActivity;
import com.example.socialfitnessapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class SocialActivity extends AppCompatActivity {
    FirebaseRecyclerOptions<Users> options;
    FirebaseRecyclerAdapter<Users, MyFriendViewHolder> adapter;
    ImageView homeBtn, socialBtn, myProfileBtn, diaryBtn;
    Button findFriendsBtn;
    RecyclerView recyclerView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseDatabase fDatabase;
    FirebaseStorage fStorage;
    DatabaseReference friendsRef;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        Initialise();
        buttons();
        LoadFriends("");
    }

    //Initialises all the view and objects in the activity
    private void Initialise() {
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fDatabase = FirebaseDatabase.getInstance("https://socialfitnessapp-ab8c4-default-rtdb.europe-west1.firebasedatabase.app");
        userID = fAuth.getCurrentUser().getUid().toString();
        friendsRef = fDatabase.getReference().child("friends").child(userID);

        homeBtn = findViewById(R.id.social_homeButton);
        socialBtn = findViewById(R.id.social_socialButton);
        myProfileBtn = findViewById(R.id.social_profileButton);
        diaryBtn  = findViewById(R.id.social_myDiaryButton);
        findFriendsBtn = findViewById(R.id.social_findFriendsButton);
        recyclerView = findViewById(R.id.social_friendsList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
    }

    // Loads all the users friends from the database
    private void LoadFriends(String s) {
        Query query = friendsRef.orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();
        adapter = new FirebaseRecyclerAdapter<Users, MyFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyFriendViewHolder holder, int position, @NonNull Users model) {
                //Downloads the user profile picture
                StorageReference profileRef = fStorage.getReference().child("users/" + getRef(position).getKey().toString() + "/profile.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Picasso.get().load(uri).into(holder.profileImage);
                    }
                });
                holder.username.setText(model.getUsername());
                // Opens the users chat when they click the message button
                holder.messageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("friendID",getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });
                // Opens the users profile when they click the button
                holder.viewProfileBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
                        intent.putExtra("userKey", getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MyFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_friend_view, parent, false);

                return new MyFriendViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    // Method that is responsible for all the buttons on the activity
    protected void buttons() {
        findFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindFriendActivity.class);
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