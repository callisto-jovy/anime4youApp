/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.02.21, 14:12
 */

package net.bplaced.abzzezz.animeapp.util.tasks.download;

import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EpisodeDownloadTaskExecutor {
    /**
     * To ensure API level 30 compatibility
     */
    private final Executor executor = Executors.newSingleThreadExecutor();

    public <R> void executeAsync(Callable<R> callable, TaskExecutor.Callback<R> callback) {
        callback.preExecute();
        executor.execute(() -> {
            try {
                callable.call();
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