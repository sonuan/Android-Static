package com.androidstatic.lib.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidstatic.lib.R;
import com.androidstatic.lib.basis.BaseListActivity;
import com.androidstatic.lib.widget.pullrecycle.BaseViewHolder;
import com.androidstatic.lib.widget.pullrecycle.layoutmanager.ILayoutManager;
import com.androidstatic.lib.widget.pullrecycle.layoutmanager.MyLinearLayoutManager;

import java.util.ArrayList;

public class SimpleListActivity extends BaseListActivity<String> {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, SimpleListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_simple_item_list, parent, false));
    }

    Handler mHandler;

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public void onRefresh(int action) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDataList == null) {
                    mDataList = new ArrayList<String>();
                }
                mDataList.add("中国");
                mDataList.add("福建");
                mDataList.add("厦门");
                mDataList.add("湖里");
                mDataList.add("软件园二期");
                mDataList.add("观日路");
                mDataList.add("中国");
                mDataList.add("福建");
                mDataList.add("厦门");
                mDataList.add("湖里");
                mDataList.add("软件园二期");
                mDataList.add("观日路");
                mDataList.add("中国");
                mDataList.add("福建");
                mDataList.add("厦门");
                mDataList.add("湖里");
                mDataList.add("软件园二期");
                mDataList.add("观日路");
                mDataList.add("中国");
                mDataList.add("福建");
                mDataList.add("厦门");
                mDataList.add("湖里");
                mDataList.add("软件园二期");
                mDataList.add("观日路");
                recycler.enableLoadMore(mDataList.size() < 50);
                adapter.notifyDataSetChanged();
                recycler.onRefreshCompleted();
            }
        }, 2000);
    }

    @Override
    protected ILayoutManager getLayoutManager() {
        return new MyLinearLayoutManager(getApplicationContext());
    }

    private class ItemViewHolder extends BaseViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindViewHolder(int position) {

        }

        @Override
        public void onItemClick(View view, int position) {

        }
    }
}
