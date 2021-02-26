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
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheEpisodeDownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheFetchDirectTask;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheRefreshTask;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheSearchTask;
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
        new AnimePaheRefreshTask(show).executeAsync(new TaskExecutor.Callback<Show>() {
            @Override
            public void onComplete(Show result) {
                updatedShow.accept(result);
            }

            @Override
            public void preExecute() {
                Logger.log("Refreshing show", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void handleSearch(String searchQuery, Consumer<List<Show>> searchResults) {
        new AnimePaheSearchTask(searchQuery).executeAsync(new TaskExecutor.Callback<List<Show>>() {
            @Override
            public void onComplete(List<Show> result) {
                searchResults.accept(result);
            }

            @Override
            public void preExecute() {
                Logger.log("Searching anime-pahe", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public JSONObject formatShowForSave(Show show) throws JSONException {
        return new JSONObject()
                .put(StringHandler.SHOW_ID, show.getID())
                .put(StringHandler.SHOW_TITLE, show.getTitle())
                .put(StringHandler.SHOW_LANG, show.getLanguage())
                .put(StringHandler.SHOW_EPISODE_COUNT, show.getEpisodes())
                .put(StringHandler.SHOW_IMAGE_URL, show.getImageURL())
                .put("session", show.getShowAdditional().getString("session"))
                .put(StringHandler.SHOW_YEAR, show.getYear())
                .put("src", show.getShowAdditional().getJSONArray("src"))
                .put(StringHandler.SHOW_PROVIDER, Providers.ANIMEPAHE.name());
    }

    @Override
    public Show getShowFromProvider(JSONObject data) throws JSONException {
        final Show show = new Show(data.getString("id"),
                data.getString("title"),
                String.valueOf(data.getJSONArray("src").length()),
                data.getString("poster"),
                "eng",
                this,
                new JSONObject()
                        .put("session", data.getString("session"))
                        .put("src", data.getJSONArray("src")));

        show.setYear(data.getString("year"));
        return show;
    }

    @Override
    public Show getShowFromSave(JSONObject showJSON) throws JSONException {
        final Show show = new Show(
                showJSON.getString(StringHandler.SHOW_ID),
                showJSON.getString(StringHandler.SHOW_TITLE),
                showJSON.getString(StringHandler.SHOW_EPISODE_COUNT),
                showJSON.getString(StringHandler.SHOW_IMAGE_URL),
                showJSON.getString(StringHandler.SHOW_LANG),
                this,
                new JSONObject()
                        .put("session", showJSON.getString("session"))
                        .put("src", showJSON.getJSONArray("src")));

        show.setYear(showJSON.getString(StringHandler.SHOW_YEAR));
        return show;
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<String>> resultURL, int... ints) {
        try {
            new AnimePaheFetchDirectTask(show.getShowAdditional().getJSONArray("src").getString(ints[1])).executeAsync(new TaskExecutor.Callback<String>() {
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
        new AnimePaheEpisodeDownloadTask(activity, url, show.getTitle(), outDirectory, ints).executeAsync();
    }

    @Override
    public void handleImportMAL(String malURL, Consumer<List<Show>> matchingShows) {

    }
}
