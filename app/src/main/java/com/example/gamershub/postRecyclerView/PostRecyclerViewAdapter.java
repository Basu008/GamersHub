package com.example.gamershub.postRecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamershub.Comments;
import com.example.gamershub.R;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostViewHolder> {

    //An arrayList of Post objects, each Post object contains the data of one single post
    private ArrayList<Post> posts;

    //To get the context of the parent activity of the fragment
    private Context context;
    private Bitmap postImage;

    private ProgressDialog progressDialog;

    //RecyclerView constructor
    public PostRecyclerViewAdapter(ArrayList<Post> posts){
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_post, parent, false);
        context = parent.getContext();
        progressDialog = new ProgressDialog(context);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        progressDialog.setMessage("Setting things up..");

        //Get's the post related info
        PostManager.setProfilePicture(holder, position, context, posts);
        holder.getPostUsername().setText(posts.get(position).getUsernameText());
        holder.getCaptionText().setText(posts.get(position).getCaption());
        holder.getLikesText().setText(posts.get(position).getLikesText());

        posts.get(position).getImageFile().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                //to get the image posted
                postImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                holder.getPostImage().setImageBitmap(postImage);
            }
        });

        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        progressDialog.show();
        query.getInBackground(posts.get(position).getPostId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    //To check if the post is already liked by the current user or not
                    if(object.getString("liked_by").contains(ParseUser.getCurrentUser().getUsername()))
                        holder.getLike().setImageResource(R.drawable.heart_liked);
                    else if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
                        holder.getLike().setImageResource(R.drawable.heart_night);
                    else
                        holder.getLike().setImageResource(R.drawable.heart_light);
                }
                else
                    holder.getLike().setImageResource(R.drawable.heart_night);
            }
        });
        progressDialog.dismiss();


        //To set like and comment icon according to the theme of the device
        switch (nightModeFlags) {
            //for night theme
            case Configuration.UI_MODE_NIGHT_YES:
                holder.getComment().setImageResource(R.drawable.comment_night);
                //to set the delete icon for only current user's post
                if(posts.get(position).getUsernameText().equals(ParseUser.getCurrentUser().getUsername())) {
                    holder.getDelete().setImageResource(R.drawable.delete_icon_night);
                    holder.getDelete().setVisibility(View.VISIBLE);
                }
                else
                    holder.getDelete().setVisibility(View.GONE);
                break;

            //for day theme
            case Configuration.UI_MODE_NIGHT_NO:
                holder.getComment().setImageResource(R.drawable.comment_light);
                if(posts.get(position).getUsernameText().equals(ParseUser.getCurrentUser().getUsername())) {
                    holder.getDelete().setImageResource(R.drawable.delete_icon_light);
                    holder.getDelete().setVisibility(View.VISIBLE);
                }
                else
                    holder.getDelete().setVisibility(View.GONE);
                break;
        }

        //Action to be done when user taps on like icon
        holder.getLike().setOnClickListener(v -> {
            query.getInBackground(posts.get(position).getPostId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null){
                        if(!object.getString("liked_by").contains(ParseUser.getCurrentUser().getUsername())) {
                            //to like the post
                            PostManager.addPostLike(holder, position, context, object.getObjectId(), posts);
                        }
                        else{
                            //to unlike the post
                            PostManager.removePostLike(holder, position, nightModeFlags, context, object.getObjectId(), posts);
                        }
                    }
                }
            });
        });


        //For user to delete their post
        holder.getDelete().setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Post")
                    .setMessage("Do you want to delete this post?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //Calls the delete method
                            PostManager.deletePost(posts.get(position).getPostId(), context);
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        //For user to add a comment
        holder.getComment().setOnClickListener(v -> {
            PostManager.addNewComment(ParseUser.getCurrentUser().getUsername(), context, posts.get(position).getPostId());
        });

        //For user to view all comments
        holder.getViewComments().setOnClickListener(v -> {
            Intent intent = new Intent(context, Comments.class);
            intent.putExtra("POST_ID", posts.get(position).getPostId());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}
