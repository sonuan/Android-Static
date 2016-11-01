package com.androidstatic.lib.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidstatic.lib.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.listview)
    ListView mLvTitle;

    private String[] mTitles = new String[]{
            "管理多种状态,加载中视图、错误视图等"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mLvTitle.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mTitles));
    }


    @OnItemClick(R.id.listview)
    void onItemClick(int position) {
        switch (position) {
            case 0:
                MutileStatusViewActivity.toActivity(this);
                break;
        }
    }
}
