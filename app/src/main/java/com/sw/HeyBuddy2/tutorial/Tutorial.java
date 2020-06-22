package com.sw.HeyBuddy2.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.sw.HeyBuddy2.Main.QMainActivity;
import com.sw.HeyBuddy2.Main.ViewPagerAdapter;
import com.sw.HeyBuddy2.R;
import com.sw.HeyBuddy2.Setting.SettingQuestionActivity;

public class Tutorial extends AppCompatActivity {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        //상태바를 없애기 위함
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_tutorial);

        viewPager = findViewById(R.id.tutorial_view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.skip);
        btnNext = findViewById(R.id.next);

        layouts = new int[]{
                R.layout.fragment_tutorial_page1,
                R.layout.fragment_tutorial_page2,
                R.layout.fragment_tutorial_page3,
                R.layout.fragment_tutorial_page4,
                R.layout.fragment_tutorial_page5,
                R.layout.fragment_tutorial_page6,

        };

        addBottomDots(0);


        pagerAdapter = new PagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveMainPage();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if(current < layouts.length){
                    viewPager.setCurrentItem(current);
                }else{
                    moveMainPage();
                }
            }
        });

    }

    private void addBottomDots(int currentPage){
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);


        dotsLayout.removeAllViews();
        for(int i = 0; i< dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0){
            dots[currentPage].setTextColor(colorsActive[currentPage]);
        };
    }

    private int getItem(int i){
        return viewPager.getCurrentItem() + i;
    }

    private void moveMainPage(){
        SharedPreferences pref = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("QcheckFirst", true);
        editor.commit();

        Intent settingsIntent = new Intent(Tutorial.this, SettingQuestionActivity.class);
        startActivity(settingsIntent);
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if(position == layouts.length - 1){
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            }else{
                btnNext.setText(getString(R.string.NEXT));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };






    public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {
        private LayoutInflater layoutInflater;

        public PagerAdapter(){

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position){
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount(){
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view,Object obj){
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            View view = (View) object;
            container.removeView(view);
        }
    }
}