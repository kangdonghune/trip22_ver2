package com.sw.HeyBuddy2.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.sw.HeyBuddy2.LoginRegiser.SelectActivity;
import com.sw.HeyBuddy2.Main.ViewPagerAdapter;
import com.sw.HeyBuddy2.R;

public class Tutorial extends AppCompatActivity {

    private ViewPager tutorial_viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    TabLayout tutorial_tabLayout;
    TabItem btnSkip, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        ViewPager viewPager = findViewById(R.id.tutorial_view_pager);
        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(fragmentPagerAdapter);
        TabLayout tabLayout =findViewById(R.id.tutorial_tabLayout);
        tabLayout.setupWithViewPager(viewPager);


    }
}