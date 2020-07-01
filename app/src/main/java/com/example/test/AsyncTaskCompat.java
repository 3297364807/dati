package com.example.test;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskCompat  {
    private static final String TAG ="111111:" ;

    @Deprecated
    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeParallel(
            AsyncTask<Params, Progress, Result> task,
            Params... params) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        return task;
    }
}
