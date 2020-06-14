package com.example.lastplease.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lastplease.R;

public class fragment_q_viewpage1 extends Fragment {
    private View view;


    public static fragment_q_viewpage1 newinstance(){
        fragment_q_viewpage1 fragment_q_viewpage1 = new fragment_q_viewpage1();
        return fragment_q_viewpage1;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_q_viewpage1, container, false);
        return view;
    }
}
