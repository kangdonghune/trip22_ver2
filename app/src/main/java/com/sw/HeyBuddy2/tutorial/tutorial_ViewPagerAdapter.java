package com.sw.HeyBuddy2.tutorial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sw.HeyBuddy2.Main.fragment_q_viewpage1;
import com.sw.HeyBuddy2.Main.fragment_q_viewpage2;

public class tutorial_ViewPagerAdapter extends FragmentPagerAdapter {

    public tutorial_ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return tutorial_page1.newInstance();

            case 1:
                return tutorial_page2.newInstance();


            case 2:
                return tutorial_page3.newInstance();


            case 3:
                return tutorial_page4.newInstance();


            case 4:
                return tutorial_page5.newInstance();


            case 5:
                return tutorial_page6.newInstance();

        }

        return null;
    }

    @Override
    public int getCount() { //스와이프 할 화면의 숫자. 현재 2개라서 2
        return 6;
    }


}
