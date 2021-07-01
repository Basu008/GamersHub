package com.example.gamershub.userRecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamershub.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    //Declaring the UI variables
    private TextView usernameText;
    private Button followButton;
    private ImageView userListProfilePic;

    //ViewHolder class constructor
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        usernameText = itemView.findViewById(R.id.userPageUsername);
        followButton = itemView.findViewById(R.id.followButton);
        userListProfilePic = itemView.findViewById(R.id.userProfilePicture);
    }
    //Getters of the ViewHolder class data members
    public TextView getUsernameText() {
        return usernameText;
    }

    public Button getFollowButton() {
        return followButton;
    }

    public ImageView getUserListProfilePic() {
        return userListProfilePic;
    }

}
