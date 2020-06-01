/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 23:44
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import java.io.File;

public class Downloader {

    /**
     * Download file
     *
     * @param url
     * @param outDir
     * @param outFileName
     * @param activity
     */
    public static void download(String url, File outDir, String outFileName, Activity activity) {
        if (!outDir.exists()) outDir.mkdir();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading File: " + outFileName);
        request.setTitle(outFileName);

        request.setDestinationInExternalPublicDir(outDir.getAbsolutePath(), outFileName);
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
