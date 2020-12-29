/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 11.07.20, 17:31
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;

import java.util.concurrent.Callable;

public class PermissionTask extends TaskExecutor implements Callable<Boolean> {

    public <R> void executeAsync(Callback<Boolean> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public Boolean call() throws Exception {
        return URLUtil.createHTTPURLConnection(StringHandler.IMAGE_URL, "POST", new String[]{"Referer", AnimeAppMain.getInstance().getAndroidId()}).getResponseCode() != 200;
    }
}
