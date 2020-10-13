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
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class ShowSaver {

    /**
     * Editor and preferences
     */
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences publicPreferences;

    /**
     * Aids used as keys. The other values stay the same
     */

    public ShowSaver(final Context context) {
        this.preferences = context.getSharedPreferences("List", Context.MODE_PRIVATE);
        this.editor = preferences.edit();
        this.publicPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Logger.log("Saver set up.", Logger.LogType.INFO);
    }

    /**
     * Add anime with key and values to preference hashmap
     * then commit
     *
     * @param all
     */
    public void addShow(final JSONObject all) throws JSONException {
        if (publicPreferences.getBoolean("check_existing", false) && containsID(all.getString(StringHandler.SHOW_ID)))
            return;
        editor.putString(String.valueOf(preferences.getAll().size()), all.toString());
        editor.commit();
    }

    /**
     * Refresh show index
     * @param details show details to overwrite
     * @param index shows index
     */
    public void refreshShow(final JSONObject details, final int index) {
        editor.putString(String.valueOf(index), details.toString());
    }


    /**
     * Check if preferences contain a certain id
     * @param id it to search
     * @return id contained
     */
    public boolean containsID(final String id) {
        return preferences.getAll().values().stream().anyMatch(o -> {
            try {
                return new JSONObject(o.toString()).getString(StringHandler.SHOW_ID).equals(id);
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
     * @param index key
     * @return new JSON object
     */
    public Optional<JSONObject> getShow(final int index) {
        try {
            return Optional.of(new JSONObject(preferences.getString(String.valueOf(index), "-1")));
        } catch (JSONException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     *
     * @return all size
     */
    public int getShowSize() {
        return preferences.getAll().size();
    }

}
