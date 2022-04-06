package com.example.socialfitnessapp.Social;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialfitnessapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendHolder extends RecyclerView.ViewHolder {

    CircleImageView profilePicture;
    TextView username;

    public FindFriendHolder(@NonNull View itemView) {
        super(itemView);
        profilePicture = itemView.findViewById(R.id.findFriend_usrViewPP);
        username = itemView.findViewById(R.id.findFriend_usrViewUsername);
    }
}
