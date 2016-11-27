package com.androidstatic.lib.widget.recyclerview.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jianghejie on 15/8/8.
 */
public abstract class BaseQuickAdapter<T, H extends BaseAdapterViewHolder> extends RecyclerView
        .Adapter<BaseAdapterViewHolder> implements View.OnClickListener {
    protected static final String TAG = BaseQuickAdapter.class.getSimpleName();

    protected final Context context;

    protected int layoutResId;

    protected List<T> mItems;

    protected boolean displayIndeterminateProgress = false;

    private OnItemClickListener mOnItemClickListener = null;

    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * Create a QuickAdapter.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     */
    public BaseQuickAdapter(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     * @param list        A new list is created out of this one to avoid mutable list
     */
    public BaseQuickAdapter(Context context, int layoutResId, List<T> list) {
        this.mItems = list == null ? new ArrayList<T>() : list;
        this.context = context;
        this.layoutResId = layoutResId;
    }

    protected BaseQuickAdapter(Context context, MultiItemTypeSupport<T> multiItemTypeSupport) {
        this(context, multiItemTypeSupport, null);
    }


    protected BaseQuickAdapter(Context context, MultiItemTypeSupport<T> multiItemTypeSupport,
                               List<T> list) {
        this.context = context;
        this.mItems = list == null ? new ArrayList<T>() : new ArrayList<T>(list);
        this.mMultiItemTypeSupport = multiItemTypeSupport;
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }


    public T getItem(int position) {
        if (position >= mItems.size()) return null;
        return mItems.get(position);
    }

    public void setData(List<T> list) {
        mItems = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiItemTypeSupport != null) {
            return mMultiItemTypeSupport.getItemViewType(position, getItem(position));
        }
        return super.getItemViewType(position);
    }

    private int mCreateCount = 0;

    @Override
    public BaseAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i("onCreateViewHolder", "------new----->>>>" + ++mCreateCount);
        View view = null;
        if (mMultiItemTypeSupport != null) {
            int layoutId = mMultiItemTypeSupport.getLayoutId(viewType);
            view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup,
                    false);
        }
        view.setOnClickListener(this);
        BaseAdapterViewHolder vh = new BaseAdapterViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(BaseAdapterViewHolder helper, int position) {
        helper.itemView.setTag(position);
        T item = getItem(position);
        convert((H) helper, item);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(H helper, T item);

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void add(T elem) {
        mItems.add(elem);
        notifyDataSetChanged();
    }


    public void addAll(List<T> elem) {
        mItems.addAll(elem);
        notifyDataSetChanged();
    }


    public void set(T oldElem, T newElem) {
        set(mItems.indexOf(oldElem), newElem);
    }


    public void set(int index, T elem) {
        mItems.set(index, elem);
        notifyDataSetChanged();
    }


    public void remove(T elem) {
        mItems.remove(elem);
        notifyDataSetChanged();
    }


    public void remove(int index) {
        mItems.remove(index);
        notifyDataSetChanged();
    }


    public void replaceAll(List<T> elem) {
        mItems.clear();
        mItems.addAll(elem);
        notifyDataSetChanged();
    }


    public boolean contains(T elem) {
        return mItems.contains(elem);
    }


    /**
     * Clear data list
     */
    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public void onItemMove(int from, int to) {
        Collections.swap(mItems, from, to);
        notifyItemMoved(from, to);
    }

}
