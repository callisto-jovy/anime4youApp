/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;

public class GogoAnimeFetchDirectTask extends TaskExecutor implements Callable<String> {

    private final String formattedIn;

    public GogoAnimeFetchDirectTask(final String formattedIn) {
        this.formattedIn = formattedIn;
    }

    /**
     * Gets the direct video url from the formatted api link
     *
     * @param in read in lines
     * @return url to mp4
     */
    private static String getVidURL(final String in) throws JSONException {
        return new JSONObject(in).getJSONArray("source").getJSONObject(0).getString("file");
    }

    public <R> void executeAsync(Callback<String> callback) {
        super.executeAsync(this, callback);
    }

    /*
    TODO: Only fetches the episodes for now. Add URL to additional json!
     */
    @Override
    public String call() throws Exception {
        return getVidURL(URLUtil.collectLines(new URL(formattedIn), ""));
    }



}
