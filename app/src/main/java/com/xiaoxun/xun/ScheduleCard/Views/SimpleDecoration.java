package com.xiaoxun.xun.ScheduleCard.Views;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class SimpleDecoration extends RecyclerView.ItemDecoration{
    private int mDividerHeight;

    public SimpleDecoration(int mDividerHeight) {
        this.mDividerHeight = mDividerHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mDividerHeight;
    }

}