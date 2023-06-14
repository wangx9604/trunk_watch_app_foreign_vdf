package com.xiaoxun.xun.gallary.control;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.xiaoxun.xun.gallary.fragments.BaseGalleryFragment;
import com.xiaoxun.xun.gallary.fragments.CloudGalleryFragment;
import com.xiaoxun.xun.gallary.interfaces.TitleButtonChangeListener;

import java.util.ArrayList;

/**
 * Created by xilvkang on 2017/8/18.
 */

public class FragmentController {

    private int containerId;
    private TitleButtonChangeListener listener;
    private FragmentManager fm;
    private ArrayList<BaseGalleryFragment> fragments;

    private static FragmentController controller;

    public static FragmentController getInstance(FragmentManager parentFragment, int containerId,
                                                 TitleButtonChangeListener lis) {
        if (controller == null) {
            controller = new FragmentController(parentFragment, containerId,lis);
        }
        return controller;
    }

    private FragmentController(FragmentManager fragment, int containerId,TitleButtonChangeListener lis) {
        this.containerId = containerId;
        listener = lis;
        //fragment嵌套fragment，调用getChildFragmentManager
        fm = fragment;

        initFragment();
    }

    private void initFragment() {
        fragments = new ArrayList<BaseGalleryFragment>();
        //fragments.add(new LocalGalleryFragment());
        fragments.add(new CloudGalleryFragment());

        FragmentTransaction ft = fm.beginTransaction();
        for(BaseGalleryFragment fragment : fragments) {
            fragment.setTitleButtonChangeListener(listener);
            ft.add(containerId, fragment);
        }
        ft.commit();
    }

    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = fragments.get(position);
        FragmentTransaction ft = fm.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    public void hideFragments() {
        FragmentTransaction ft = fm.beginTransaction();
        for(Fragment fragment : fragments) {
            if(fragment != null) {
                ft.hide(fragment);
            }
        }
        ft.commitAllowingStateLoss();
    }

    public BaseGalleryFragment getFragment(int position) {
        return fragments.get(position);
    }

    public void FragmentClear(){
        fragments.clear();
        controller = null;
    }

    public void FragmentsOnBackPress(){
        for(BaseGalleryFragment fragment : fragments){
            fragment.getAdapterGrid().AllChooseItemCancel();
        }
    }
}
