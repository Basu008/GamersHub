package com.example.gamershub.ui.upload;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gamershub.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.io.ByteArrayOutputStream;


public class UploadFragment extends Fragment {

    private UploadModel uploadModel;

    //UI components declaration
    private EditText captionText;
    private ImageView imagePreview;
    private Button uploadImage, chooseImage;

    //Bitmap object to store users post
    private Bitmap selectedImage;

    //To get the result after calling other activity(To get the image after opening gallery)
    private ActivityResultLauncher<Intent> imageResultLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        uploadModel = new ViewModelProvider(this).get(UploadModel.class);
        View root = inflater.inflate(R.layout.fragment_upload, container, false);

        imagePreview = root.findViewById(R.id.imageUploadPreview);

        //Buttons
        uploadImage = root.findViewById(R.id.uploadButton);
        chooseImage = root.findViewById(R.id.chooseImageButton);

        //EditTexts
        captionText = root.findViewById(R.id.captionTextView);

        ///ActivityLauncher initialisation
        imageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            previewImage(data);
                        }
                    }
                });

        chooseImage.setOnClickListener(v -> {
            //Asking permission if it's not given, using this method we will also choose the image
            askForPermission();
        });

        uploadImage.setOnClickListener(v -> {
            //Checks if user has chosen an image or not. If they have, then upload it to the server.
            if(selectedImage == null)
                Toast.makeText(getContext(), "No image found! Choose a image first", Toast.LENGTH_SHORT).show();
            else{
                saveImageToServer();
            }
        });

        return root;
    }

    //Method to save the image to the server
    public void saveImageToServer(){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image.png", bytes);

        ParseObject object = new ParseObject("Posts");

        //This extra details are used for displaying the post and managing it's likes
        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.put("image", parseFile);
        object.put("likes", 0);
        object.put("caption", captionText.getText().toString() + "");
        object.put("liked_by", " ");

        ProgressDialog progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(getContext(), "Post uploaded !", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Something Went wrong, check you internet connection or try again", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    //This method will display the image so the user can see how it will look when it will be posted
    public void previewImage(Intent data) {

        try{
            Uri uri = data.getData();
            selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            imagePreview.setImageBitmap(selectedImage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    //This method will ask for permission from the user, and if denied,
    private void askForPermission(){

        //Dialog box to show the user that they have to give permission in order to upload image
        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getContext())
                        .withTitle("Permission Needed")
                        .withMessage("In order to proceed we need access to your external storage")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.drawable.warning)
                        .build();

        //This will ask permission from the user till the user accepts.
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

}