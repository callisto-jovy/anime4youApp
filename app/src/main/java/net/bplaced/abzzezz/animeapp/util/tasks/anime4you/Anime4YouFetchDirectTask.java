/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:05
 */

package net.bplaced.abzzezz.animeapp.util.tasks.anime4you;

import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.impl.Anime4YouHolder;
import net.bplaced.abzzezz.animeapp.util.scripter.ScriptUtil;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Anime4YouFetchDirectTask extends TaskExecutor implements Callable<String>, Anime4YouHolder {

    private final String aid;
    private final int episode;

    public Anime4YouFetchDirectTask(String aid, int episode) {
        this.aid = aid;
        this.episode = (episode + 1);
    }

    public <R> void executeAsync(Callback<String> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public String call() throws Exception {
        return new BufferedReader(new InputStreamReader(
                URLUtil.createHTTPURLConnection(REQUEST_URL, "POST",
                        new String[]{"User-Agent", episode + StringUtil.splitter + aid + StringUtil.splitter.concat(ScriptUtil.generateRandomKey())},
                        new String[]{"Referer", AnimeAppMain.getInstance().getAndroidID()}).getInputStream())).lines().collect(Collectors.joining());
    }
}
