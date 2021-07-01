package com.example.gamershub.ui.usersList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamershub.R;
import com.example.gamershub.userRecyclerView.User;
import com.example.gamershub.userRecyclerView.UserRecyclerViewAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserListFragment extends Fragment {

    private UsersListViewModel usersListViewModel;

    //UI components declaration
    private RecyclerView usersList;
    private TextView loadingScreen;

    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        usersListViewModel = new ViewModelProvider(this).get(UsersListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_users, container, false);

        //List of users in the app
        ArrayList<User> users = new ArrayList<>();

        progressDialog = new ProgressDialog(container.getContext());

        //Recycle View initialisation
        usersList = root.findViewById(R.id.usersList);

        //TextView initialisation
        loadingScreen = root.findViewById(R.id.loadingUser);

        usersList.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersInteraction");
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject object: objects){
                            //Adds the users except the current user to the list
                            users.add(new User(object.getString("username"), object.getString("following_list")));
                        }
                        //switches between the loading screen and users list
                        loadingScreen.animate().alpha(0);
                        usersList.setVisibility(View.VISIBLE);
                        usersList.setAdapter(new UserRecyclerViewAdapter(users));
                    }
                    else
                        Log.e("Error : ", "Error");
                }
                else
                    Log.e("Error : ", e.getMessage());
            }
        });


        progressDialog.dismiss();

        return root;
    }
}