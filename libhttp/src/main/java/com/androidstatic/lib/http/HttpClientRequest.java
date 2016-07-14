package com.androidstatic.lib.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**********************
 * @author: wusongyuan
 * @date: 2016-07-14
 * @desc:
 **********************/
public class HttpClientRequest {

    private static HttpClientRequest mInstance;
    private static Context mCtx;
    public RequestQueue mRequestQueue;

    private HttpClientRequest(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized HttpClientRequest getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HttpClientRequest(context);
        }
        return mInstance;
    }

    /**
     * Returns a Volley request queue for creating network requests
     *
     * @return {@link com.android.volley.RequestQueue}
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds a request to the Volley request queue
     *
     * @param request is the request to add to the Volley queue
     */
    public <T> void addRequest(Request<T> request) {
        getRequestQueue().add(request);
    }
}
