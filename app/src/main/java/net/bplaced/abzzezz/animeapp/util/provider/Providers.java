/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:22
 */

package net.bplaced.abzzezz.animeapp.util.provider;

import net.bplaced.abzzezz.animeapp.util.provider.providers.Anime4you;
import net.bplaced.abzzezz.animeapp.util.provider.providers.GogoAnime;

public enum Providers {

    ANIME4YOU(new Anime4you()), GOGOANIME(new GogoAnime());

    private final Provider provider;

    Providers(Provider provider) {
        this.provider = provider;
    }

    public static Provider getProvider(final java.lang.String enumValue) {
        return valueOf(enumValue).getProvider();
    }

    public Provider getProvider() {
        return provider;
    }
}
