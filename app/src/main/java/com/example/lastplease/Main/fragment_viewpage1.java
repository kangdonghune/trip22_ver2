package com.example.lastplease.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lastplease.R;
import com.example.lastplease.Setting.SettingQuestionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_viewpage1 extends Fragment {
    private View view;
    TextView checkLang;
    TextView checkLo;
    ImageView ask;
    ImageView findbuddy;
    private FirebaseFirestore db;
    private String currentUserID;
    String nlang="";
    String nl="";
    private FirebaseAuth mAuth;



    public static fragment_viewpage1 newinstance(){
        fragment_viewpage1 fragment_viewpage1 = new fragment_viewpage1();
        return fragment_viewpage1;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_viewpage1, container, false);

        return view;
    }
}
