package com.androidstatic.lib.widget.recyclerview.adapter.listview;

public interface MultiItemTypeSupport<T>
{
	int getLayoutId(int viewType);
	
	int getViewTypeCount();
	
	int getItemViewType(int position, T t);
}