/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:07
 */

package net.bplaced.abzzezz.animeapp.util.tasks.anime4you;

import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.scripter.Anime4YouDBSearch;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class Anime4YouDataBaseTask implements Callable<Show> {

    private final Anime4YouDBSearch anime4YouDBSearch;
    private final java.lang.String id;

    public Anime4YouDataBaseTask(java.lang.String id, Anime4YouDBSearch anime4YouDBSearch) {
        this.anime4YouDBSearch = anime4YouDBSearch;
        this.id = id;
    }

    @Override
    public Show call() {
        try {
            return Providers.ANIME4YOU.getProvider().getShow(new JSONObject(anime4YouDBSearch.getShowDetails("{\"aid\":\"" + id.concat("\""))));
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
