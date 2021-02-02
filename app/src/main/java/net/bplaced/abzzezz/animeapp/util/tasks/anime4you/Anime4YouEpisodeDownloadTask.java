/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.anime4you;

import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.tasks.EpisodeDownloadTask;

import java.io.File;
import java.net.URL;

public class Anime4YouEpisodeDownloadTask extends EpisodeDownloadTask {

    private int notifyID;

    public Anime4YouEpisodeDownloadTask(SelectedActivity application, URL url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    /**
     * Call method downloads file.
     *
     * @return
     * @throws Exception
     */
    @Override
    public String call() throws Exception {
        Logger.log("New download thread started: " + notifyID, Logger.LogType.INFO);
        return super.call();
    }
}
