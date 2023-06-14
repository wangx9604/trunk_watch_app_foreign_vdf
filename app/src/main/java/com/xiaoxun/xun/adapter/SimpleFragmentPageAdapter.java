package com.xiaoxun.xun.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import android.view.ViewGroup;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.activitys.NewMainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuiyufeng
 * @Description: SimpleFragmentPageAdapter
 * @date 2018/10/17 19:15
 */
public class SimpleFragmentPageAdapter extends FragmentPagerAdapter {

    private ArrayList<NewMainActivity.PageData> mFragments;
    private FragmentManager fragmentManager;
    private List<String> tags;
    private ImibabyApp mApp;

    public SimpleFragmentPageAdapter(FragmentManager fm, ArrayList<NewMainActivity.PageData> fragments,ImibabyApp mApp) {
        super(fm);
        this.tags = new ArrayList<>();
        this.fragmentManager = fm;
        this.mFragments = fragments;
        this.mApp=mApp;
    }

    public void setNewFragments(ArrayList<NewMainActivity.PageData> fragments) {
        if (this.tags != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            for (int i = 0; i < tags.size(); i++) {
                fragmentTransaction.remove(fragmentManager.findFragmentByTag(tags.get(i)));
            }
            fragmentTransaction.commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
            tags.clear();
        }
        int tagsSize=tags.size();
        this.mFragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position).mFrag;
    }

    @Override
    public int getCount() {
        int count = mFragments.size();
        return mFragments.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        tags.add(makeFragmentName(container.getId(), getItemId(position)));
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        this.fragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int size =mFragments.size();
        if(position < size){
            Fragment fragment = mFragments.get(position).mFrag;
            fragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}

