package com.example.zxd1997.myzhihudaily;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class contentActivity extends AppCompatActivity {
    String id;
    public Handler handler=new Handler(){
        public void handleMessage(Message msg){
            final NewsContent newsContent=new Gson().fromJson((String)msg.obj,NewsContent.class);
            WebView webView=findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            String s="";
            for (String i:newsContent.getCss()){
                s+="<link rel=\"stylesheet\" href=\""+i+"\" type=\"text/css\" />";
            }
            Log.d("css", "setCss: "+s);
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            getImage(newsContent.getImage());
                        }
                    }
            ).start();
            newsContent.setBody(s+ newsContent.getBody());
            webView.getSettings().setDefaultTextEncodingName("UTF-8");
            webView.loadData(newsContent.getBody(),"text/html;charset=UTF-8",null);
//            webView.setY(-500);
        }
    };
    Handler handler1=new Handler(){
        public void handleMessage(Message msg){
            CollapsingToolbarLayout toolbar=findViewById(R.id.toolbar_layout);
            toolbar.setBackground(new BitmapDrawable((Bitmap)msg.obj));
        }
    };
    private void getImage(String url) {
        try {
            OkHttpClient client =new OkHttpClient();
            Request request=new Request.Builder()
                    .url(url)
                    .build();
            Response response=client.newCall(request).execute();
            byte[] bytes=response.body().bytes();
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            Message msg=new Message();
            msg.obj=bitmap;
            handler1.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getFromService(){
        try {
            OkHttpClient client =new OkHttpClient();
            Request request=new Request.Builder()
                    .url("https://news-at.zhihu.com/api/4/news/"+id)
                    .build();
            Log.d("uri", "getFromService: "+"https://news-at.zhihu.com/api/4/news/"+id);
            Response response=client.newCall(request).execute();
            Message msg=new Message();
            msg.obj=response.body().string();
            Log.d("content", "getFromService: "+msg.obj.toString());
            this.handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setTitle(intent.getStringExtra("title"));
        id=intent.getStringExtra("id");
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        getFromService();
                    }
                }
        ).start();
    }
}
