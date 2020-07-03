/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.07.20, 20:30
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import android.content.SharedPreferences;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;

/**
 * Keeps track of all animes marked with notifications. Every hour the list gets checked for a new episode
 */

public class AnimeNotifications {


    /**
     * Editor and preferences
     */
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private final DataBaseSearch dataBaseSearch;

    /**
     * Aids used as keys. The other values stay the same
     */

    public AnimeNotifications(Context context) {
        this.dataBaseSearch = new DataBaseSearch();
        this.preferences = context.getSharedPreferences("AnimeNotifications", Context.MODE_PRIVATE);
        this.editor = preferences.edit();
        Logger.log("Anime Notifications set up.", Logger.LogType.INFO);
        //  editor.clear().apply();
    }

    /**
     * Add anime with key and values to preference hashmap
     * then commit
     *
     * @param key
     */
    public void add(String key, String episodes) {
        if (getPreferences().contains(key)) return;

        editor.putString(key, episodes);
        editor.apply();
    }

    public void updateKey(String key, String newCount) {
        editor.remove(key);
        editor.putString(key, newCount);
        editor.apply();
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    /**
     * Remove key from map then instantly commit
     *
     * @param key
     */
    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }
}
