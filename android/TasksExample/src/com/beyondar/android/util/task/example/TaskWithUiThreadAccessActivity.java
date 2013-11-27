package com.beyondar.android.util.task.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beyondar.android.util.annotation.OnUiThread;
import com.beyondar.android.util.task.BaseTask;
import com.beyondar.android.util.task.TaskExecutor;
import com.beyondar.android.util.task.TaskResult;


public class TaskWithUiThreadAccessActivity extends Activity {

    private Button buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStart = (Button) findViewById(R.id.buttonStart);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTask customTask = new CustomTask(1);
                TaskExecutor.getInstance().addTask(customTask);
            }
        });
    }

    private class CustomTask extends BaseTask{

        public CustomTask(long id) {
            super(123);
        }

        @Override
        @OnUiThread
        public void onFinish() {
            Toast.makeText(TaskWithUiThreadAccessActivity.this, "Task finished", Toast.LENGTH_SHORT).show();
        }

        @Override
        @OnUiThread
        public TaskResult runTask() {
            Toast.makeText(TaskWithUiThreadAccessActivity.this, "Doing something", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
