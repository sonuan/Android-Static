package com.androidstatic.lib.widget.pullrecycle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidstatic.lib.widget.R;

import java.util.ArrayList;
import java.util.List;

import static com.androidstatic.lib.widget.pullrecycle.BaseListAdapter.State.END;
import static com.androidstatic.lib.widget.pullrecycle.BaseListAdapter.State.ERROR;
import static com.androidstatic.lib.widget.pullrecycle.BaseListAdapter.State.HIDE;
import static com.androidstatic.lib.widget.pullrecycle.BaseListAdapter.State.IDLE;
import static com.androidstatic.lib.widget.pullrecycle.BaseListAdapter.State.LOADING;


/**
 * Created by Stay on 7/3/16.
 * Powered by www.stay4it.com
 */
public abstract class BaseListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    protected static final int VIEW_TYPE_LOAD_MORE_FOOTER = Integer.MIN_VALUE - 1;
    protected static final int VIEW_TYPE_HEADER = Integer.MIN_VALUE - 1000;
    protected boolean isLoadMoreFooterShown;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private static List<Integer> sHeaderTypes = new ArrayList<>();//每个header必须有不同的type,不然滚动的时候顺序会变化

    static class HeaderViewBean {
        public int viewType;
        public View view;
    }

    public void addHeaderView(View view) {
        sHeaderTypes.add(VIEW_TYPE_HEADER + mHeaderViews.size());
        mHeaderViews.add(view);
        notifyDataSetChanged();
    }

    //根据header的ViewType判断是哪个header
    private View getHeaderViewByType(int itemType) {
        if (!isHeaderType(itemType)) {
            return null;
        }
        return mHeaderViews.get(itemType - VIEW_TYPE_HEADER);
    }

    //判断一个type是否为HeaderType
    private boolean isHeaderType(int itemViewType) {
        return mHeaderViews.size() > 0 && sHeaderTypes.contains(itemViewType);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeaderType(viewType)) {
            return new SimpleViewHolder(getHeaderViewByType(viewType));
        }
        if (viewType == VIEW_TYPE_LOAD_MORE_FOOTER) {
            return onCreateLoadMoreFooterViewHolder(parent);
        }
        return onCreateNormalViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (isHeader(position)) {
            return;
        }
        int adjPosition = position - getHeadersCount();
        holder.onBindViewHolder(adjPosition);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (isFooter(holder.getLayoutPosition()) && isHeader(holder.getLayoutPosition()) && lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) lp;
            params.setFullSpan(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeader(position) || isFooter(position)) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    public boolean isHeader(int position) {
        return position >= 0 && position < mHeaderViews.size();
    }


    public boolean isFooter(int position) {
        if (isLoadMoreFooterShown) {
            return position == getItemCount() - 1;
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getDataCount() + (isLoadMoreFooterShown ? 1 : 0);
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        int adjPosition = position - getHeadersCount();
        if (isHeader(position)) {
            position = position - 1;
            return sHeaderTypes.get(position);
        }
        if (isFooter(position)) {
            return VIEW_TYPE_LOAD_MORE_FOOTER;
        }
        return getDataViewType(adjPosition);
    }

    protected abstract int getDataCount();

    protected abstract BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType);

    protected BaseViewHolder onCreateLoadMoreFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_pull_to_refresh_footer, parent,
                false);
        return new LoadMoreFooterViewHolder(view);
    }

    protected int getDataViewType(int position) {
        return 0;
    }

    public void onLoadMoreStateChanged(boolean isShown) {
        this.isLoadMoreFooterShown = isShown;
        if (isShown) {
            notifyItemInserted(getItemCount());
        } else {
            notifyItemRemoved(getItemCount());
        }
    }

    public boolean isLoadMoreFooter(int position) {
        return isLoadMoreFooterShown && position == getItemCount() - 1;
    }

    public boolean isSectionHeader(int position) {
        return false;
    }

    public enum State {
        /**
         * 隐藏状态
         */
        HIDE,
        /**
         * 闲置状态，也就是初始状态
         */
        IDLE,
        /**
         * 正在加载状态
         */
        LOADING,
        /**
         * 没有更多可加载的数据时显示的状态
         */
        END,
        /**
         * 出错时的状态显示
         */
        ERROR
    }

    private static class LoadMoreFooterViewHolder extends BaseViewHolder {


        public LoadMoreFooterViewHolder(View view) {
            super(view);
        }

        @Override
        public void onBindViewHolder(int position) {

        }

        @Override
        public void onItemClick(View view, int position) {

        }


        /**
         * 设置为初始状态
         */
        private void setLoadMoreLoading() {
            setLoadMoreState(LOADING);
        }

        /**
         * 设置为初始状态
         */
        public void setLoadMoreIdle() {
            setLoadMoreState(IDLE);
        }

        /**
         * loadMore显示为空白
         */
        public void setLoadMoreHide() {
            setLoadMoreState(HIDE);
        }


        /**
         * 没有更多需要加载的时候显示End
         */
        public void setLoadMoreEnd() {
            setLoadMoreState(END);
        }

        /**
         * 加载更多出错
         */
        public void setLoadMoreError() {
            setLoadMoreState(ERROR);
        }

        /**
         * 更新loadMore组件的状态
         */
        private void setLoadMoreState(State state) {
            switch (state) {
                case HIDE:
                case IDLE:
                    break;
                case LOADING:
                    break;
                case END:
                    break;
                case ERROR:
                    break;
            }
        }
    }


    static class SimpleViewHolder extends BaseViewHolder {

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindViewHolder(int position) {

        }

        @Override
        public void onItemClick(View view, int position) {

        }
    }
}