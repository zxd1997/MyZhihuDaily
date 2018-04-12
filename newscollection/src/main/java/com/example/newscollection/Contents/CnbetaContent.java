package com.example.newscollection.Contents;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;

import com.example.newscollection.Adapters.CnbetaAdapter;
import com.example.newscollection.Beans.Cnbeta;
import com.example.newscollection.R;
import com.example.newscollection.SimpleDecoration;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CnbetaContent implements Content {
    static CnbetaContent cnbetaContent = null;
    List<Cnbeta> cnbetas = new ArrayList<Cnbeta>();
    CnbetaAdapter cnbetaAdapter;
    public Handler handler = new Handler() {
        public void handleMessage(Message message) {
            Log.d("obj", "HandleMessage: " + message.obj);
            String xml = (String) message.obj;
            Log.d("xml", "HandleMessage: " + xml);

            try {
                XmlPullParser parser = Xml.newPullParser();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.getBytes());
                parser.setInput(byteArrayInputStream, "utf-8");
                int eventtype = parser.getEventType();
                Cnbeta cnbeta = null;
                Log.d("event", "HandleMessage: " + eventtype + parser.END_DOCUMENT);
                while (eventtype != parser.END_DOCUMENT) {
                    Log.d("event", "HandleMessage: " + eventtype);
                    switch (eventtype) {
                        case XmlPullParser.START_TAG: {
                            String name = parser.getName();
                            Log.d("name", "HandleMessage: " + name);
                            switch (name) {
                                case "feed": {
                                    cnbetas.clear();
                                    break;
                                }
                                case "title": {
                                    if (cnbeta != null) {
                                        cnbeta.setTitle(parser.nextText());
                                        Log.d("title", "HandleMessage: " + cnbeta.getTitle());
                                    }
                                    break;
                                }
                                case "entry": {
                                    cnbeta = new Cnbeta();
                                    break;
                                }
                                case "link": {
                                    if (cnbeta != null) {
                                        cnbeta.setLink(parser.getAttributeValue(0));
                                        Log.d("link", "HandleMessage: " + cnbeta.getLink());
                                    }
                                    break;
                                }
                                case "summary": {
                                    cnbeta.setHtml(parser.nextText());
                                    Log.d("html", "HandleMessage: " + cnbeta.getHtml());
                                    break;
                                }
                                case "published": {
                                    String tmp = parser.nextText();
                                    tmp = tmp.replace("T", " ");
                                    tmp = tmp.replace("Z", "");
                                    cnbeta.setPublished(tmp);
                                    Log.d("publichsed", "HandleMessage: " + cnbeta.getPublished());
                                }
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG: {
                            if (parser.getName().equals("entry")) {
                                cnbetas.add(cnbeta);
                            }
                        }
                    }
                    eventtype = parser.next();
                }
            } catch (XmlPullParserException e) {

            } catch (IOException e) {

            }
            TextView textView = (TextView) view.findViewById(R.id.fragment_content);
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            cnbetaAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getFromService("http://rssdiy.com/u/2/cnbeta.xml");
            }
        });
        recyclerView.addItemDecoration(new SimpleDecoration(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        cnbetaAdapter = new CnbetaAdapter(context, cnbetas);
        recyclerView.setAdapter(cnbetaAdapter);
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
