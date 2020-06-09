/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.05.20, 20:50
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import ga.abzzezz.util.data.URLUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Checks if the byte (version) is smaller than the Byte value of the String on the host
 */
public class AutoUpdater extends AsyncTask<Activity, Void, Void> {

    /**
     * Version
     */
    public static float version = 33F;

    private String checkUpdate() throws MalformedURLException {
        return URLUtil.getURLContentAsString(new URL(URLHandler.checkURL));
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        try {
            if (!checkUpdate().equals("NULL")) {
                if (version < Float.valueOf(checkUpdate())) {
                    File outDic = new File(Environment.DIRECTORY_DOWNLOADS, "Anime4you-Update");
                    String fileName = "appUpdate Version:" + checkUpdate() + ".apk";
                    Downloader.download(URLHandler.updateURL, outDic, fileName, activities[0]);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
