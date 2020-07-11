/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 14:05
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.annotation.SuppressLint;
import android.content.Context;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.logging.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DownloadTracker {

    private final File trackerFile;

    public DownloadTracker(Context context) {
        this.trackerFile = new File(context.getFilesDir(), "DownloadTracker.xml");
        Logger.log("Download Tracker set up", Logger.LogType.INFO);
        try {
            trackerFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Submit track to tracker list
     *
     * @param information string to add
     */
    public void submitTrack(final String information) {
        @SuppressLint("SimpleDateFormat") final String time = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis()));
        final String track = time + "\n" + information + "\n";
        try (final FileOutputStream fos = new FileOutputStream(trackerFile, true)) {
            fos.write(track.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearTrack() {
        Logger.log("Clearing track: " + trackerFile.delete(), Logger.LogType.INFO);
        try {
            trackerFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return list, same as before
     *
     * @return
     */
    public ArrayList<String> getList() {
        try {
            return (ArrayList<String>) FileUtil.getFileContentAsList(trackerFile);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
