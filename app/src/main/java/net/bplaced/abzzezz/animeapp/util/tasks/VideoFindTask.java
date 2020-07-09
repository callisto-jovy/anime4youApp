/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 09.07.20, 22:54
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import android.app.Activity;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.scripter.ScriptUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class VideoFindTask extends TaskExecutor implements Callable<String> {

    private final int aid;
    private final int episode;
    private final Activity context;

    public VideoFindTask(final int aid, final int episode, Activity context) {
        this.aid = aid;
        this.episode = episode;
        this.context = context;
    }

    public <R> void executeAsync(Callback<String> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public String call() throws Exception {
        final URL url = new URL("http://abzzezz.bplaced.net/app/request.php");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.addRequestProperty("User-Agent", (episode + 1) + StringUtil.splitter + aid + StringUtil.splitter + ScriptUtil.generateRandomKey());
        connection.addRequestProperty("Referer", AnimeAppMain.getInstance().getAndroidId());
        connection.connect();
        return new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining());
    }

    public void makeText(final String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
