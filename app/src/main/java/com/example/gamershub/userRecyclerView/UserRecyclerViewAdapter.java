package com.example.gamershub.userRecyclerView;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.gamershub.OtherUserProfile;
import com.example.gamershub.OtherUserProfile;
import com.example.gamershub.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserViewHolder> {

    //An arrayList of User objects, each User object contains the data of one single post
    private ArrayList<User> users;

    //To get the context of the parent activity of the fragment
    private Context context;

    private UserManager userManager = new UserManager();

    //To get the current user
    private String currentUser = ParseUser.getCurrentUser().getUsername();

    public UserRecyclerViewAdapter(ArrayList<User> users){ this.users = users; }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        context = parent.getContext();
        View view = layoutInflater.inflate(R.layout.view_holder_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        //Get the username of the user
        holder.getUsernameText().setText(users.get(position).getUsername());
        //get the profile picture of the user
        setProfilePicture(holder, position);
        //if the current user follows this user, then sets the button text to unfollow, otherwise, follow
        setFollowingButton(holder, position);

        //To manage when current user click on the follow/unfollow button
        holder.getFollowButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.addFollowing(holder, users.get(position),context);
            }
        });

        //Opens user's profile when tapped on it
        holder.getUsernameText().setOnClickListener(v -> {
            Intent intent = new Intent(context, OtherUserProfile.class);
            intent.putExtra("USERNAME", users.get(position).getUsername());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() { return users.size(); }

    public void setFollowingButton(UserViewHolder holder, int position){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersInteraction");
        query.whereEqualTo("username", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e == null){
                    for(ParseObject object: objects){
                        if(object.getString("following_list").contains(users.get(position).getUsername()))
                            holder.getFollowButton().setText(R.string.unFollowBtn);
                        else
                            holder.getFollowButton().setText(R.string.followBtn);
                    }
                }
                else
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProfilePicture(UserViewHolder holder, int position) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", users.get(position).getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null && objects.size() > 0){
                    for(ParseUser user: objects) {
                        ParseFile file = user.getParseFile("profilePic");
                        if (file != null) {
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    holder.getUserListProfilePic().setImageBitmap(bitmap);
                                }
                            });
                        }
                    }
                }
            }
        });

    }
}
