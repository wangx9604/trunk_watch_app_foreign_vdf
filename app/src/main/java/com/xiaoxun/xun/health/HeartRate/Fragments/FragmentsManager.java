package com.xiaoxun.xun.health.HeartRate.Fragments;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentsManager {
    List<Fragment> fragments;

    public FragmentsManager(){
        init();
    }

    private void init(){
        fragments = new ArrayList<>();
        fragments.add(new DayHeartRateFragment());
        fragments.add(new WeekHeartRateFragment());
        fragments.add(new MonthHeartRateFragment());
    }

    public int getFragmentsSize(){
        return (fragments != null ? fragments.size() : 0);
    }

    public Fragment getFragment(int pos){
        return fragments.get(pos);
    }

    public void refreshDayData(){
        if(fragments != null && fragments.size() != 0) {
            DayHeartRateFragment dayHeartRateFragment = (DayHeartRateFragment) fragments.get(0);
            dayHeartRateFragment.refreshCurDayData();
        }
    }
}
