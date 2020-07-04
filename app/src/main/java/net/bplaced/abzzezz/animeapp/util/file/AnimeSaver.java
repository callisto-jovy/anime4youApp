/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 14:08
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collector;

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
    }

    /**
     * Add anime with key and values to preference hashmap
     * then commit
     *
     * @param all
     */
    public void add(String... all) {
        if (all[0].equals("ERROR")) return;

        boolean check = publicPreferences.getBoolean("check_existing", false);
        String add = all[0].replaceAll(":", "") + StringUtil.splitter + all[1] + StringUtil.splitter + all[2] + StringUtil.splitter + all[3];
        String key = String.valueOf(preferences.getAll().size());

        if (check) {
            if (!containsAid(all[3])) {
                editor.putString(key, add);
                editor.commit();
            }
        } else {
            editor.putString(key, add);
            editor.commit();
        }
    }

    public boolean containsAid(String aid) {
        return preferences.getAll().values().stream().anyMatch(o -> o.toString().split(StringUtil.splitter)[3].equals(aid));
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
        //Remove key (int)
        editor.remove(String.valueOf(key));
        /*
        Move all upcoming entries one down
         */
        for (int i = key; i < preferences.getAll().size() - /*One gone */ 1; i++) {
            editor.putString(String.valueOf(i), preferences.getString(String.valueOf(i + /* Next one */ 1), "NULL"));
            editor.remove(String.valueOf(i + 1));
        }
        //Apply to file
        editor.apply();
    }

    /**
     * Name: 0
     * Episodes: 1
     * ImageURL: 2
     * AID: 3
     *
     * @param index
     * @return
     */
    public String[] getAll(int index) {
        return preferences.getString(String.valueOf(index), "NULL").split(StringUtil.splitter);
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
