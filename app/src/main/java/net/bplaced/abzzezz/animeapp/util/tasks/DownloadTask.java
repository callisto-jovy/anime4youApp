/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.07.20, 23:30
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
import net.bplaced.abzzezz.animeapp.activities.main.SelectedAnimeActivity;
import net.bplaced.abzzezz.animeapp.util.IntentHelper;
import net.bplaced.abzzezz.animeapp.util.receiver.StopDownloadingReceiver;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;

public class DownloadTask extends TaskExecutor implements Callable<String>, TaskExecutor.Callback<String> {

    private final SelectedAnimeActivity application;
    private final int[] count;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notification;
    private int notifyID;
    private boolean cancel;
    private FileOutputStream fileOutputStream;
    private File outFile;
    private final String url;
    private final String name;


    public DownloadTask(final SelectedAnimeActivity application, final String url, final String name, final int[] count) {
        this.application = application;
        this.name = name;
        this.url = url;
        this.count = count;
    }


    public <R> void executeAsync() {
        super.executeAsync(this, this);
    }

    /**
     * Call method downloads file.
     *
     * @return
     * @throws Exception
     */
    @Override
    public String call() throws Exception {
        Logger.log("New download thread started" + notifyID, Logger.LogType.INFO);
        final File outDir = new File(application.getFilesDir(), name);

        if (!outDir.exists()) outDir.mkdir();
        this.outFile = new File(outDir, count[1] + ".mp4");

        //Open new URL connection
        final URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.setRequestProperty("User-Agent", StringHandler.USER_AGENT);
        urlConnection.connect();
        //Open Stream
        this.fileOutputStream = new FileOutputStream(outFile);
        final ReadableByteChannel readableByteChannel = Channels.newChannel(urlConnection.getInputStream());
        //Copy from channel to channel
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        //Close stream
        Logger.log("Done copying streams, closing stream", Logger.LogType.INFO);
        fileOutputStream.close();
        return name + count[1];
    }

    @Override
    public void onComplete(String result) {
        //Cancel notification
        notificationManagerCompat.cancel(notifyID);
        //Add to download tracker
        if (!isCancelled())
            AnimeAppMain.getInstance().getDownloadTracker().submitTrack("Downloaded Episode: " + result);

        this.notification = new NotificationCompat.Builder(application, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.information).setColor(Color.GREEN).setContentText("Episode-download done")
                .setContentTitle("Done downloading episode: " + result)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        //Notify, reuse old id
        if (!isCancelled()) notificationManagerCompat.notify(notifyID, notification.build());
        //Reset adapter
        application.resetAdapter();
        //Delay and start if not cancelled
        if (!isCancelled()) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (count[0] < count[2]) {
                    count[0]++;
                    count[1]++;
                    application.downloadEpisode(count[1], count[2], count[0]);
                }
            }, Long.parseLong(PreferenceManager.getDefaultSharedPreferences(application).getString("download_delay", "0")) * 1000);
        }
        /*
         * Check if stopped
         */
        if (isCancelled()) {
            Logger.log("Threading was stopped. Cancelled stop, after further downloading was stopped", Logger.LogType.INFO);
            cancel = false;
        }
    }

    /**
     * Pre execute method. Creates notifications and task id
     */
    @Override
    public void preExecute() {
        //Create notification
        this.notifyID = (int) System.currentTimeMillis() % 10000;
        this.notificationManagerCompat = NotificationManagerCompat.from(application);

        final Intent notificationActionIntent = new Intent(application, StopDownloadingReceiver.class);
        notificationActionIntent.setData(Uri.parse("" + notifyID));
        Logger.log("Assigned thread id:" + notifyID, Logger.LogType.INFO);
        //Put object key
        IntentHelper.addObjectForKey(this, String.valueOf(notifyID));
        final PendingIntent stopDownloadingPendingIntent = PendingIntent.getBroadcast(application, 1, notificationActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.notification = new NotificationCompat.Builder(application, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.download)
                .setContentText("Currently downloading episode: " + count[1])
                .setContentTitle("Episode Download")
                .setPriority(NotificationCompat.PRIORITY_HIGH).addAction(R.drawable.ic_cancel, "Stop downloading", stopDownloadingPendingIntent)
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
            application.resetAdapter();
        } catch (IOException e) {
            Logger.log("Error closing task stream", Logger.LogType.ERROR);
            e.printStackTrace();
        }
        //Set canceled true
        this.cancel = true;
        Logger.log("Task cancelled, Streams flushed", Logger.LogType.INFO);
        outFile.delete();
    }
}
