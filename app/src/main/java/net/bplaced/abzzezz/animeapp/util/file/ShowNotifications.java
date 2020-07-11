/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.07.20, 20:30
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import android.content.SharedPreferences;
import ga.abzzezz.util.logging.Logger;

/**
 * Keeps track of all animes marked with notifications. Every hour the list gets checked for a new episode
 */

public class ShowNotifications {


    /**
     * Editor and preferences
     */
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    /**
     * Aids used as keys. The other values stay the same
     */

    public ShowNotifications(final Context context) {
        this.preferences = context.getSharedPreferences("AnimeNotifications", Context.MODE_PRIVATE);
        this.editor = preferences.edit();
        Logger.log("Anime Notifications set up.", Logger.LogType.INFO);
    }

    /**
     * Add anime with key and values to preference hashmap
     * then commit
     *
     * @param key
     */
    public void add(final String key, final String episodes) {
        if (getPreferences().contains(key)) return;

        editor.putString(key, episodes);
        editor.apply();
    }

    /**
     * Update given key
     *
     * @param key
     * @param newCount
     */
    public void updateKey(final String key, final int newCount) {
        editor.remove(key);
        editor.putString(key, String.valueOf(newCount));
        editor.apply();
    }

    /**
     * @return
     */
    public SharedPreferences getPreferences() {
        return preferences;
    }

    /**
     * Remove key from map then instantly commit
     *
     * @param key
     */
    public void remove(final String key) {
        editor.remove(key);
        editor.apply();
    }
}
