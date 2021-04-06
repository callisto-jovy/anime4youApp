/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONObject;

import java.util.Optional;
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
        final Optional<JSONObject> providerJSON = showIn.getProviderJSON(Providers.GOGOANIME.getProvider());

        final int start = providerJSON.map(jsonObject -> jsonObject.optInt("ep_start")).orElse(0);
        final int end = providerJSON.map(jsonObject -> jsonObject.optInt("ep_end")).orElse(0);

        showIn.addEpisodesForProvider(GogoAnimeFetcher.fetchReferrals(showIn.getID(), start, end), Providers.GOGOANIME.getProvider());
        return showIn;
    }
}
