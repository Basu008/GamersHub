package com.example.gamershub.commentRecyclerView;

import com.parse.ParseFile;

//This class will help us to get the data of each comment of a post

public class Comment {

    //Declaring variables to hold the data of a comment
    private String usernameString, commentString, commentId;

    //We are taking the profile picture as ParseFile i.e the format it is stored in the database
    private ParseFile file;

    //Comment class constructor
    public Comment(String usernameString, String commentString, ParseFile file, String commentId) {
        this.usernameString = usernameString;
        this.commentString = commentString;
        this.file = file;
        this.commentId = commentId;
    }

    //Getters of the data members
    //As we are not using setter anywhere, there's no need to declare setters
    public String getUsernameString() {
        return usernameString;
    }

    public String getCommentString() {
        return commentString;
    }

    public ParseFile getFile() {
        return file;
    }

    public String getCommentId() {
        return commentId;
    }


}
