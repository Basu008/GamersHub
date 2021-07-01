package com.example.gamershub.postRecyclerView;

import com.parse.ParseFile;

//This class will help us to get the data of each post
public class Post {

    //Declaring variables to hold the data of a comment
    private String usernameText, likesText, caption, objectId;

    //We are taking the profile picture as ParseFile i.e the format it is stored in the database
    private ParseFile imageFile;

    //Post class constructor
    public Post(String username, String likesText, String caption, ParseFile imageFile, String objectId) {
        this.usernameText = username;
        this.likesText = likesText;
        this.caption = caption;
        this.imageFile = imageFile;
        this.objectId = objectId;
    }

    //Getters of the data members
    //As we are not using setter anywhere, there's no need to declare setters
    public String getUsernameText() {
        return usernameText;
    }

    public String getLikesText() {
        return likesText;
    }

    public void setLikesText(String likesText) {
        this.likesText = likesText;
    }

    public String getCaption() {
        return caption;
    }

    public ParseFile getImageFile() {
        return imageFile;
    }

    public String getPostId() {
        return objectId;
    }

}