/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.02.21, 15:42
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.holders.AnimePaheHolder;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AnimePaheSearchTask extends TaskExecutor implements Callable<List<JSONObject>>, AnimePaheHolder {

    private final String searchQuery;

    public AnimePaheSearchTask(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void executeAsync(Callback<List<JSONObject>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<JSONObject> call() throws Exception {
        final List<JSONObject> showsOut = new ArrayList<>();

        final String collected = URLUtil.collectLines(URLUtil.createHTTPSURLConnection(String.format(SEARCH_API, searchQuery), new String[]{"User-Agent", RandomUserAgent.getRandomUserAgent()}), "");

        final JSONArray showsIn = new JSONObject(collected).getJSONArray("data");
        //TODO: Adds all results. Distinction needed?
        for (int i = 0; i < showsIn.length(); i++) {
            final JSONObject show = new AnimePaheFetchCallable(showsIn.getJSONObject(i)).call();
            if (!showsOut.contains(show)) showsOut.add(show);
        }
        return showsOut;
    }
}
