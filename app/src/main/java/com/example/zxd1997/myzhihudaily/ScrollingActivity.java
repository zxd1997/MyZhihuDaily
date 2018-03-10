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
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ScrollingActivity extends AppCompatActivity {

    public void getFromService(){
        try {
            OkHttpClient client =new OkHttpClient();
            Request request=new Request.Builder()
                    .url("https://news-at.zhihu.com/api/4/news/latest")
                    .build();
            Response response=client.newCall(request).execute();
            Message msg=new Message();
            msg.obj=response.body().string();
            this.handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("HandlerLeak")
    public Handler handler=new Handler(){
        public void handleMessage(Message msg){
//            TextView textView=(TextView)findViewById(R.id.text);
//            textView.setText((String)msg.obj);
            Daily daily=new Gson().fromJson((String)msg.obj,Daily.class);
            final List<Daily.Story> stories=daily.getStories();
            List<Daily.Story> topStorie=daily.getTopStories();
            String tmp=daily.getDate();
            for (Daily.Story i :stories){
                tmp+=i.getId();
            }
            Log.d("ListView", "handleMessage: "+tmp);
//            textView.setText(tmp);
            StoryAdapter storyAdapter=new StoryAdapter(ScrollingActivity.this,R.layout.listview_content,stories);
            MyListView listView=findViewById(R.id.listView);
            listView.setAdapter(storyAdapter);
            listView.setOnClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                    Daily.Story story=stories.get(position);
                    Intent intent=new Intent(ScrollingActivity.this,contentActivity.class);
                    intent.putExtra("id",story.getId()+"");
                    intent.putExtra("title",story.getTitle());
                    startActivity(intent);
                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        getFromService();
                    }
                }
        ).start();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Snackbar snackbar=Snackbar.make(view, "Refreshed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                getFromService();
                                snackbar.show();
                            }
                        }
                ).start();
            }
        });
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
