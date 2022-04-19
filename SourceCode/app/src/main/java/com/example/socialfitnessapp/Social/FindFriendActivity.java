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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toolbar;

import com.example.socialfitnessapp.R;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class FindFriendActivity extends AppCompatActivity {

    FirestoreRecyclerOptions<Users> options;
    FirestoreRecyclerAdapter<Users, FindFriendHolder> adapter;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseStorage fStorage;
    String userID;
    CollectionReference userReference;
    RecyclerView recyclerView;
    ImageView backBtn;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        initialise();
        buttons();
        LoadUsers("");
        searchUsers();
    }

    // Searches for specified user in list
    private void searchUsers() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            // When there is a change in text update the list of users
            @Override
            public boolean onQueryTextChange(String newText) {
                LoadUsers(newText);
                return false;
            }
        });
    }

    // Initialises all the buttons on the activity
    private void buttons() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SocialActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Initialises all the views and object in the activity
    private void initialise() {
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid().toString();
        userReference = fStore.collection("users");

        recyclerView = findViewById(R.id.FindFriends_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.findFriend_searchView);
        backBtn = findViewById(R.id.findFriend_backButton);
    }

    // Loads all the users that are in the database
    private void LoadUsers (String s) {
        Query query = userReference.orderBy("username").startAt(s).endAt(s+ "\uf8ff");
        options = new FirestoreRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();
        adapter = new FirestoreRecyclerAdapter<Users, FindFriendHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendHolder holder, int position, @NonNull Users model) {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                // Checks document snap shot and userID and removes itself from being displayed
                if(fAuth.getCurrentUser().getUid().equals(snapshot.getId().toString())) {

                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));

                } else {
                    //Downloads the user profile picture
                    StorageReference profileRef = fStorage.getReference().child("users/" + snapshot.getId().toString() + "/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Picasso.get().load(uri).into(holder.profilePicture);
                        }
                    });

                    holder.username.setText(model.getUsername());
                }
                // When a profile is clicked open it for viewing
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
                        intent.putExtra("userKey", snapshot.getId().toString());
                        startActivity(intent);
                        // We don't finish() so that the user just comes back where they left off
                    }
                });

            }

            @NonNull
            @Override
            public FindFriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_find_friend_view, parent, false);


                return new FindFriendHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}