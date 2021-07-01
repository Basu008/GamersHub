package com.example.gamershub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamershub.commentRecyclerView.Comment;
import com.example.gamershub.commentRecyclerView.CommentRecyclerViewAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Comments extends AppCompatActivity {

    //RecyclerView to show all the comments
    private RecyclerView commentsList;

    //String to hold post's Id
    private String postId;

    //UI component declaration
    private TextView placeHolder;

    //List of Comment objects to store the value of all the comments of a post
    private ArrayList<Comment> comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //RecyclerView initialisation
        commentsList = findViewById(R.id.commentsList);

        //TextView initialisation
        placeHolder = findViewById(R.id.placeHolderComment);

        //Gets postId from the Post fragment
        postId = getIntent().getStringExtra("POST_ID");

        commentsList.setLayoutManager(new LinearLayoutManager(this));

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereContains("postId", postId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() == 0){
                        //Shows placeholder text if there are no comments
                        commentsList.setVisibility(View.GONE);
                        placeHolder.setVisibility(View.VISIBLE);
                    }
                    else{
                        for(ParseObject object: objects){
                            String comment = object.getString("comment");
                            String comment_by = object.getString("comment_by");
                            String comment_id = object.getObjectId();

                            userQuery.whereContains("username", comment_by);
                            userQuery.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> objects, ParseException e) {
                                    if(e == null && objects.size() > 0){
                                        for(ParseUser user : objects){
                                            //give the details of the comment and also the profile picture of the commenter
                                            ParseFile file = user.getParseFile("profilePic");
                                            comments.add(new Comment(comment_by, comment, file, comment_id));
                                        }

                                        commentsList.setAdapter(new CommentRecyclerViewAdapter(comments));
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });
    }
}