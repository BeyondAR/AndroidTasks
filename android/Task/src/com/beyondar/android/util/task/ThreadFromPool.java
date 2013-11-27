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

import java.util.Vector;

public class ThreadFromPool extends Thread {

	private final Object mLock = new Object();

	private long mId;

	private boolean mStop;
	private Vector<BaseTask> mTaskList;
	private OnThreadFromPoolStop mOnThreadFromPoolStop;
	private OnFinishTaskListener mTaskListener;

	//private String tag = "ThreadFromPool";

	private long mMaxSleepingTime;
	private long mLastTime;

	/**
	 * 
	 * @param id
	 *            the thread id
	 * @param onFinishTaskListener
	 *            Listener to know when a thread finish its job (and is waiting)
	 * @param onThreadFromPoolStop
	 *            Listener to notify when a thread stop
	 * @param maxInactiveTime
	 *            This is the time in milliseconds which this thread will wait
	 *            until end if is inactive. So after maxInactiveTime if any task
	 *            is assigned, this thread will be removed. If this value is 0,
	 *            this thread allays will be alive until mStop it.
	 */
	public ThreadFromPool(int id, OnFinishTaskListener onFinishTaskListener,
			OnThreadFromPoolStop onThreadFromPoolStop, long maxInactiveTime) {
		mTaskListener = onFinishTaskListener;
		mId = id;
		mOnThreadFromPoolStop = onThreadFromPoolStop;
		mTaskList = new Vector<BaseTask>(1, 1);
		mStop = false;
		mMaxSleepingTime = maxInactiveTime;
	}

	/**
	 * Define the new thread maxInactiveTime. This is the time in milliseconds
	 * which this thread will wait until end if is inactive. So after
	 * maxInactiveTime if any task is assigned, this thread will be removed. If
	 * this value is 0, this thread allays will be alive until stop it.
	 * 
	 * @param maxSleepingTime
	 */
	public void setMaxThreadInactiveTime(long maxSleepingTime) {
		if (mMaxSleepingTime != maxSleepingTime) {
			mMaxSleepingTime = maxSleepingTime;
			wakeUp();
		}
	}

	/**
	 * Get the id of this thread
	 * 
	 * @return
	 */
	public long getTaskId() {
		return mId;
	}

	/**
	 * Stop this thread. But first it will try to do all the task in the queue
	 */
	public void stopTask() {

		mTaskList.removeAllElements();
		if (mOnThreadFromPoolStop != null) {
			mOnThreadFromPoolStop.onThreadStops(this);
		}

		synchronized (mLock) {
			mStop = true;
			mLock.notify();
		}
		
		//stop();

	}

	public void interrupt() {
		stopTask();
		super.interrupt();
	}

	/**
	 * Add the next task to process, if an other task is executing, the new task
	 * will be added to the queue
	 * 
	 * @param task
	 *            next task
	 * @return Return true if the task has been added, false otherwise.
	 */
	public synchronized boolean addTask(BaseTask task) {
		if (mTaskList.size() > 1) {
			// LogCat.i(tag,
			// "WARNINGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGg  "
			// + task.getTaskId());
		}

		synchronized (mLock) {
			if (mStop) {
				return false;
			}
			mLock.notify();
			mTaskList.addElement(task);
			// LogCat.i(tag, "====thead mId=" + mId + "  BaseTask mId=" +
			// task.getTaskId());
		}
		return true;
	}

	/**
	 * Force the thread to check for new tasks
	 */
	public void wakeUp() {
		synchronized (mLock) {
			mLock.notify();
		}
	}

	/**
	 * Finalize the task and do the last job (notify the
	 * {@link OnFinishTaskListener})
	 * 
	 * @param task
	 *            The finalized task
	 * @param result
	 *            The result of this task
	 */
	private void finalizeTask(BaseTask task, TaskResult result) {
		if (mTaskListener != null) {
			mTaskListener.onFinishTask(result, task, this);
		}
	}

	public void run() {
		while (!mStop) {

			for (int i = 0; i < mTaskList.size(); i++) {

				BaseTask task = (BaseTask) mTaskList.elementAt(i);

				// LogCat.i(tag, "###Running task " + task.getTaskId());
				TaskResult result = task.executeTask();

				finalizeTask(task, result);

				synchronized (mLock) {
					if (mTaskList.size() > 0) {
						mTaskList.removeElementAt(i);
						i--;
					}
				}
			}

			synchronized (mLock) {
				if (!mStop && mTaskList.size() == 0) {
					try {
						mLastTime = System.currentTimeMillis();
						mLock.wait(mMaxSleepingTime);
						//long timeT = (System.currentTimeMillis() - mLastTime);
						// LogCat.i(tag, "*************  timeT=" + timeT
						// + " Max Sleepingtime=" + mMaxSleepingTime);
						if ((System.currentTimeMillis() - mLastTime) > mMaxSleepingTime
								&& mTaskList.size() == 0) {
							mStop = true;
							// LogCat.i(tag,
							// "Thread Killed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!   mId="
							// + getTaskId());
							if (mOnThreadFromPoolStop != null) {
								mOnThreadFromPoolStop.onThreadStops(this);
							}
							return;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

}
