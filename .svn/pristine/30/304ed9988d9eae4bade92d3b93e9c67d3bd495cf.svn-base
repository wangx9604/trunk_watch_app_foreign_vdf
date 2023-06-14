package com.xiaoxun.xun.base.glidelifecycle;



import com.xiaoxun.xun.base.CollectionsUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created on 2017/11/20 上午11:22.
 * leo linxiaotao1993@vip.qq.com
 */
public class ActivityFragmentLifeCycleManager implements LifeCycleManager, LifeCycleCallBack {

    private final Set<LifeCycleCallBack> mLifeCycleCallBacks =
            Collections.newSetFromMap(new WeakHashMap<LifeCycleCallBack, Boolean>());

    private boolean mIsStoped;
    private boolean mIsStarted;
    private boolean mIsDestroyed;
    private boolean mIsCleared;

    @Override
    public void addLifeCycleListener(LifeCycleCallBack listener) {

        if (mIsCleared) {
            return;
        }

        mLifeCycleCallBacks.add(listener);

        if (mIsDestroyed) {
            listener.onDestroy();
        } else if (mIsStarted) {
            listener.onStart();
        } else if (mIsStoped) {
            listener.onStop();
        }
    }

    @Override
    public void removeListener(LifeCycleCallBack listener) {
        mLifeCycleCallBacks.remove(listener);
    }

    @Override
    public void clearListeners() {
        mLifeCycleCallBacks.clear();
        mIsCleared = true;
    }


    @Override
    public void onStart() {
        mIsStarted = true;
        final List<LifeCycleCallBack> snapshot = CollectionsUtils.getSnapshot(mLifeCycleCallBacks);
        for (LifeCycleCallBack callBack : snapshot) {
            callBack.onStart();
        }
    }

    @Override
    public void onStop() {
        mIsStoped = true;

        final List<LifeCycleCallBack> snapshot = CollectionsUtils.getSnapshot(mLifeCycleCallBacks);
        for (LifeCycleCallBack callBack : snapshot) {
            callBack.onStop();
        }
    }

    @Override
    public void onDestroy() {
        mIsDestroyed = true;

        final List<LifeCycleCallBack> snapshot = CollectionsUtils.getSnapshot(mLifeCycleCallBacks);
        for (LifeCycleCallBack callBack : snapshot) {
            callBack.onDestroy();
        }
    }
}
