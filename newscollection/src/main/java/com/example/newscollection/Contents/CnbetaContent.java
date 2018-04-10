package com.example.newscollection.Contents;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import com.example.newscollection.R;
import com.example.newscollection.SimpleDecoration;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CnbetaContent implements Content {
    static CnbetaContent cnbetaContent = null;
    public Handler handler = new Handler() {
        public void handleMessage(Message message) {
            Log.d("obj", "HandleMessage: " + message.obj);
            String xml = (String) message.obj;
            Log.d("xml", "HandleMessage: " + xml);
            try {
                XmlPullParser parser = Xml.newPullParser();

                Log.d("parse", "HandleMessage: ");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.getBytes());
                parser.setInput(byteArrayInputStream, "utf-8");
                int eventtype = parser.getEventType();

                Log.d("event", "HandleMessage: " + eventtype);
                while (eventtype != parser.END_DOCUMENT) {
                    switch (eventtype) {
                        case XmlPullParser.START_TAG: {
                            Log.d("start_tag", "HandleMessage: " + parser.getName() + "\n" + parser.getAttributeValue(0));
                        }
                    }
                    parser.next();
                }
            } catch (XmlPullParserException e) {

            } catch (IOException e) {

            }
        }
    };
    View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    private CnbetaContent() {
    }

    public static CnbetaContent getInstance() {
        if (cnbetaContent == null)
            cnbetaContent = new CnbetaContent();
        return cnbetaContent;
    }

    @Override
    public void init(View view, Context context) {

        Log.d("init", "init: ");
        this.view = view;
        recyclerView = view.findViewById(R.id.recycleview);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        recyclerView.addItemDecoration(new SimpleDecoration(context));
        getFromService("http://rssdiy.com/u/2/cnbeta.xml");
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
                Log.d("msg", "onResponse: " + msg.obj);
                handler.sendMessage(msg);
            }
        });
    }
}
