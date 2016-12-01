package com.androidstatic.lib.http.rxhttp;

import rx.Observable;
import rx.functions.Func1;

/**
 * author: wusongyuan
 * date: 2016.12.02
 * desc:
 */

public class StringToJSONObjectFun1<T> implements Func1<ResponseInfo<T>, Observable<T>> {

    @Override
    public Observable<T> call(ResponseInfo<T> responseInfo) {
        if (responseInfo.getCode()!= 200) {  //如果code返回的不是200,则抛出ApiException异常，否则返回data数据
            return Observable.error(new ApiException(responseInfo.getCode(),responseInfo.getMessage()));
        }
        return Observable.just(responseInfo.getData());
    }
}
