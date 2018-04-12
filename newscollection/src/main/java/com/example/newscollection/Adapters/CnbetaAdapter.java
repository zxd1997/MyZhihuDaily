package com.example.newscollection.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newscollection.Activities.ContentActivity;
import com.example.newscollection.Beans.Cnbeta;
import com.example.newscollection.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class CnbetaAdapter extends RecyclerView.Adapter<CnbetaAdapter.ViewHolder> {
    List<Cnbeta> cnbetas;
    Context context;

    public CnbetaAdapter(Context context, List<Cnbeta> cnbetas) {
        this.cnbetas = cnbetas;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_content, parent, false);
        viewHolder = new ViewHolder(view);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContentActivity.class);
                int position = viewHolder.getAdapterPosition();
                Cnbeta cnbeta = cnbetas.get(position);
                intent.putExtra("title", cnbeta.getTitle());
                intent.putExtra("html", cnbeta.getHtml());
                intent.putExtra("link", cnbeta.getLink());
                intent.putExtra("published", cnbeta.getPublished());
                intent.putExtra("type", "Cnbeta");
                context.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cnbeta cnbeta = cnbetas.get(position);
        holder.textView.setText(cnbeta.getTitle());
        holder.textView1.setText(cnbeta.getPublished());
    }

    @Override
    public int getItemCount() {
        return cnbetas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        SimpleDraweeView simpleDraweeView;
        View view;
        TextView textView1;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView = itemView.findViewById(R.id.newsTitle);
            simpleDraweeView = itemView.findViewById(R.id.imageView);
            textView1 = itemView.findViewById(R.id.time);
            simpleDraweeView.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);
        }
    }
}
