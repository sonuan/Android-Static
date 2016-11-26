package com.androidstatic.lib.widget.recyclerview.adapter.recyclerview;

public interface MultiItemTypeSupport<T> {

    int getLayoutId(int viewType);

    int getItemViewType(int position, T t);
}