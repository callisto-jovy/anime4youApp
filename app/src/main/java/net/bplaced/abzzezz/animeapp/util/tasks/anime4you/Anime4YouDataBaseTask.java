/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:07
 */

package net.bplaced.abzzezz.animeapp.util.tasks.anime4you;

import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.ProviderType;
import net.bplaced.abzzezz.animeapp.util.scripter.Anime4YouDBSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class Anime4YouDataBaseTask implements Callable<Show> {

    private final Anime4YouDBSearch anime4YouDBSearch;
    private final String id;

    public Anime4YouDataBaseTask(String id, Anime4YouDBSearch anime4YouDBSearch) {
        this.anime4YouDBSearch = anime4YouDBSearch;
        this.id = id;
    }

    @Override
    public Show call() {
        try {
            return new Show(getDetails(new JSONObject(anime4YouDBSearch.getShowDetails("{\"aid\":\"" + id.concat("\"")))), ProviderType.ANIME4YOU);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getDetails(final JSONObject jsonObject) throws Exception {
        return new JSONObject()
                .put(StringHandler.SHOW_ID, id)
                .put(StringHandler.SHOW_IMAGE_URL, StringHandler.COVER_DATABASE.concat(jsonObject.getString("image_id")))
                .put(StringHandler.SHOW_EPISODE_COUNT, jsonObject.getString("Letzte"))
                .put(StringHandler.SHOW_TITLE, jsonObject.getString("titel"))
                .put(StringHandler.SHOW_LANG, jsonObject.getString("Untertitel"))
                .put(StringHandler.SHOW_YEAR, jsonObject.getString("Jahr"))
                .put(StringHandler.SHOW_PROVIDER, StringHandler.SHOW_PROVIDER_ANIME4YOU);
    }
}
