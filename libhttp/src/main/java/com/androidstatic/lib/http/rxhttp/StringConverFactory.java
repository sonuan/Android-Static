package com.androidstatic.lib.http.rxhttp;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * author: wusongyuan
 * date: 2016.12.02
 * desc:
 */

public final class StringConverFactory extends Converter.Factory {

    public static final String TAG = "StringConverFactory";

    public static StringConverFactory create() {
        return new StringConverFactory();
    }

    private StringConverFactory() {
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Log.i(TAG, "responseBodyConverter: ");
        return new StringResponseBodyConverter<Object>();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations, Retrofit retrofit) {
        Log.i(TAG, "requestBodyConverter: ");
        return new StringRequestBodyConverter<RequestBody>();
    }
}
