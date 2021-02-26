/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:22
 */

package net.bplaced.abzzezz.animeapp.util.provider;

import net.bplaced.abzzezz.animeapp.util.provider.providers.AnimeCloud;
import net.bplaced.abzzezz.animeapp.util.provider.providers.AnimePahe;
import net.bplaced.abzzezz.animeapp.util.provider.providers.GogoAnime;
import net.bplaced.abzzezz.animeapp.util.provider.providers.Twistmoe;

public enum Providers {

    GOGOANIME(new GogoAnime()),
    TWISTMOE(new Twistmoe()),
    ANIMEPAHE(new AnimePahe()),
    ANIMECLOUD(new AnimeCloud()),
    //Null provider, for old providers; Skipped when iterating
    NULL(null);

    private final Provider provider;

    Providers(Provider provider) {
        this.provider = provider;
    }

    public static Provider getProvider(final String enumValue) {
        return valueOf(enumValue).getProvider();
    }

    public Provider getProvider() {
        return provider;
    }
}
