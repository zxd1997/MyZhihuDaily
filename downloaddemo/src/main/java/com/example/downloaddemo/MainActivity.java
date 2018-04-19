package com.example.downloaddemo;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.lang.ref.Reference;

public class MainActivity extends AppCompatActivity {
    static final String uri = "http://xza.mqego.com/cn.share.app.apk";
    static long contentid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "download.apk");
                if (file != null && file.exists()) {
                    file.delete();
                }
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
                request.setTitle("download");
                request.setDescription("test");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "download.apk");
                request.setMimeType("application/vnd.android.package-archive");
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                contentid = downloadManager.enqueue(request);
            }
        });
    }
}
