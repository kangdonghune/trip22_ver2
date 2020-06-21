package com.sw.HeyBuddy2.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sw.HeyBuddy2.R;

public class tutorial_page3 extends Fragment {
    View view;

    public static tutorial_page3 newInstance(){
        tutorial_page3 tutorial_page3 = new tutorial_page3();
        return tutorial_page3;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutorial_page3, container, false);

        return view;
    }
}
