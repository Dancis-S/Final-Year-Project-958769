package com.example.socialfitnessapp.Social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.socialfitnessapp.R;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        initialise();
        LoadUsers("");
    }

    private void initialise() {
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid().toString();
        userReference = fStore.collection("users");

        recyclerView = findViewById(R.id.FindFriends_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void LoadUsers (String s) {
        Query query = userReference.orderBy("username").startAt(s).endAt(s+ "\uf8ff");
        options = new FirestoreRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();
        adapter = new FirestoreRecyclerAdapter<Users, FindFriendHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendHolder holder, int position, @NonNull Users model) {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                // Checks document snap shot and userID and removes itself from being displayed
                if(fAuth.getCurrentUser().getUid().equals(snapshot.getId().toString())) {   //getKey().toString()))

                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));

                } else {
                    if(model.getProfilePicture() == null) {// check that there is a PP otherwise nothing
                        holder.profilePicture.setImageResource(R.drawable.profile_icon);
                    } else {
                        Picasso.get().load(model.getProfilePicture()).into(holder.profilePicture);
                    }

                    holder.username.setText(model.getUsername());
                }

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