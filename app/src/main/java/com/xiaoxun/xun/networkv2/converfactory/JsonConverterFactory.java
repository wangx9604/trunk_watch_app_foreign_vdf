package com.xiaoxun.xun.networkv2.converfactory;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JsonConverterFactory extends Converter.Factory {
    private final Gson gson;
    private final String mKey;
    private final String mToken;

    public JsonConverterFactory(Gson gson, String mKey, String mToken) {
        this.mKey = mKey;
        this.mToken = mToken;
        if (gson == null) throw new NullPointerException("gson == null");
            this.gson = gson;
    }

    public static JsonConverterFactory create(String mToken, String mKey){
        return create(new Gson(), mKey, mToken);
    }

    public static JsonConverterFactory create(Gson gson, String mKey, String mToken){
        return new JsonConverterFactory(gson, mKey, mToken);
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonResponseBodyConverter<>(gson, adapter, mKey);
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonRequestBodyConverter<>(gson, adapter, mKey, mToken);
    }
}
