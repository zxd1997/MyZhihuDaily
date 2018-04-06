package com.example.newscollection;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SimpleDecoration extends RecyclerView.ItemDecoration {
    private int dividerHeight;

    public SimpleDecoration(Context context) {
        dividerHeight = 15;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }
}
