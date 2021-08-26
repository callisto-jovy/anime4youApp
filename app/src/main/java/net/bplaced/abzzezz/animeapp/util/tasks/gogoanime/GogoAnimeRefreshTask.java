/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 23:37
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

    @Override
    public Show call() throws Exception {
        final Optional<JSONObject> providerJSON = showIn.getProviderJSON(Providers.GOGOANIME.getProvider());

        final int start = providerJSON.map(jsonObject -> jsonObject.optInt("ep_start")).orElse(0);

        showIn.addEpisodesForProvider(GogoAnimeFetcher.fetchReferrals(showIn.getID(), start), Providers.GOGOANIME.getProvider());
        return showIn;
    }
}
