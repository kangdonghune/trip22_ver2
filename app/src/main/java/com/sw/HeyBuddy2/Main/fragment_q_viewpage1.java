package com.sw.HeyBuddy2.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sw.HeyBuddy2.FindBuddy.QListActivity;
import com.sw.HeyBuddy2.FindBuddy.QSecondActivity;
import com.sw.HeyBuddy2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class fragment_q_viewpage1 extends Fragment {
    private View view;
    ImageView ask;
    ImageView ask_arrow;
    ImageView findbuddy;
    private FirebaseFirestore db;
    private String currentUserID;
    private FirebaseAuth mAuth;


    public static fragment_q_viewpage1 newinstance(){
        fragment_q_viewpage1 fragment_q_viewpage1 = new fragment_q_viewpage1();
        return fragment_q_viewpage1;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_q_viewpage1, container, false);



        ask=(ImageView)view.findViewById(R.id.Ask);
        findbuddy=(ImageView)view.findViewById(R.id.Find_Buddy);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        findbuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), QSecondActivity.class);
                startActivity(intent);
            }
        });
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), QListActivity.class);
                startActivity(intent);
            }
        });
        ask_arrow = view.findViewById(R.id.ask_arrow);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.arrow_move);
        ask_arrow.startAnimation(anim);





        return view;
    }
}
