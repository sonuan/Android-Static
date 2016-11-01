package com.androidstatic.lib.basis.core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步任务线程池
 * <p>
 * 异步任务线程池维护异步任务池和线程池。
 * <p>
 * 异步任务池根据异步任务的优先级别进行先后调用。
 * <p>
 * 线程池根据配置方案及当前异步任务池的数量，自动进行线程的创建及销毁。
 * 
 * @author kycq
 *
 */
public final class ThreadExecutor {
	/** 异步任务池 */
	private Set<AsyncTask<?, ?, ?>> mTaskSet = new HashSet<AsyncTask<?, ?, ?>>();
	/** 异步任务池，根据异步任务的优先级别进行排序 */
	private final PriorityBlockingQueue<AsyncTask<?, ?, ?>> mTaskQueue = new PriorityBlockingQueue<AsyncTask<?, ?, ?>>();

	/** 核心线程数量 */
	private final int mCorePoolSize;
	/** 最大线程数量 */
	private final int mMaxPoolSize;
	/** 超时时间 */
	private final long mAliveTime;
	/** 超时时间单位 */
	private final TimeUnit mTimeUnit;
	/** 线程队列 */
	private final LinkedBlockingQueue<TaskExecutor> mTaskExecutors;

	/** 线程池状态 */
	private boolean isDestory = false;
	/** 线程池序列码生成器 */
	private AtomicInteger mSequenceCode = new AtomicInteger();

	/**
	 * 构造方法
	 * 
	 * @param corePoolSize
	 *            核心线程数量
	 * @param maxPoolSize
	 *            最大线程数量
	 * @param aliveTime
	 *            线程超时时间
	 * @param timeUnit
	 *            线程超时时间单位
	 */
	public ThreadExecutor(int corePoolSize, int maxPoolSize, long aliveTime,
			TimeUnit timeUnit) {
		mCorePoolSize = corePoolSize;
		mMaxPoolSize = maxPoolSize;
		mAliveTime = aliveTime;
		mTimeUnit = timeUnit;

		// 线程池创建时，立即运行核心线程
		mTaskExecutors = new LinkedBlockingQueue<TaskExecutor>();
		for (int i = 0; i < corePoolSize; i++) {
			createThread();
		}
	}

	/**
	 * 获取核心线程数量
	 * 
	 * @return 核心线程数量
	 */
	public int getCorePoolSize() {
		return mCorePoolSize;
	}

	/**
	 * 获取最大同时运行线程数量
	 * 
	 * @return 最大线程数量
	 */
	public int getMaxPoolSize() {
		return mMaxPoolSize;
	}

	/**
	 * 获取线程超时时间
	 * 
	 * @return 线程超时时间
	 */
	public long getAliveTime() {
		return mAliveTime;
	}

	/**
	 * 获取线程超时时间单位
	 * 
	 * @return 线程超时时间单位
	 */
	public TimeUnit getTimeUnit() {
		return mTimeUnit;
	}

	/**
	 * 根据情况判断是否结束线程
	 * 
	 * @param taskThread
	 *            线程实例
	 * @return true 结束该线程
	 *         <p>
	 *         false 保留该线程
	 */
	protected synchronized boolean threadTimeOut(TaskExecutor taskThread) {
		if (mCorePoolSize < mTaskExecutors.size()) {
			mTaskExecutors.remove(taskThread);
			return true;
		}
		return false;
	}

	/**
	 * 创建新线程
	 */
	private synchronized void createThread() {
		if (isDestory) {// 线程池销毁时，不再创建任何新线程
			return;
		}

		TaskExecutor taskThread = new TaskExecutor(this, mTaskQueue);
		taskThread.start();
		mTaskExecutors.add(taskThread);
	}

	/**
	 * 添加异步任务到异步任务池中
	 * 
	 * @param asyncTask
	 *            异步任务实例
	 */
	protected synchronized void execute(AsyncTask<?, ?, ?> asyncTask) {
		if (isDestory) {// 线程池销毁时，不再添加任何异步任务
			return;
		}

		synchronized (mTaskSet) {
			mTaskSet.add(asyncTask);
		}
		// 设置线程池序列码
		asyncTask.setSequenceCode(mSequenceCode.incrementAndGet());
		mTaskQueue.add(asyncTask);

		// 判断当前线程数量，根据情况创建新线程同步运行异步任务
		if (mTaskQueue.size() > mCorePoolSize
				&& mTaskQueue.size() < mMaxPoolSize) {
			createThread();
		}
	}

	/**
	 * 结束异步任务，并移除异步任务池
	 * 
	 * @param asyncTask
	 *            异步任务实例
	 * @param result
	 *            异步任务结果
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	synchronized void finish(AsyncTask asyncTask, Object result) {
		asyncTask.publishResult(result);
		synchronized (mTaskSet) {
			mTaskSet.remove(asyncTask);
		}
	}

	/**
	 * 取消所有异步任务
	 */
	public void cancelAll() {
		synchronized (mTaskSet) {
			for (AsyncTask<?, ?, ?> task : mTaskSet) {
				task.cancel();
			}
		}
	}

	/**
	 * 取消指定标签的所有异步任务
	 * 
	 * @param tag
	 *            异步任务标签
	 */
	public void cancelAll(Object tag) {
		if (tag == null) {
			return;
		}

		synchronized (mTaskSet) {
			for (AsyncTask<?, ?, ?> task : mTaskSet) {
				if (tag.equals(task.getTag())) {
					task.cancel();
				}
			}
		}
	}

	/**
	 * 销毁线程池
	 */
	public synchronized void destory() {
		isDestory = true;
		cancelAll();
		for (TaskExecutor taskThread : mTaskExecutors) {
			taskThread.destory();
		}
	}

	/**
	 * 创建指定核心线程数的线程池
	 * 
	 * @param corePoolSize
	 *            核心线程数
	 * @return 异步任务线程池
	 */
	public static ThreadExecutor newCoreThreadPool(int corePoolSize) {
		return new ThreadExecutor(corePoolSize, corePoolSize, 0L,
				TimeUnit.SECONDS);
	}

	/**
	 * 创建指定最大并行线程数的线程池
	 * 
	 * @param corePoolSize
	 *            核心线程数
	 * @param maxPoolSize
	 *            最大并行线程数
	 * @return 异步任务线程池
	 */
	public static ThreadExecutor newFixedThreadPool(int corePoolSize,
			int maxPoolSize) {
		return new ThreadExecutor(corePoolSize, maxPoolSize, 60L,
				TimeUnit.SECONDS);
	}

	/**
	 * 创建单线程的线程池
	 * 
	 * @return 异步任务线程池
	 */
	public static ThreadExecutor newSingleThreadExecutor() {
		return new ThreadExecutor(1, 1, 0L, TimeUnit.SECONDS);
	}

	/**
	 * 创建最大线程数量为Integer.MAX_VALUE的线程池
	 * 
	 * @return 异步任务线程池
	 */
	public static ThreadExecutor newCachedThreadPool() {
		return new ThreadExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS);
	}
}
