package com.xiaoxun.xun.networkv2;

import com.blankj.utilcode.util.ActivityUtils;
import com.xiaoxun.xun.Url;

import com.xiaoxun.xun.networkv2.apis.Retrofit2Api;
import com.xiaoxun.xun.region.XunKidsDomain;
import com.xiaoxun.xun.utils.LogUtil;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetReqByRetrofit2 {

    private static Retrofit2Api getXunDialShop(){
        LogUtil.e("getXunDialShop --- ");
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(XunKidsDomain.getInstance(ActivityUtils.getTopActivity()).getXunKidsShopDomain())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(Retrofit2Api.class);
    }


    public static void getXunDialShop(String mRequestData, Callback<ResponseBody> mCallback) {
        LogUtil.e("getXunDialShop --- retrofit ");
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), mRequestData);
        Call<ResponseBody> mCall = getXunDialShop().xunDialShopCheckVisibleApi(requestBody);
        mCall.enqueue(mCallback);
    }

}
