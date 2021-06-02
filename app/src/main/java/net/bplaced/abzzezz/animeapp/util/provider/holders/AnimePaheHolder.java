/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.04.21, 18:01
 */

package net.bplaced.abzzezz.animeapp.util.provider.holders;

import java.util.regex.Pattern;

public interface AnimePaheHolder {

    String SEARCH_API = "https://animepahe.com/api?m=search&q=%s"; //Animepahe's search api, with pre-made format

    String EPISODE_API = "https://animepahe.com/api?m=release&id=%s&sort=episode_asc&page=%d"; //Animepahe's episode api, with pre-made format returns all the episodes available

    String STREAM_API = "https://animepahe.com/api?m=embed&id=%s&session=%s&p=kwik"; //Animepahe's stream episode api, with pre-made format

    String ANIME_PAHE_REFERER = "https://animepahe.com/";

    Pattern VIDEO_SRC_PATTERN = Pattern.compile("const source='(.*?)'"); //Video source pattern to extract the direct video url...
}
