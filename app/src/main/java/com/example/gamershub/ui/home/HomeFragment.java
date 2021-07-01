package com.example.gamershub.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamershub.R;
import com.example.gamershub.postRecyclerView.Post;
import com.example.gamershub.postRecyclerView.PostRecyclerViewAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    //UI components declaration
    private RecyclerView usersFeed;
    private TextView placeHolderText;
    private ProgressDialog progressDialog;

    //String to get the current user
    private String currentUser;


    //List of Post objects
    private ArrayList<Post> posts = new ArrayList<>();

    //List of users that the current user follows
    private ArrayList<String> usersFollowed = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //RecyclerView initialisation
        usersFeed = root.findViewById(R.id.user_feed);

        //TextView initialisation
        placeHolderText = root.findViewById(R.id.homePlaceHolderText);

        //Setting a layout manager for the the RecyclerView
        usersFeed.setLayoutManager(new LinearLayoutManager(container.getContext()));

        //Getting the current user
        currentUser = ParseUser.getCurrentUser().getUsername();

        if(!currentUser.equals("")) {
            ParseQuery<ParseObject> getPostQuery = ParseQuery.getQuery("Posts");
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading Posts..");
            progressDialog.show();
            ParseQuery<ParseObject> getFollowedQuery = ParseQuery.getQuery("UsersInteraction");
            //Checks for users that are followed by the current user
            getFollowedQuery.whereContains("follower_list", currentUser);
            //The user should be able to see their post, so we add current user to the list
            usersFollowed.add(currentUser);
            getFollowedQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {
                        for (ParseObject object : objects) {
                            //Adds all the followed users to the list
                            usersFollowed.add(object.getString("username"));
                        }
                        usersFollowed.add(currentUser);
                    } else if (objects.size() == 0)
                        usersFollowed.add(currentUser);
                    else {
                        //if the user has no post and follows no one, then the user's feed will be empty
                        usersFeed.setVisibility(View.INVISIBLE);
                        placeHolderText.setVisibility(View.VISIBLE);
                    }

                    getPostQuery.whereContainedIn("username", usersFollowed);
                    getPostQuery.orderByDescending("createdAt");
                    getPostQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null && objects.size() > 0) {
                                for (ParseObject object : objects) {
                                    //adds individual post of the users followed by the current user
                                    posts.add(new Post(object.getString("username"), object.getInt("likes") + " likes",
                                            object.getString("caption"), object.getParseFile("image"), object.getObjectId()));
                                }
                            } else {
                                usersFeed.setVisibility(View.INVISIBLE);
                                placeHolderText.setVisibility(View.VISIBLE);
                            }
                            progressDialog.dismiss();
                            //sends the posts list to the recyclerView adapter
                            usersFeed.setAdapter(new PostRecyclerViewAdapter(posts));
                        }
                    });
                }
            });
        }

        return root;
    }
}