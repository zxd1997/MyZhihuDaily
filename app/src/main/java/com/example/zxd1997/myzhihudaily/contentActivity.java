package com.example.zxd1997.myzhihudaily;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class contentActivity extends AppCompatActivity {
    String id, title;
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            final NewsContent newsContent = new Gson().fromJson((String) msg.obj, NewsContent.class);
            WebView webView = findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            String s = "";
            for (String i : newsContent.getCss()) {
                s += "<link rel=\"stylesheet\" href=\"" + i + "\" type=\"text/css\" />";
            }
            Log.d("css", "setCss: " + s);
            getImage(newsContent.getImage());
            newsContent.setBody(s + newsContent.getBody());
            webView.getSettings().setDefaultTextEncodingName("UTF-8");
            webView.loadData(newsContent.getBody(), "text/html;charset=UTF-8", null);
//            webView.setY(-500);
        }
    };
    Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            CollapsingToolbarLayout toolbar = findViewById(R.id.toolbar_layout);
            Bitmap bitmap = ((Bitmap) msg.obj);
            Matrix matrix = new Matrix();
            matrix.setScale(4.6f, 4.6f);
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            BitmapDrawable drawable = new BitmapDrawable(resizeBmp);
            drawable.setTileModeX(Shader.TileMode.CLAMP);
            toolbar.setBackground(drawable);
        }
    };

    private void getImage(String url) {
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
                byte[] bytes = response.body().bytes();
                Message msg = new Message();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                msg.obj = bitmap;
                handler1.sendMessage(msg);
            }
        });
    }

    public void getFromService() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://news-at.zhihu.com/api/4/news/" + id)
                .build();
        Log.d("uri", "getFromService: " + "https://news-at.zhihu.com/api/4/news/" + id);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.share) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, title + "\nhttp://daily.zhihu.com/story/" + id + "\nFrom MyZhihuDaily");
                    startActivity(Intent.createChooser(intent, "Share"));
                }
                return true;
            }
        });
        final Intent intent = getIntent();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        title = intent.getStringExtra("title");
        setTitle(title);
        id = intent.getStringExtra("id");
        getFromService();
        FloatingActionButton fab = findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(contentActivity.this, CommentActivity.class);
                intent1.putExtra("id", id);
                startActivity(intent1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }
}
