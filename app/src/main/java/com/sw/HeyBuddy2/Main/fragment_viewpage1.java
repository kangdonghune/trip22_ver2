package com.sw.HeyBuddy2.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sw.HeyBuddy2.FindBuddy.ListActivity;
import com.sw.HeyBuddy2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class fragment_viewpage1 extends Fragment {
    private View view;
    TextView checkLang;
    TextView checkLo;
    ImageView answer;
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
        answer=(ImageView)view.findViewById(R.id.answer);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), ListActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }
}
