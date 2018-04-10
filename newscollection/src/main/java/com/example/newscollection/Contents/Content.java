package com.example.newscollection.Contents;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface Content {
    public void init(View view, Context context);

    public void getFromService(String uri);
}
