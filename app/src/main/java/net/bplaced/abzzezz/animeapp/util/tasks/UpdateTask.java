/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.12.20, 20:12
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import androidx.core.content.FileProvider;
import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;

public class UpdateTask extends TaskExecutor implements Callable<File>, TaskExecutor.Callback<File> {

    private final Context context;

    public UpdateTask(final Context application) {
        this.context = application;
    }

    public void executeAsync() {
        super.executeAsync(this, this);
    }

    @Override
    public File call() throws Exception {
        final boolean updateNeeded = AnimeAppMain.getInstance().getVersion() < Float.parseFloat(URLUtil.getURLContentAsString(new URL(StringHandler.APP_VERSION_TXT)));
        Logger.log("Update needed: " + updateNeeded, Logger.LogType.INFO);
        if (updateNeeded) {
            AnimeAppMain.getInstance().setVersionOutdated(true);

            final File outFile = File.createTempFile("Anime4youUpdate", ".apk", context.getExternalFilesDir(
                    Environment.DIRECTORY_DOWNLOADS));


            //Open Stream
            final FileOutputStream fileOutputStream = new FileOutputStream(outFile);
            final ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(StringHandler.UPDATE_APK).openStream());
            //Copy from channel to channel
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            Logger.log("Done downloading", Logger.LogType.INFO);
            return outFile;
        } else
            return null;
    }

    @Override
    public void onComplete(File result) throws Exception {
        if (result != null)
            install(result);
    }

    @Override
    public void preExecute() {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void install(final File apk) throws IOException {
        Log.d("Updater", "Trying to install...");
        Uri uri = null;
        Intent intent;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            try {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", apk);
            } catch (Exception e) {
                e.printStackTrace();
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            final File file = new File(context.getExternalCacheDir(), "update.apk");
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.getChannel().transferFrom(Channels.newChannel(new FileInputStream(apk)), 0, Long.MAX_VALUE);
            fileOutputStream.close();

            // make file readable
            if (file.setReadable(true))
                uri = Uri.fromFile(file);

            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        Log.d("Updater", "Starting install intent...");

        context.startActivity(intent);
    }

}
