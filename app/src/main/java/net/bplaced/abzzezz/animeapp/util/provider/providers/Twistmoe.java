/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.12.20, 17:26
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeDecodeSourcesTask;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeEpisodeDownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeFetchCallable;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeSearchTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Twistmoe extends Provider {

    public Twistmoe() {
        super("TWISTMOE");
    }

    @Override
    public void refreshShow(final Show show, final Consumer<Show> updatedShow) {
        new TaskExecutor().executeAsync(() ->
                new TwistmoeFetchCallable(show.getProviderJSON(this).getString("slug")).call(), new TaskExecutor.Callback<JSONObject>() {
            @Override
            public void onComplete(JSONObject result) {
                try {
                    show.addEpisodesForProvider(result.getJSONArray("src"), Twistmoe.this);
                    updatedShow.accept(show);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void preExecute() {
                Logger.log("Refreshing twist.moe", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void getShowEpisodeReferrals(Show show, Consumer<JSONArray> showReferrals) {
        new TwistmoeSearchTask(show.getID(), show.getShowTitle()).executeAsync(new TaskExecutor.Callback<List<JSONObject>>() {
            @Override
            public void onComplete(List<JSONObject> result) {
                if (result.size() >= 1) {
                    //This is bad..... I haven't thought this through.... I have to gamble i guess
                    try {
                        final JSONObject providerJSON = show.getProviderJSON(Twistmoe.this);
                        providerJSON.put("slug", result.get(0).getString("url"));
                        show.updateProviderJSON(Twistmoe.this, providerJSON);

                        showReferrals.accept(result.get(0).getJSONArray("src"));
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void preExecute() {
                Logger.log("Searching twist.moe", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<String>> resultURL, int... ints) {
        try {
            new TwistmoeDecodeSourcesTask(show.getShowEpisodes(this).getString(ints[1])).executeAsync(new TaskExecutor.Callback<String>() {
                @Override
                public void onComplete(String result) {
                    resultURL.accept(Optional.of(result));
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
        new TwistmoeEpisodeDownloadTask(activity, url, show.getShowTitle(), outDirectory, ints).executeAsync();
    }
}
