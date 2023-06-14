package com.xiaoxun.xun.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

/**
 * @author cuiyufeng
 * @Description: JGridView
 * @date 2018/12/13 18:50
 */
public class JGridView extends LinearLayout {
    private Adapter adapter;
    private int columnNums = 4;
    private OnItemClickListener onItemClickListener;
    private int spac;

    public JGridView(Context context) {
        this(context, null);
    }

    public JGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    public void setAdapter(Adapter adapter) {
        this.adapter=adapter;
        refresh();
    }
    public void refresh(){
        removeAllViews();
        int line = adapter.getCount()  / columnNums;
        int count=adapter.getCount();
        if(line*columnNums<adapter.getCount())
            line++;

        for (int i = 0; true & i <line ; i++) {
            LinearLayout layout = new LinearLayout(getContext());
            for (int j = 0; j < columnNums; j++) {
                layout.setOrientation(HORIZONTAL);
                final int pos = i * columnNums + j;
                View v = null;
                if (pos < adapter.getCount()){
                    v = adapter.getView(pos, null, null);
                    v.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            if (onItemClickListener != null)
                                onItemClickListener.onItemClick(null, arg0, pos,pos);
                        }
                    });
                }else{
                    v = new View(getContext());
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
                /*if(adapter.getCount() == pos){
                }else{
                }*/
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                lp.weight=1.0f;
                if (j == 0)
                    lp.rightMargin = spac / 2;
                else
                    lp.leftMargin = spac / 2;
                v.setLayoutParams(lp);
                layout.addView(v);
            }
            addView(layout);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.LinearLayout#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * @param i
     */
    public void setNumColumns(int i) {

    }

    /**
     * @param i
     */
    public void setHorizontalSpacing(int i) {
        spac = i;
    }

    /**
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        // TODO Auto-generated method stub
        this.onItemClickListener = onItemClickListener;
    }
}

