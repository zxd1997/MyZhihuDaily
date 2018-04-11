package com.example.newscollection.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_content, parent, false);
        viewHolder = new ViewHolder(view);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cnbeta cnbeta = cnbetas.get(position);
        holder.textView.setText(cnbeta.getTitle());
    }

    @Override
    public int getItemCount() {
        return cnbetas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        SimpleDraweeView simpleDraweeView;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView = itemView.findViewById(R.id.newsTitle);
            simpleDraweeView = itemView.findViewById(R.id.imageView);
            simpleDraweeView.setVisibility(View.GONE);
        }
    }
}
