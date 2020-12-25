/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:05
 */

package net.bplaced.abzzezz.animeapp.util.tasks.anime4you;

import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.provider.providers.Anime4you;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.ricecode.similarity.JaroStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Anime4YouSearchDBTask extends TaskExecutor implements Callable<List<Show>> {

    private final java.lang.String input;
    private final SimilarityStrategy stringSimilarity = new JaroStrategy();

    public Anime4YouSearchDBTask(final java.lang.String input) {
        this.input = input;
    }

    public <R> void executeAsync(Callback<List<Show>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<Show> call() throws Exception {
        final List<Show> showsOut = new ArrayList<>();
        final JSONArray showsIn = new JSONArray(Anime4you.ANIME_4_YOU_DB_SEARCH.getDataBase());
        final Anime4you decoder = (Anime4you) Providers.ANIME4YOU.getProvider();

        for (int i = 0; i < showsIn.length(); i++) {
            final Show show = decoder.getShow(showsIn.getJSONObject(i));

            if (stringSimilarity.score(show.getTitle(), input) > 0.8 && !showsOut.contains(show)) {
                showsOut.add(show);
            }
        }
        return showsOut;
    }
}
