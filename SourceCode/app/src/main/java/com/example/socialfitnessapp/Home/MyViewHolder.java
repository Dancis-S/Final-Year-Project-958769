package com.example.socialfitnessapp.Home;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialfitnessapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    ImageView postImage, likeImage, commentImage;
    TextView username, timeAgo, postDesc, likeCounter, commentCounter;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        // Initialise all the member variables on the single_post_view
        profileImage = itemView.findViewById(R.id.main_profileImagePost);
        postImage = itemView.findViewById(R.id.main_postImage);
        likeImage = itemView.findViewById(R.id.main_likeImage);
        commentImage = itemView.findViewById(R.id.main_commentImage);
        username = itemView.findViewById(R.id.main_profileUsernamePost);
        timeAgo = itemView.findViewById(R.id.main_timeAgo);
        postDesc = itemView.findViewById(R.id.main_postDescription);
        likeCounter = itemView.findViewById(R.id.main_likeCounter);
        commentCounter = itemView.findViewById(R.id.main_commentCounter);

    }

    // Method that calculates total count and check if user has liked post alread or not
    public void countLikes(String postKey, String userID, DatabaseReference likeRef) {
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalLikes = (int) snapshot.getChildrenCount();
                    likeCounter.setText(totalLikes + "");
                } else {
                    likeCounter.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //check if the user already like the post upon initialising the view
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userID).exists()) {
                    likeImage.setColorFilter(Color.GREEN);
                } else {
                    likeImage.setColorFilter(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
