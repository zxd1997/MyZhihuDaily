package com.example.newscollection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;
    List<String> tags;

    public MyFragmentAdapter(FragmentManager fragmentManager, List<Fragment> fragments, List<String> tags) {
        super(fragmentManager);
        this.fragments = fragments;
        this.tags = tags;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tags.get(position);
    }
}
