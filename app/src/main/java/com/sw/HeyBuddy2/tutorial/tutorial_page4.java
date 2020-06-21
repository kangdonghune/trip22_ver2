package com.sw.HeyBuddy2.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sw.HeyBuddy2.R;

public class tutorial_page4 extends Fragment {
    View view;

    public static tutorial_page4 newInstance(){
        tutorial_page4 tutorial_page4 = new tutorial_page4();
        return tutorial_page4;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutorial_page4, container, false);

        return view;
    }
}
