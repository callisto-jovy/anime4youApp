/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 23:37
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.datatypes.ArrayHelper;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.ricecode.similarity.JaroStrategy;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;

public class GogoAnimeSearchTask extends TaskExecutor implements Callable<Optional<JSONObject>> {

    private final String searchQuery;

    public GogoAnimeSearchTask(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void executeAsync(Callback<Optional<JSONObject>> callback) {
        super.executeAsync(this, callback);
    }


    @Override
    public Optional<JSONObject> call() throws IOException {
        final String[] urls = GogoAnimeFetcher.getURLsFromSearch(searchQuery);
        if (urls.length == 0) return Optional.empty(); //No urls have been found

        final JaroStrategy strategy = new JaroStrategy();

        final int best = ArrayHelper.mostSimilarString(urls, searchQuery, strategy, s -> s.substring(s.lastIndexOf("/")));

        if (best == -1) return Optional.empty();

        try {
            final GogoAnimeFetcher fetcher = new GogoAnimeFetcher(urls[best]);
            final String id = fetcher.getID();
            final String episodeStart = fetcher.getEpisodeStart();

            return Optional.of(new JSONObject()
                    .put("id", id)
                    .put("ep_start", episodeStart)
                    .put("referrals", fetcher.getFetchedReferrals()
                    ));
        } catch (final IOException | JSONException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
