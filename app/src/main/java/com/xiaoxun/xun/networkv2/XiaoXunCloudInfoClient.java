package com.xiaoxun.xun.networkv2;


import com.xiaoxun.xun.networkv2.apis.OnResponCallBack;
import com.xiaoxun.xun.utils.LogUtil;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class XiaoXunCloudInfoClient {
    private final String TAG = XiaoXunCloudInfoClient.this.getClass().getSimpleName();
    private boolean isTest;

    private XiaoXunCloudInfoClient(boolean isTest) {
        this.isTest = isTest;
    }

    public static final class Builder{
        private boolean isTest = false;
        public Builder setIsTest(boolean isTest){
            this.isTest = isTest;
            return this;
        }
        public XiaoXunCloudInfoClient build(){
            return new XiaoXunCloudInfoClient(isTest);
        }
    }


    public void onGetXunDialShopVisible(String mRequestData, OnResponCallBack<ResponseBody> mCallBack){
        LogUtil.e("getXunDialShop --- xuncloudinfo ");
        NetReqByRetrofit2.getXunDialShop(mRequestData, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body() != null) {
                    mCallBack.onCallBack(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ResponseBody mInfo = ResponseBody.create(MediaType.parse("text/plain"),"{\"RC\":-10011}");
                mCallBack.onCallBack(mInfo);
            }
        });
    }

}
