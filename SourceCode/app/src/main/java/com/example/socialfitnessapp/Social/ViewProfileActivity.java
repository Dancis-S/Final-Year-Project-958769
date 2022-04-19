package com.example.socialfitnessapp.Social;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialfitnessapp.R;
import com.google.android.gms.common.FirstPartyScopes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ViewProfileActivity extends AppCompatActivity {
    ImageView profilePicture, cancelBtn;
    Button addFriendBtn, declineBtn;
    TextView username, name, surname, bio;
    String friendID, currentState, PPUrl, userID, myPP, myUsername;
    FirebaseStorage fStorage;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseDatabase fBase;
    DatabaseReference requestRef, friendsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        friendID = getIntent().getStringExtra("userKey"); // gets the users ID for the page being visitied
        Initialise();
        Buttons();
        DownloadProfilePic();
        FetchInformation();
        CheckUserFriendship();

    }

    // Check whether the user is already friends with the user they are viewing
    private void CheckUserFriendship() {
        friendsRef.child(userID).child(friendID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    currentState = "friend";
                    addFriendBtn.setText("Message");
                    declineBtn.setText("Unfriend");
                    declineBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Checks that you already sent friend request to the other user
        requestRef.child(userID).child(friendID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.child("status").getValue().toString().equals("pending")) {
                        currentState = "Request_sent_Pending";
                        addFriendBtn.setText("Cancel Friend Request");
                    }
                    if(snapshot.child("status").getValue().toString().equals("declined")) {
                        currentState = "Request_sent_Declined";
                        addFriendBtn.setText("Cancel Friend Request");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //For when someone else sent YOU the a request
        requestRef.child(friendID).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.child("status").getValue().toString().equals("pending")) {
                        currentState="he_sent_pending";
                        addFriendBtn.setText("Accept Friend Request");
                        declineBtn.setText("Decline Friend Request");
                        declineBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Method that is called when you want to unfriend a user
    private void Unfriend(String userID) {
        if (currentState.equals("friend")) {
            friendsRef.child(userID).child(friendID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {// removing the data from the other user also
                    if(task.isSuccessful()) {
                        friendsRef.child(friendID).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(ViewProfileActivity.this, "You have successfully unfriended", Toast.LENGTH_SHORT).show();
                                    currentState = "nothing";
                                    addFriendBtn.setText("Send Friend Request");
                                    declineBtn.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }
        if(currentState.equals("he_sent_pending")) {
            HashMap hashMap = new HashMap();
            hashMap.put("status", "decline");
            requestRef.child(friendID).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ViewProfileActivity.this, "You have declined their request", Toast.LENGTH_SHORT).show();
                        currentState="he_sent_declined";
                        addFriendBtn.setVisibility(View.GONE);
                        declineBtn.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    //Downloads the users profile picture and sets it
    protected void DownloadProfilePic() {
        StorageReference storageRef = fStorage.getReference();
        StorageReference profileRef = storageRef.child("users/" + friendID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                PPUrl = uri.toString();
                Picasso.get().load(uri).into(profilePicture);
            }
        });
    }

    // Method that fetches the users information and then displays it
    private void FetchInformation() {
        DocumentReference documentReference = fStore.collection("users").document(friendID);
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
    private void Buttons() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unfriend(userID);
            }
        });

        //Deals with sending the friend request
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriend(friendID);
            }
        });

    }

    // Method that allows a user to add a friend, checks if request already sent
    private void AddFriend(String pageID) {
        System.out.println("Button clicked" + " and " + currentState);
        if(currentState.equals("nothing")) {
            HashMap hashMap = new HashMap();
            hashMap.put("status", "pending");
            requestRef.child(userID).child(friendID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ViewProfileActivity.this, "You have sent a Friend Request", Toast.LENGTH_SHORT).show();
                        currentState = "Request_sent_Pending";
                        addFriendBtn.setText("Cancel Request");

                    } else {
                        Toast.makeText(ViewProfileActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // Checks if request already sent or if other user declined it
        if(currentState.equals("Request_sent_Pending") || currentState.equals("User_declined")) {
            requestRef.child(userID).child(friendID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ViewProfileActivity.this, "You have cancelled Friend request", Toast.LENGTH_SHORT).show();
                        currentState = "nothing";
                        addFriendBtn.setText("Send Friend Request");


                    } else {
                        Toast.makeText(ViewProfileActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // If the user also sent you a friend request then accept it and add to friends list
        if(currentState.equals("he_sent_pending")) {
            //First fetch your info from database to add onto the friend database
            downloadProfilePic();
            getMyUsername();
            requestRef.child(friendID).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) { //their info to your friend list info
                        HashMap hashMapMe = new HashMap();
                        hashMapMe.put("status", "friend");
                        hashMapMe.put("username", username.getText().toString().trim());
                        hashMapMe.put("profileImageUrl", PPUrl);
                        friendsRef.child(userID).child(friendID).updateChildren(hashMapMe).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()) { // Add your info to their friend list info
                                    HashMap hashMapThem = new HashMap();
                                    hashMapThem.put("status", "friend");
                                    hashMapThem.put("username", myUsername);
                                    hashMapThem.put("profileImageUrl", myPP);
                                    friendsRef.child(friendID).child(userID).updateChildren(hashMapThem).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            Toast.makeText(ViewProfileActivity.this, "You added friend", Toast.LENGTH_SHORT).show();
                                            currentState = "friend";
                                            addFriendBtn.setText("Message");
                                            declineBtn.setText("Unfriend");
                                            declineBtn.setVisibility(View.VISIBLE);
                                            //Add unfriend button.
                                        }
                                    });
                                }
                            }
                        });
                    }

                }
            });
        }

        if(currentState.equals("friend")) {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("friendID",friendID);
            startActivity(intent);
        }
    }

    private void getMyUsername() {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                myUsername = value.getString("username");
            }
        });
    }

    //Gets the download URL for your profile picture incase you want to add friend
    protected void downloadProfilePic() {
        StorageReference profileRef = fStorage.getReference().child("users/" + userID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                myPP = url;

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    // Initialises all the views and objects in the activity
    private void Initialise() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        fBase = FirebaseDatabase.getInstance("https://socialfitnessapp-ab8c4-default-rtdb.europe-west1.firebasedatabase.app");
        requestRef = fBase.getReference().child("requests");
        friendsRef = fBase.getReference().child("friends");

        userID = fAuth.getCurrentUser().getUid();
        profilePicture = findViewById(R.id.viewProfile_profilePicture);
        cancelBtn = findViewById(R.id.viewProfile_backBtn);
        declineBtn = findViewById(R.id.viewProfile_declineButton);
        declineBtn.setVisibility(View.GONE);
        addFriendBtn = findViewById(R.id.viewProfile_addFriendBtn);
        username = findViewById(R.id.viewProfile_username);
        name = findViewById(R.id.viewProfile_name);
        surname = findViewById(R.id.viewProfile_surname);
        bio = findViewById(R.id.viewProfile_bio);
        currentState = "nothing";

    }
}