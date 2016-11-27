package com.androidstatic.lib.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidstatic.lib.R;
import com.androidstatic.lib.basis.imageloader.ImageLoader;
import com.androidstatic.lib.basis.imageloader.glide.GlideImageConfig;
import com.androidstatic.lib.basis.imageloader.glide.GlideImageLoaderStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author: wusongyuan
 * date: 2016.11.27
 * desc:
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ItemViewHolder>{

    String[] imageUrls;
    public MyRecyclerViewAdapter(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imageloader_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.setData(imageUrls[position]);
    }

    @Override
    public int getItemCount() {
        return this.imageUrls == null ? 0 : this.imageUrls.length;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder{
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

    public interface OnLisenter{

    }
}
