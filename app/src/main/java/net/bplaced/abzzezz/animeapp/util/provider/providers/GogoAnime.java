/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:30
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.ProviderType;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONException;
import org.json.JSONObject;

public class GogoAnime extends Provider {

    public GogoAnime() {
        super(ProviderType.GOGOANIME, "");
    }

    @Override
    public JSONObject format(Show show) throws JSONException {
        return null;
    }

    @Override
    public Show getShow(JSONObject data) throws JSONException {
        return null;
    }

    @Override
    public void handleDownload() {

    }
}
