package com.androidstatic.lib.http.rxhttp;

import java.util.HashMap;

import okhttp3.RequestBody;
import rx.Observable;

/**
 * author: wusongyuan
 * date: 2016.12.02
 * desc:
 */

public class MobileApi extends BaseApi{

    public static NetworkApi networkApi;
    public static Observable obserable;

    public static NetworkApi getNetworkApi() { //使用NetworkApiBuilder创建networkApi
        if(networkApi==null ){
            networkApi = new RtHttp.NetworkApiBuilder()
                    .addSession()               //添加sessionId
                    .addParameter()             //添加固定参数
                    .build();
        }
        return networkApi;
    }

    public static Observable getObserable(Observable observable) {
        obserable = new ObserableBuilder(observable)
                .addApiException()   //添加apiExcetion过滤
                .build();
        return obserable;
    }

    public static Observable response(HashMap map, int protocolId) {
        RequestBody body = toBody(map);
        return getObserable(getNetworkApi().response(protocolId, body));
    }
}
