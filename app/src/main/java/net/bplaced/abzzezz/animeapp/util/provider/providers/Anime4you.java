/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:28
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.ProviderType;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONException;
import org.json.JSONObject;

public class Anime4you extends Provider {

    public Anime4you() {
        super(ProviderType.ANIME4YOU, StringHandler.DATABASE);
    }

    @Override
    public JSONObject format(final Show show) throws JSONException {
        return new JSONObject()
                .put(StringHandler.SHOW_ID, show.getID())
                .put(StringHandler.SHOW_IMAGE_URL, show.getImageURL())
                .put(StringHandler.SHOW_EPISODE_COUNT, show.getEpisodes())
                .put(StringHandler.SHOW_TITLE, show.getTitle())
                .put(StringHandler.SHOW_LANG, show.getLanguage())
                .put(StringHandler.SHOW_YEAR, show.getYear())
                .put(StringHandler.SHOW_PROVIDER, ProviderType.ANIME4YOU.name());
    }

    @Override
    public Show getShow(final JSONObject data) throws JSONException {
        return new Show(data.getString("aid"),
                data.getString("titel"),
                data.getString("Letzte"),
                StringHandler.COVER_DATABASE.concat(data.getString("image_id")),
                data.getString("Untertitel"),
                ProviderType.ANIME4YOU.getProvider());
    }


    @Override
    public void handleDownload() {

    }
}
