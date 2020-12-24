/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.gogoanime.GogoAnimeFetcher;
import net.bplaced.abzzezz.animeapp.util.provider.ProviderType;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class GogoAnimeFetchTask extends TaskExecutor implements Callable<Show> {

    private final String urlIn;

    public GogoAnimeFetchTask(final String urlIn) {
        this.urlIn = urlIn;
    }

    public <R> void executeAsync(Callback<Show> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public Show call() throws Exception {
        final GogoAnimeFetcher fetcher = new GogoAnimeFetcher(urlIn);
        final String id = fetcher.getID();
        final String imageURL = fetcher.fetchImage0();
        final String episodeStart = fetcher.getEpisodeStart();
        final String episodeEnd = fetcher.getEpisodeEnd();

        final JSONArray episodes = new JSONArray();

        for (final String fetchedDirectURL : fetcher.getFetchedDirectURLs()) episodes.put(fetchedDirectURL);

        return new Show(new JSONObject()
                .put(StringHandler.SHOW_ID, id)
                .put(StringHandler.SHOW_LANG, "english")
                .put("ep_start", episodeStart)
                .put("ep_end", episodeEnd)
                .put(StringHandler.SHOW_IMAGE_URL, imageURL)
                .put("episodes", episodes), ProviderType.GOGOANIME);
    }
}
