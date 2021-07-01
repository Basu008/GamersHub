package com.example.gamershub.ui.userProfile;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gamershub.R;
import com.example.gamershub.signinactivities.SignIn;
import com.example.gamershub.signinactivities.UserInfo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class UserProfileFragment extends Fragment {

    private UserProfileViewModel mViewModel;

    //UI components declaration
    private TextView username, followers, following, name, dob, editUserInfo, bioText;
    private Button signOut;
    private FloatingActionButton uploadProfilePic;
    private ImageView profilePic;

    //String to get the number of followers and followed user
    private String followersCount, followingCount;

    private ProgressDialog progressDialog;

    //Bitmap object to get bitmap image from server, and then set the profile picture
    private Bitmap selectedImage, profileImage;

    //Get the result on activity switch
    private ActivityResultLauncher<Intent> imageResultLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        //UI components initialisation
        //TextView
        username = root.findViewById(R.id.otherUserUsername);
        followers = root.findViewById(R.id.otherUserFollowersText);
        following = root.findViewById(R.id.otherUserFollowingText);
        name = root.findViewById(R.id.profilePageNameText);
        dob = root.findViewById(R.id.profilePageDOBText);
        editUserInfo = root.findViewById(R.id.profilePageEditUserInfo);
        bioText = root.findViewById(R.id.bioText);

        //Button
        signOut = root.findViewById(R.id.signOutButton);

        //Floating Action Button
        uploadProfilePic = root.findViewById(R.id.floatingActionButton);

        //ImageView
        profilePic = root.findViewById(R.id.profilePageProfilePicture);


        imageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            previewImage(data);
                        }
                    }
                });


        progressDialog = new ProgressDialog(container.getContext());
        progressDialog.setMessage("Loading user details..");
        progressDialog.show();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e == null){
                    //Sets the value of the textViews
                    username.setText(user.getUsername());
                    name.setText(user.getString("Name"));
                    dob.setText(user.getString("DOB"));
                    bioText.setText(user.getString("bio"));
                    ParseFile file = user.getParseFile("profilePic");
                    if(file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    profileImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    //set the profile picture
                                    profilePic.setImageBitmap(profileImage);
                                }
                            }
                        });
                    }
                }
                else
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        ParseQuery<ParseObject> newQuery = ParseQuery.getQuery("UsersInteraction");
        newQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        newQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject object: objects){
                        //Gets and sets the followers and following count
                        followersCount = object.getInt("followers") + " Followers";
                        followingCount = object.getInt("following") + " Following";
                        followers.setText(followersCount);
                        following.setText(followingCount);
                    }
                }
                else
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });



        //On clicking sign out button
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code to sign out the user
                showAlert("Signing out", "You really want to sign out?");
            }
        });

        //Opens the user info page to change the user's info
        editUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserInfo.class);
                getActivity().startActivity(intent);
            }
        });

        //Method to change/upload profile picture
        uploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForPermission();
            }
        });

        return root;
    }

    //Alert that ask the user whether to sign out or not, if accepted, signs out the user
    private void showAlert(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ParseUser.logOutInBackground(e -> {
                            progressDialog.dismiss();
                            if (e == null){
                                Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(getContext(), SignIn.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog signOut = builder.create();
        signOut.show();
    }

    private void askForPermission(){

        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getContext())
                        .withTitle("Permission Needed")
                        .withMessage("In order to proceed we need access to your external storage")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.drawable.warning)
                        .build();

        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        imageResultLauncher.launch(intent);
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        dialogPermissionListener.onPermissionDenied(response);
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    public void previewImage(Intent data) {

        try{
            Uri uri = data.getData();
            selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            saveImageToServer();
            profilePic.setImageBitmap(selectedImage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveImageToServer(){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("dp.png", bytes);

        ParseUser user = ParseUser.getCurrentUser();
        ProgressDialog progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Updating profile picture...");
        progressDialog.show();

        if(user != null) {
            user.put("profilePic", parseFile);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getContext(), "Profile picture can't be updated, Try again", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }

    }

}