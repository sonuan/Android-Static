/**
 * Copyright 2013 Joan Zapata
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.androidstatic.lib.widget.recyclerview.adapter.listview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseQuickAdapter<T, H extends BaseAdapterHelper> extends BaseAdapter {

    protected static final String TAG = BaseQuickAdapter.class.getSimpleName();

    protected final Context context;

    protected int layoutResId;

    protected List<T> mListT;

    protected boolean displayIndeterminateProgress = false;


    public BaseQuickAdapter(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }


    public BaseQuickAdapter(Context context, int layoutResId, List<T> list) {
        this.mListT = list == null ? new ArrayList<T>() : new ArrayList<T>(list);
        this.context = context;
        this.layoutResId = layoutResId;
    }

    protected MultiItemTypeSupport<T> mMultiItemSupport;

    public BaseQuickAdapter(Context context,
                            MultiItemTypeSupport<T> multiItemSupport) {
        this(context, null, multiItemSupport);
    }

    public BaseQuickAdapter(Context context, ArrayList<T> list,
                            MultiItemTypeSupport<T> multiItemSupport) {
        this.mMultiItemSupport = multiItemSupport;
        this.mListT = list == null ? new ArrayList<T>() : new ArrayList<T>(list);
        this.context = context;
    }

    @Override
    public int getCount() {
        int extra = displayIndeterminateProgress ? 1 : 0;
        return mListT.size() + extra;
    }

    @Override
    public T getItem(int position) {
        if (position >= mListT.size())
            return null;
        return mListT.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (mMultiItemSupport != null)
            return mMultiItemSupport.getViewTypeCount() + 1;
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (displayIndeterminateProgress) {
            if (mMultiItemSupport != null)
                return position >= mListT.size() ? 0 : mMultiItemSupport.getItemViewType(position,
                        mListT.get(position));
        } else {
            if (mMultiItemSupport != null)
                return mMultiItemSupport.getItemViewType(position, mListT.get(position));
        }

        return position >= mListT.size() ? 0 : 1;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (displayIndeterminateProgress && getItemViewType(position) == 0) {
            return createIndeterminateProgressView(convertView, parent);
        }
        final H helper = getAdapterHelper(position, convertView, parent);
        T item = getItem(position);
        helper.setAssociatedObject(item);
        convert(helper, item);
        return helper.getView();

    }

    private View createIndeterminateProgressView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            FrameLayout container = new FrameLayout(context);
            container.setForegroundGravity(Gravity.CENTER);
            ProgressBar progress = new ProgressBar(context);
            container.addView(progress);
            convertView = container;
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return position < mListT.size();
    }

    public void add(T elem) {
        mListT.add(elem);
        notifyDataSetChanged();
    }

    public void addAll(List<T> elem) {
        mListT.addAll(elem);
        notifyDataSetChanged();
    }

    public void set(T oldElem, T newElem) {
        set(mListT.indexOf(oldElem), newElem);
    }

    public void set(int index, T elem) {
        mListT.set(index, elem);
        notifyDataSetChanged();
    }

    public void remove(T elem) {
        mListT.remove(elem);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        mListT.remove(index);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> elem) {
        mListT.clear();
        mListT.addAll(elem);
        notifyDataSetChanged();
    }

    public boolean contains(T elem) {
        return mListT.contains(elem);
    }


    public void clear() {
        mListT.clear();
        notifyDataSetChanged();
    }

    public void showIndeterminateProgress(boolean display) {
        if (display == displayIndeterminateProgress)
            return;
        displayIndeterminateProgress = display;
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return mListT;
    }

    public void setData(List<T> data) {
        this.mListT = data;
        notifyDataSetChanged();
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(H helper, T item);

    /**
     * You can override this method to use a custom BaseAdapterHelper in order to fit your needs
     *
     * @param position    The position of the item within the adapter's data set of the item whose
     *                    view we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this
     *                    view is non-null and of an appropriate type
     *                    before using. If it is not possible to convert this view to display the
     *                    correct
     *                    data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that
     *                    this View
     *                    is always of the right type (see
     *                    {@link #getViewTypeCount()} and {@link #getItemViewType(int)} ).
     * @param parent      The parent that this view will eventually be attached to
     * @return An instance of BaseAdapterHelper
     */
    protected abstract H getAdapterHelper(int position, View convertView, ViewGroup parent);

}
