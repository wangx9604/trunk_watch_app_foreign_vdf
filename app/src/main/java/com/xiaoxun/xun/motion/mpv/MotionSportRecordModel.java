package com.xiaoxun.xun.motion.mpv;

import com.xiaoxun.xun.networkv2.apis.MotionApi;
import com.xiaoxun.xun.networkv2.beans.BaseVPInfo;
import com.xiaoxun.xun.networkv2.beans.MotionResoponseInfo;
import com.xiaoxun.xun.networkv2.retrofitclient.MotionRetrofitClient;
import com.xiaoxun.xun.utils.LogUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MotionSportRecordModel implements ISportRecordApi{

    private ISportRecordApi mPresenter;

    public MotionSportRecordModel(ISportRecordApi mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void requestSportRecordMonth(BaseVPInfo mRequest) {
        String mRecordType = mRequest.getmInfo2();
        String encryptInfo = mRequest.getmInfo1();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), encryptInfo);
        MotionApi mApi = MotionRetrofitClient.getMotionRetrofitClient(mRequest.getmContext());

        Callback<ResponseBody> mCallback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //1:解析数据
                try {
                    String mResponseInfo = response.body().string();
                    LogUtil.e("responseInfo:" + mResponseInfo);
                    JSONObject mJsonInfo = (JSONObject) JSONValue.parse(mResponseInfo);
                    int code = (int) mJsonInfo.get("code");
                    if (code == 1) {
                        responseSportRecordMonth(new MotionResoponseInfo(1, mJsonInfo.get("data").toString()));
                    } else {
                        //1:发送失败数据
                        responseSportRecordMonth(new MotionResoponseInfo(-1, ""));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //1:发送失败数据
                    responseSportRecordMonth(new MotionResoponseInfo(-1, ""));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //1:发送失败数据
                responseSportRecordMonth(new MotionResoponseInfo(-1, ""));
            }
        };
        if("0".equals(mRecordType)) {
            mApi.getSportReportMonth(requestBody).enqueue(mCallback);
        }else{
            mApi.getScoreReportMonth(requestBody).enqueue(mCallback);
        }
    }

    @Override
    public void responseSportRecordMonth(MotionResoponseInfo mInfo) {
        mPresenter.responseSportRecordMonth(mInfo);
    }

    @Override
    public void requestSportRecordDaily(BaseVPInfo mRequest) {
        String mRecordType = mRequest.getmInfo2();
        String encryptInfo = mRequest.getmInfo1();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), encryptInfo);
        MotionApi mApi = MotionRetrofitClient.getMotionRetrofitClient(mRequest.getmContext());
        Callback<ResponseBody> mCallback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //1:解析数据
                try {
                    String mResponseInfo = response.body().string();
                    LogUtil.e("responseInfo:"+mResponseInfo);
                    JSONObject mJsonInfo = (JSONObject) JSONValue.parse(mResponseInfo);
                    int code = (int) mJsonInfo.get("code");
                    if (code == 1){
                        responseSportRecordDaily(new MotionResoponseInfo(1, mResponseInfo));
                    }else{
                        //1:发送失败数据
                        responseSportRecordDaily(new MotionResoponseInfo(-1,""));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //1:发送失败数据
                    responseSportRecordDaily(new MotionResoponseInfo(-1,""));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //1:发送失败数据
                responseSportRecordDaily(new MotionResoponseInfo(-1,""));
            }
        };
        if("0".equals(mRecordType)) {
            mApi.getSportReportDaily(requestBody).enqueue(mCallback);
        }else{
            mApi.getScoreReportDaily(requestBody).enqueue(mCallback);
        }
    }

    @Override
    public void responseSportRecordDaily(MotionResoponseInfo mInfo) {
        mPresenter.responseSportRecordDaily(mInfo);
    }
}
