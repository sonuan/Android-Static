package com.androidstatic.lib.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.androidstatic.lib.R;
import com.androidstatic.lib.basis.ExpandActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MutileStatusViewActivity extends ExpandActivity {


    @BindView(R.id.btn_loading)
    Button mBtnLoading;
    @BindView(R.id.btn_retry)
    Button mBtnRetry;
    @BindView(R.id.btn_content)
    Button mBtnContent;
    @BindView(R.id.btn_empty)
    Button mBtnEmpty;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, MutileStatusViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected Object onCreateView() {
        return R.layout.activity_mutile_status_view;
    }

    @Override
    public void initViews() {
        super.initViews();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @OnClick({R.id.btn_loading, R.id.btn_retry, R.id.btn_content, R.id.btn_empty})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_loading:
                showLoading();
                break;
            case R.id.btn_retry:
                break;
            case R.id.btn_content:
                break;
            case R.id.btn_empty:
                break;
        }
    }
}
