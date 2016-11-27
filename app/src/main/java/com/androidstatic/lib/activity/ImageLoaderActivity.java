package com.androidstatic.lib.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androidstatic.lib.R;
import com.androidstatic.lib.adapter.MyRecyclerViewAdapter;
import com.androidstatic.lib.basis.ExpandActivity;
import com.androidstatic.lib.model.Images;

import butterknife.BindView;

public class ImageLoaderActivity extends ExpandActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, ImageLoaderActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.recylerview)
    public RecyclerView mRecyclerView;

    String[] mImageUrls;


    @Override
    protected Object onCreateView() {
        return R.layout.activity_image_loader;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mImageUrls = Images.imageThumbUrls;
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(mImageUrls);
        mRecyclerView.setAdapter(adapter);
    }
}
