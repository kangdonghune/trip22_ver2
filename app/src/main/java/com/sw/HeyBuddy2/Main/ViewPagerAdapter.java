package com.sw.HeyBuddy2.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return fragment_viewpage1.newinstance();

            case 1:
                return fragment_viewpage2.newinstance();
        }

        return null;
    }

    @Override
    public int getCount() { //스와이프 할 화면의 숫자. 현재 2개라서 2
        return 2;
    }


    //하단의 텝 인디케이터에 제목을 전달
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Home";

            case 1:
                return "Feed";
        }
        return null;
    }
}
