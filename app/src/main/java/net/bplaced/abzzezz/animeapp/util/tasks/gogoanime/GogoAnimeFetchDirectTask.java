/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 23:34
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.util.Optional;
import java.util.concurrent.Callable;

public class GogoAnimeFetchDirectTask extends TaskExecutor implements Callable<Optional<String>> {

    private final String referral;

    public GogoAnimeFetchDirectTask(final String referral) {
        this.referral = referral;
    }

    public void executeAsync(final Callback<Optional<String>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public Optional<String> call() throws Exception {
        return GogoAnimeFetcher.fetchDownloadLink(referral);
    }

}
