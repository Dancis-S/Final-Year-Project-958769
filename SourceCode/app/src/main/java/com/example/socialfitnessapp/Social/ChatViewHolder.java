package com.example.socialfitnessapp.Social;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialfitnessapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    CircleImageView firstPP, secondPP;
    TextView firstMsg, secondMsg;


    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);

        firstPP = itemView.findViewById(R.id.message_firstProfilePic);
        secondPP = itemView.findViewById(R.id.message_secondProfilePic);
        firstMsg = itemView.findViewById(R.id.message_firstText);
        secondMsg = itemView.findViewById(R.id.message_secondText);

    }
}
