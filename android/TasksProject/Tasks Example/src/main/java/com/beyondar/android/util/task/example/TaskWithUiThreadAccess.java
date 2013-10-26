package com.beyondar.android.util.task.example;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.beyondar.android.util.task.Task;
import com.beyondar.android.util.task.TaskResult;

public class TaskWithUiThreadAccess extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_list);
    }

    private class CustomTask extends Task{

        public CustomTask(long id) {
            super(id);
        }

        @Override
        public TaskResult runTask() {
            return null;
        }
    }

}
