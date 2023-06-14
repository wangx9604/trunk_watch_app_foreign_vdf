package com.xiaoxun.xun.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.GridData;
import com.xiaoxun.xun.views.myHScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xilvkang on 2016/6/13.
 */
public class GridAdapter extends BaseAdapter {
    private Context context;
    private String[] title;
    private ArrayList<GridData> data;
    private int colnum;
    private LayoutInflater mInflater = null;
    private LinearLayout titlely;

    private int adaptertype = 1;
    public List<ViewHolder> mHolderList = new ArrayList<ViewHolder>();
    public GridAdapter(Context context, int colnum, String[] title, ArrayList<GridData> data, LayoutInflater mInflater,
                       LinearLayout titlely) {
        this.context = context;
        this.colnum = colnum;
        this.data = data;
        this.title = title;
        this.mInflater = mInflater;
        this.titlely = titlely;
        if (colnum == 3) {
            adaptertype = 0;
        } else if (colnum == 5) {
            adaptertype = 1;
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.detail_item, null);
            holder = new ViewHolder();
            myHScrollView scrollView1 = convertView.findViewById(R.id.horizontalScrollView1);

            holder.scrollView = scrollView1;
            holder.txt2 = convertView.findViewById(R.id.te03);
            holder.txt3 = convertView.findViewById(R.id.te04);
            holder.txt4 = convertView.findViewById(R.id.te05);
            holder.txt5 = convertView.findViewById(R.id.te06);
            holder.txt6 = convertView.findViewById(R.id.te07);

            holder.txt2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.txt3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.txt4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.txt5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.txt6.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            if (adaptertype == 0) {
                holder.txt5.setVisibility(View.GONE);
                holder.txt6.setVisibility(View.GONE);
            }
            myHScrollView headSrcrollView = titlely.findViewById(R.id.horizontalScrollView1);
            headSrcrollView.AddOnScrollChangedListener(new OnScrollChangedListenerImp(scrollView1));

            convertView.setTag(holder);
            mHolderList.add(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt2.setText(data.get(position).data0);
        holder.txt3.setText(data.get(position).data1);
        holder.txt4.setText(data.get(position).data2);
        holder.txt5.setText(data.get(position).data3);
        holder.txt6.setText(data.get(position).data4);
        return convertView;
    }

    class OnScrollChangedListenerImp implements com.xiaoxun.xun.views.myHScrollView.OnScrollChangedListener {
        myHScrollView mScrollViewArg;

        public OnScrollChangedListenerImp(myHScrollView scrollViewar) {
            mScrollViewArg = scrollViewar;
        }

        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            mScrollViewArg.smoothScrollTo(l, t);
        }
    }
    class ViewHolder {
        TextView txt2;
        TextView txt3;
        TextView txt4;
        TextView txt5;
        TextView txt6;

        HorizontalScrollView scrollView;
    }
}

