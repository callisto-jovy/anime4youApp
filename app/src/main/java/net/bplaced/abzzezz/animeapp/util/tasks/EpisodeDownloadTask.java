/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.12.20, 19:34
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.IntentHelper;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.receiver.StopDownloadReceiver;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class EpisodeDownloadTask extends TaskExecutor implements Callable<String>, TaskExecutor.Callback<String> {

    protected final SelectedActivity application;
    protected final int[] count;
    protected final URL url;
    protected final String name;
    protected final File outDir;
    protected boolean cancel;
    protected FileOutputStream fileOutputStream;
    protected File outFile;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notification;
    private int notifyID;

    public EpisodeDownloadTask(SelectedActivity application, URL url, String name, File outDir, int[] count) {
        this.application = application;
        this.name = name;
        this.url = url;
        this.count = count;
        this.outDir = outDir;
    }

    public void executeAsync() {
        super.executeAsync(this, this);
    }

    @Override
    public String call() throws Exception {
        Logger.log("New download thread started: " + notifyID, Logger.LogType.INFO);
        if (!outDir.exists()) outDir.mkdir();
        this.outFile = new File(outDir, count[1] + ".mp4");
        try {
            //Open new URL connection
            final URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent", StringHandler.USER_AGENT);
            urlConnection.connect();

            URLUtil.copyFileFromURL(urlConnection, outFile);

            Logger.log("Done copying streams, closing stream", Logger.LogType.INFO);
            fileOutputStream.close();
            return name.concat(": ") + count[1];
        } catch (MalformedURLException e) {
            cancel();
            return name.concat(": ") + count[1];
        }
    }

    @Override
    public void onComplete(String result) throws Exception {
        //Cancel notification
        notificationManagerCompat.cancel(notifyID);

        if (!isCancelled()) {
            //Create new "download-done" notification
            this.notification = new NotificationCompat.Builder(application, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.information).setColor(Color.GREEN).setContentText("Episode-download done")
                    .setContentTitle("Done downloading episode: " + result)
                    .setPriority(NotificationCompat.PRIORITY_MAX);
            //Notify, reuse old id
            notificationManagerCompat.notify(notifyID, notification.build());
            //Increase count
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (count[0] < count[2]) {
                    count[0]++;
                    count[1]++;
                    application.getEpisode(count[1], count[2], count[0], false);
                }
            }, Long.parseLong(PreferenceManager.getDefaultSharedPreferences(application).getString("download_delay", "0")) * 1000);
        }

        application.refreshAdapter();
        //Set cancelled to false
        if (isCancelled()) {
            Logger.log("Threading was stopped. Cancelled stop, after further downloading was stopped", Logger.LogType.INFO);
            cancel = false;
        }
    }

    @Override
    public void preExecute() {
        //Create notification
        this.notifyID = (int) System.currentTimeMillis() % 10000;
        this.notificationManagerCompat = NotificationManagerCompat.from(application);

        final Intent notificationActionIntent = new Intent(application, StopDownloadReceiver.class);
        notificationActionIntent.setData(Uri.parse("" + notifyID));
        Logger.log("Assigned thread id: " + notifyID, Logger.LogType.INFO);
        //Put object key
        IntentHelper.addObjectForKey(this, String.valueOf(notifyID));
        final PendingIntent stopDownloadingPendingIntent = PendingIntent.getBroadcast(application, 1, notificationActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.notification = new NotificationCompat.Builder(application, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.download)
                .setContentText("Currently downloading episode: " + count[1] + " from show: " + name)
                .setContentTitle("Episode Download")
                .setPriority(NotificationCompat.PRIORITY_HIGH).addAction(R.drawable.cancel, "Stop downloading", stopDownloadingPendingIntent)
                .setOngoing(true);
        this.notificationManagerCompat.notify(notifyID, notification.build());
    }

    /**
     * @return task cancelled
     */
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Cancel task
     */
    public void cancel() {
        if (fileOutputStream == null) return;
        try {
            fileOutputStream.flush();
            fileOutputStream.close();
            application.refreshAdapter();
        } catch (IOException e) {
            Logger.log("Error closing task stream", Logger.LogType.ERROR);
            e.printStackTrace();
        }
        //Set cancelled true
        this.cancel = true;
        Logger.log("Task cancelled, Streams flushed", Logger.LogType.INFO);
        outFile.delete();
    }

}

