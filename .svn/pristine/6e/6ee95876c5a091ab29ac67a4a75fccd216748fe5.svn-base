package com.xiaoxun.xun.networkv2.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class XunHttpReqUtils {

    private static final String TAG = "XunHttpReqUtils ";

    public interface OperationCallback {
        void onSuccess(String result);

        void onFail(String error);
    }

    public static void reqPost(String url, String reqBody, final OperationCallback callback) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, reqBody);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG ,  " getXunScoreXunTask IOException = " + e.toString());
                callback.onFail("fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String result = response.body().string();
                Log.i(TAG , " getXunScoreXunTask result = " + result);
                callback.onSuccess(result);
            }
        });
    }

    public static void reqGet(String url, final OperationCallback callback) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG ,  " getXunScoreXunTask IOException = " + e.toString());
                callback.onFail("fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String result = response.body().string();
                Log.i(TAG , " getXunScoreXunTask result = " + result);
                callback.onSuccess(result);
            }
        });
    }
}
