package com.example.gamershub.ui.upload;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.gamershub.R;

public class UploadFragment extends Fragment {

    private UploadModel uploadModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        uploadModel = new ViewModelProvider(this).get(UploadModel.class);
        View root = inflater.inflate(R.layout.fragment_upload, container, false);
        return root;
    }
}