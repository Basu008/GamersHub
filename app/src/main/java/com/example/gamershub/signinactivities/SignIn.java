package com.example.gamershub.signinactivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//Back4app packages
import com.example.gamershub.ui.HomePage;
import com.example.gamershub.R;
import com.parse.ParseException;
import com.parse.ParseUser;


public class SignIn extends AppCompatActivity {

    //UI components declaration
    private EditText username, password;
    private Button signIn, signUp;

    //ProgressDialog initialisation(to see whether user has verified the email or not)
    ProgressDialog progressDialog;

    //Send the Active User to the Homepage Activity
    public static final String ACTIVE_USER = "ACTIVE_USER";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        progressDialog = new ProgressDialog(this);

        //UI components initialisation
        //EditTexts
        username = findViewById(R.id.usernameSingInTextView);
        password = findViewById(R.id.passwordSingInTextView);

        //Buttons
        signIn = findViewById(R.id.signInButton);
        signUp = findViewById(R.id.signUpNavButton);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            switchActivity(SignIn.this, HomePage.class);
        }

        //Let the user sign in
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignIn(username.getText().toString(), password.getText().toString());
            }
        });

        //Navigate to sign up page
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    //method to switch between activities
    private void switchActivity(Context context, Class<?> cls){
        Intent intent = new Intent(context, cls);
        intent.putExtra(ACTIVE_USER, username.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    //Show if the user has logged in or not as an alert
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    //Method to perform sign in activity
    private void userSignIn(String username, String password) {
        progressDialog.setMessage("Signing in...");
        progressDialog.show();
        ParseUser.logInInBackground(username, password, (ParseUser parseUser, ParseException e) -> {
            progressDialog.dismiss();
            if (parseUser != null) {
                //Checks if the user has filled up the starting user information page
                if(ParseUser.getCurrentUser().getBoolean("InfoSaved")) {
                    switchActivity(SignIn.this, HomePage.class);
                }
                else
                    switchActivity(SignIn.this, UserInfo.class);
            } else {
                ParseUser.logOut();
                showAlert("Login Fail", e.getMessage() + " Please try again");
            }
        });
    }

}