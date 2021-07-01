package com.example.gamershub.signinactivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//Back4app packages
import com.example.gamershub.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    //UI components declaration
    private EditText email,username, password, rePassword;
    private Button signUp, signIn;

    //ProgressDialog initialisation(to see whether user has verified the email or not)
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(this);

        //UI components initialisation
        //EditTexts
        email = findViewById(R.id.emailTextView);
        username = findViewById(R.id.usernameTextView);
        password = findViewById(R.id.passwordTextView);
        rePassword = findViewById(R.id.rePasswordTextView);

        //Buttons
        signUp = findViewById(R.id.singUpButton);
        signIn = findViewById(R.id.singInNavButton);



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checks if the user has entered same passwords
                if(!password.getText().toString().equals(rePassword.getText().toString())) {
                    showPasswordAlert("Passwords don't match");
                    password.setText("");
                    rePassword.setText("");
                }
                //checks if the user has enter a password following the given criteria
                else if(!isValidPassword(password.getText().toString())) {
                    showPasswordAlert("Password not strong enough");
                    password.setText("");
                    rePassword.setText("");
                }
                else{
                    signUpMethod(username.getText().toString(), password.getText().toString(), email.getText().toString());
                }

            }
        });


        //Navigate to sign in page
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

    }

    //Let's the user sign into the app
    private void signUpMethod(String username, String password, String email) {
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();
        ParseObject object = new ParseObject("UsersInteraction");
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.signUpInBackground(e -> {
            progressDialog.dismiss();
            if (e == null) {
                ParseUser.logOut();
                //this info is saved in UsersInteraction table, used to maintain following related activities
                object.put("username", username);
                object.put("following_list", "");
                object.put("follower_list", "");
                object.saveInBackground();
                showAlert("Account Created Successfully!", "Please verify your email before Login", false);
            } else {
                ParseUser.logOut();
                showAlert("Error Account Creation failed", "Account could not be created" + " :" + e.getMessage(), true);
            }
        });

    }

    //This shows an alert telling the user what's wrong with the password they have entered
    private void showPasswordAlert(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title);

        //Checks what is the cause of error and sets appropriate error message
        if(title.equals("Passwords don't match"))
            builder.setMessage("The password should match before you can sign in");
        else
            builder.setMessage(" **It should contain at least 8 characters and at most 20 characters.\n" +
                    "**It should contain at least one digit.\n" +
                    "**It should contain at least one upper case alphabet.\n" +
                    "**It should contain at least one lower case alphabet.\n" +
                    "**It should contain at least one special character which includes !@#$%&*()-+=^.\n" +
                    "**It should not contain any white space.");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Method to create an alert box
    private void showAlert(String title, String message, boolean error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    // don't forget to change the line below with the names of your Activities
                    if (!error) {
                        Intent intent = new Intent(SignUp.this, SignIn.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    //Method to check whether the password is valid under the given criteria (using regex)
    public static boolean isValidPassword(String password)
    {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";
        Pattern p = Pattern.compile(regex);
        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches();
    }
}