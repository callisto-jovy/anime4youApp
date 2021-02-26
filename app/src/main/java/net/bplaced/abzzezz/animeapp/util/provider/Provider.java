/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:21
 */

package net.bplaced.abzzezz.animeapp.util.provider;

import android.content.Context;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Provider {

    private final String name;

    public Provider(String name) {
        this.name = name;
    }

    public abstract void refreshShow(final Show show, final Consumer<Show> updatedShow);

    public abstract void handleSearch(final String searchQuery, final Consumer<List<Show>> searchResults);

    public abstract JSONObject formatShowForSave(final Show show) throws JSONException;

    public abstract Show getShowFromProvider(final JSONObject data) throws JSONException;

    public abstract Show getShowFromSave(JSONObject showJSON) throws JSONException;

    public abstract void handleURLRequest(Show show, final Context context, Consumer<Optional<String>> resultURL, int... ints);

    public abstract void handleDownload(SelectedActivity activity, final String url, final Show show, final File outDirectory, final int... ints);

    public abstract void handleImportMAL(final String malURL, final Consumer<List<Show>> matchingShows);

    public String getName() {
        return name;
    }

}
