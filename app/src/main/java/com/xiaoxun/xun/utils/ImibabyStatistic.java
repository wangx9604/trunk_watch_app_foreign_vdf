package com.xiaoxun.xun.utils;

import android.view.View;
import android.widget.RelativeLayout;

import java.util.Date;

/**
 * Created by huangqilin on 2015/8/28.
 */
public class ImibabyStatistic {
    public static void locationStatistic(long time, String eid){
        Date today = new Date();
        String type = "Location_"+(today.getHours()+1);
        String key;
        if(time >0 && time <=5){
            key = "ok_5";
        }else if(time >5 && time <=10){
            key = "ok_10";
        }else if(time > 10 && time <= 20){
            key = "ok_20";
        }else if(time > 20 && time <= 30){
            key = "ok_30";
        }else if(time > 30 && time <= 40){
            key = "ok_40";
        }else if(time > 40 && time <= 50){
            key = "ok_50";
        }else if(time > 50 && time <60){
            key = "ok_60";
        }else{
            key = "fail";
            time = 0;
        }
    }
//    private static String getLocationStatictye(){
//        Date today = new Date();
//        return "Location_"+(today.getHours()+1);
//    }

    public static void setLayout(View view,int x,int y)
    {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)view.getLayoutParams();
        lp.setMargins(x,y,x+view.getWidth(),y+view.getHeight());
        view.setLayoutParams(lp);
    }
}

