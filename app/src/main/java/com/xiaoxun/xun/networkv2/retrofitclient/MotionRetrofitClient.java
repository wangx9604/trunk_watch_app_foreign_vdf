package com.xiaoxun.xun.networkv2.retrofitclient;

import android.content.Context;

import com.xiaoxun.xun.OVERSEAURL;
import com.xiaoxun.xun.Url;
import com.xiaoxun.xun.networkv2.apis.MotionApi;
import com.xiaoxun.xun.region.XunKidsDomain;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MotionRetrofitClient {

    public static MotionApi getMotionRetrofitClient(Context context) {
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(XunKidsDomain.getInstance(context).getXunKidsDcenterDomain())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(MotionApi.class);
    }
}
