package com.xiaoxun.xun.gallary.fragments;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.gallary.adapter.BaseGridAdapter;
import com.xiaoxun.xun.gallary.interfaces.TitleButtonChangeListener;
import com.xiaoxun.xun.gallary.swiplayout.SHSwipeRefreshLayout;

/**
 * Created by xilvkang on 2017/8/23.
 */

public abstract class BaseGalleryFragment extends Fragment {
    ImibabyApp mApp;
    BaseGridAdapter adapterGrid;
    SHSwipeRefreshLayout swipeRefreshLayout;
    TitleButtonChangeListener statuChange;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.local_gallery_fragment_ly, container, false);
        mApp = (ImibabyApp) getActivity().getApplication();
        initConfig();
        initdata();
        initviews(v);
        initSwiplayout(v);
        return v;
    }

    abstract void initdata();
    abstract void initviews(View v);
    abstract void initConfig();
    abstract void swipLayoutRefresh();
    abstract void swipLayoutLoading();
    public abstract void downloadImageList(final String time, final int type);

    public BaseGridAdapter getAdapterGrid(){
        return adapterGrid;
    }
    public void setTitleButtonChangeListener(TitleButtonChangeListener lis){
        statuChange = lis;
    }
    private void initSwiplayout(View v) {
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.refresh_view, null);
        final TextView textView = view.findViewById(R.id.title);
        swipeRefreshLayout.setFooterView(view);
        swipeRefreshLayout.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {
                swipLayoutRefresh();
            }

            @Override
            public void onLoading() {
                swipLayoutLoading();
            }

            /**
             * 监听下拉刷新过程中的状态改变
             * @param percent 当前下拉距离的百分比（0-1）
             * @param state 分三种状态{NOT_OVER_TRIGGER_POINT：还未到触发下拉刷新的距离；OVER_TRIGGER_POINT：已经到触发下拉刷新的距离；START：正在下拉刷新}
             */
            @Override
            public void onRefreshPulStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        swipeRefreshLayout.setRefreshViewText("下拉刷新");
                        break;
                    case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                        swipeRefreshLayout.setRefreshViewText("松开刷新");
                        break;
                    case SHSwipeRefreshLayout.START:
                        swipeRefreshLayout.setRefreshViewText("正在刷新");
                        break;
                }
            }

            @Override
            public void onLoadmorePullStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        textView.setText("上拉加载");
                        break;
                    case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                        textView.setText("松开加载");
                        break;
                    case SHSwipeRefreshLayout.START:
                        textView.setText("正在加载...");
                        break;
                }
            }
        });
    }
}
