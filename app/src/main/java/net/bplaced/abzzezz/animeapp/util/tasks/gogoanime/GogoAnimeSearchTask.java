/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GogoAnimeSearchTask extends TaskExecutor implements Callable<List<JSONObject>> {

    private final String searchQuery;

    public GogoAnimeSearchTask(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void executeAsync(Callback<List<JSONObject>> callback) {
        super.executeAsync(this, callback);
    }


    @Override
    public List<JSONObject> call() throws IOException {

        final List<JSONObject> showsOut = new ArrayList<>();

        final String[] urls = GogoAnimeFetcher.getURLsFromSearch(searchQuery);

        for (final String url : urls) {
            try {
                final GogoAnimeFetcher fetcher = new GogoAnimeFetcher(url);
                final String id = fetcher.getID();
                final String episodeStart = fetcher.getEpisodeStart();
                final String episodeEnd = fetcher.getEpisodeEnd();

                showsOut.add(
                        new JSONObject()
                                .put("id", id)
                                .put("ep_start", episodeStart)
                                .put("ep_end", episodeEnd)
                                .put("referrals", fetcher.getFetchedDirectURLs())
                );
            } catch (final IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return showsOut;
    }
}
