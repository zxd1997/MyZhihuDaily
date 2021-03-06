package com.example.newscollection.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newscollection.Adapters.ZhihuCommentAdapter;
import com.example.newscollection.Beans.ZhihuComments;
import com.example.newscollection.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZhihuCommentFragment extends Fragment {
    private static final String ID = "id";
    List<ZhihuComments.Comment> allComments = new ArrayList<ZhihuComments.Comment>();
    ZhihuCommentAdapter zhihuCommentAdapter;
    RecyclerView recyclerView;
    View view;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("msg", "HandlerMessage: " + (String) msg.obj);
            ZhihuComments comments = new Gson().fromJson((String) msg.obj, ZhihuComments.class);
            String tmp = "";
            List<ZhihuComments.Comment> comment = comments.getComments();
            for (int i = comment.size() - 1; i >= 0; i--) {
                tmp += comment.get(i).getContent() + comment.get(i).isReply() + "\n";
                allComments.add(comment.get(i));
            }
            Log.d("comments", "HandlerMessage: " + tmp + allComments.size());
            if (allComments.size() == 0) return;
            TextView textView = view.findViewById(R.id.textcomment);
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            zhihuCommentAdapter.notifyDataSetChanged();
        }
    };
    private String mParam1;

    public ZhihuCommentFragment() {
        // Required empty public constructor
    }

    public static ZhihuCommentFragment newInstance(String param1) {
        ZhihuCommentFragment fragment = new ZhihuCommentFragment();
        Bundle args = new Bundle();
        args.putString(ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public synchronized void getFromService(String uri) {
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
                Log.d("content", "getFromService: " + msg.obj.toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_zhihu_comment, container, false);
        recyclerView = view.findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String id = getArguments().getString("id");
        Log.d("id", "onCreateView: " + id);
        getFromService("https://news-at.zhihu.com/api/4/story/" + id + "/short-comments");
        getFromService("https://news-at.zhihu.com/api/4/story/" + id + "/long-comments");
        zhihuCommentAdapter = new ZhihuCommentAdapter(allComments);
        recyclerView.setAdapter(zhihuCommentAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }


}
