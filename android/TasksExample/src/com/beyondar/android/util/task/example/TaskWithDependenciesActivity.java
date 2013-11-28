package com.beyondar.android.util.task.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beyondar.android.util.annotation.OnUiThread;
import com.beyondar.android.util.task.BaseTask;
import com.beyondar.android.util.task.TaskExecutor;
import com.beyondar.android.util.task.TaskResult;

public class TaskWithDependenciesActivity extends Activity {

	private Button buttonStart;
	private TextView textView;

	private static int taskId = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		buttonStart = (Button) findViewById(R.id.buttonStart);
		textView = (TextView) findViewById(R.id.textView);

		buttonStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				textView.setText("");
				CustomTask customTask = new CustomTask(taskId++);
				CustomTask customTask2 = new CustomTask(taskId++);
				
				customTask.setTaskIdToWait(customTask2.getTaskId());
				customTask2.setDelay(true);
				
				textView.append("Adding task " + customTask.getTaskId() + "\n");
				TaskExecutor.getInstance().addTask(customTask);
				textView.append("Adding task " + customTask2.getTaskId() + "\n");
				TaskExecutor.getInstance().addTask(customTask2);
				textView.append("---------------\n");
			}
		});
	}

	private class CustomTask extends BaseTask {

		private boolean delay;
		
		public CustomTask(long id) {
			super(id);
		}
		public void setDelay(boolean delay){
			this.delay = delay;
		}

		@Override
		@OnUiThread
		public void onFinish() {
			textView.append("Task finished: " + getTaskId() + "\n");
		}

		@Override
		public TaskResult runTask() {
			Log.d(getClass().getSimpleName(), "Doing something ");
			if(delay){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
}
