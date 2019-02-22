package com.example.suraj.philomath;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyFragmentAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> al1=new ArrayList<Fragment>();
    ArrayList<String> al2=new ArrayList<String>();

    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        return al1.get(i);
    }

    @Override
    public int getCount() {
        return al1.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return al2.get(position);
    }
}
