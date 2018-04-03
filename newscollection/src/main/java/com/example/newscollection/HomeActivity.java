package com.example.newscollection;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        List<String> tags = new ArrayList<String>();
        List<Fragment> fragments = new ArrayList<Fragment>();
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tags.add("ZhihuDaily");
        tags.add("Cnbeta");
        tags.add("Others");
        tabLayout.addTab(tabLayout.newTab().setText(tags.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tags.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tags.get(2)));
        fragments.add(MyFragment.newInstance("ZhihuDaily"));
        fragments.add(MyFragment.newInstance("Cnbeta"));
        fragments.add(MyFragment.newInstance("Others"));
        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), fragments, tags));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
