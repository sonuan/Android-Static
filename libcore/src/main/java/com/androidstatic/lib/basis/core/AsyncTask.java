package com.androidstatic.lib.basis.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 异步任务
 * <p>
 * 异步任务使用方式与系统的AsyncTask保持一致，并对其进行各方面的优化操作，提高调用效率。
 * 
 * @author kycq
 *
 * @param <Param>
 *            异步任务运行参数
 * @param <Progress>
 *            异步任务进度参数
 * @param <Result>
 *            异步任务运行结果
 */
public abstract class AsyncTask<Param, Progress, Result> implements
		Comparable<AsyncTask<Param, Progress, Result>> {
	/** 异步任务开始运行消息 */
	private static final int MESSAGE_PUBLISH_START = 0x1;
	/** 异步任务运行进度消息 */
	private static final int MESSAGE_PUBLISH_PROGRESS = 0x2;
	/** 异步任务运行结果消息 */
	private static final int MESSAGE_PUBLISH_RESULT = 0x3;

	/** CPU数量 */
	private static final int CPU_COUNT = Runtime.getRuntime()
			.availableProcessors();
	/** 核心线程数 */
	private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
	/** 最大线程数 */
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	/** 线程保留时间 */
	private static final int KEEP_ALIVE = 60;

	/** 公用线程池 */
	public static final ThreadExecutor THREAD_POOL_EXECUTOR = new ThreadExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS);
	/** 默认异步线程池 */
	private static volatile ThreadExecutor mDefaultExecutor = THREAD_POOL_EXECUTOR;
	/** 消息处理工具 */
	private static final InternalHandler mHandler = new InternalHandler();

	/** 异步任务运行状态 */
	private volatile Status mStatus = Status.PENDING;
	/** 异步任务取消状态 */
	private final AtomicBoolean mCancelled = new AtomicBoolean();
	/** 异步任务优先级别 */
	private Priority mPriority = Priority.NORMAL;
	/** 异步任务线程池序列码 */
	private int mSequenceCode;

	/** 异步任务运行参数 */
	Param[] mParams;

	/** 异步任务标签 */
	private Object mTag;

	final void setSequenceCode(int sequenceCode) {
		mSequenceCode = sequenceCode;
	}

	/**
	 * 获取异步任务标签
	 * 
	 * @return 异步任务标签
	 */
	public final Object getTag() {
		return mTag;
	}

	/**
	 * 设置异步任务标签
	 * 
	 * @param tag
	 *            异步任务标签
	 */
	public final void setTag(Object tag) {
		mTag = tag;
	}

	/**
	 * 获取异步任务优先级别
	 * 
	 * @return 异步任务优先级别
	 */
	public final Priority getPriority() {
		return mPriority;
	}

	/**
	 * 设置异步任务优先级别
	 * 
	 * @param priority
	 *            异步任务优先级别
	 */
	public final void setPriority(Priority priority) {
		mPriority = priority;
	}

	/**
	 * 使用默认线程池运行异步任务
	 * 
	 * @param params
	 *            异步任务运行参数
	 */
	public final void execute(Param... params) {
		executeOnExecutor(mDefaultExecutor, params);
	}

	/**
	 * 使用自定义线程池运行异步任务
	 * 
	 * @param exec
	 *            自定义线程池
	 * @param params
	 *            异步任务运行参数
	 * @return 异步任务实例
	 */
	public final AsyncTask<Param, Progress, Result> executeOnExecutor(
			ThreadExecutor exec, Param... params) {
		if (mStatus != Status.PENDING) {
			switch (mStatus) {
			case RUNNING:
				throw new IllegalStateException("Cannot execute task:"
						+ " the task is already running.");
			case FINISHED:
				throw new IllegalStateException("Cannot execute task:"
						+ " the task has already been executed "
						+ "(a task can be executed only once)");
			default:
				break;
			}
		}

		mStatus = Status.RUNNING;

		mParams = params;
		onPreExecute();
		exec.execute(this);

		return this;
	}

	/**
	 * 异步任务运行前准备
	 */
	protected void onPreExecute() {
	}

	/**
	 * 异步任务开始运行
	 */
	protected void onStart() {
	}

	/**
	 * 异步任务后台运行
	 * 
	 * @param params
	 *            异步任务运行参数
	 * @return 异步任务运行结果
	 */
	protected abstract Result doInBackground(Param... params);

	/**
	 * 异步任务进度
	 * 
	 * @param progress
	 *            异步任务进度参数
	 */
	protected void onProgress(Progress... progress) {
	}

	/**
	 * 异步任务运行结束
	 * 
	 * @param result
	 *            异步任务运行结果
	 */
	protected void onResult(Result result) {
	}

	/**
	 * 异步任务取消运行
	 * 
	 * @param result
	 *            异步任务运行结果
	 */
	protected void onCancelled(Result result) {
	}

	/**
	 * 获取当前异步任务取消状态
	 * 
	 * @return true 异步任务已取消
	 *         <p>
	 *         false 异步任务未取消
	 */
	public final boolean isCancelled() {
		return mCancelled.get();
	}

	/**
	 * 取消当前异步任务
	 */
	public void cancel() {
		mCancelled.set(true);
	}

	/**
	 * 判断异步任务是否结束
	 * 
	 * @return true 已结束
	 *         <p>
	 *         false 未结束
	 */
	public final boolean isFinished() {
		return mStatus == Status.FINISHED;
	}

	/**
	 * 结束异步任务，根据取消状态判断调用方法
	 * 
	 * @param result
	 *            异步任务运行结果
	 */
	protected final void finish(Result result) {
		if (isCancelled()) {
			onCancelled(result);
		} else {
			onResult(result);
		}
		mStatus = Status.FINISHED;
	}

	/**
	 * 发布异步任务开始运行消息
	 */
	protected final void publishStart() {
		if (!isCancelled()) {
			mHandler.obtainMessage(MESSAGE_PUBLISH_START,
					new AsyncTaskResult<Result>(this)).sendToTarget();
		}
	}

	/**
	 * 发布异步任务运行进度消息
	 * 
	 * @param progress
	 *            异步任务运行进度参数
	 */
	public final void publishProgress(Progress... progress) {
		if (!isCancelled()) {
			mHandler.obtainMessage(MESSAGE_PUBLISH_PROGRESS,
					new AsyncTaskResult<Progress>(this, progress))
					.sendToTarget();
		}
	}

	/**
	 * 发布异步任务运行结果消息
	 * 
	 * @param result
	 *            异步任务运行结果
	 */
	@SuppressWarnings("unchecked")
	protected final void publishResult(Result result) {
		mHandler.obtainMessage(MESSAGE_PUBLISH_RESULT,
				new AsyncTaskResult<Result>(this, result)).sendToTarget();
	}

	/**
	 * 先根据优先级别排序，优先级别相同，根据任务序列码进行排序
	 * 
	 * @param another
	 *            对比异步任务
	 * @return -1 优先级别低
	 *         <p>
	 *         0 优先级别相同
	 *         <p>
	 *         1 优先级别高
	 */
	@Override
	public final int compareTo(AsyncTask<Param, Progress, Result> another) {
		if (mPriority.ordinal() < another.mPriority.ordinal()) {
			return -1;
		} else if (mPriority.ordinal() > another.mPriority.ordinal()) {
			return 1;
		} else {
			if (mSequenceCode < another.mSequenceCode) {
				return -1;
			} else if (mSequenceCode > another.mSequenceCode) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * 异步任务消息发布工具
	 * 
	 * @author kycq
	 *
	 */
	private static class InternalHandler extends Handler {

		public InternalHandler() {
			super(Looper.getMainLooper());
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void handleMessage(Message msg) {
			AsyncTaskResult result = (AsyncTaskResult) msg.obj;
			switch (msg.what) {
			case MESSAGE_PUBLISH_START:
				result.mTask.onStart();
				break;
			case MESSAGE_PUBLISH_PROGRESS:
				result.mTask.onProgress(result.mData);
				break;
			case MESSAGE_PUBLISH_RESULT:
				result.mTask.finish(result.mData[0]);
				break;
			}
		}
	}

	/**
	 * 异步任务封装消息
	 * 
	 * @author kycq
	 *
	 * @param <Data>
	 *            数据参数类型
	 */
	@SuppressWarnings("rawtypes")
	private static class AsyncTaskResult<Data> {
		/** 异步任务 */
		final AsyncTask mTask;
		/** 数据参数 */
		final Data[] mData;

		AsyncTaskResult(AsyncTask task, Data... data) {
			mTask = task;
			mData = data;
		}

		AsyncTaskResult(AsyncTask task) {
			mTask = task;
			mData = null;
		}
	}

	/**
	 * 异步任务状态表
	 * 
	 * @author kycq
	 *
	 */
	public enum Status {
		/** 等待运行状态 */
		PENDING,
		/** 运行中状态 */
		RUNNING,
		/** 运行结束状态 */
		FINISHED,
	}

	/**
	 * 异步任务等级优先表
	 * 
	 * @author kycq
	 *
	 */
	public enum Priority {
		/** 低 */
		LOW,
		/** 正常 */
		NORMAL,
		/** 高级 */
		HIGH,
		/** 紧急 */
		IMMEDIATE
	}
}
