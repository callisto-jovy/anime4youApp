/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:16
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import android.os.Handler;
import android.os.Looper;
import ga.abzzezz.util.logging.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskExecutor {

    /**
     * To ensure API level 30 compatibility
     */
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        callback.preExecute();
        executor.execute(() -> {
            try {
                final R result = callable.call();
                handler.post(() -> {
                    try {
                        callback.onComplete(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                Logger.log("Running task", Logger.LogType.ERROR);
                e.printStackTrace();
            }
        });
    }

    public interface Callback<R> {
        void onComplete(R result) throws Exception;

        void preExecute();
    }
}
