Android tasks
=====

This is a small library to create a custom pool thread for Android. It uses annotations to  be able to run the code on the UI thread. It also allows the developer to add dependency between tasks, for example if the task A need to wait for the task B, it can be configured in a way that A will be executed once B is finished.

## Create a task and execute it

The first thing that we need to do is to create the task:

```java
    private class CustomTask extends BaseTask{

        public CustomTask() {
            super();
        }

        @Override
        public void onFinish() {
            //Do something to finalize the task
        }

        @Override
        public TaskResult runTask() {
            //Do the main stuff
            return null;
        }
    }
```

Once we have it ready we only need to call `TaskExecutor` to run the task:

```java
CustomTask customTask = new CustomTask();
TaskExecutor.getInstance().addTask(customTask);
```

## Run the task in the UI thread

To do that we only need to use the `@OnUiThread` annotation in the method that we want to run on the main loop (`onFinish()` and/or `runTask()`):

```java
    private class CustomTask extends BaseTask{

        public CustomTask() {
            super();
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
```

## Tasks with dependencies
It is possible to define a task to be executed only when an other task is finished. For instance if we have the task A that depends on the task B we can use the method `setTaskIdToWait` to force the task A to wait B to finish

```java
CustomTask taskA = new CustomTask(taskId++);
CustomTask taskB = new CustomTask(taskId++);
				
taskA.setTaskIdToWait(taskB.getTaskId());
			
TaskExecutor.getInstance().addTask(taskA);
TaskExecutor.getInstance().addTask(taskB);
```


