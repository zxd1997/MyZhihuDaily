package com.example.newscollection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZhihuContent implements Content {
    private static ZhihuContent zhihuContent = null;
    final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    List<Daily.Story> stories;
    Calendar calendar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    View view;
    ZhihuAdapter zhihuAdapter;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(Message message) {
            Daily daily = new Gson().fromJson((String) message.obj, Daily.class);
            stories.addAll(daily.getStories());
            if (stories.size() < 6) {
                calendar.add(Calendar.DATE, -1);
                getFromService("https://news-at.zhihu.com/api/4/news/before/" + format.format(calendar.getTime()));
            }
            TextView textView = (TextView) view.findViewById(R.id.fragment_content);
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            zhihuAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private ZhihuContent() {
        stories = new ArrayList<>();
    }

    public static ZhihuContent getInstance() {
        if (zhihuContent == null)
            return new ZhihuContent();
        else return zhihuContent;
    }

    @Override
    public void init(View view, Context context) {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        this.view = view;
        recyclerView = view.findViewById(R.id.recycleview);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                recyclerView.smoothScrollToPosition(0);
                stories.clear();
                zhihuAdapter.notifyDataSetChanged();
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                getFromService("https://news-at.zhihu.com/api/4/news/before/" + format.format(calendar.getTime()));
            }
        });
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        recyclerView.addItemDecoration(new SimpleDecoration(context));
        zhihuAdapter = new ZhihuAdapter(context, stories);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(zhihuAdapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == zhihuAdapter.getItemCount() - 1) {
                    swipeRefreshLayout.setRefreshing(true);
                    calendar.add(Calendar.DATE, -1);
                    getFromService("https://news-at.zhihu.com/api/4/news/before/" + format.format(calendar.getTime()));
                }
            }
        });
        getFromService("https://news-at.zhihu.com/api/4/news/before/" + format.format(calendar.getTime()));
    }

    @Override
    public synchronized void getFromService(String uri) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(uri)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.obj = response.body().string();
                handler.sendMessage(msg);
            }
        });
    }
}
