package com.androidstatic.lib.widget.recyclerview.adapter.recyclerview;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstatic.lib.widget.pullrecycle.BaseViewHolder;

/**
 * Created by jianghejie on 15/8/8.
 */
public class BaseAdapterViewHolder extends BaseViewHolder {
    private SparseArray<View> views;
    public BaseAdapterViewHolder(View itemView){
        super(itemView);
        this.views = new SparseArray<View>();
    }

    public TextView getTextView(int viewId) {
        return retrieveView(viewId);
    }

    public Button getButton(int viewId) {
        return retrieveView(viewId);
    }

    public ImageView getImageView(int viewId) {
        return retrieveView(viewId);
    }

    public View getView(int viewId) {
        return retrieveView(viewId);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T retrieveView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onBindViewHolder(int position) {

    }
}
