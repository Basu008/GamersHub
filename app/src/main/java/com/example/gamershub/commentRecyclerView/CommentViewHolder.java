package com.example.gamershub.commentRecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamershub.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {


    //Declaring the UI variables
    private ImageView commenterProfilePic;
    private TextView commenterUsername, comment;
    private LinearLayout commentLayout;

    //ViewHolder class constructor
    public CommentViewHolder(@NonNull View view) {
        super(view);
        commenterProfilePic = view.findViewById(R.id.commentProfilePicture);
        commenterUsername = view.findViewById(R.id.commentUsername);
        comment = view.findViewById(R.id.comment);
        commentLayout = view.findViewById(R.id.commentLayout);
    }

    //Getters
    public ImageView getCommenterProfilePic() {
        return commenterProfilePic;
    }

    public TextView getCommenterUsername() {
        return commenterUsername;
    }

    public TextView getCommentText() {
        return comment;
    }

    public LinearLayout getCommentLayout() {
        return commentLayout;
    }
}
