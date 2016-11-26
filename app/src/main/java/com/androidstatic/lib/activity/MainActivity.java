package com.androidstatic.lib.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidstatic.lib.R;
import com.androidstatic.lib.basis.ExpandActivity;

import butterknife.BindView;
import butterknife.OnItemClick;

public class MainActivity extends ExpandActivity {

    @BindView(R.id.listview_main)
    ListView mLvTitle;

    private String[] mTitles = new String[]{
            "管理多种状态,加载中视图、错误视图等",
            "图片加载封装",
            "ListActivity封装"
    };

    @Override
    protected Object onCreateView() {
        return R.layout.activity_main;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mLvTitle.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mTitles));
    }

    @OnItemClick(R.id.listview_main)
    void onItemClick(int position) {
        switch (position) {
            case 0:
                MutileStatusViewActivity.toActivity(this);
                break;
            case 1:
                ImageLoaderActivity.toActivity(this);
                break;
            case 2:
                SimpleListActivity.toActivity(this);
                break;
        }
    }
}
