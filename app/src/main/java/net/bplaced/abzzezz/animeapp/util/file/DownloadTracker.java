/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 14:05
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import android.content.SharedPreferences;
import ga.abzzezz.util.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;

public class DownloadTracker {

    /**
     * Editor and preferences
     */
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    /**
     * Aids used as keys. The other values stay the same
     */

    public DownloadTracker(Context context) {
        this.preferences = context.getSharedPreferences("DownloadTracker", Context.MODE_PRIVATE);
        this.editor = preferences.edit();
        Logger.log("Download Tracker set up", Logger.LogType.INFO);
    }

    public void submitTrack(String information) {
        editor.putString(String.valueOf(preferences.getAll().size()), information);
        editor.commit();
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
