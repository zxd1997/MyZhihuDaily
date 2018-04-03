package com.example.zxd1997.myzhihudaily;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ScrollingActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    StoryAdapter storyAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Daily.Story> stories = new ArrayList<Daily.Story>();
    String today;
    Calendar calendar;
    final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public synchronized void handleMessage(Message msg) {
            synchronized (this) {
                Log.d("msg", "handleMessage: " + msg.obj);
                Daily daily = new Gson().fromJson((String) msg.obj, Daily.class);
                stories.addAll(daily.getStories());
                if (stories.size() == 0) return;
                if (stories.size() <= 6) {
                    calendar.add(Calendar.DATE, -1);
                    getFromService(format.format(calendar.getTime()));
                }
                TextView textView = findViewById(R.id.textView3);
                textView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                storyAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    public synchronized void getFromService(String date) {
        Log.d("date", "getFromService: " + date);
        synchronized (this) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://news-at.zhihu.com/api/4/news/before/" + date)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    Message msg = new Message();
                    msg.obj = response.body().string();
                    handler.sendMessage(msg);
                    Log.d("msg", "onResponse: " + (String) msg.obj);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        today = format.format(calendar.getTime());
        Log.d("date", "onCreate: " + today);
        setContentView(R.layout.activity_scrolling);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                stories.clear();
                recyclerView.scrollToPosition(0);
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                today = format.format(calendar.getTime());
                getFromService(today);
            }

        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(ScrollingActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(ScrollingActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(ScrollingActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(ScrollingActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(ScrollingActivity.this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ScrollingActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        storyAdapter = new StoryAdapter(ScrollingActivity.this, stories);
        recyclerView.setAdapter(storyAdapter);
//                    calendar.add(Calendar.DATE, -1);
//                    getFromService(format.format(calendar.getTime()));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getFromService(today);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
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
