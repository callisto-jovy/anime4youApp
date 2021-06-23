/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 12.05.21, 14:38
 */

package net.bplaced.abzzezz.animeapp.util.provider.holders;

import java.util.regex.Pattern;

public interface GogoAnimeHolder {

    String BASE_URL = "https://gogoanime.sh";
    //Download API endpoint
    String API_URL = "https://streamani.net/download?id=%s";
    //Start, end, anime-id, Returns a "list" containing all redirects to the other episodes
    String EPISODE_API_URL = "https://ajax.gogo-load.com/ajax/load-list-episode?ep_start=%d&ep_end=%d&id=%d";
    //TODO: New search API (it ain't broke, so don't fix it :))
    String SEARCH_URL = "https://gogoanime.so/search.html?keyword=%s";

    Pattern PATTERN = Pattern.compile("id=.+&");
}
