package com.example.newscollection.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

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
        Log.d("fragment", "getItem: " + fragments.get(position).getTag());
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
