/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;

import java.util.concurrent.Callable;

public class GogoAnimeRefreshTask extends TaskExecutor implements Callable<Show> {

    private final Show showIn;

    public GogoAnimeRefreshTask(final Show showIn) {
        this.showIn = showIn;
    }

    public void executeAsync(Callback<Show> callback) {
        super.executeAsync(this, callback);
    }

    /*
    TODO: Only fetches the episodes for now. Add URL to additional json!
     */
    @Override
    public Show call() throws Exception {
        final int start = showIn.getProviderJSON(Providers.GOGOANIME.getProvider()).getInt("ep_start");
        final int end = showIn.getProviderJSON(Providers.GOGOANIME.getProvider()).getInt("ep_end");
        final String[] fetchedDirectURLs = GogoAnimeFetcher.fetchIDs(showIn.getID(), start, end);

        final JSONArray episodes = new JSONArray();

        for (final String fetchedDirectURL : fetchedDirectURLs) episodes.put(fetchedDirectURL);

        showIn.addEpisodesForProvider(episodes, Providers.GOGOANIME.getProvider());
        return showIn;
    }
}
