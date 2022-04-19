package com.example.socialfitnessapp.Social;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialfitnessapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyFriendViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    TextView username;
    Button viewProfileBtn, messageBtn;

    public MyFriendViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.friend_usrPP);
        username = itemView.findViewById(R.id.friend_Username);
        viewProfileBtn = itemView.findViewById(R.id.friend_viewProfileButton);
        messageBtn = itemView.findViewById(R.id.friend_messageButton);
    }
}
