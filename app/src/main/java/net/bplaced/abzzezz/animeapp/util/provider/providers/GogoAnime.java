/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:30
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.gogoanime.GogoAnimeEpisodeDownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.gogoanime.GogoAnimeFetchDirectTask;
import net.bplaced.abzzezz.animeapp.util.tasks.gogoanime.GogoAnimeRefreshTask;
import net.bplaced.abzzezz.animeapp.util.tasks.gogoanime.GogoAnimeSearchTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GogoAnime extends Provider {

    public GogoAnime() {
        super("GOGOANIME");
    }

    @Override
    public void refreshShow(Show show, Consumer<Show> updatedShow) {
        new GogoAnimeRefreshTask(show).executeAsync(new TaskExecutor.Callback<Show>() {
            @Override
            public void onComplete(Show result) {
                updatedShow.accept(result);
            }

            @Override
            public void preExecute() {
                Logger.log("Updating show from gogo-anime", Logger.LogType.INFO);
            }
        });
    }


    @Override
    public void getShowEpisodeReferrals(Show show, Consumer<JSONArray> showReferrals) {
        new GogoAnimeSearchTask(show.getShowTitle()).executeAsync(new TaskExecutor.Callback<List<JSONObject>>() {
            @Override
            public void onComplete(final List<JSONObject> result) {
                if (result.size() > 1) {
                    //This is bad..... I haven't thought this through.... I have to gamble i guess
                    show.getProviderJSON(GogoAnime.this).ifPresent(providerJSON -> {
                        try {
                            final JSONObject resultJSON = result.get(0);

                            providerJSON.put("ep_start", resultJSON.getInt("ep_start"))
                                    .put("ep_end", resultJSON.getInt("ep_end"));

                            show.updateProviderJSON(GogoAnime.this, providerJSON);

                            showReferrals.accept(resultJSON.getJSONArray("referrals"));
                        } catch (final JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void preExecute() {
                Logger.log("Searching gogo-anime", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<String>> resultURL, int... ints) {
        try {
            final JSONArray episodes = show.getShowEpisodes(this);
            new GogoAnimeFetchDirectTask(episodes.getString(episodes.length() - (ints[1] + 1))).executeAsync(new TaskExecutor.Callback<Optional<String>>() {
                @Override
                public void onComplete(final Optional<String> result) {
                    resultURL.accept(result);
                }

                @Override
                public void preExecute() {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleDownload(SelectedActivity activity, String url, Show show, File outDirectory, int... ints) {
        new GogoAnimeEpisodeDownloadTask(activity, url, show.getShowTitle(), outDirectory, ints).executeAsync();
    }
}
