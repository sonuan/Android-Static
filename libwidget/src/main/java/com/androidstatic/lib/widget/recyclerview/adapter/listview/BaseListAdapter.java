package com.androidstatic.lib.widget.recyclerview.adapter.listview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidstatic.lib.widget.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author: wusongyuan
 * date: 2016.11.28
 * desc:
 */

public abstract class BaseListAdapter<T> extends BaseAdapter {

    private List<T> mItems;

    public void setItems(List<T> items) {
        setItems(items, true);
    }

    public void setItems(List<T> items, boolean isNeedNotify) {
        mItems = items;
        notify(isNeedNotify);
    }

    public void addItems(List<T> items) {
        addItems(items, true);
    }

    public void addItems(List<T> items, boolean isNeedNotify) {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        mItems.addAll(items);
        notify(isNeedNotify);
    }

    private void notify(boolean isNeedNotify) {
        if (isNeedNotify) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public T getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        T t = getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = onCreateViewHolder(parent, viewType);
            if (holder != null && holder.itemView != null) {
                convertView = holder.itemView;
                convertView.setTag(R.id.contentViewTag, holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.contentViewTag);
        }
        if (holder != null) {
            holder.itemViewType = viewType;
            holder.itemId = getItemId(position);
            holder.onBindViewHolder(t, position);
        }
        return convertView;
    }

    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract static class ViewHolder<T> {
        private final View itemView;
        private int itemViewType = RecyclerView.INVALID_TYPE;
        private long itemId = RecyclerView.NO_ID;
        private BaseAdapterHelper helper;

        public ViewHolder(View view) {
            if (view == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            itemView = view;
            helper = new BaseAdapterHelper(itemView);
        }

        public abstract void onBindViewHolder(T t, int position);
    }
}
