package com.xiaoxun.xun.adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by zhangjun5 on 2019/6/20.
 */

public class PageAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> viewLists;

    public PageAdapter(Context context, FragmentManager fm, ArrayList<Fragment> arrayList){
        super(fm);
        this.viewLists = arrayList;
    }

    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public Fragment getItem(int position) {
        return viewLists.get(position);
    }

}
