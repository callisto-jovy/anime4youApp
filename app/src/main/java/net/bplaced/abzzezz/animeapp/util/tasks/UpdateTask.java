/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.12.20, 20:12
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import com.blankj.utilcode.util.AppUtils;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.BuildConfig;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;

public class UpdateTask extends TaskExecutor implements Callable<File>, TaskExecutor.Callback<File> {

    private final Context context;

    public UpdateTask(final Context context) {
        this.context = context;
    }

    public void executeAsync() {
        super.executeAsync(this, this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public File call() throws Exception {
        final boolean updateNeeded = AnimeAppMain.getInstance().getVersion() < Float.parseFloat(URLUtil.collectLines(new URL(StringHandler.APP_VERSION_TXT), ""));
        Logger.log("Update needed: " + updateNeeded, Logger.LogType.INFO);
        if (updateNeeded) {
            AnimeAppMain.getInstance().setVersionOutdated(true);

            final File outFile = File.createTempFile("update", ".apk", context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
            final Uri outFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", outFile);

            final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(StringHandler.UPDATE_APK));
            request.setDescription("Downloading File: ".concat("Update"));
            request.setTitle("Update");
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, outFile.getName());

            final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadID = downloadManager.enqueue(request);

            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadID) {
                        AppUtils.installApp(outFileUri);

                        Logger.log("Done downloading", Logger.LogType.INFO);
                    }
                }
            }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
        return null;
    }

    @Override
    public void onComplete(File result) throws Exception {
    }

    @Override
    public void preExecute() {
    }
}
