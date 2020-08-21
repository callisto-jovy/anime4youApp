/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 20:08
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
    public static void download(final String url, final File outDir, final String outFileName, final Activity activity) {
        if (!outDir.exists()) outDir.mkdir();
        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading File: ".concat(outFileName));
        request.setTitle(outFileName);

        request.setDestinationInExternalPublicDir(outDir.getAbsolutePath(), outFileName);
        final DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
