package com.example.zxd1997.myzhihudaily;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by zxd1997 on 2018/03/10.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    int id;
    List<Daily.Story> stories;
    private Context context;
    @SuppressLint("HandlerLeak")

    public StoryAdapter(Context context, List<Daily.Story> stories) {
        this.stories = stories;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == stories.size())
            return 1;
        else return 0;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_content, parent, false);
            viewHolder = new ViewHolder(view);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    Daily.Story story = stories.get(position);
                    Intent intent = new Intent(context, contentActivity.class);
                    intent.putExtra("id", story.getId() + "");
                    intent.putExtra("title", story.getTitle());
                    context.startActivity(intent);
                }
            });
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
            viewHolder = new ViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < stories.size()) {
            Daily.Story story = stories.get(position);
            holder.textView.setText(story.getTitle());
            Uri uri = Uri.parse(story.getImages().get(0));
            holder.imageView.setImageURI(uri);
        }
    }

    @Override
    public int getItemCount() {
        return stories.size() == 0 ? 0 : stories.size() + 1;
    }

    class FootHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public FootHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textView4);
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imageView;
        TextView textView;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.imageView = (SimpleDraweeView) view.findViewById(R.id.imageView);
            this.textView = (TextView) view.findViewById(R.id.textView);
        }
    }
}
