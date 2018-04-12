package com.example.newscollection.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

import android.support.v7.widget.Toolbar;

import com.example.newscollection.Beans.ZhihuNewsContent;
import com.example.newscollection.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ZhihuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZhihuFragment extends Fragment {
    static final int JSON = 1;
    static final int IMAGE = 0;
    private static final String ID = "id";
    private static final String TITLE = "title";
    WebView webView;
    SimpleDraweeView simpleDraweeView;
    public Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == JSON) {
                ZhihuNewsContent zhihuNewsContent = new Gson().fromJson((String) message.obj, ZhihuNewsContent.class);
                String s = "";
                for (String i : zhihuNewsContent.getCss()) {
                    s += "<link rel=\"stylesheet\" href=\"" + i + "\" type=\"text/css\" />";
                }
                zhihuNewsContent.setBody(s + zhihuNewsContent.getBody());
                Uri uri = Uri.parse(zhihuNewsContent.getImage());
                Log.d("image", "handleMessage: " + zhihuNewsContent.getImage());
                simpleDraweeView.setImageURI(uri);
                webView.getSettings().setDefaultTextEncodingName("UTF-8");
                webView.loadData(zhihuNewsContent.getBody(), "text/html;charset=UTF-8", null);
            }
        }
    };
    private String mParam1;
    private String mParam2;

    public ZhihuFragment() {
        // Required empty public constructor
    }

    public static ZhihuFragment newInstance(String param1, String param2) {
        ZhihuFragment fragment = new ZhihuFragment();
        Bundle args = new Bundle();
        args.putString(ID, param1);
        args.putString(TITLE, param2);
        fragment.setArguments(args);
        Log.d("news", "newInstance: " + param1 + param2);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_content, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ID);
            mParam2 = getArguments().getString(TITLE);
        }
    }

    public synchronized void getFromService(String url) {
        Log.d("URL", "getFromService: " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String type = response.header("Content-Type");
                Log.d("type", "onResponse: " + type);
                Message message = new Message();
                if (type.contains("json")) {
                    message.what = JSON;
                    message.obj = response.body().string();
                }
                handler.sendMessage(message);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zhihu, container, false);
        webView = (WebView) view.findViewById(R.id.webview);
        simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.content_image);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Toolbar toolbar = view.findViewById(R.id.toolbar2);
        toolbar.setTitle(getArguments().getString(TITLE));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        setHasOptionsMenu(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_share) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, getArguments().getString(TITLE) + "\nhttp://daily.zhihu.com/story/" + getArguments().getString(ID) + "\nFrom Zhihu Daily");
                    startActivity(Intent.createChooser(intent, "Share"));
                }
                return true;
            }
        });
        Log.d("uri", "onCreateView: " + "https://news-at.zhihu.com/api/4/news/" + getArguments().getString(ID));
        getFromService("https://news-at.zhihu.com/api/4/news/" + getArguments().getString(ID));
        return view;
    }


}
