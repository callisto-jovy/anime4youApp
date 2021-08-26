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
import net.sandrohc.jikan.model.enums.UserAnimeWatchingStatus;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

public class MyAnimeListSyncTask extends TaskExecutor implements Callable<Boolean>, MyAnimeListHolder {

    private final String username;
    private final Jikan jikan;

    public MyAnimeListSyncTask(final Jikan jikan, final String username) {
        this.username = username;
        this.jikan = jikan;
    }

    public void executeAsync(Callback<Boolean> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public Boolean call() throws Exception {
        final UserAnimeQueryFactory animeQueryFactory = jikan
                .query()
                .user(username)
                .anime();

        //Adds all planed to watch and watching shows
        Flux.concat(
                animeQueryFactory.list(0).status(UserAnimeWatchingStatus.WATCHING).execute(),
                animeQueryFactory.list(0).status(UserAnimeWatchingStatus.PLAN_TO_WATCH).execute())
                .toStream()
                .forEach(userAnime ->
                        AnimeAppMain.INSTANCE.getShowSaver().addShow(
                                new Show(
                                        String.valueOf(userAnime.malId),
                                        userAnime.title,
                                        userAnime.totalEpisodes,
                                        userAnime.image_url,
                                        userAnime.score,
                                        userAnime.watchedEpisodes,
                                        userAnime.watchingStatus
                                )));
        return true;
    }
}
