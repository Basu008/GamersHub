package com.example.gamershub.userRecyclerView;

import android.content.Context;
import android.widget.Toast;

import com.example.gamershub.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

//This class contains all the methods related to post interactions
public class UserManager {

    private int followers, following;
    private String currentUser = ParseUser.getCurrentUser().getUsername();

    public void addFollowing(UserViewHolder userViewHolder, User user, Context context){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersInteraction");
        query.whereEqualTo("username", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject object: objects){
                        if(!object.getString("following_list").contains(user.getUsername())) {
                            userViewHolder.getFollowButton().setText(R.string.unFollowBtn);
                            following = object.getInt("following");
                            if(object.getString("following_list").isEmpty())
                                object.put("following_list", user.getUsername());
                            else
                                object.put("following_list", object.getString("following_list") + user.getUsername());
                            addFollower(user, context, true);
                            object.put("following", following + 1);

                        }
                        else {
                            userViewHolder.getFollowButton().setText(R.string.followBtn);
                            object.put("following_list", object.getString("following_list").replace(user.getUsername(), ""));
                            following = object.getInt("following");
                            addFollower(user, context, false);
                            object.put("following", following - 1);
                        }
                        object.saveInBackground();
                    }

                }
                else
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void addFollower(User user, Context context, boolean add){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersInteraction");
        query.whereEqualTo("username", user.getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject object: objects){
                        if(add){
                            if(object.getString("follower_list").isEmpty())
                                object.put("follower_list", currentUser);
                            else
                                object.put("follower_list", object.getString("follower_list") + currentUser);
                            object.put("followers", object.getInt("followers") + 1);
                        }
                        else {
                            object.put("follower_list", object.getString("follower_list").replace(currentUser, ""));
                            object.put("followers", object.getInt("followers") - 1);
                        }
                        object.saveInBackground();
                    }

                }
                else
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
