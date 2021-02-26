/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.02.21, 15:43
 */

package net.bplaced.abzzezz.animeapp.util.provider.holders;

import java.util.regex.Pattern;

public interface AnimePaheHolder {

    String SEARCH_API = "https://animepahe.com/api?m=search&q=%s";

    String EPISODE_API = "https://animepahe.com/api?m=release&id=%s&sort=episode_asc&page=1";

    String STREAM_API = "https://animepahe.com/api?m=embed&id=%s&session=%s&p=kwik";

    String ANIME_PAHE_REFERER = "https://animepahe.com/";

    Pattern videoSrcPattern = Pattern.compile("const source='(.*?)'");
}
