package com.beyondar.android.util.task.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{

    private ListView mLisViewt;
    private String[] values = new String[] { "Task with UI thread access", "Task with dependencies" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_list);

        mLisViewt = (ListView) findViewById(R.id.examplesList);

        fillList();

    }

    private void fillList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        mLisViewt.setAdapter(adapter);
        mLisViewt.setOnItemClickListener(this);
    }

    private void openActivity(Class<? extends Activity> ActivityClass) {
        Intent intent = new Intent(this, ActivityClass);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        switch (pos) {
            case 0:
                openActivity(TaskWithUiThreadAccessActivity.class);
                break;
            case 1:
                openActivity(TaskWithDependenciesActivity.class);
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }

}
