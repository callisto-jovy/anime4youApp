/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GogoAnimeSearchTask extends TaskExecutor implements Callable<List<Show>> {

    private final String searchQuery;

    public GogoAnimeSearchTask(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void executeAsync(Callback<List<Show>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<Show> call() throws IOException {
        final List<Show> showsOut = new ArrayList<>();

        final String[] urls = GogoAnimeFetcher.getURLsFromSearch(searchQuery);

        for (final String url : urls) {
            try {
                final GogoAnimeFetcher fetcher = new GogoAnimeFetcher(url);
                final String id = fetcher.getID();
                final String title = fetcher.getShowTitle();
                final String imageURL = fetcher.fetchImage0();
                final String episodeStart = fetcher.getEpisodeStart();
                final String episodeEnd = fetcher.getEpisodeEnd();

                final JSONArray episodes = new JSONArray();

                for (final String fetchedDirectURL : fetcher.getFetchedDirectURLs())
                    episodes.put(fetchedDirectURL);

                showsOut.add(Providers.GOGOANIME.getProvider().getShow(new JSONObject()
                        .put(StringHandler.SHOW_ID, id)
                        .put(StringHandler.SHOW_TITLE, title)
                        .put(StringHandler.SHOW_LANG, "eng")
                        .put("ep_start", episodeStart)
                        .put("ep_end", episodeEnd)
                        .put(StringHandler.SHOW_IMAGE_URL, imageURL)
                        .put("episodes", episodes)));

            } catch (final IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return showsOut;
    }
}
