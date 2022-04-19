package com.example.socialfitnessapp.Social;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialfitnessapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    FirebaseStorage fStorage;
    FirebaseDatabase fDatabase;
    DatabaseReference messageRef;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    TextView username, onlineStatus;
    EditText inputText;
    CircleImageView profilePicture;
    ImageView uploadPicture, backBtn, sendBtn;
    String userID, friendID;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Chat> option;
    FirebaseRecyclerAdapter<Chat, ChatViewHolder> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        friendID = getIntent().getStringExtra("friendID");

        Initialise();
        Buttons();
        LoadUserData();
        LoadMessages();

    }


    // Initialises all the views and object on the activity
    private void Initialise() {
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fDatabase = FirebaseDatabase.getInstance("https://socialfitnessapp-ab8c4-default-rtdb.europe-west1.firebasedatabase.app");
        userID = fAuth.getCurrentUser().getUid().toString();
        messageRef = fDatabase.getReference().child("messages");

        username = findViewById(R.id.chat_username);
        onlineStatus = findViewById(R.id.chat_onlineStatus);
        inputText = findViewById(R.id.chat_chatBox);
        profilePicture = findViewById(R.id.chat_ProfilePicture);
        uploadPicture = findViewById(R.id.chat_iamgeIcon);
        backBtn = findViewById(R.id.chat_backButton);
        sendBtn = findViewById(R.id.chat_sendButton);
        recyclerView = findViewById(R.id.chat_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

    }

    // Initialises all the buttons and their functions on the activity
    private void Buttons() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Messaging();
            }
        });

    }

    //Loads the users information into the chat
    private void LoadUserData() {
        DownloadProfilePics();//Loads the users profile picture

        DocumentReference documentReference = fStore.collection("users").document(friendID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText(value.getString("username"));
            }
        });

    }

    //Downloads the users profile picture and your profile picture and sets them
    protected void DownloadProfilePics() {
        // Get the friends profile picture for use
        StorageReference profileRef = fStorage.getReference().child("users/" + friendID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePicture);//sets the other users PP
            }
        });

    }

    // Method that allows a user to send a message
    private void Messaging() {
        String message = inputText.getText().toString().trim();
        if(message.isEmpty()) {
            ;
        } else {
            HashMap hashMap = new HashMap();
            hashMap.put("message", message);
            hashMap.put("status", "unseen");
            hashMap.put("userID", userID);
            messageRef.child(friendID).child(userID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        messageRef.child(userID).child(friendID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()) {
                                    inputText.setText("Enter Text...");
                                    Toast.makeText(ChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    // Method to load all the messages between the users
    private void LoadMessages() {
        option = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(messageRef.child(userID).child(friendID), Chat.class).build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Chat model) {
                if(model.getUserID().equals(userID)) {
                    holder.firstMsg.setVisibility(View.GONE);
                    holder.firstPP.setVisibility(View.GONE);
                    holder.secondMsg.setVisibility(View.VISIBLE);
                    holder.secondPP.setVisibility(View.VISIBLE);

                    StorageReference profileRef = fStorage.getReference().child("users/" + userID + "/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri).into(holder.secondPP);
                        }
                    });

                    holder.secondMsg.setText(model.getMessage());


                } else {
                    holder.firstMsg.setVisibility(View.VISIBLE);
                    holder.firstPP.setVisibility(View.VISIBLE);
                    holder.secondMsg.setVisibility(View.GONE);
                    holder.secondPP.setVisibility(View.GONE);

                    holder.firstMsg.setText(model.getMessage());

                    StorageReference profileRef = fStorage.getReference().child("users/" + friendID + "/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri).into(holder.firstPP);
                        }
                    });

                }
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message_view, parent,false);
                return new ChatViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}