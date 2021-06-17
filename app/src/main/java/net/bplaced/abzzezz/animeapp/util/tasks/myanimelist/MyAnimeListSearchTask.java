/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.04.21, 23:28
 */

package net.bplaced.abzzezz.animeapp.util.tasks.myanimelist;

import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.sandrohc.jikan.Jikan;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MyAnimeListSearchTask extends TaskExecutor implements Callable<List<Show>> {

    private final String searchQuery;

    public MyAnimeListSearchTask(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void executeAsync(Callback<List<Show>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<Show> call() throws Exception {
        final Jikan jikan = new Jikan();
        return jikan
                .query()
                .anime()
                .search()
                .query(searchQuery)
                .execute()
                .toStream()
                .map(anime ->
                        new Show(
                                String.valueOf(anime.malId),
                                anime.title,
                                anime.episodes,
                                anime.imageUrl,
                                anime.score
                        )
                ).collect(Collectors.toList());
    }
}
