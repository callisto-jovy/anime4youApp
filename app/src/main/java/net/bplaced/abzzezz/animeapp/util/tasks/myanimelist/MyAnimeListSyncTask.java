/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 30.05.21, 18:10
 */

package net.bplaced.abzzezz.animeapp.util.tasks.myanimelist;

import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.provider.holders.MyAnimeListHolder;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.sandrohc.jikan.Jikan;
import net.sandrohc.jikan.factory.UserAnimeQueryFactory;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

public class MyAnimeListSyncTask extends TaskExecutor implements Callable<Boolean>, MyAnimeListHolder {

    private final String user;
    private final Jikan jikan;

    public MyAnimeListSyncTask(final Jikan jikan, final String user) {
        this.user = user;
        this.jikan = jikan;
    }

    public void executeAsync(Callback<Boolean> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public Boolean call() throws Exception {
        //Plan to watch = 6
        //Watching = 1
        final UserAnimeQueryFactory animeQueryFactory = jikan
                .query()
                .user(user)
                .anime();

        //Adds all planed to watch and watching shows
        Flux.concat(animeQueryFactory.list(1).execute(), animeQueryFactory.list(6).execute())
                .toStream()
                .forEach(userAnime ->
                        AnimeAppMain.getInstance().getShowSaver().addShow(
                                new Show(
                                        String.valueOf(userAnime.malId),
                                        userAnime.title,
                                        userAnime.totalEpisodes,
                                        userAnime.image_url,
                                        userAnime.score,
                                        userAnime.watchedEpisodes
                                )));
        return true;
    }
}
