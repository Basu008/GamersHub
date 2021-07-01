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
import com.example.gamershub.ui.upload.UploadModel;

public class UserProfileFragment extends Fragment {

    private UserProfileViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

}