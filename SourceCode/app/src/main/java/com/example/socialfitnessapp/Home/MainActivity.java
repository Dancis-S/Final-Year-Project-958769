package com.example.socialfitnessapp.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.socialfitnessapp.Authentication.LoginActivity;
import com.example.socialfitnessapp.MyDiary.DiaryTracking;
import com.example.socialfitnessapp.MyDiary.MyDiaryActivity;
import com.example.socialfitnessapp.Profile.ProfileActivity;
import com.example.socialfitnessapp.R;
import com.example.socialfitnessapp.Social.SocialActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    FirebaseDatabase fDatabase;
    DatabaseReference postRef, likeRef, commentRef;
    StorageReference postImageRef;
    String userID, date, username, userProfilePicture;
    ImageView addImagePost, homeButton, socialBtn, myProfileBtn, diaryBtn;
    EditText inputPostDescription;
    Uri imageUri;
    ProgressDialog mLoadingBar;
    Button sendImagePost;
    FirebaseRecyclerAdapter<Posts, MyViewHolder> adapter;
    FirebaseRecyclerOptions<Posts> options;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise(); // Initialises all the views on the screen
        buttons(); //contain all buttons and their functionality
        checkDay(); // Checks that the documents for the day exists, if not creates new ones
        LoadPosts(); // Loads all the posts from the database and displays them

    }

    //Fetches the URL for the users profile picture for a given post
    protected void downloadProfilePic() {

        StorageReference profPicStorage = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = profPicStorage.child("users/" + userID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                userProfilePicture = url;
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    // Method that calculates the time since a post was created
    private String calculateTimeAgo(String datePost) {
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try{
            long time = format.parse(datePost).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    };

    // Method that will fetch all the posts and display them
    private void LoadPosts() {
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(postRef, Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Posts model) {
                String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
                String timeAgo = calculateTimeAgo(model.getDatePost());
                holder.timeAgo.setText(timeAgo);
                holder.username.setText(model.getUsername());
                Picasso.get().load(model.getPostImageUrl()).into(holder.postImage);
                if(model.getUserProfileImageUrl() == null) { // if no profile pic put the default
                    holder.profileImage.setImageResource(R.drawable.profile_icon);
                } else {
                    Picasso.get().load(model.getUserProfileImageUrl()).into(holder.profileImage);
                }
                holder.countLikes(postKey, userID, likeRef);
                // Implements like button
                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeRef.child(postKey).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Checks whether user has like the post or not and updates values accordingly
                                if(snapshot.exists()) {
                                    likeRef.child(postKey).child(userID).removeValue();
                                    holder.likeImage.setColorFilter(Color.GRAY);
                                } else {
                                    likeRef.child(postKey).child(userID).setValue("like");
                                    holder.likeImage.setColorFilter(Color.BLUE);
                                }
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_post_view, parent, false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    // Initialises all the views and variables in the activity
    private void initialise() {

        socialBtn = findViewById(R.id.home_socialButton);
        myProfileBtn = findViewById(R.id.home_myProfileButton);
        diaryBtn = findViewById(R.id.home_diaryButton);
        homeButton = findViewById(R.id.home_homeButton);
        mLoadingBar = new ProgressDialog(this);
        recyclerView = findViewById(R.id.main_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fDatabase = FirebaseDatabase.getInstance("https://socialfitnessapp-ab8c4-default-rtdb.europe-west1.firebasedatabase.app");
        fStorage = FirebaseStorage.getInstance();
        postRef = fDatabase.getReference().child("posts");
        likeRef = fDatabase.getReference().child("likes");
        postImageRef =  fStorage.getReference().child("postImages");
        userID = fAuth.getCurrentUser().getUid().toString();

        downloadProfilePic(); // Downloads the url of the users profile picture
        getUsername(); // fetches the username form the database
        sendImagePost = findViewById(R.id.home_postButton);
        addImagePost = findViewById(R.id.home_uploadImage);
        inputPostDescription = findViewById(R.id.home_inputText);


        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Fetches the username from the database
    private void getUsername() {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username = (value.getString("username"));

            }
        });
    }

    // Checks if the document exists, if not creates new one then calls fetch data
    private void checkDay() {
        DocumentReference documentReference = fStore.collection("users/" + userID + "/myDiary").document(date);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.exists()) {
                    addNewDay();
                }
            }
        });

    }

    // Method that is called when there is a new day and creates a new day object
    protected void addNewDay() {
        DiaryTracking today = new DiaryTracking(date);
        DocumentReference documentReference = fStore.collection("users/" + userID + "/myDiary").document(date);
        HashMap<String, DiaryTracking> diary = new HashMap<>();
        diary.put(date, today);
        documentReference.set(diary);
    }

    // Method that is responsible for all the buttons on the activity
    protected void buttons() {

        socialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SocialActivity.class);
                startActivity(intent);
            }
        });

        myProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        diaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyDiaryActivity.class);
                startActivity(intent);
            }
        });

        // Button to logout
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // logs out the user
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sendImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost();
            }
        });

        addImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int REQUEST_CODE = 101;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

    }

    // Method that is called when image needs to be selected from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK && data!= null) {
            imageUri=data.getData();
            addImagePost.setImageURI(imageUri);
        }
    }

    //Method that allows the user to add a post to the timeline
    private void addPost() {
        String postDescription = inputPostDescription.getText().toString().trim();
        if(postDescription.isEmpty()) {
            inputPostDescription.setError("Please Enter a Description");
        }
        else if(imageUri == null) {
            Toast.makeText(this, "Please select and image", Toast.LENGTH_SHORT).show();
        }
        else {
            mLoadingBar.setTitle("Adding Post");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm");
            String strDate = format.format(date);
            postImageRef.child(userID + strDate).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        postImageRef.child(userID + strDate).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                HashMap post = new HashMap();
                                post.put("datePost", strDate);
                                post.put("postImageUrl", uri.toString());
                                post.put("postDesc", postDescription);
                                post.put("username", username);
                                post.put("userProfileImageUrl", userProfilePicture);
                                postRef.child(userID + strDate).updateChildren(post).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()) {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(MainActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                                            addImagePost.setImageResource(R.drawable.image_icon);
                                            inputPostDescription.setText("Say something");
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                            mLoadingBar.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    else{
                        mLoadingBar.dismiss();
                        Toast.makeText(MainActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }


}