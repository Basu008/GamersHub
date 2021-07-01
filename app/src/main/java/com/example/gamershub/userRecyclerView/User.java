package com.example.gamershub.userRecyclerView;
import com.parse.ParseFile;

import java.util.ArrayList;


//This class will help us to get name of each users in the list
public class User {

    //Declaring variables to hold the data of a comment
    private String username;
    private String following;

    //User class constructor
    public User(String username, String following){
        this.username = username;
        this.following = following;
    }

    //Getters of the data members
    //As we are not using setter anywhere, there's no need to declare setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}