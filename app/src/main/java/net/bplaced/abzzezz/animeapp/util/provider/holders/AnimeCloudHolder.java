/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.02.21, 12:53
 */

package net.bplaced.abzzezz.animeapp.util.provider.holders;

public interface AnimeCloudHolder {
    //TODO: Nonce

    String SEARCH_API = "https://animecloud.org/wp-json/dooplay/search/?keyword=%s&nonce=25514244e4";




    default String getGeneratedNonce() {
        return "";
    }
}
