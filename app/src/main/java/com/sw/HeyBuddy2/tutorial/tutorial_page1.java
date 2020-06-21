package com.sw.HeyBuddy2.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sw.HeyBuddy2.R;

public class tutorial_page1 extends Fragment {
    View view;

    public static tutorial_page1 newInstance(){
        tutorial_page1 tutorial_page1 = new tutorial_page1();
        return tutorial_page1;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutorial_page1, container, false);

        return view;
    }
}
