package com.androidstatic.lib.widget.statusview;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

/**
 * Created by zhy on 15/8/26.
 */
public class LoadingAndRetryLayout extends FrameLayout implements View.OnTouchListener {
    private View mLoadingView;
    private View mRetryView;
    private View mContentView;
    private View mEmptyView;
    private LayoutInflater mInflater;

    private static final String TAG = LoadingAndRetryLayout.class.getSimpleName();
    private ViewStub mLoadingViewStub;
    private ViewStub mEmptyViewStub;
    private ViewStub mRetryViewStub;


    public LoadingAndRetryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
    }


    public LoadingAndRetryLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadingAndRetryLayout(Context context) {
        this(context, null);
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public void showLoading() {
        if (mLoadingView == null && mLoadingViewStub != null) {
            mLoadingView = mLoadingViewStub.inflate();
        }
        if (isMainThread()) {
            showView(mLoadingView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mLoadingView);
                }
            });
        }
    }

    public void showRetry() {
        if (mRetryView == null && mRetryViewStub != null) {
            mRetryView = mRetryViewStub.inflate();
        }
        if (isMainThread()) {
            showView(mRetryView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mRetryView);
                }
            });
        }

    }

    public void showContent() {
        if (isMainThread()) {
            showView(mContentView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mContentView);
                }
            });
        }
    }

    public void showEmpty() {
        if (mEmptyView == null && mRetryViewStub != null) {
            mEmptyView = mEmptyViewStub.inflate();
        }
        if (isMainThread()) {
            showView(mEmptyView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mEmptyView);
                }
            });
        }
    }


    private void showView(View view) {
        if (view == null) return;
        view.setOnTouchListener(this);
        if (view == mLoadingView) {
            mLoadingView.setVisibility(View.VISIBLE);
            //if (mContentView != null) mContentView.setVisibility(View.GONE);
            if (mRetryView != null) mRetryView.setVisibility(View.GONE);
            if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
        } else if (view == mRetryView) {
            mRetryView.setVisibility(View.VISIBLE);
            //if (mContentView != null) mContentView.setVisibility(View.GONE);
            if (mLoadingView != null) mLoadingView.setVisibility(View.GONE);
            if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
        } else if (view == mContentView) {
            mContentView.setVisibility(View.VISIBLE);
            if (mLoadingView != null) mLoadingView.setVisibility(View.GONE);
            if (mRetryView != null) mRetryView.setVisibility(View.GONE);
            if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
        } else if (view == mEmptyView) {
            mEmptyView.setVisibility(View.VISIBLE);
            //if (mContentView != null) mContentView.setVisibility(View.GONE);
            if (mLoadingView != null) mLoadingView.setVisibility(View.GONE);
            if (mRetryView != null) mRetryView.setVisibility(View.GONE);
        }


    }

    public View setContentView(int layoutId) {
        return setContentView(mInflater.inflate(layoutId, this, false));
    }

    public View setLoadingView(int layoutId) {
        mLoadingViewStub = new ViewStub(getContext());
        mLoadingViewStub.setLayoutResource(layoutId);
        return setLoadingView(mLoadingViewStub);
    }

    public View setEmptyView(int layoutId) {
        mEmptyViewStub = new ViewStub(getContext());
        mEmptyViewStub.setLayoutResource(layoutId);
        return setEmptyView(mEmptyViewStub);
    }

    public View setRetryView(int layoutId) {
        mRetryViewStub = new ViewStub(getContext());
        mRetryViewStub.setLayoutResource(layoutId);
        return setRetryView(mRetryViewStub);
    }

    public ViewStub setLoadingView(ViewStub viewStub) {
        ViewStub loadingView = mLoadingViewStub;
        if (loadingView != null) {
            Log.w(TAG, "you have already set a loading viewstub and would be instead of this new one.");
        }
        removeView(loadingView);
        addView(viewStub);
        mLoadingViewStub = viewStub;
        return mLoadingViewStub;
    }

    public View setLoadingView(View view) {
        View loadingView = mLoadingView;
        if (loadingView != null) {
            Log.w(TAG, "you have already set a loading view and would be instead of this new one.");
        }
        removeView(loadingView);
        addView(view);
        mLoadingView = view;
        return mLoadingView;
    }

    public ViewStub setEmptyView(ViewStub view) {
        View emptyView = mEmptyViewStub;
        if (emptyView != null) {
            Log.w(TAG, "you have already set a empty viewstub and would be instead of this new one.");
        }
        removeView(emptyView);
        addView(view);
        mEmptyViewStub = view;
        return mEmptyViewStub;
    }

    public View setEmptyView(View view) {
        View emptyView = mEmptyView;
        if (emptyView != null) {
            Log.w(TAG, "you have already set a empty view and would be instead of this new one.");
        }
        removeView(emptyView);
        addView(view);
        mEmptyView = view;
        return mEmptyView;
    }

    public ViewStub setRetryView(ViewStub view) {
        View retryView = mRetryViewStub;
        if (retryView != null) {
            Log.w(TAG, "you have already set a retry viewstub and would be instead of this new one.");
        }
        removeView(retryView);
        addView(view);
        mRetryViewStub = view;
        return mRetryViewStub;

    }

    public View setRetryView(View view) {
        View retryView = mRetryView;
        if (retryView != null) {
            Log.w(TAG, "you have already set a retry view and would be instead of this new one.");
        }
        removeView(retryView);
        addView(view);
        mRetryView = view;
        return mRetryView;

    }

    public View setContentView(View view) {
        View contentView = mContentView;
        if (contentView != null) {
            Log.w(TAG, "you have already set a content view and would be instead of this new one.");
        }
        removeView(contentView);
        addView(view);
        mContentView = view;
        return mContentView;
    }

    public View getRetryView() {
        return mRetryView;
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public View getContentView() {
        return mContentView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}