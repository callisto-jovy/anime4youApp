/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.02.21, 15:41
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheEpisodeDownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheFetchDirectTask;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheRefreshTask;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheSearchTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class AnimePahe extends Provider {

    public AnimePahe() {
        super("ANIMEPAHE");
    }

    @Override
    public void refreshShow(Show show, Consumer<Show> updatedShow) {
        new AnimePaheRefreshTask(show).executeAsync(new TaskExecutor.Callback<JSONObject>() {
            @Override
            public void onComplete(JSONObject result) {
                //This is bad..... I haven't thought this through.... I have to gamble i guess
                try {
                    final JSONObject providerJSON = show.getProviderJSON(AnimePahe.this);
                    providerJSON.put("session", result.getString("session"));
                    show.updateProviderJSON(AnimePahe.this, providerJSON);

                    updatedShow.accept(show);
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void preExecute() {
                Logger.log("Refreshing show", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void getShowEpisodeReferrals(final Show show, final Consumer<JSONArray> showReferrals) {
        new AnimePaheSearchTask(show.getShowTitle()).executeAsync(new TaskExecutor.Callback<List<JSONObject>>() {
            @Override
            public void onComplete(List<JSONObject> result) {
                if (result.size() >= 1) {
                    //This is bad..... I haven't thought this through.... I have to gamble i guess
                    try {
                        final JSONObject providerJSON = show.getProviderJSON(AnimePahe.this);
                        providerJSON.put("session", result.get(0).getString("session"));
                        show.updateProviderJSON(AnimePahe.this, providerJSON);

                        showReferrals.accept(result.get(0).getJSONArray("src"));
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void preExecute() {
                Logger.log("Searching anime-pahe", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<String>> resultURL, int... ints) {
        try {
            new AnimePaheFetchDirectTask(show.getShowEpisodes(this).getString(ints[1])).executeAsync(new TaskExecutor.Callback<String>() {
                @Override
                public void onComplete(final String result) {
                    resultURL.accept(Optional.of(result));
                }

                @Override
                public void preExecute() {
                    Logger.log("Fetching direct video link", Logger.LogType.INFO);
                }
            });
        } catch (final JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleDownload(SelectedActivity activity, String url, Show show, File outDirectory, int... ints) {
        new AnimePaheEpisodeDownloadTask(activity, url, show.getShowTitle(), outDirectory, ints).executeAsync();
    }
}
