package com.example.gamershub.postRecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamershub.R;

import org.w3c.dom.Text;

public class PostViewHolder extends RecyclerView.ViewHolder {

    //Declaring the UI variables
    private TextView postUsername, captionText, likesText, viewComments;
    private ImageView like, comment, delete, postImage, profilePic;

    //ViewHolder class constructor
    public PostViewHolder(@NonNull View view) {
        super(view);
        postUsername = view.findViewById(R.id.postUsername);
        captionText = view.findViewById(R.id.captionText);
        likesText = view.findViewById(R.id.likesText);
        like = view.findViewById(R.id.likeIcon);
        comment = view.findViewById(R.id.commentIcon);
        delete = view.findViewById(R.id.deleteIcon);
        postImage = view.findViewById(R.id.postImage);
        profilePic = view.findViewById(R.id.profilePicture);
        viewComments = view.findViewById(R.id.viewCommentsText);
    }

    //Getters of the viewholder class data members
    public TextView getPostUsername() {
        return postUsername;
    }

    public TextView getCaptionText() {
        return captionText;
    }

    public TextView getLikesText() {
        return likesText;
    }

    public ImageView getLike() {
        return like;
    }

    public ImageView getComment() {
        return comment;
    }

    public ImageView getPostImage() {
        return postImage;
    }

    public ImageView getProfilePic() {
        return profilePic;
    }

    public ImageView getDelete() {
        return delete;
    }

    public TextView getViewComments() {
        return viewComments;
    }

}
