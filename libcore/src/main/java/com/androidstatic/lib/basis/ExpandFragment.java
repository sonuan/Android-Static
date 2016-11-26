package com.androidstatic.lib.basis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.androidstatic.lib.basis.core.AsyncTask;
import com.androidstatic.lib.basis.core.ThreadExecutor;
import com.androidstatic.lib.widget.statusview.LoadingAndRetryManager;
import com.androidstatic.lib.widget.statusview.OnLoadingAndRetryListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import butterknife.ButterKnife;

/**
 * Fragment扩展类，统一风格。
 * 
 * @author kycq
 * 
 */
public abstract class ExpandFragment extends Fragment {
	/** 调用结果:正常 */
	public static final int RESULT_OK = -1;
	/** 调用结果:取消 */
	public static final int RESULT_CANCELED = 0;

	/** Views实例工具 */
	private LayoutInflater mInflater;
	/** 根View */
	private ViewGroup mRootView;
	/***/
	private ViewGroup mDecorView;
	/** 主View */
	private View mContentView;

	/** 请求编码 */
	private int mRequestCode = RESULT_CANCELED;
	/** 结果编码 */
	private int mResultCode = RESULT_CANCELED;
	/** 结果返回数据 */
	private Intent mResultData;

	/** 管理多种状态，加载中视图、错误视图、空数据视图、网络异常视图、内容视图*/
	private LoadingAndRetryManager mLoadingAndRetryManager;

	/** Activity任务线程池 */
	private static ThreadExecutor mThreadExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
	/** Activity生命周期相关的异步任务池 */
	private SparseArray<ActivityTask> mActivityTasks = new SparseArray<ActivityTask>();

	/** 子Fragment管理实例 */
	protected FragmentManager mChildFManager;
	/** Fragment堆栈 */
	private HashMap<Integer, ArrayList<String>> mStackFragments;
	/** 当前展示Fragments */
	private HashMap<Integer, String> mCurrentFragments;
	/** Fragment附属资源ID */
	private int mResId = -1;

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
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			mRequestCode = bundle.getInt("ExpandFragment_requestCode",
					RESULT_CANCELED);
		}
	}

	@Override
	public final View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		mRootView = container;
		mDecorView = new FrameLayout(getActivity());
		ButterKnife.bind(this, mDecorView);
		return mDecorView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@SuppressLint("UseSparseArrays")
	@SuppressWarnings("unchecked")
	@Override
	public final void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mChildFManager = getChildFragmentManager();
		if (savedInstanceState != null) {
			mStackFragments = (HashMap<Integer, ArrayList<String>>) savedInstanceState
					.get("ExpandFragment_mStackFragments");
			mCurrentFragments = (HashMap<Integer, String>) savedInstanceState
					.get("ExpandFragment_mCurrentFragments");
		}
		if (mStackFragments == null) {
			mStackFragments = new HashMap<Integer, ArrayList<String>>();
		}
		if (mCurrentFragments == null) {
			mCurrentFragments = new HashMap<Integer, String>();
		}

		for (Entry<Integer, String> entry : mCurrentFragments.entrySet()) {
			int resId = entry.getKey();
			String tag = entry.getValue();
			changeFragment(resId, findFragmentByTag(resId, tag), tag);
		}

		Object contentView = onCreateView();
		if (contentView == null) {
			throw new NullPointerException("onCreateView() is null, please set LayoutResID or View to return.");
		}
		if (!(contentView instanceof Integer) && !(contentView instanceof View)) {
			throw new IllegalArgumentException("onCreateView() value must be LayoutResID or View.");
		} else if (contentView instanceof Integer) {
			setContentView((Integer) contentView);
		} else if (contentView instanceof View) {
			setContentView((View) contentView);
		}

		initCreate(savedInstanceState);
		
	}

	/**
	 * {@linkplain Fragment#onCreate(Bundle) <span
	 * style="color:#0000FF">onCreate(Bundle)</span>}扩展
	 * 
	 * @param savedInstanceState
	 *            保存信息
	 */
	protected void initCreate(Bundle savedInstanceState) {
		initConfig(savedInstanceState);
		initViews();
		initMultipleStatusView();
		initData(savedInstanceState);
	}

	/**
	 * 初始化配置方法，在{@linkplain ExpandFragment#initViews() <span
	 * style="color:#0000FF">initViews()</span>}之前调用
	 * 
	 * @param savedInstanceState
	 *            保存信息
	 */
	public void initConfig(Bundle savedInstanceState) {
	}

	/**
	 * 获取ContentView
	 * @return contentView, must be LayoutResID or View.
	 */
	protected abstract Object onCreateView();

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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("ExpandFragment_mStackFragments",
				mStackFragments);
		outState.putSerializable("ExpandFragment_mCurrentFragments",
				mCurrentFragments);
	}

	/**
	 * 获取Fragment放置的根视图
	 * 
	 * @return Fragment根View
	 */
	public ViewGroup getRootView() {
		return mRootView;
	}

	/**
	 * 统一风格，同{@linkplain Activity#setContentView(int) <span
	 * style="color:#0000FF">Activity.setContentView(int)</span>}
	 * 
	 * @param layoutResID
	 *            主View资源ID
	 */
	private void setContentView(int layoutResID) {
		mContentView = mInflater.inflate(layoutResID, mDecorView, false);
		mDecorView.addView(mContentView);
	}

	private void setContentView(View view) {
		mContentView = view;
		mDecorView.addView(mContentView);
	}

	/**
	 * 获取Fragment展示内容
	 * 
	 * @return Fragment展示内容
	 */
	public View getContentView() {
		return mContentView;
	}

	/**
	 * 统一风格，同{@linkplain Activity#findViewById(int) <span
	 * style="color:#0000FF">Activity.findViewById(int)</span>}
	 * 
	 * @param id
	 *            view资源ID
	 * @return view实例
	 */
	public View findViewById(int id) {
		return mContentView.findViewById(id);
	}

	/**
	 * 统一风格，同{@linkplain Activity#startActivity(Intent) <span
	 * style="color:#0000FF">Activity.startActivity(Intent)</span>}
	 *
	 * @param intent
	 *            传递数据
	 */
	public final void startFragment(Intent intent) {
		try {
			String className = intent.getComponent().getClassName();
			if (getActivity() instanceof ExpandFragmentActivity) {
				ExpandFragmentActivity activity = (ExpandFragmentActivity) getActivity();
				ExpandFragment fragment = (ExpandFragment) Class.forName(
						className).newInstance();
				fragment.setArguments(intent.getExtras());
				activity.putFragment(fragment, fragment.getClass().getName());
			} else {
				throw new IllegalArgumentException(
						"Intent ComponentName must be a ExpandFragmentActivity Class ");
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 统一风格，同{@linkplain Activity#startActivityForResult(Intent, int) <span
	 * style="color:#0000FF">Activity.startActivityForResult(Intent,
	 * int)</span>}
	 *
	 * @param intent
	 *            传递数据
	 * @param requestCode
	 *            请求编码
	 */
	public final void startFragmentForResult(Intent intent, int requestCode) {
		String code = intent.getStringExtra("ExpandFragment_requestCode");
		if (code != null) {
			throw new IllegalArgumentException(
					"Intent params can't named ExpandFragment_requestCode");
		}
		intent.putExtra("ExpandFragment_requestCode", requestCode);
		startFragment(intent);
	}

	/**
	 * 统一风格，同<span
	 * style="color:#0000FF">Activity.onActivityResult(int,int,Bundle)</span>
	 * 
	 * @param requestCode
	 *            请求编码
	 * @param resultCode
	 *            结果编码
	 * @param data
	 *            返回数据
	 */
	public void onFragmentResult(int requestCode, int resultCode, Intent data) {
	}

	/**
	 * 统一风格，同{@linkplain Activity#setResult(int) <span
	 * style="color:#0000FF">Activity.setResult(int)</span>}
	 * 
	 * @param resultCode
	 *            结果编码
	 */
	public final void setResult(int resultCode) {
		mResultCode = resultCode;
	}

	/**
	 * 统一风格，同{@linkplain Activity#setResult(int, Intent) <span
	 * style="color:#0000FF">Activity.setResult(int, Intent)</span>}
	 * 
	 * @param resultCode
	 *            结果编码
	 * @param data
	 *            返回数据
	 */
	public final void setResult(int resultCode, Intent data) {
		mResultCode = resultCode;
		mResultData = data;
	}

	/**
	 * 统一风格，同{@linkplain Activity#finish() <span
	 * style="color:#0000FF">Activity.finish()</span>}
	 */
	public final void finish() {
		ExpandFragment fragment = null;

		ExpandFragment parentFragment = (ExpandFragment) getParentFragment();
		if (parentFragment != null) {
			fragment = parentFragment.popFragment();
			if (fragment != null) {
				fragment.onFragmentResult(mRequestCode, mResultCode,
						mResultData);
			}
		} else {
			ExpandFragmentActivity activity = (ExpandFragmentActivity) getActivity();
			fragment = activity.popFragment();
			if (fragment != null) {
				fragment.onFragmentResult(mRequestCode, mResultCode,
						mResultData);
			}
		}
	}

	/**
	 * 统一风格，同{@linkplain Activity#onBackPressed() <span
	 * style="color:#0000FF">Activity.onBackPressed()</span>}
	 */
	public void onBackPressed() {
		if (mLoadingAndRetryManager != null && mLoadingAndRetryManager.isLoading()) {
			hideLoading();
			return;
		}

		ArrayList<String> stack = mStackFragments.get(mResId);
		if (stack == null || stack.size() == 0) {
			finish();
		} else {
			String top = stack.get(stack.size() - 1);
			ExpandFragment fragment = findFragmentByTag(mResId, top);
			fragment.onBackPressed();
		}
	}

	@Override
	public void onDestroy() {
		for (int index = 0; index < mActivityTasks.size(); index++) {
			mActivityTasks.valueAt(index).cancel();
		}
		mActivityTasks.clear();
		super.onDestroy();
	}

	/**
	 * 获取默认堆栈数量
	 * 
	 * @return 堆栈数量
	 */
	public int size() {
		return size(mResId);
	}

	/**
	 * 获取指定资源ID堆栈数量
	 *
	 * @param resId
	 *            资源ID
	 * @return 堆栈数量
	 */
	public int size(int resId) {
		ArrayList<String> stack = mStackFragments.get(resId);
		if (stack == null) {
			return 0;
		}
		return stack.size();
	}

	/**
	 * 设置默认资源ID
	 * 
	 * @param resId
	 *            资源ID
	 */
	public final void setFragmentViewId(int resId) {
		mResId = resId;
	}

	/**
	 * 获取默认资源ID的ExpandFragment
	 * 
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment findFragmentById() {
		return findFragmentById(mResId);
	}

	/**
	 * 获取指定资源ID的ExpandFragment
	 * 
	 * @param resId
	 *            资源ID
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment findFragmentById(int resId) {
		return (ExpandFragment) mChildFManager.findFragmentById(resId);
	}

	/**
	 * 获取默认资源ID的ExpandFragment
	 * 
	 * @param clazz
	 *            ExpandFragment类型
	 * @param <T>
	 *            ExpandFragment类型
	 * @return ExpandFragment实例
	 */
	@SuppressWarnings("unchecked")
	public final <T> T findFragmentByClass(Class<T> clazz) {
		return (T) findFragmentByTag(mResId, clazz.getName());
	}

	/**
	 * 获取指定资源ID的ExpandFragment
	 * 
	 * @param resId
	 *            资源ID
	 * @param clazz
	 *            ExpandFragment类型
	 * @param <T>
	 *            ExpandFragment类型
	 * @return ExpandFragment实例
	 */
	@SuppressWarnings("unchecked")
	public final <T> T findFragmentByClass(int resId, Class<T> clazz) {
		return (T) findFragmentByTag(resId, clazz.getName());
	}

	/**
	 * 获取默认资源ID的ExpandFragment
	 * 
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment findFragmentByTag(String tag) {
		return findFragmentByTag(mResId, tag);
	}

	/**
	 * 获取指定资源ID的ExpandFragment
	 * 
	 * @param resId
	 *            资源ID
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment findFragmentByTag(int resId, String tag) {
		return (ExpandFragment) mChildFManager.findFragmentByTag(resId + "@"
				+ tag);
	}

	/**
	 * 获取默认资源ID的当前展示ExpandFragment
	 * 
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment getCurrentFragment() {
		return getCurrentFragment(mResId);
	}

	/**
	 * 获取指定资源ID的当前展示ExpandFragment
	 * 
	 * @param resId
	 *            资源ID
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment getCurrentFragment(int resId) {
		return findFragmentByTag(resId, mCurrentFragments.get(resId));
	}

	/**
	 * 配置动画效果
	 * 
	 * @param resId
	 *            资源ID
	 * @param enterFragment
	 *            进入的ExpandFragment
	 * @param exitFragment
	 *            退出的ExpandFragment
	 * @param transaction
	 *            操作实例
	 * @param remove
	 *            true 退栈操作
	 *            <p>
	 *            false 进栈操作
	 */
	public void onCreateAnimation(int resId, ExpandFragment enterFragment,
			ExpandFragment exitFragment, FragmentTransaction transaction,
			boolean remove) {
	}

	/**
	 * 过期的动画配置
	 * 
	 * @param transit
	 *            回转
	 * @param enter
	 *            true 进入
	 * @param nextAnim
	 *            下个动画
	 * @return 动画
	 */
	@Deprecated
	@Override
	public final Animation onCreateAnimation(int transit, boolean enter,
			int nextAnim) {
		return super.onCreateAnimation(transit, enter, nextAnim);
	}

	/**
	 * 替换默认资源ID当前展示ExpandFragment
	 * 
	 * @param fragment
	 *            ExpandFragment实例
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment replaceFragment(ExpandFragment fragment) {
		return replaceFragment(mResId, fragment, fragment.getClass().getName());
	}

	/**
	 * 替换指定资源ID当前展示ExpandFragment
	 * 
	 * @param resId
	 *            资源ID
	 * @param fragment
	 *            ExpandFragment实例
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment replaceFragment(int resId,
			ExpandFragment fragment) {
		return replaceFragment(resId, fragment, fragment.getClass().getName());
	}

	/**
	 * 替换默认资源ID当前展示ExpandFragment
	 * 
	 * @param fragment
	 *            ExpandFragment实例
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment replaceFragment(ExpandFragment fragment,
			String tag) {
		return replaceFragment(mResId, fragment, tag);
	}

	/**
	 * 替换指定资源ID当前展示ExpandFragment
	 * 
	 * @param resId
	 *            资源ID
	 * @param fragment
	 *            ExpandFragment实例
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment replaceFragment(int resId,
			ExpandFragment fragment, String tag) {
		FragmentTransaction ft = mChildFManager.beginTransaction();

		ExpandFragment exitFragment = null;
		String exitTag = mCurrentFragments.get(resId);
		if (exitTag != null) {
			exitFragment = findFragmentByTag(resId, exitTag);
		}
		onCreateAnimation(resId, fragment, exitFragment, ft, false);
		mCurrentFragments.put(resId, tag);

		ft.replace(resId, fragment, resId + "@" + tag);
		ft.commitAllowingStateLoss();

		return fragment;
	}

	/**
	 * 变更默认资源ID当前展示ExpandFragment
	 *
	 * @param fragment
	 *            ExpandFragment实例
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment changeFragment(ExpandFragment fragment) {
		return changeFragment(mResId, fragment, fragment.getClass().getName());
	}

	/**
	 * 变更指定资源ID当前展示ExpandFragment
	 *
	 * @param resId
	 *            资源ID
	 * @param fragment
	 *            ExpandFragment实例
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment changeFragment(int resId,
			ExpandFragment fragment) {
		return changeFragment(resId, fragment, fragment.getClass().getName());
	}

	/**
	 * 变更默认资源ID当前展示ExpandFragment
	 *
	 * @param fragment
	 *            ExpandFragment实例
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment changeFragment(ExpandFragment fragment,
			String tag) {
		return changeFragment(mResId, fragment, tag);
	}

	/**
	 * 变更指定资源ID当前展示ExpandFragment
	 *
	 * @param resId
	 *            资源ID
	 * @param fragment
	 *            ExpandFragment实例
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment changeFragment(int resId,
			ExpandFragment fragment, String tag) {
		FragmentTransaction ft = mChildFManager.beginTransaction();
		ExpandFragment exitFragment = null;
		String exitTag = mCurrentFragments.get(resId);
		if (exitTag != null) {
			exitFragment = findFragmentByTag(resId, exitTag);
		}
		onCreateAnimation(resId, fragment, exitFragment, ft, false);
		mCurrentFragments.put(resId, tag);

		if (mChildFManager.getFragments() != null) {
			for (Fragment f : mChildFManager.getFragments()) {
				if (f != null && f.getId() == resId) {
					ft.hide(f);
				}
			}
		}

		if (findFragmentByTag(resId, tag) == null) {
			ft.add(resId, fragment, resId + "@" + tag);
		} else {
			fragment = findFragmentByTag(resId, tag);
		}
		ft.show(fragment);
		ft.commitAllowingStateLoss();

		return fragment;
	}

	/**
	 * 添加默认资源ID的堆栈的ExpandFragment
	 *
	 * @param fragment
	 *            ExpandFragment实例
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment putFragment(ExpandFragment fragment) {
		return putFragment(mResId, fragment, fragment.getClass().getName());
	}

	/**
	 * 添加指定资源ID的堆栈的ExpandFragment
	 *
	 * @param resId
	 *            资源ID
	 * @param fragment
	 *            ExpandFragment实例
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment putFragment(int resId, ExpandFragment fragment) {
		return putFragment(resId, fragment, fragment.getClass().getName());
	}

	/**
	 * 添加默认资源ID的堆栈的ExpandFragment
	 * 
	 * @param fragment
	 *            ExpandFragment实例
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment putFragment(ExpandFragment fragment, String tag) {
		return putFragment(mResId, fragment, tag);
	}

	/**
	 * 添加指定资源ID的堆栈的ExpandFragment
	 *
	 * @param resId
	 *            资源ID
	 * @param fragment
	 *            ExpandFragment实例
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment putFragment(int resId, ExpandFragment fragment,
			String tag) {
		ArrayList<String> stack = mStackFragments.get(resId);
		if (stack == null) {
			stack = new ArrayList<String>();
			mStackFragments.put(resId, stack);
		}
		tag = tag + "#" + stack.size();

		FragmentTransaction ft = mChildFManager.beginTransaction();
		ExpandFragment exitFragment = null;
		String exitTag = mCurrentFragments.get(resId);
		if (exitTag != null) {
			exitFragment = findFragmentByTag(resId, exitTag);
		}
		onCreateAnimation(resId, fragment, exitFragment, ft, false);
		mCurrentFragments.put(resId, tag);

		if (mChildFManager.getFragments() != null) {
			for (Fragment f : mChildFManager.getFragments()) {
				if (f != null && f.getId() == resId) {
					ft.hide(f);
				}
			}
		}

		ft.add(resId, fragment, resId + "@" + tag);
		ft.commitAllowingStateLoss();

		stack.add(tag);

		return fragment;
	}

	/**
	 * 弹出默认资源ID的堆栈的ExpandFragment
	 * 
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment popFragment() {
		return popFragment(mResId);
	}

	/**
	 * 添加指定资源ID的堆栈的展示ExpandFragment
	 *
	 * @param resId
	 *            资源ID
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment popFragment(int resId) {
		ArrayList<String> stack = mStackFragments.get(resId);
		if (stack == null || stack.size() <= 1) {
			finish();
			return null;
		} else {
			String exitTag = stack.get(stack.size() - 1);
			String enterTag = stack.get(stack.size() - 2);

			FragmentTransaction ft = mChildFManager.beginTransaction();
			ExpandFragment enterFragment = findFragmentByTag(resId, enterTag);
			ExpandFragment exitFragment = findFragmentByTag(resId, exitTag);
			onCreateAnimation(resId, enterFragment, exitFragment, ft, true);

			ft.remove(exitFragment);
			ft.show(enterFragment);
			ft.commitAllowingStateLoss();

			mCurrentFragments.put(resId, enterTag);
			stack.remove(stack.size() - 1);

			return enterFragment;
		}
	}

	/**
	 * 移除默认资源ID的ExpandFragment
	 * 
	 * @param fragment
	 *            ExpandFragment实例
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment removeFragment(ExpandFragment fragment) {
		return removeFragment(mResId, fragment, fragment.getClass().getName());
	}

	/**
	 * 移除指定资源ID的ExpandFragment
	 *
	 * @param resId
	 *            资源ID
	 * @param fragment
	 *            ExpandFragment实例
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment removeFragment(int resId,
			ExpandFragment fragment) {
		return removeFragment(resId, fragment, fragment.getClass().getName());
	}

	/**
	 * 移除默认资源ID的ExpandFragment
	 * 
	 * @param fragment
	 *            ExpandFragment实例
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment removeFragment(ExpandFragment fragment,
			String tag) {
		return removeFragment(mResId, fragment, tag);
	}

	/**
	 * 移除指定资源ID的ExpandFragment
	 *
	 * @param resId
	 *            资源ID
	 * @param fragment
	 *            ExpandFragment实例
	 * @param tag
	 *            ExpandFragment标签
	 * @return ExpandFragment实例
	 */
	public final ExpandFragment removeFragment(int resId,
			ExpandFragment fragment, String tag) {
		FragmentTransaction ft = mChildFManager.beginTransaction();
		ft.remove(fragment);
		ft.commitAllowingStateLoss();
		return fragment;
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
	 * 与ExpandFragment生命周期相关的异步任务类
	 * 
	 * @author kycq
	 *
	 */
	public static class ActivityTask extends AsyncTask<Object, Object, Object> {
		/** 生命周期相关的ExpandFragment */
		private ExpandFragment mFragment;
		/** 异步任务标志 */
		private int mWhat;

		/**
		 * 构造方法
		 * 
		 * @param fragment
		 *            生命周期相关的ExpandFragment
		 * @param what
		 *            异步任务标志
		 */
		public ActivityTask(ExpandFragment fragment, int what) {
			mFragment = fragment;
			mWhat = what;
		}

		@Override
		protected Object doInBackground(Object... params) {
			return mFragment.onTaskBackground(mWhat, params);
		}

		@Override
		protected void onResult(Object result) {
			mFragment.onTaskResult(mWhat, result);
			remove();
		}

		@Override
		protected void onCancelled(Object result) {
			mFragment.onTaskCancelled(mWhat, result);
			remove();
		}

		protected void remove() {
			mFragment.mActivityTasks.delete(mWhat);
		}
	}
}
