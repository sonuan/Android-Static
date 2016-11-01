package com.androidstatic.lib.basis;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.androidstatic.lib.basis.core.AsyncTask;
import com.androidstatic.lib.basis.core.ThreadExecutor;
import com.androidstatic.lib.widget.statusview.LoadingAndRetryManager;
import com.androidstatic.lib.widget.statusview.OnLoadingAndRetryListener;

import butterknife.ButterKnife;


/**
 * Activity扩展类，统一风格。
 * 
 * @author kycq
 * 
 */
public abstract class ExpandActivity extends Activity {
	/** 管理多种状态，加载中视图、错误视图、空数据视图、网络异常视图、内容视图*/
	private LoadingAndRetryManager mLoadingAndRetryManager;

	/** ExpandActivity任务线程池 */
	private static ThreadExecutor mThreadExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
	/** ExpandActivity生命周期相关的异步任务池 */
	private SparseArray<ActivityTask> mActivityTasks = new SparseArray<ActivityTask>();

	/**
	 * 配置任务线程池
	 * 
	 * @param threadExecutor
	 *            任务线程池
	 */
	public static void setThreadExecutor(ThreadExecutor threadExecutor) {
		mThreadExecutor = threadExecutor;
	}

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initCreate(savedInstanceState);
		ButterKnife.bind(this);
	}

	/**
	 * {@linkplain Activity#onCreate(Bundle) <span
	 * style="color:#0000FF">onCreate(Bundle)</span>}扩展
	 * 
	 * @param savedInstanceState
	 *            保存信息
	 */
	protected void initCreate(Bundle savedInstanceState) {
		initConfig(savedInstanceState);
		initMultipleStatusView();
		initViews();
		initData(savedInstanceState);
	}

	/**
	 * 初始化配置方法，在{@linkplain ExpandActivity#initViews() <span
	 * style="color:#0000FF">initViews()</span>}之前调用
	 * 
	 * @param savedInstanceState
	 *            保存信息
	 */
	public void initConfig(Bundle savedInstanceState) {
	}

	/**
	 * 初始化Views方法
	 */
	public void initViews() {

	}

	/**
	 * 初始化数据方法
	 * 
	 * @param savedInstanceState
	 *            保存信息
	 */
	public void initData(Bundle savedInstanceState) {
	}

	private void initMultipleStatusView() {
		if (mLoadingAndRetryManager == null) {
			mLoadingAndRetryManager = new LoadingAndRetryManager(this, new OnLoadingAndRetryListener() {
				@Override
				public void setRetryEvent(View retryView) {

				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		for (int index = 0; index < mActivityTasks.size(); index++) {
			mActivityTasks.valueAt(index).cancel();
		}
		mActivityTasks.clear();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (mLoadingAndRetryManager != null && mLoadingAndRetryManager.isLoading()) {
			hideLoading();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * 显示默认的进度控件
	 */
	public final void showLoading() {
		if (mLoadingAndRetryManager != null) {
			mLoadingAndRetryManager.showLoading();
		}
	}

	/**
	 * 隐藏默认的进度控件
	 */
	public final void hideLoading() {
		if (mLoadingAndRetryManager != null) {
			mLoadingAndRetryManager.showContent();
		}
	}



	/**
	 * 运行生命周期相关的异步任务
	 * 
	 * @param what
	 *            异步任务标志
	 * @param params
	 *            异步任务运行参数
	 * @return 异步任务实例
	 */
	protected final ActivityTask execute(int what, Object... params) {
		return execute(mThreadExecutor, new ActivityTask(this, what), params);
	}

	/**
	 * 运行生命周期相关的异步任务
	 * 
	 * @param threadExecutor
	 *            任务线程池
	 * @param what
	 *            异步任务标志
	 * @param params
	 *            异步任务运行参数
	 * @return 异步任务实例
	 */
	protected final ActivityTask execute(ThreadExecutor threadExecutor,
			int what, Object... params) {
		return execute(threadExecutor, new ActivityTask(this, what), params);
	}

	/**
	 * 运行生命周期相关的异步任务
	 * 
	 * @param activityTask
	 *            异步任务实例
	 * @param params
	 *            异步任务运行参数
	 * @return 异步任务实例
	 */
	protected final ActivityTask execute(ActivityTask activityTask,
			Object... params) {
		return execute(mThreadExecutor, activityTask, params);
	}

	/**
	 * 运行生命周期相关的异步任务
	 * 
	 * @param threadExecutor
	 *            任务线程池
	 * @param activityTask
	 *            异步任务实例
	 * @param params
	 *            异步任务运行参数
	 * @return 异步任务实例
	 */
	protected final ActivityTask execute(ThreadExecutor threadExecutor,
			ActivityTask activityTask, Object... params) {
		if (mActivityTasks.get(activityTask.mWhat) != null) {
			mActivityTasks.get(activityTask.mWhat).cancel();
		}

		activityTask.executeOnExecutor(threadExecutor, params);
		mActivityTasks.append(activityTask.mWhat, activityTask);
		return activityTask;
	}

	/**
	 * 取消指定的异步任务
	 * 
	 * @param what
	 *            异步任务标志
	 */
	protected final void cancel(int what) {
		ActivityTask activityTask = mActivityTasks.get(what);
		if (activityTask != null) {
			activityTask.cancel();
			mActivityTasks.remove(what);
		}
	}

	/**
	 * 获取异步任务当前状态
	 * 
	 * @param what
	 *            异步任务标志
	 * @return true 任务已取消
	 */
	protected final boolean isCancelled(int what) {
		ActivityTask activityTask = mActivityTasks.get(what);
		if (activityTask != null) {
			return activityTask.isCancelled();
		}
		return true;
	}

	/**
	 * 异步任务后台处理
	 * 
	 * @param what
	 *            异步任务标志
	 * @param params
	 *            异步任务运行参数
	 * @return 异步任务运行结果
	 */
	protected Object onTaskBackground(int what, Object... params) {
		return null;
	}

	/**
	 * 异步任务结果
	 * 
	 * @param what
	 *            异步任务标志
	 * @param result
	 *            异步任务运行结果
	 */
	protected void onTaskResult(int what, Object result) {
	}

	/**
	 * 异步任务结果取消
	 * 
	 * @param what
	 *            异步任务标志
	 * @param result
	 *            异步任务运行结果
	 */
	protected void onTaskCancelled(int what, Object result) {
	}

	/**
	 * 与Activity生命周期相关的异步任务类
	 * 
	 * @author kycq
	 *
	 */
	public static class ActivityTask extends AsyncTask<Object, Object, Object> {
		/** 生命周期相关的Activity */
		private ExpandActivity mActivity;
		/** 异步任务标志 */
		private int mWhat;

		/**
		 * 构造方法
		 * 
		 * @param activity
		 *            生命周期相关的Activity
		 * @param what
		 *            异步任务标志
		 */
		public ActivityTask(ExpandActivity activity, int what) {
			mActivity = activity;
			mWhat = what;
		}

		@Override
		protected Object doInBackground(Object... params) {
			return mActivity.onTaskBackground(mWhat, params);
		}

		@Override
		protected void onResult(Object result) {
			mActivity.onTaskResult(mWhat, result);
			remove();
		}

		@Override
		protected void onCancelled(Object result) {
			mActivity.onTaskCancelled(mWhat, result);
			remove();
		}

		protected void remove() {
			mActivity.mActivityTasks.delete(mWhat);
		}
	}

}
