package com.xiaoxun.xun.networkv2.apis;

import com.xiaoxun.xun.networkv2.beans.HeadImageResponseInfo;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Retrofit2Api {

    @Headers({"Content-Type:application/json;charset=utf-8"})
    @POST("/dvsinfo")
    Call<HeadImageResponseInfo> getHeadImageInfoOnApi(@Body RequestBody requestBody);


    @Headers({"Content-Type:application/json;charset=utf-8"})
    @POST("/checkvisible")
    Call<ResponseBody> xunScoreCheckVisibleApi(@Body RequestBody requestBody);

    @Headers({"Content-Type:application/json;charset=utf-8"})
    @POST("/app/v1/show")
    Call<ResponseBody> xunDialShopCheckVisibleApi(@Body RequestBody requestBody);

}
