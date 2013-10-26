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

/**
 * 
 *         This is the task where the developer can define the stuff to do
 * 
 */
public abstract class Task implements TaskStub{

	public static final int TASK_TYPE_NORMAL = 6978;
	public static final int TASK_TYPE_TIMER = TASK_TYPE_NORMAL + 1;

	private long mId;
	private boolean mRunInBackground;

	private boolean mRunning;

	private boolean mWaitTaskToFinish;
	private int mTaskToWait;

	public Task(long id) {
		mRunning = false;
		mRunInBackground = false;
		mId = id;
		mWaitTaskToFinish = false;
	}

	/**
	 * Get the Task type: <br>
	 * - TASK_TYPE_NORMAL --> Task to execute using the FIFO queue <br>
	 * - TASK_TYPE_TIMER --> Task to execute using the timer queue
	 * 
	 * @return
	 */
	public int getTaskType() {
		return TASK_TYPE_NORMAL;
	}

	/**
	 * Set true if the task can be executed when the app is in background
	 * 
	 * @param run
	 */
	public final void setRunInBackground(boolean run) {
		mRunInBackground = run;
	}

	/**
	 * Check if this task can run in background
	 * 
	 * @return
	 */
	public final boolean runInBackground() {
		return mRunInBackground;
	}

	/**
	 * Check if the task is being executed
	 * 
	 * @return true if is already running, false otherwise
	 */
	public boolean isRunning() {
		return mRunning;
	}

	/**
	 * Execute the task!
	 * 
	 * @return The output of this task ({@link TaskResult}
	 */
	public TaskResult executeTask() {
		mRunning = true;
		TaskResult out = null;

		// out = task.preprocessor();
		// if (out == null) {
		// out = new TaskResult(false, TaskResult.TASK_MESSAGE_UNKNOWN, null,
		// null);
		// }
		// if (out.error()) {
		// task.onKillTask(out);
		// return out;
		// }

		out = checkDependencies();

		if (out == null) {
			out = new TaskResult(mId, false, TaskResult.TASK_MESSAGE_UNKNOWN,
					null, null);
		}
		if (out.msg() == TaskResult.TASK_MESSAGE_WAIT_OTHER_TASK_TO_FINISH) {
			mRunning = false;
			return out;
		}
		mWaitTaskToFinish = false;
		if (out.error() || out.msg() == TaskResult.TASK_MESSAGE_ERROR_CHECKING_DEPENDENCIES) {
			onKillTask(out);
			mRunning = false;
			return out;
		}

		out = runTask();

		if (out == null) {
			out = new TaskResult(mId, false, TaskResult.TASK_MESSAGE_UNKNOWN,
					null, null);
		}
		if (out.error()) {
			onKillTask(out);
			mRunning = false;
			return out;
		}

		onFinish();
		mRunning = false;
		return out;

	}

	/**
	 * Use this method to stop this task until the task with the defined id will
	 * finish. After the desired task will finish, the method
	 * checkDependencies() will be executed again. <br>
	 * To notify the {@link TaskExecutor} that this task has to wait, remember
	 * to return the {@link TaskResult} with the message
	 * TaskResult.TASK_MESSAGE_WAIT_OTHER_TASK_TO_FINISH
	 * 
	 * @param id
	 *            The task to wait before continue executing this task
	 * @return The {@link TaskResult} instance with the message
	 *         TaskResult.TASK_MESSAGE_WAIT_OTHER_TASK_TO_FINISH
	 */
	protected TaskResult waitUntilTaksFinish(int id) {
		mTaskToWait = id;
		mWaitTaskToFinish = true;

		return new TaskResult(mId, false,
				TaskResult.TASK_MESSAGE_WAIT_OTHER_TASK_TO_FINISH,
				"Waiting for the task id=" + mTaskToWait, null);
	}

	/**
	 * Get the id of the task that have to be executed before this task. Don't
	 * forget to call the method "isWaitingUntilTaskFinish()" before use this
	 * getTaskIdToWait(), because the task id could be any number
	 * 
	 * @return the id of the task to wait
	 */
	public int getTaskIdToWait() {
		return mTaskToWait;
	}

	/**
	 * Check if this task have to wait until a certain task will finish.
	 * 
	 * @return true if there is a task to wait before execute this
	 */
	public boolean isWaitingUntilOtherTaskFinishes() {
		return mWaitTaskToFinish;
	}

	// private Vector listeners;

	/**
	 * Get the task ID
	 * 
	 * @return the task id
	 */
	public long getIdTask() {
		return mId;
	}

	// /**
	// * This method execute the operations needed during the preprocessor.
	// Return
	// * true if it should continue, false otherwise
	// *
	// * @return ({@link TaskResult} with the info about the process. Use
	// * "taskOutputCode.error = true" to stop the process
	// */
	// public abstract TaskResult preprocessor();

	/**
	 * Override this method to check the dependencies <br>
	 * Check if an other task is needed. <br>
	 * You can use the history ({@link TaskResult}) to see if a task has been
	 * executed and wait until a certain task will finish using the protected
	 * method method waitUntilTaksFinish(id)
	 * 
	 * @return ({@link TaskResult} with the info about the process. Set the
	 *         {@link TaskResult} erro's flag to stop the process.
	 */
	public TaskResult checkDependencies(){ return null;};

	/**
	 * This method is called when the task is killed. Override this method to manage when the task is killed
	 * 
	 * @param outpuCode
	 *            The error code. See {@link TaskResult} variables
	 */
	public void onKillTask(TaskResult outpuCode){};

}
