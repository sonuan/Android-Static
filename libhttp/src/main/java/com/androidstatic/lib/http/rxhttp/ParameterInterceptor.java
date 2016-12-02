package com.androidstatic.lib.http.rxhttp;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ParameterInterceptor implements Interceptor {
         @Override
         public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //get请求后面追加共同的参数
        HttpUrl httpUrl = request.url().newBuilder()   //使用addQueryParameter()在url后面添加参数
                .addQueryParameter("userId", "")
                .build();
        request = request.newBuilder().url(httpUrl).build();
        return chain.proceed(request);
    }
}