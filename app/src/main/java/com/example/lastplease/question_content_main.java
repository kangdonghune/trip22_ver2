package com.example.lastplease;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lastplease.Main.ViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link question_content_main#newinstance} factory method to
 * create an instance of this fragment.
 */
public class question_content_main extends Fragment {
    int i=0;
    ViewPager viewPager;

    public question_content_main(){

    }

    public static question_content_main newinstance(){
        question_content_main question_content_main = new question_content_main();
        return question_content_main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_content_main, container, false);
        viewPager=(ViewPager)view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(0);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}