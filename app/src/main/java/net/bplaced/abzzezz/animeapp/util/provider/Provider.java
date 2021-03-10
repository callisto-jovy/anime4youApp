/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:21
 */

package net.bplaced.abzzezz.animeapp.util.provider;

import android.content.Context;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONArray;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Provider {

    private final String name;

    public Provider(String name) {
        this.name = name;
    }

    public abstract void refreshShow(final Show show, final Consumer<Show> updatedShow);

    public abstract void getShowEpisodeReferrals(final Show show, final Consumer<JSONArray> showReferrals);

    public abstract void handleURLRequest(Show show, final Context context, Consumer<Optional<String>> resultURL, int... ints);

    public abstract void handleDownload(SelectedActivity activity, final String url, final Show show, final File outDirectory, final int... ints);

    public String getName() {
        return name;
    }

}
