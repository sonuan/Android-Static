package com.androidstatic.lib.http.rxhttp;

import java.util.HashMap;

import rx.Observable;

/**
 * author: wusongyuan
 * date: 2016.12.02
 * desc:
 */

public class WebApi extends BaseApi {

    public static final int ROLLER = 1;
    public static final int FRUIT = 2;
    public static final int WX = 3;
    public static NetworkApi networkApi;
    public static Observable observable;

    public static NetworkApi getNetworkApi(String baseUrl, HashMap map) {
        networkApi = new RtHttp.NetworkApiBuilder()
                .setBaseUrl(baseUrl)
                .addDynamicParameter(map)
                .setConvertFactory(StringConverFactory.create())
                .build();
        return networkApi;
    }

    //public static NetworkApi getRollerApi(HashMap map) {
    //    return getNetworkApi(Web.getRollerUrl(), map);
    //}
    //
    //public static NetworkApi getFruitApi(HashMap map) {
    //    return getNetworkApi(Web.getFruitUrl(), map);
    //}
    //
    //public static NetworkApi getWxApi(HashMap map) {
    //    return getNetworkApi(Web.getWXUrl(), map);
    //}

    public static Observable getObserable(Observable observable) {
        observable = new ObserableBuilder(observable)
                .isWeb()
                .build();
        return observable;
    }

    public static Observable post(HashMap map, String action) {
        NetworkApi networkApi = getNetworkApi("http://gank.io/api/", map);
        observable = networkApi.get(1);
        return getObserable(observable);
    }
}
