/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 11.07.20, 17:31
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class PermissionTask extends TaskExecutor implements Callable<Boolean> {

    public <R> void executeAsync(Callback<Boolean> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public Boolean call() throws Exception {
        final URL url = new URL(StringHandler.USER_URL);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Referer", AnimeAppMain.getInstance().getAndroidId());
        connection.connect();
        final InputStream inputStream = connection.getInputStream();
        final String response = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
        return !response.equals("200");
    }
}
