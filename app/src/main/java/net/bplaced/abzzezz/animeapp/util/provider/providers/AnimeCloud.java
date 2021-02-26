/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.02.21, 12:50
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class AnimeCloud extends Provider {

    public AnimeCloud() {
        super("ANIMECLOUD");
    }

    @Override
    public void refreshShow(Show show, Consumer<Show> updatedShow) {

    }

    @Override
    public void handleSearch(String searchQuery, Consumer<List<Show>> searchResults) {

    }

    @Override
    public JSONObject formatShowForSave(Show show) throws JSONException {
        return null;
    }

    @Override
    public Show getShowFromProvider(JSONObject data) throws JSONException {
        return null;
    }

    @Override
    public Show getShowFromSave(JSONObject showJSON) throws JSONException {
        return null;
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<String>> resultURL, int... ints) {

    }

    @Override
    public void handleDownload(SelectedActivity activity, String url, Show show, File outDirectory, int... ints) {

    }

    @Override
    public void handleImportMAL(String malURL, Consumer<List<Show>> matchingShows) {

    }
}
