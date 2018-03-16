package com.example.zxd1997.myzhihudaily;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {
    public String id;
    RecyclerView recyclerView;
    CommentAdapter commentAdapter;
    public List<Comments.Comment> allComments = new ArrayList<Comments.Comment>();

    public void getFromService(String uri) {
        Log.d("uri", "getFromService: " + uri);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
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
                //Log.d("content", "getFromService: " + msg.obj.toString());
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("msg", "HandlerMessage: " + (String) msg.obj);
            Comments comments = new Gson().fromJson((String) msg.obj, Comments.class);
            String tmp = "";
            List<Comments.Comment> comment = comments.getComments();
            for (int i = comment.size() - 1; i >= 0; i--) {
                tmp += comment.get(i).getContent() + comment.get(i).isReply() + "\n";
                allComments.add(comment.get(i));
            }
            Log.d("comments", "HandlerMessage: " + tmp + allComments.size());
            if (allComments.size() == 0) return;
            TextView textView = findViewById(R.id.textView2);
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            commentAdapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        getFromService("https://news-at.zhihu.com/api/4/story/" + id + "/short-comments");
        getFromService("https://news-at.zhihu.com/api/4/story/" + id + "/long-comments");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(CommentActivity.this, DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CommentActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(allComments);
        recyclerView.setAdapter(commentAdapter);
    }

}
