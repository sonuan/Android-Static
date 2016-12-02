package com.androidstatic.lib.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.androidstatic.lib.R;
import com.androidstatic.lib.basis.ExpandActivity;
import com.androidstatic.lib.http.rxhttp.ApiSubscriber;
import com.androidstatic.lib.http.rxhttp.RtHttp;
import com.androidstatic.lib.http.rxhttp.WebApi;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class RtHttpActivity extends ExpandActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, RtHttpActivity.class);
        context.startActivity(intent);
    }

    private static final String TAG = "RtHttpActivity";
    @BindView(R.id.tv_rthttp)
    TextView mTvRtHttp;
    @Override
    protected Object onCreateView() {
        return R.layout.activity_rthttp;
    }

    @OnClick(R.id.btn_rthttp)
    void onClick() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uuid", "Kkkk");
        RtHttp.with(this).setObservable(WebApi.post(hashMap, ""))
                .subscriber(new ApiSubscriber() {
                    @Override
                    public void onNext(Object o) {
                        Log.i(TAG, "onNext: " + (o instanceof JSONObject)+ "  " + o.toString());
                        //GankListBean lstBean = (GankListBean) o;
                    }
                });

    }
}
