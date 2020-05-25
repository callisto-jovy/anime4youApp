/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 14:52
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class EpisodeDownloader {

    public int currentIndex, episodesTotal, downloadAID;
    public String currentLink = "";
    private long downloadID;
    /*
    Passed on variables
     */
    private FloatingActionButton downloadButton;

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                if (currentIndex <= episodesTotal) downloadButton.callOnClick();
            }
        }
    };

    public void download(String url, String fileName, Activity activity, String fileExtension) {
        File outDic = new File(Environment.DIRECTORY_DOWNLOADS, fileName.substring(0, fileName.indexOf("::")));
        if (!outDic.exists()) outDic.mkdir();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading File: " + fileName);
        request.setTitle(fileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(outDic.getAbsolutePath(), fileName + "." + fileExtension);
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        this.downloadID = manager.enqueue(request);
        activity.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void setButton(FloatingActionButton downloadButton) {
        this.downloadButton = downloadButton;
    }
}
