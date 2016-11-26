package com.androidstatic.lib;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.androidstatic.lib.widget.statusview.LoadingAndRetryManager;

import butterknife.ButterKnife;

/**
 * author: wusongyuan
 * date: 2016.11.01
 *desc:
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LoadingAndRetryManager.BASE_LOADING_LAYOUT_ID = R.layout.layout_base_loading;
        LoadingAndRetryManager.BASE_EMPTY_LAYOUT_ID = R.layout.layout_base_empty;
        LoadingAndRetryManager.BASE_RETRY_LAYOUT_ID = R.layout.layout_base_retry;

        ButterKnife.setDebug(BuildConfig.DEBUG);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
