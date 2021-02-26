/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.02.21, 13:24
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.provider.holders.AnimePaheHolder;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class AnimePaheRefreshTask extends TaskExecutor implements Callable<Show>, AnimePaheHolder {

    private final Show showIn;

    public AnimePaheRefreshTask(final Show showIn) {
        this.showIn = showIn;
    }

    public void executeAsync(final Callback<Show> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public Show call() throws Exception {
        final String collected = URLUtil.collectLines(URLUtil.createHTTPSURLConnection(String.format(SEARCH_API, showIn.getTitle()), new String[]{"User-Agent", RandomUserAgent.getRandomUserAgent()}), "");

        return Providers.ANIMEPAHE.getProvider().getShowFromProvider(new AnimePaheFetchCallable(new JSONObject(collected).getJSONArray("data").getJSONObject(0)).call());
    }
}
