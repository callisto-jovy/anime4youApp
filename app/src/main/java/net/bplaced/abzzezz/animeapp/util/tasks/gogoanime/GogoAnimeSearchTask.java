/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.gogoanime.GogoAnimeFetcher;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GogoAnimeSearchTask extends TaskExecutor implements Callable<List<Show>> {

    private final java.lang.String searchQuery;

    public GogoAnimeSearchTask(final java.lang.String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public <R> void executeAsync(Callback<List<Show>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<Show> call() throws Exception {
        final List<Show> showsOut = new ArrayList<>();

        final java.lang.String[] urls = GogoAnimeFetcher.getURLsFromSearch(searchQuery);

        for (final java.lang.String url : urls) {
            final GogoAnimeFetcher fetcher = new GogoAnimeFetcher(url);
            final java.lang.String id = fetcher.getID();
            final java.lang.String title = fetcher.getShowTitle();
            final java.lang.String imageURL = fetcher.fetchImage0();
            final java.lang.String episodeStart = fetcher.getEpisodeStart();
            final java.lang.String episodeEnd = fetcher.getEpisodeEnd();

            final JSONArray episodes = new JSONArray();

            for (final java.lang.String fetchedDirectURL : fetcher.getFetchedDirectURLs())
                episodes.put(fetchedDirectURL);

            showsOut.add(Providers.GOGOANIME.getProvider().getShow(new JSONObject()
                    .put(StringHandler.SHOW_ID, id)
                    .put(StringHandler.SHOW_TITLE, title)
                    .put(StringHandler.SHOW_LANG, "eng")
                    .put("ep_start", episodeStart)
                    .put("ep_end", episodeEnd)
                    .put(StringHandler.SHOW_IMAGE_URL, imageURL)
                    .put("episodes", episodes)));
        }
        return showsOut;
    }
}
