/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.05.20, 19:54
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;

import java.util.ArrayList;
import java.util.Collection;

public class AnimeSaver {

    /**
     * Editor and preferences
     */
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences publicPreferences;

    /**
     * Aids used as keys. The other values stay the same
     */

    public AnimeSaver(Context context) {
        this.preferences = context.getSharedPreferences("AnimeList", Context.MODE_PRIVATE);
        this.editor = preferences.edit();
        this.publicPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Logger.log("Anime Saver set up.", Logger.LogType.INFO);
        preferences.getAll().entrySet().forEach(System.out::println);
    }

    /**
     * Add anime with key and values to preference hashmap
     * then commit
     *
     * @param all
     */
    public void add(String... all) {
        boolean check = publicPreferences.getBoolean("check_existing", false);
        String add = all[0].replaceAll(":", "") + StringUtil.splitter + all[1] + StringUtil.splitter + all[2] + StringUtil.splitter + all[3];
        if (check) {
            if (!containsAid(all[3])) {
                editor.putString(String.valueOf(preferences.getAll().size()), add);
                editor.commit();
            }
        } else {
            editor.putString(String.valueOf(preferences.getAll().size()), add);
            editor.commit();
        }
    }

    public boolean containsAid(String aid) {
        return preferences.getAll().values().stream().filter(o -> o.toString().split(StringUtil.splitter)[3].equals(aid)).count() >= 1;
    }

    /**
     * Calls @add
     *
     * @param string
     */
    public void add(String string) {
        add(string.split(StringUtil.splitter));
    }

    /**
     * Remove key from map then instantly commit
     *
     * @param key
     */
    public void remove(int key) {
        editor.remove(String.valueOf(key));
        editor.apply();
    }

    /**
     * Name: 0
     * Episodes: 1
     * ImageURL: 2
     * AID: 3
     *
     * @param anime
     * @return
     */
    public String[] getAll(int anime) {
        return preferences.getString(String.valueOf(anime), "NULL").split(StringUtil.splitter);
    }

    /**
     * Return list, same as before
     *
     * @return
     */
    public ArrayList<String> getList() {
        return new ArrayList<>((Collection<? extends String>) preferences.getAll().values());
    }

}
