/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.04.21, 18:02
 */

package net.bplaced.abzzezz.animeapp.util.provider.holders;

import net.bplaced.abzzezz.animeapp.util.Constant;

import java.util.concurrent.ThreadLocalRandom;

public interface TwistmoeHolder {

    String SHOW_API = "https://twist.moe/api/anime/";
    String KEY = "267041df55ca2b36f2e322d05ee2c9cf";//"LXgIVP&PorO68Rq7dTx8N^lP!Fa5sGJ^*XK";
    String STREAM_URL = "https://cdn.twist.moe/";
    String[][] REQUEST_HEADERS = new String[][]{new String[]{"x-access-token", "0df14814b9e590a1f26d3071a4ed7974"}, new String[]{"User-Agent", Constant.USER_AGENT}};

    default String getRequestToken() {
        final String[] tokens = {"0df14814b9e590a1f26d3071a4ed7974"};
        return tokens[ThreadLocalRandom.current().nextInt(tokens.length)];
    }
}
