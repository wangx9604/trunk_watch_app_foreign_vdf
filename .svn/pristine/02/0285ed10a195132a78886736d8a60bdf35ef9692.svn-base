package com.xiaoxun.xun.health.motion.mpv;

import com.xiaoxun.xun.motion.mpv.ISportRecordApi;
import com.xiaoxun.xun.motion.mpv.MotionSportRecordModel;
import com.xiaoxun.xun.networkv2.beans.BaseVPInfo;
import com.xiaoxun.xun.networkv2.beans.MotionResoponseInfo;

import java.lang.ref.WeakReference;

public class MotionPresenterImpl implements ISportRecordApi {

    private WeakReference<ISportRecordApi> IView;
    private ISportRecordApi model;

    public MotionPresenterImpl(ISportRecordApi IView) {
        this.IView = new WeakReference<>(IView);;
        this.model = new MotionSportRecordModel(this);
    }

    @Override
    public void requestSportRecordMonth(BaseVPInfo mRequest) {
        model.requestSportRecordMonth(mRequest);
    }

    @Override
    public void responseSportRecordMonth(MotionResoponseInfo mInfo) {
        if(IView != null && IView.get() != null) {
            IView.get().responseSportRecordMonth(mInfo);
        }
    }

    @Override
    public void requestSportRecordDaily(BaseVPInfo mRequest) {
        model.requestSportRecordDaily(mRequest);
    }

    @Override
    public void responseSportRecordDaily(MotionResoponseInfo mInfo) {
        if(IView != null && IView.get() != null) {
            IView.get().responseSportRecordDaily(mInfo);
        }
    }
}
