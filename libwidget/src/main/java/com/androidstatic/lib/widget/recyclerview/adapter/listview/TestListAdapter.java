package com.androidstatic.lib.widget.recyclerview.adapter.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * author: wusongyuan
 * date: 2016.11.29
 * desc:
 */

public class TestListAdapter extends BaseListAdapter<Object> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO: 2016/11/29
        return new ItemViewHolder(new TextView(parent.getContext()));
    }

    private static class ItemViewHolder extends ViewHolder{

        public ItemViewHolder(View view) {
            super(view);
        }

        @Override
        public void onBindViewHolder(Object o, int position) {
            // TODO: 2016/11/29
        }
    }
}
