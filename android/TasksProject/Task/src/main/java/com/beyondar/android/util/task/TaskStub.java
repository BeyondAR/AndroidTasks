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

public interface TaskStub {

    /**
     * The method where all the stuff is done.
     *
     * @return ({@link TaskResult} with the info about the process. Set the
     *         {@link TaskResult} error's flag to stop the process.
     */
    public TaskResult runTask();

    /**
     * Override this method to execute the last method before finish the task
     *
     */
    public void onFinish();
}
