package com.xiaoxun.xun.networkv2.retrofitclient;

import android.content.Context;

import com.xiaoxun.xun.networkv2.beans.DaoMaster;
import com.xiaoxun.xun.networkv2.beans.DaoSession;

public class GreenDaoManager {
    private static GreenDaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private GreenDaoManager(Context context){

        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context,"imibaby_action_db",null);
        mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public static GreenDaoManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new GreenDaoManager(context);
        }
        return mInstance;
    }

    public DaoMaster getMaster(){
        return mDaoMaster;
    }

    public DaoSession getDaoSession(){
        return mDaoSession;
    }

    public DaoSession getNewSession(){
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }

}

