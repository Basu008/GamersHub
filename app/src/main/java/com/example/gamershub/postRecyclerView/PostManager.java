package com.example.gamershub.postRecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamershub.R;
import com.example.gamershub.userRecyclerView.UserViewHolder;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


//This class contains all the methods related to post interactions
public class PostManager {

    public static void deletePost(String postId, Context context){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.getInBackground(postId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                Toast.makeText(context, "Post Deleted Successfully, Try changing the page to see the changes", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(context, "Post can't be deleted. Try again", Toast.LENGTH_SHORT).show();

                            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Comments");
                            query1.whereContains("postId", postId);
                            query1.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e == null && objects.size() > 0){
                                        for(ParseObject object : objects){
                                            object.deleteInBackground();
                                        }
                                    }
                                }
                            });

                        }
                    });
                }
                else
                    Toast.makeText(context, "Post not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void setProfilePicture(PostViewHolder holder, int position, Context context, @NotNull ArrayList<Post> posts) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", posts.get(position).getUsernameText());
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
                                    holder.getProfilePic().setImageBitmap(bitmap);
                                }
                            });
                        }
                    }
                }
            }
        });

    }

    public static void addPostLike(PostViewHolder holder, int position, Context context, String postID, @NotNull ArrayList<Post> posts){

        holder.getLike().setImageResource(R.drawable.heart_liked);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.getInBackground(postID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    String liked_by = object.getString("liked_by");
                    if(liked_by.isEmpty()){
                        object.put("liked_by", "," + ParseUser.getCurrentUser().getUsername());
                    }
                    else {
                        object.put("liked_by", object.getString("liked_by") + "," + ParseUser.getCurrentUser().getUsername());
                    }
                    object.put("likes", object.getInt("likes") + 1);
                    object.saveInBackground();
                }
                holder.getLikesText().setText(object.getInt("likes") + " likes");
                posts.get(position).setLikesText(object.getInt("likes") + " likes");
            }
        });

    }

    public static void addNewComment(String commentBy, Context context, String postId){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.popup_newcomment, null);

        TextView newComment = view.findViewById(R.id.newComment);
        ImageView saveComment = view.findViewById(R.id.saveComment);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        saveComment.setOnClickListener(v -> {
            if(!newComment.getText().toString().isEmpty() && !newComment.getText().toString().equals("")){
                ParseObject object = new ParseObject("Comments");
                object.put("postId", postId);
                object.put("comment_by", commentBy);
                object.put("comment", newComment.getText().toString());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(context, "Comment added, go to view all comments", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(context, "Comment can't be added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public static void removePostLike(PostViewHolder holder, int position, int nightMode, Context context, String postID, @NotNull ArrayList<Post> posts){

        if(nightMode == Configuration.UI_MODE_NIGHT_YES)
            holder.getLike().setImageResource(R.drawable.heart_night);
        else
            holder.getLike().setImageResource(R.drawable.heart_light);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.getInBackground(postID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    String liked_by = object.getString("liked_by");
                    if(!liked_by.isEmpty()) {
                        object.put("liked_by", liked_by.replace("," + ParseUser.getCurrentUser().getUsername(), ""));
                    }
                    else
                        object.put("liked_by", "");
                    object.put("likes", object.getInt("likes") - 1);
                    object.saveInBackground();
                }
                holder.getLikesText().setText(object.getInt("likes") + " likes");
                posts.get(position).setLikesText(object.getInt("likes") + " likes");
            }
        });

    }

}
