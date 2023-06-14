package com.xiaoxun.xun.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xiaoxun.xun.base.glidelifecycle.ActivityFragmentLifeCycleManager;
import com.xiaoxun.xun.base.glidelifecycle.LifeCycleCallBack;


/**
 * 设置BaseActivity生命周期监听
 * BaseActivity_With_Frgment 继承  BaseLifeCycleActivity
 * 业务功能 Activity 继承 BaseActivity_With_Frgment
 */
public abstract class BaseLifeCycleActivity extends AppCompatActivity implements  BaseView {

    private ActivityFragmentLifeCycleManager mLifeCycleManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mLifeCycleManager == null) {
            mLifeCycleManager = new ActivityFragmentLifeCycleManager();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mLifeCycleManager.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLifeCycleManager.onStop();
    }


    @Override
    public void onDestroy() {


        releaseResources();

        super.onDestroy();
    }


    @Override
    public void addLifeCycleListener(LifeCycleCallBack listener) {
        mLifeCycleManager.addLifeCycleListener(listener);
    }

    @Override
    public void removeListener(LifeCycleCallBack listener) {
        mLifeCycleManager.removeListener(listener);
    }

    @Override
    public void clearListeners() {
        if (mLifeCycleManager != null) {
            mLifeCycleManager.clearListeners();
            mLifeCycleManager = null;
        }
    }

    public void releaseResources() {
        mLifeCycleManager.onDestroy();
        clearListeners();
    }


}
