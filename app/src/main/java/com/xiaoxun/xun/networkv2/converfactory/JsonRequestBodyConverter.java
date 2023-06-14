package com.xiaoxun.xun.networkv2.converfactory;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.LogUtil;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

public class JsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private final String mKey;
    private final String mToken;

    public JsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter, String mKey, String mToken) {
        this.gson = gson;
        this.adapter = adapter;
        this.mKey = mKey;
        this.mToken = mToken;
    }

    @Nullable
    @Override
    public RequestBody convert(T value) throws IOException {
        LogUtil.e("jsonrequestbody:"+value.toString());
        String encryptInfo = Base64.encodeToString(AESUtil.encryptAESCBC(value.toString(),
                mKey, mKey),
                Base64.NO_WRAP) + mToken;
        return RequestBody.create(MEDIA_TYPE, encryptInfo);
    }
}
