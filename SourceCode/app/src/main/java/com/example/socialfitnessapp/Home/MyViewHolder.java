package com.example.socialfitnessapp.Home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialfitnessapp.R;

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
}
