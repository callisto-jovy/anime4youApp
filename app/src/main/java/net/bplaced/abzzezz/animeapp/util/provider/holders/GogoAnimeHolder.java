/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 12.05.21, 14:38
 */

package net.bplaced.abzzezz.animeapp.util.provider.holders;

import java.util.regex.Pattern;

public interface GogoAnimeHolder {

    String BASE_URL = "https://gogoanime.pe";

    String BASE_SEARCH_API = "https://gogoanime.pe/search.html?keyword=%s";

    //Start, end, anime-id, Returns a "list" containing all redirects to the other episodes
    String EPISODE_API_URL = "https://ajax.gogo-load.com/ajax/load-list-episode?ep_start=%d&ep_end=1000000&id=%d";

    String FORMATTED_SEARCH_API = "https://gogoanime.pe/search.html?keyword=%s";

    Pattern SOURCE_PATTERN = Pattern.compile("(?<=sources:\\[\\{file: ')[^']+");
}
