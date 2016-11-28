package com.androidstatic.lib.widget.recyclerview.adapter.listview;

import android.content.Context;

/**
 * author: wusongyuan
 * date: 2016.11.29
 * desc:
 */

public class TestQAdapter extends QuickAdapter<Object> {
    public TestQAdapter(Context context) {
        super(context, new MultiItemTypeSupport<Object>() {
            @Override
            public int getLayoutId(int viewType) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 0;
            }

            @Override
            public int getItemViewType(int position, Object o) {
                return 0;
            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, Object item) {

    }
}
