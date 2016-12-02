package com.androidstatic.lib.http.rxhttp;

import java.util.HashMap;

/**
 * author: wusongyuan
 * date: 2016.12.02
 * desc:
 */
public class ImiRequestBean {
    private long requeststamp;
    private HashMap data;

    public long getRequeststamp() {
        return requeststamp;
    }

    public void setRequeststamp(long requeststamp) {
        this.requeststamp = requeststamp;
    }

    public HashMap getData() {
        return data;
    }

    public void setData(HashMap data) {
        this.data = data;
    }
}
