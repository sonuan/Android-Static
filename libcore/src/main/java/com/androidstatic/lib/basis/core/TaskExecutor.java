package com.androidstatic.lib.basis.core;

import android.os.Process;

import java.util.concurrent.BlockingQueue;

/**
 * 异步任务线程
 * <p>
 * 根据线程池保持的线程数量，保留一定的线程在后台运行，并在指定时间内判断当前线程的存在状态，进行销毁或保留。
 * <p>
 * 线程运行时，阻断式读取任务池，直到超时或读取到任务。如果存在任务，则运行任务，否则判断当前线程状态。
 * 
 * @author kycq
 *
 */
public final class TaskExecutor extends Thread {
	/** 异步任务线程池 */
	private ThreadExecutor mExecutor;
	/** 异步任务池 */
	private BlockingQueue<AsyncTask<?, ?, ?>> mTaskQueue;
	/** 线程保留状态 */
	private boolean isDestory = false;

	/**
	 * 构造方法
	 * 
	 * @param executor
	 *            异步任务线程池
	 * @param taskQueue
	 *            异步任务池
	 */
	TaskExecutor(ThreadExecutor executor,
			BlockingQueue<AsyncTask<?, ?, ?>> taskQueue) {
		mExecutor = executor;
		mTaskQueue = taskQueue;
	}

	/**
	 * 销毁当前线程
	 */
	protected void destory() {
		isDestory = true;
		interrupt();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

		AsyncTask task;
		while (true) {
			try {
				// 指定超时时间，阻断式读取异步任务池
				task = mTaskQueue.poll(mExecutor.getAliveTime(),
						mExecutor.getTimeUnit());
				if (task == null) {// 读取不到异步任务，则判断当前线程状态
					if (mExecutor.threadTimeOut(this) || isDestory) {
						return;
					}
					continue;
				}
			} catch (InterruptedException e) {
				if (isDestory) {
					return;
				}
				continue;
			}

			// 当异步任务池中依旧存在异步任务时，必须将所有任务读取结束后，才可结束线程
			
			task.publishStart();
			if (task.isCancelled()) {
				mExecutor.finish(task, null);
				continue;
			}
			mExecutor.finish(task, task.doInBackground(task.mParams));
		}
	}
}
