package com.example.gamershub.signinactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamershub.ui.HomePage;
import com.example.gamershub.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;


public class UserInfo extends AppCompatActivity {

    //UI components declaration
    private EditText nameText, phoneText, bioText;
    private TextView dobText;
    private Button save;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ParseUser currentUser;

    //Variables for supporting tasks
    private String username, name, dob, phoneNumber, bio;
    private int day;
    private int month;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        final int[] yearInput = new int[1];

        //UI components initialisation
        nameText = findViewById(R.id.userNameText);
        phoneText = findViewById(R.id.userPhoneText);
        dobText = findViewById(R.id.userDobText);
        bioText = findViewById(R.id.userBioText);
        save = findViewById(R.id.userInfoSaveButton);

        //Creating a date spinner
        dobText.setOnClickListener(view -> {
            //Getting the current date
            Calendar cal = Calendar.getInstance();
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH);
            year = cal.get(Calendar.YEAR);
            //Creating the spinner
            DatePickerDialog dialog = new DatePickerDialog(
                    UserInfo.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year,month,day
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });


        //Getting input from date spinner
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                yearInput[0] = year;
                month = month + 1;
                dob = day + "/" + month + "/" + year;
                dobText.setText(dob);
            }
        };

        //getting the current user
        username = getIntent().getStringExtra(SignIn.ACTIVE_USER);

        save.setOnClickListener(view -> {
            name = nameText.getText().toString();
            phoneNumber = phoneText.getText().toString();
            dob = dobText.getText().toString();
            bio = bioText.getText().toString();
            //Checking all the possible exception
            if(name.isEmpty() || phoneNumber.isEmpty() || dob.isEmpty())
                Toast.makeText(UserInfo.this, "Can't skip this buddy, gotta fill all the details", Toast.LENGTH_LONG).show();

            else if(!isStringOnlyAlphabet(name.replace(" ", "t")))
                Toast.makeText(UserInfo.this, "Son of Elon Musk? Maybe try a name with only alphabets", Toast.LENGTH_LONG).show();

            else if(yearInput[0] > 2006)
                Toast.makeText(UserInfo.this, "Might not be young to play games\nBut too young for this community, Sorry", Toast.LENGTH_SHORT).show();

            else if(phoneNumber.length() != 10)
                Toast.makeText(UserInfo.this, "We won't share you number, you can enter a real one ;)", Toast.LENGTH_LONG).show();

            else {
                dobText.setText(R.string.dobText);
                currentUser = ParseUser.getCurrentUser();
                if(currentUser != null){
                    //saves the data in Users table
                    currentUser.put("Name", name);
                    currentUser.put("DOB", dob);
                    currentUser.put("PhoneNumber", phoneNumber);
                    currentUser.put("InfoSaved", true);
                    currentUser.put("bio", bio);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Toast.makeText(UserInfo.this, "Information updated", Toast.LENGTH_SHORT).show();
                                switchActivity();
                            }
                            else
                                Toast.makeText(UserInfo.this, "There was some error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

        });


    }

    //Method to check if the name have anything other than alphabets
    private boolean isStringOnlyAlphabet(String str)
    {
        return ((str != null)
                && (!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }

    //Method to switch between activity
    private void switchActivity(){
        Intent intent = new Intent(UserInfo.this, HomePage.class);
        intent.putExtra(SignIn.ACTIVE_USER, username);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}