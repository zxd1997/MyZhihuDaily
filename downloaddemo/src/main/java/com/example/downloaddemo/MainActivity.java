package com.example.downloaddemo;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.Reference;

public class MainActivity extends AppCompatActivity {
    static final String uri = "http://xza.mqego.com/cn.share.app.apk";
    static long contentid;
    DownloadManager downloadManager;
    TextView textView;
    ProgressBar progressBar;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
//            Log.d("handler", "handleMessage: "+(String)message.obj+" "+message.what);
            if (message.what == 1) {
                double tmp = (double) message.obj;
//                Log.d("handler", "handleMessage: "+tmp);
                textView.setText(tmp + "%" + "");
                progressBar.setProgress((int) tmp);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.text);
        progressBar = findViewById(R.id.progressBar);
        registerReceiver(new DownloadReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, new DownoadObserever(handler));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "download.apk");
                if (file != null && file.exists()) {
                    file.delete();
                }
                progressBar.setVisibility(View.VISIBLE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
                request.setTitle("download");
                request.setDescription("test");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "download.apk");
                request.setMimeType("application/vnd.android.package-archive");
                downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                contentid = downloadManager.enqueue(request);
            }
        });
    }

    class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownLoadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.d("id", "onReceive: " + completeDownLoadId);
            Intent intentinstall = new Intent();
            intentinstall.setAction(Intent.ACTION_VIEW);
            intentinstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri fileuri = null;
            if (completeDownLoadId == contentid) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fileuri = FileProvider.getUriForFile(context, "fileProvider", new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "download.apk"));
                    intentinstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
            intentinstall.setDataAndType(fileuri, "application/vnd.android.package-archive");
            context.startActivity(intentinstall);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, intentinstall, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentTitle("downloaded")
                    .setContentText("测试下载完成")
                    .setContentIntent(pendingIntent)
                    .setTicker("finished")
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_launcher_background);
            notificationManager.notify(1, builder.build());
            textView.setText("finished");
        }
    }

    class DownoadObserever extends ContentObserver {


        public DownoadObserever(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            int[] states = new int[]{0, 0, 0};
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(contentid);
            Cursor cursor = null;
            try {
                cursor = downloadManager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    states[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    states[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    states[2] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                }
            } catch (Exception e) {

            } finally {
                cursor.close();
            }
            Message message = new Message();
            message.what = 1;
            message.obj = ((double) states[0] / (double) states[1]) * 100;
            handler.sendMessage(message);
            Log.d("progress", "onChange: " + ((double) states[0] / (double) states[1]) * 100 + "%");
        }
    }
}
