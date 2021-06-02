/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 05.03.21, 20:46
 */

package net.bplaced.abzzezz.animeapp.util.show;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * Class to save all shows to a shared preference file
 */
public class ShowSaver {

    /**
     * Editor and preferences
     */
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences publicPreferences;
    /**
     * List containing all shows.
     */
    private final List<Show> shows = new ArrayList<>();

    @SuppressLint("CommitPrefEdits")
    public ShowSaver(final Context context) {
        this.preferences = context.getSharedPreferences("List", Context.MODE_PRIVATE); //Create File to read and write from
        this.editor = preferences.edit(); //Get editor for the shared preference
        this.publicPreferences = PreferenceManager.getDefaultSharedPreferences(context); //Get the public preferences for settings
        //List of empty shows, that will be removed after iterating
        final List<Integer> emptyIndices = new ArrayList<>();

        //Iterate over all preference entries, each one representing a show. then load this show from it's provider
        for (int i = 0; i < preferences.getAll().size(); i++) {
            final String preference = preferences.getString(String.valueOf(i), "");

            if (preference.isEmpty()) {
                Logger.log(String.format(Locale.ENGLISH, "Show at index %d is empty. Will be removed; Skipping entry", i), Logger.LogType.INFO);
                emptyIndices.add(i);
            } else {
                try {
                    this.shows.add(new Show(new JSONObject(preference))); //Add show; Show loads itself internally from the passed JSON object
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //Remove all empty indices
        emptyIndices.forEach(this::remove);
        Logger.log("Saver set up.", Logger.LogType.INFO);
    }

    /**
     * Commits a show to the preferences
     *
     * @param show show to add
     */
    private void commitShow(final Show show) {
        final String preferenceSize = String.valueOf(preferences.getAll().size());
        editor.putString(preferenceSize, show.toString());
        Logger.log("State committing show: " + editor.commit(), Logger.LogType.INFO);
    }

    /**
     * Updates a certain index
     *
     * @param show  show to be updated
     * @param index index to the show
     */
    private void updateShow(final Show show, final int index) {
        editor.putString(String.valueOf(index), show.toString());
        editor.commit();
    }

    /**
     * Removes a show from a certain index
     *
     * @param index index for the show to be removed
     */
    private void removeShow(final int index) {
        //Remove key (int)
        editor.remove(String.valueOf(index));
        /*
        Move all upcoming entries one down
         */
        for (int i = index; i < preferences.getAll().size() - /*One gone */ 1; i++) {
            editor.putString(String.valueOf(i), preferences.getString(String.valueOf(i + /* Next one */ 1), "NULL"));
            editor.remove(String.valueOf(i + 1));
        }
        //Apply to file
        editor.commit();
    }


    /**
     * Add show with key and values to preference hashmap
     * then commit
     *
     * @param show show to be added
     */
    public void addShow(final Show show) {
        if (publicPreferences.getBoolean("check_existing", false) && containsShow(show))
            return; //Check if settings is checked, if so ignore duplicates

        this.shows.add(show);
        this.commitShow(show);
    }

    /**
     * Not used anymore. Was used for anime4you. For compatibility and so no code is deleted this function is kept
     */
    public void addShow(final JSONObject jsonObject) throws JSONException {
        final Show show = new Show(jsonObject);
        if (publicPreferences.getBoolean("check_existing", false) && containsShow(show)) return;

        this.shows.add(show);
        this.commitShow(show);
    }

    /**
     * Refresh show index
     *
     * @param show  show details to overwrite
     * @param index shows index
     */
    public void refreshShow(final Show show, final int index) {
        shows.set(index, show);
        this.updateShow(show, index);
    }


    /**
     * Check if a show already exists
     *
     * @param show to check
     * @return show contained?
     */
    public boolean containsShow(final Show show) {
        return shows.contains(show);
    }

    /**
     * Remove key from map then instantly commit
     *
     * @param index index to remove show from
     */
    public void remove(final int index) {
        shows.remove(index);
        this.removeShow(index);
    }

    /**
     * Returns the index of the supplied show object
     *
     * @param show show to get the index
     * @return the show's index, returns -1 if the show wasn't found
     */
    public int getIndex(final Show show) {
        return shows.indexOf(show);
    }

    /**
     * @param index key
     * @return new JSON object
     */
    public Optional<Show> getShow(final int index) {
        return Optional.ofNullable(shows.get(index));
    }

    /**
     * @return the show lists size
     */
    public int getShowSize() {
        return shows.size();
    }

    /**
     * Get episode file from index
     *
     * @param index the requested index
     * @return optional with the file or empty
     */
    public Optional<File> getEpisodeFile(final int index, final File showDirectory) {
        if (showDirectory.listFiles() != null) {
            final String indexString = String.valueOf(index);
            for (final File file : showDirectory.listFiles()) {
                if (file.isFile() && file.getName().substring(0, file.getName().lastIndexOf(".")).equals(indexString)) {
                    return Optional.of(file);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get the show's last episode, e.g. the file with the highest integer
     *
     * @return the highest integer found, comparing all the different filenames
     */
    public int getLatestEpisode(final File showDirectory) {
        if (showDirectory.listFiles() != null) {
            try (final Stream<File> files = Arrays.stream(showDirectory.listFiles())) {
                final OptionalInt highest = files
                        .filter(File::isFile)
                        .map(s -> StringUtil.extractNumberI(s.getName().substring(0, s.getName().lastIndexOf("."))))
                        .mapToInt(integer -> integer)
                        .max();
                if (highest.isPresent()) return highest.getAsInt() + 1;
                else return 0;
            }
        }
        return 0;
    }

    /**
     * Check if a certain episode is downloaded
     * TODO: Create and update a local index
     *
     * @param index index to search for
     * @return if the file has been found
     */
    public boolean isEpisodeDownloaded(final int index, final File showDirectory) {
        if (showDirectory.listFiles() != null) {
            for (final File file : showDirectory.listFiles()) {
                if (file.isFile()) {
                    if (file.getName().substring(0, file.getName().lastIndexOf(".")).equals(String.valueOf(index)))
                        return true;
                }
            }
        }
        return false;
    }


}
