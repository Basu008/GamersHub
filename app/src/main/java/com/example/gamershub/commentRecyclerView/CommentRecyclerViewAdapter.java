package com.example.gamershub.commentRecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamershub.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

//Recycler View Adapter for the list of comments.
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private Context context;

    //An arrayList of Comment type objects, each Comment object contains the data of one single comment
    private ArrayList<Comment> comments;

    //RecyclerView constructor
    public CommentRecyclerViewAdapter(ArrayList<Comment> comments){

        this.comments = comments;

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_comment, parent, false);
        context = parent.getContext();

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        //Sets the value of the comment and the user who wrote the comment
        holder.getCommenterUsername().setText(comments.get(position).getUsernameString());
        holder.getCommentText().setText(comments.get(position).getCommentString());

        //Sets the profile picture of the commenter
        ParseFile imageFile = comments.get(position).getFile();
        if(imageFile != null) {
            imageFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    //converts the parse file into bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    holder.getCommenterProfilePic().setImageBitmap(bitmap);
                }
            });
        }

        //For user to delete their comment
        holder.getCommentLayout().setOnClickListener(v -> {
            if(ParseUser.getCurrentUser().getUsername().equals(comments.get(position).getUsernameString()))
                alertDialogBox(comments.get(position).getCommentId(), position, holder);
        });

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    //Opens a dialog box when user wants to delete the comment
    public void alertDialogBox(String commentId, int position, CommentViewHolder holder){
        AlertDialog dialog =  new AlertDialog.Builder(context)
                .setTitle("Delete comment")
                .setMessage("Are you sure you want to delete the comment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteComment(commentId, position, holder);
                    }
                })
                .create();

        dialog.show();
    }

    //actual method for deleting the comment
    public void deleteComment(String commentId, int position, CommentViewHolder holder){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.getInBackground(commentId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null)
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Toast.makeText(context, "Comment Deleted", Toast.LENGTH_SHORT).show();
                                holder.getCommentLayout().setVisibility(View.GONE);
                            }
                        }
                    });
            }
        });

    }
}
