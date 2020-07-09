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
import org.json.JSONException;
import org.json.JSONObject;

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

    public AnimeSaver(final Context context) {
        this.preferences = context.getSharedPreferences("AnimeList", Context.MODE_PRIVATE);
        this.editor = preferences.edit();
        this.publicPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Logger.log("Anime Saver set up.", Logger.LogType.INFO);
        //TODO: Remove for next version
        preferences.getAll().values().forEach(o -> {
            if (o.toString().contains(StringUtil.splitter)) {
                editor.clear().commit();
            }
        });

    }

    /**
     * Add anime with key and values to preference hashmap
     * then commit
     *
     * @param all
     */
    public void add(final JSONObject all) throws JSONException {
        if (publicPreferences.getBoolean("check_existing", false) && containsID(all.getString("id"))) return;

        String key = String.valueOf(preferences.getAll().size());
        editor.putString(key, all.toString());
        editor.commit();
    }

    public boolean containsID(final String id) {
        return preferences.getAll().values().stream().anyMatch(o -> {
            try {
                return new JSONObject(o.toString()).getString("id").equals(id);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    /**
     * Remove key from map then instantly commit
     *
     * @param key
     */
    public void remove(final int key) {
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
     * @param index key
     * @return
     */
    public JSONObject getAll(final int index) {
        try {
            return new JSONObject(preferences.getString(String.valueOf(index), "-1"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
