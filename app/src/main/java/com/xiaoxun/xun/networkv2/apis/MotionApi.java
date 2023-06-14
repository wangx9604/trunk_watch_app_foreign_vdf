package com.xiaoxun.xun.networkv2.apis;


import com.xiaoxun.xun.networkv2.beans.MotionReportBean;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MotionApi {
    @Headers({"Content-Type:application/json;charset=utf-8"})
    @POST("/hcxl/sportReport")
    Call<MotionReportBean> getSportReportWeekly(@Body RequestBody mParmas);

    @Headers({"Content-Type:application/json;charset=utf-8"})
    @POST("/hcxl/sportDataMonth")
    Call<ResponseBody> getSportReportMonth(@Body RequestBody mParmas);

    @Headers({"Content-Type:application/json;charset=utf-8"})
    @POST("/hcxl/sportData")
    Call<ResponseBody> getSportReportDaily(@Body RequestBody mParmas);


    @Headers({"Content-Type:application/json;charset=utf-8"})
    @POST("/splan/pointsGetMonth")
    Call<ResponseBody> getScoreReportMonth(@Body RequestBody mParmas);

    @Headers({"Content-Type:application/json;charset=utf-8"})
    @POST("/splan/pointsGet")
    Call<ResponseBody> getScoreReportDaily(@Body RequestBody mParmas);


}
