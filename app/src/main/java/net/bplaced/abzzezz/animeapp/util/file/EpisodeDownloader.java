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
import android.os.Environment;

import java.io.File;

public class EpisodeDownloader {

    private long downloadID;

    public void download(String url, String fileName, Activity activity, String fileExtension) {
        File outDic = new File(Environment.DIRECTORY_DOWNLOADS, fileName.substring(0, fileName.indexOf("::")));
        if (!outDic.exists()) outDic.mkdir();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading File: " + fileName);
        request.setTitle(fileName);
        request.setDestinationInExternalPublicDir(outDic.getAbsolutePath(), fileName + "." + fileExtension);
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        this.downloadID = manager.enqueue(request);
    }
}
