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
