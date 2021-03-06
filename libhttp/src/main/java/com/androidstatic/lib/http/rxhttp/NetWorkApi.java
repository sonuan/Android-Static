package com.androidstatic.lib.http.rxhttp;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * author: wusongyuan
 * date: 2016.12.01
 * desc:
 */

public interface NetworkApi {

    @GET("data/Android/10/{page}")
    Observable<GankListBean> get(@Path("page") int page);

    @POST("open/open.do")
    Observable<Object> post(@Query("ACID") int acid, @Body RequestBody entery);

    @POST("open/open.do")
    Observable<ResponseInfo<Object>> response(@Query("ACID") int acid, @Body RequestBody  entery);
}
