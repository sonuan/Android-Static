package com.androidstatic.lib.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidstatic.lib.R;
import com.androidstatic.lib.basis.ExpandActivity;
import com.androidstatic.lib.basis.imageloader.ImageLoader;
import com.androidstatic.lib.basis.imageloader.glide.GlideImageConfig;
import com.androidstatic.lib.basis.imageloader.glide.GlideImageLoaderStrategy;
import com.androidstatic.lib.model.Images;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ItemViewHolder>{

        String[] imageUrls;
        public MyRecyclerViewAdapter(String[] imageUrls) {
            this.imageUrls = imageUrls;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imageloader_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.setData(imageUrls[position]);
        }

        @Override
        public int getItemCount() {
            return this.imageUrls == null ? 0 : this.imageUrls.length;
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.ivImage)
            public ImageView ivImage;
            ImageLoader mImageLoader;
            public ItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(ItemViewHolder.this, itemView);//用butterKnife绑定
                mImageLoader = new ImageLoader(new GlideImageLoaderStrategy());
            }

            public void setData(String url) {
                //GlideImageLoader.loadUri(ivImage, Uri.parse(url));
                //Glide.with(ivImage.getContext()).load(url).into(ivImage);
                //ivImage.setImageResource(R.mipmap.ic_launcher);
                mImageLoader.loadImage(ivImage.getContext().getApplicationContext(), GlideImageConfig.builder().url(url).imagerView(ivImage).build());
            }
        }
    }
}
