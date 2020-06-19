package com.sw.HeyBuddy2.Main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class q_ViewPagerAdapter extends FragmentPagerAdapter {

    public q_ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return fragment_q_viewpage1.newinstance();

            case 1:
                return fragment_q_viewpage2.newinstance();
        }

        return null;
    }

    @Override
    public int getCount() { //스와이프 할 화면의 숫자. 현재 2개라서 2
        return 2;
    }
}
