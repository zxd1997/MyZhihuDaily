package com.example.zxd1997.myzhihudaily;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zxd1997 on 2018/03/10.
 */

public class StoryAdapter extends ArrayAdapter<Daily.Story> {
    int id;
    @SuppressLint("HandlerLeak")

    public StoryAdapter(Context context, int textViewResourceId, List<Daily.Story> stories){
        super(context,textViewResourceId,stories);
        id=textViewResourceId;
    }
    class ViewHolder{
        ImageView imageView;
        TextView textView;
        public ViewHolder(ImageView imageView,TextView textView){
            this.imageView=imageView;
            this.textView=textView;
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Daily.Story story=getItem(position);
        final View view;
        final ViewHolder viewHolder;
        if (convertView==null) {
            view = LayoutInflater.from(getContext()).inflate(id, parent, false);
            viewHolder=new ViewHolder((ImageView)view.findViewById(R.id.imageView),(TextView)view.findViewById(R.id.textView));
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
//        TextView title=(TextView)view.findViewById(R.id.textView);
//        title.setText(story.getTitle());
        viewHolder.textView.setText(story.getTitle());
        @SuppressLint("HandlerLeak") final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                viewHolder.imageView.setImageBitmap((Bitmap) msg.obj);
            }
        };
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client =new OkHttpClient();
                            Request request=new Request.Builder()
                                    .url(story.getImages().get(0))
                                    .build();
                            Response response=client.newCall(request).execute();
                            byte[] bytes=response.body().bytes();
                            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            Message msg=new Message();
                            msg.obj=bitmap;
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
        return view;
    }
}
