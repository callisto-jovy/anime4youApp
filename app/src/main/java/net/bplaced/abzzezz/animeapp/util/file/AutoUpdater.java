/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.05.20, 20:50
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.app.Activity;
import android.os.Environment;
import ga.abzzezz.util.data.URLUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Checks if the byte (version) is smaller than the Byte value of the String on the host
 */
public class AutoUpdater {

    /**
     * Version
     */
    public static float version = 30F;

    /**
     * @param activity
     */
    public void update(Activity activity) {
        try {
            if (version < Float.valueOf(checkUpdate())) {
                File outDic = new File(Environment.DIRECTORY_DOWNLOADS, "Anime4you-Update");
                String fileName = "appUpdate Version:" + checkUpdate() + ".apk";
                Downloader.download(URLHandler.updateURL, outDic, fileName, activity);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private String checkUpdate() throws MalformedURLException {
        return URLUtil.getURLContentAsString(new URL(URLHandler.checkURL));
    }
}
