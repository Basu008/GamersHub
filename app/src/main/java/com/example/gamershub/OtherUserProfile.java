package com.example.gamershub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class OtherUserProfile extends AppCompatActivity {

    //UI components declaration
    private TextView username, followers, following, name, bioText;
    private ImageView profilePic;

    //Bitmap to get user's profile pic
    private Bitmap serverPic;

    //String to get followers and following count
    private String followersCount, followingCount;

    private ProgressDialog progressDialog;

    //String to get which user's info is being asked to display
    private String userTappedOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        //UI components initialisation
        //TextView
        username = findViewById(R.id.otherUserUsername);
        followers = findViewById(R.id.otherUserFollowersText);
        following = findViewById(R.id.otherUserFollowingText);
        name = findViewById(R.id.otherUserNameText);
        bioText = findViewById(R.id.otherUserbioText);

        //ImageView
        profilePic = findViewById(R.id.otherUserProfilePicture);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading user details..");
        progressDialog.show();

        //Gets user from the UserList fragment
        userTappedOn = getIntent().getStringExtra("USERNAME");


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContains("username", userTappedOn);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null && objects.size() > 0){
                    for(ParseUser object: objects){
                        //Sets the information of the user
                        username.setText(object.getUsername());
                        name.setText(object.getString("Name"));
                        bioText.setText(object.getString("bio"));

                        //Gets the parse file of the profile picture from the server
                        ParseFile file = object.getParseFile("profilePic");
                        if(file != null) {
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        serverPic = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        //Sets the profile picture of the user
                                        profilePic.setImageBitmap(serverPic);
                                    }
                                }
                            });
                        }
                    }
                }
                else
                    Toast.makeText(OtherUserProfile.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        ParseQuery<ParseObject> newQuery = ParseQuery.getQuery("UsersInteraction");
        newQuery.whereEqualTo("username", userTappedOn);
        newQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        //Gets and sets the followers and following count
                        followersCount = object.getInt("followers") + " Followers";
                        followingCount = object.getInt("following") + " Following";
                        followers.setText(followersCount);
                        following.setText(followingCount);
                    }
                } else
                    Toast.makeText(OtherUserProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}