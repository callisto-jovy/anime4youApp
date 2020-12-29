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
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeSearchTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Twistmoe extends Provider {

    public Twistmoe() {
        super("TWISTMOE");
    }

    @Override
    public void refreshShow(Show show, Consumer<Show> updatedShow) {

    }

    @Override
    public void handleSearch(String searchQuery, Consumer<List<Show>> searchResults) {
        new TwistmoeSearchTask(searchQuery).executeAsync(new TaskExecutor.Callback<List<Show>>() {
            @Override
            public void onComplete(List<Show> result) {
                searchResults.accept(result);
            }

            @Override
            public void preExecute() {
                Logger.log("Searching twist.moe", Logger.LogType.INFO);
            }
        });
    }


    @Override
    public Show decode(JSONObject showJSON) throws JSONException {
        return new Show(
                showJSON.getString(StringHandler.SHOW_ID),
                showJSON.getString(StringHandler.SHOW_TITLE),
                showJSON.getString(StringHandler.SHOW_EPISODE_COUNT),
                showJSON.getString(StringHandler.SHOW_IMAGE_URL),
                showJSON.getString(StringHandler.SHOW_LANG),
                Providers.TWISTMOE.getProvider(),
                new JSONObject().put("src", showJSON.getJSONArray("src")));
    }

    @Override
    public JSONObject format(Show show) throws JSONException {
        return new JSONObject()
                .put(StringHandler.SHOW_ID, show.getID())
                .put(StringHandler.SHOW_TITLE, show.getTitle())
                .put(StringHandler.SHOW_LANG, show.getLanguage())
                .put(StringHandler.SHOW_EPISODE_COUNT, show.getEpisodes())
                .put(StringHandler.SHOW_IMAGE_URL, show.getImageURL())
                .put("src", show.getShowAdditional().getJSONArray("src"))
                .put(StringHandler.SHOW_PROVIDER, Providers.GOGOANIME.name());
    }

    @Override
    public Show getShow(JSONObject data) throws JSONException {
        final String title = data.getString("title");
        final String imageURL = String.format(StringHandler.IMAGE_URL, title);
        final JSONObject additional = new JSONObject().put("src", data.getJSONArray("sources"));
        return new Show(data.getString("id"), title, data.getString("episodes"), imageURL, "eng-sub", Providers.TWISTMOE.getProvider(), additional);
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<URL>> resultURL, int... ints) {

    }

    @Override
    public void handleDownload(SelectedActivity activity, URL url, Show show, File outDirectory, int... ints) {

    }

}
