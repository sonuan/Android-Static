package com.androidstatic.lib.http.rxhttp;

import org.json.JSONException;
import org.json.JSONObject;

import rx.functions.Func1;

/**
 * author: wusongyuan
 * date: 2016.12.02
 * desc:
 */

public class StringToJSONObjectFun1 implements Func1<String, JSONObject> {

    @Override
    public JSONObject call(String text) {
        try {
            return new JSONObject(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
