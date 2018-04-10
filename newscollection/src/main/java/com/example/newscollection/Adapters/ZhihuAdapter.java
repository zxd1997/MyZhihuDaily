package com.example.newscollection.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newscollection.Activities.ContentActivity;
import com.example.newscollection.Beans.Daily;
import com.example.newscollection.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class ZhihuAdapter extends RecyclerView.Adapter<ZhihuAdapter.ViewHolder> {
    List<Daily.Story> stories;
    Context context;

    public ZhihuAdapter(Context context, List<Daily.Story> stories) {
        this.context = context;
        this.stories = stories;
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
                    Intent intent = new Intent(context, ContentActivity.class);
                    intent.putExtra("id", story.getId() + "");
                    intent.putExtra("title", story.getTitle());
                    intent.putExtra("type", "Zhihu");
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
    public int getItemViewType(int position) {
        if (position == stories.size())
            return 1;
        else return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < getItemCount() - 1) {
            Daily.Story story = stories.get(position);
            holder.textView.setText(story.getTitle());
            Uri uri = Uri.parse(story.getImages().get(0));
            holder.simpleDraweeView.setImageURI(uri);
        }
    }

    @Override
    public int getItemCount() {
        return stories.size() == 0 ? 0 : stories.size() + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView simpleDraweeView;
        TextView textView;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.newsTitle);
            view = itemView;
        }
    }

    class FootHolder extends RecyclerView.ViewHolder {
        TextView textView;

        private FootHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView4);
        }
    }
}
