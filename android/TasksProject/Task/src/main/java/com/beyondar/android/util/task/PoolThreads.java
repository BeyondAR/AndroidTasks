/*
 * Copyright (C) 2013 BeyondAR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.beyondar.android.util.task;

import java.util.ArrayList;

/**
 *         This class is used to define a pool of threads to establish the
 *         maximum allowable threads running at the same time.<br>
 *         This class is used by the {@link TaskExecutor}
 * 
 */
public class PoolThreads implements OnFinishTaskListener, OnThreadFromPoolStop {

	/** Max number of threads in the pool by default */
	public static final int DEFAULT_MAX_THREADS = 4;

	/**
	 * Max default time (in milliseconds) that a thread will wait without any
	 * task assigned before being removed
	 */
	public static final int DEFAULT_MAX_THREAD_INACTIVE_TIME = 5000;

	private volatile int mMaxThreads;

	private volatile int mThreadIdGen;

	private volatile int mThreadCounter;

	/* The list with the free threads */
	private ArrayList<ThreadFromPool> mFreeThreadPool;

	private volatile long mMaxThreadInactiveTime;

	private volatile boolean mKillThreads;

	private OnFinishTaskListener mOnFinishTaskListener;

	// private String tag = "PoolThreads";

	/**
	 * Define the maximum number of threads in the pool
	 * 
	 * @param maxThreads
	 *            The maximum number of threads that the pool will allow
	 */
	public PoolThreads(int maxThreads) {
		this.mMaxThreads = maxThreads;
		init(DEFAULT_MAX_THREAD_INACTIVE_TIME);

	}

	/**
	 * The maximum number of threads is {@link #DEFAULT_MAX_THREADS}=
	 * {@value #DEFAULT_MAX_THREADS}
	 *
	 */
	public PoolThreads() {
		this.mMaxThreads = DEFAULT_MAX_THREADS;
		init(DEFAULT_MAX_THREAD_INACTIVE_TIME);

	}

	/**
	 * Define the maximum number of threads in the pool
	 *
	 * @param maxThreads
	 *            The maximum number of threads that the pool will allow
	 * @param maxThreadInactiveTime
	 *            When the pool will create a thread, it will uses this time to
	 *            set the max inactive time for a thread before being removed
	 */
	public PoolThreads(int maxThreads, long maxThreadInactiveTime) {
		this.mMaxThreads = maxThreads;
		init(maxThreadInactiveTime);
		this.mMaxThreadInactiveTime = maxThreadInactiveTime;
	}

	/**
	 * When the pool will create a thread, it will uses this time to set the max
	 * inactive time for a thread before being removed. Using this method, the
	 * system will remove all the existing threads from the pool (The current
	 * task, if there are any task being executed, will be finished as expected)
	 *
	 * @param maxThreadInactiveTime
	 *            The new time in milliseconds
	 */
	public void setMaxThreadInactiveTime(long maxThreadInactiveTime) {
		this.mMaxThreadInactiveTime = maxThreadInactiveTime;
	}

	/**
	 * Get the maximum time which a thread will be inactive before being removed
	 *
	 * @return Max inactive time
	 */
	public long getMaxThreadInactiveTime() {
		return this.mMaxThreadInactiveTime;
	}

	/**
	 * Specify if you want all the threads as a temporal threads. Its means that
	 * when the thread will finish the task, it will be destroyed, and if a new
	 * task arrive, a new temporal thread will be created
     *
     * @param temporal Set true to mark al the thread as temporal, false otherwise
	 */
	public void temporalThreads(boolean temporal) {
		mKillThreads = temporal;

		for (int i = 0; i < mFreeThreadPool.size(); i++) {
			ThreadFromPool thread = mFreeThreadPool.get(i);
			thread.stopTask();
		}

	}

	private void init(long maxThreadInactiveTime) {
		mThreadCounter = 0;
		mThreadIdGen = 0;
		mKillThreads = false;
		this.mMaxThreadInactiveTime = maxThreadInactiveTime;

		mFreeThreadPool = new ArrayList<ThreadFromPool>(mMaxThreads);
		// poolTherad_busy = new Vector(mMaxThreads, 1);

		// threadsInUse = 0;
	}

	/**
	 * Stop all the sleeping threads
	 */
	public void stopAllSleepingThreads() {
		for (int i = 0; i < mFreeThreadPool.size(); i++) {
			ThreadFromPool thread = (ThreadFromPool) mFreeThreadPool.get(i);
			thread.stopTask();
		}
	}

	/**
	 * Get the maxim number of concurrent Tasks. the default value is 6
	 *
	 * @return
	 */
	public int getMaxConcurrentTasks() {
		return mMaxThreads;
	}

	/**
	 * Set the maxim number of concurrent Tasks. the default value is 6
	 *
	 * @param max
	 */
	public void setMaxConcurrentTasks(int max) {
		mMaxThreads = max;
	}

	/**
	 * Get the maximum number of threads available in the pool
	 *
	 * @return maximum number of threads.
	 */
	public int getMaxThreads() {
		return mMaxThreads;
	}

	/**
	 * Get a free thread to execute the task
     *
     * @return An available thread
	 *
	 */
	public synchronized ThreadFromPool getFreeThread() {
		ThreadFromPool thread = null;
		if (mFreeThreadPool.size() > 0) {
			thread = mFreeThreadPool.get(0);
			mFreeThreadPool.remove(0);
		} else if (mThreadCounter < mMaxThreads) {
			thread = new ThreadFromPool(mThreadIdGen, this, this,
                    mMaxThreadInactiveTime);
			thread.start();
			mThreadCounter++;
			mThreadIdGen++;
		}

		return thread;
	}

	/**
	 * Set the listener to execute when a task is finished
	 *
	 * @param onFinishTaskListener
	 */
	public void setOnFinishTaskListener(OnFinishTaskListener onFinishTaskListener) {
		this.mOnFinishTaskListener = onFinishTaskListener;
	}

	public void onFinishTask(TaskResult result, Task task, ThreadFromPool thread) {
		if (mKillThreads) {
			thread.stopTask();
		} else {

			thread.setMaxThreadInactiveTime(mMaxThreadInactiveTime);
			mFreeThreadPool.add(thread);
			// LogCat.i(tag, "Adding thread from task id=" + task.getIdTask());
		}
		if (mOnFinishTaskListener != null && task != null) {
			mOnFinishTaskListener.onFinishTask(result, task, null);
		}
	}

	public void onThreadStops(ThreadFromPool thread) {
		removeThread(thread);
		// LogCat.i(tag, "-- id Thread=" + thread.getIdTask() +
		// " has been stopped");

	}

	/**
	 * This method notify the pool that the specified thread has stopped
	 * 
	 * @param thread Thread to be removed
	 */
	private synchronized void removeThread(ThreadFromPool thread) {
		mThreadCounter--;
	}

}
