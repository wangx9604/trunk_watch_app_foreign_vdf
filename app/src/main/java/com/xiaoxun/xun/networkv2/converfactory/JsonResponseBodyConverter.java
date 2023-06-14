package com.xiaoxun.xun.networkv2.converfactory;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.xiaoxun.xun.utils.AESUtil;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private final String mKey;

    public JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, String mKey) {
        this.gson = gson;
        this.adapter = adapter;
        this.mKey = mKey;
    }

    @Nullable
    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String response=value.string();
            byte[] decBase64 = Base64.decode(response, Base64.NO_WRAP);
            byte[] decMessage = AESUtil.decryptAESCBC(decBase64, mKey, mKey);
            T result = adapter.fromJson(new String(decMessage));
            return result;
        } finally {
            value.close();
        }
    }
}
