/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.02.21, 14:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.download;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.IntentHelper;
import net.bplaced.abzzezz.animeapp.util.M3U8Util;
import net.bplaced.abzzezz.animeapp.util.connection.RBCWrapper;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.receiver.StopDownloadReceiver;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class EpisodeDownloadTask extends EpisodeDownloadTaskExecutor implements Callable<String>, TaskExecutor.Callback<String> {

    protected final SelectedActivity application;
    protected final int[] count;
    protected final String url;
    protected final String name;
    protected final File outDir;
    protected final EpisodeDownloadProgressHandler progressHandler;
    private boolean cancel;
    protected File outFile;
    protected FileOutputStream fileOutputStream; //Fileoutputstream, can be closed if canceled
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notification;
    private int notifyID;
    private int totalBytes, progress;

    public EpisodeDownloadTask(SelectedActivity application, String url, String name, File outDir, int[] count) {
        this.application = application;
        this.name = name;
        this.url = url;
        this.count = count;
        this.outDir = outDir;

        this.progressHandler = new EpisodeDownloadProgressHandler() {
            @Override
            public void onDownloadCompleted(String s) {
                try {
                    onComplete(s);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDownloadProgress(int newReadBytes) {
                progress += newReadBytes;
                notification.setProgress(totalBytes, progress, false);
                notificationManagerCompat.notify(notifyID, notification.build());
            }

            @Override
            public void receiveTotalSize(int totalByteSize) {
                totalBytes = totalByteSize;

                final Intent notificationActionIntent = new Intent(application, StopDownloadReceiver.class);
                notificationActionIntent.setData(Uri.parse("" + notifyID));
                final PendingIntent stopDownloadingPendingIntent = PendingIntent.getBroadcast(application, 1, notificationActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification = new NotificationCompat.Builder(application, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.download)
                        .setContentText("Currently downloading episode: " + count[1] + " from show: " + name)
                        .setContentTitle("Episode Download")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .addAction(R.drawable.cancel, "Stop downloading", stopDownloadingPendingIntent)
                        .setProgress(totalByteSize, 0, true)
                        .setOnlyAlertOnce(true)
                        .setOngoing(true);

                notificationManagerCompat.notify(notifyID, notification.build());
            }

            @Override
            public void onErrorThrown(String message) {
                cancelExecution();
                sendErrorNotification(message);
                Logger.log(message, Logger.LogType.ERROR);
                try {
                    onComplete(message);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        };
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
            final URLConnection urlConnection = URLUtil.createURLConnection(url, 0, 0,
                    new String[]{"User-Agent", Constant.USER_AGENT});

            URLUtil.copyFileFromRBC(new RBCWrapper(
                            Channels.newChannel(urlConnection.getInputStream()),
                            urlConnection.getContentLength(),
                            progressHandler::onDownloadProgress
                    ),
                    outFile,
                    fileOutputStream -> this.fileOutputStream = fileOutputStream);

            Logger.log("Done copying streams, closing stream", Logger.LogType.INFO);

            progressHandler.onDownloadCompleted(name.concat(": ") + count[1]);
            return null;
        } catch (final MalformedURLException e) {
            progressHandler.onErrorThrown(getError(e));
            this.cancelExecution();
            return null;
        }
    }

    @Override
    public void onComplete(String result) throws Exception {
        //Cancel notification
        notificationManagerCompat.cancel(notifyID);

        if (!this.isCancelled()) {
            this.notification = new NotificationCompat.Builder(application, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.information).setColor(Color.GREEN)
                    .setContentText("Episode-download done")
                    .setContentTitle("Done downloading episode: " + result)
                    .setPriority(NotificationCompat.PRIORITY_MAX);
            //Create new "download-done" notification
            notificationManagerCompat.notify(notifyID, notification.build());  //Notify, reuse old id
            //Increase count
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (count[0] < count[2]) {
                            count[0]++;
                            count[1]++;
                            application.getEpisode(count[1], count[2], count[0], false);
                        }
                    },
                    Long.parseLong(PreferenceManager.getDefaultSharedPreferences(application).getString("download_delay", "0")) * 1000);
        }
        this.refreshAdapter();
        //Set cancelled to false
        if (this.isCancelled()) {
            Logger.log("Threading was stopped. Cancelled stop, after further downloading was stopped", Logger.LogType.INFO);
            cancel = false;
        }
    }

    @Override
    public void preExecute() {
        //Create notification
        this.notifyID = (int) System.currentTimeMillis() % 10000;
        this.notificationManagerCompat = NotificationManagerCompat.from(application);


        Logger.log("Assigned thread id: " + notifyID, Logger.LogType.INFO);
        //Put object key
        IntentHelper.addObjectForKey(this, String.valueOf(notifyID));

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
    public void cancelExecution() {
        if (this.fileOutputStream != null) {
            try {
                this.fileOutputStream.flush();
                this.fileOutputStream.close();
            } catch (final IOException e) {
                sendErrorNotification(e.getLocalizedMessage());
            }
        }
        this.refreshAdapter();
        //Set cancelled true
        this.cancel = true;
        Logger.log("Task cancelled, Streams flushed; File deleted: " + outFile.delete(), Logger.LogType.INFO);


    }

    protected long startFFDefaultTask(final List<String> ffmpegArguments, final String url, final String[]... requestHeaders) throws IOException {
        final int totalSegments = M3U8Util.getSegments(url, requestHeaders);
        progressHandler.receiveTotalSize(totalSegments);

        Config.enableLogCallback(message -> {
            if (message.getText().contains("Opening")) {
                progressHandler.onDownloadProgress(1);
            }
        });

        return FFmpeg.executeAsync(ffmpegArguments.toArray(new String[]{}), (executionId, returnCode) -> {
            if (returnCode == RETURN_CODE_SUCCESS) {
                progressHandler.onDownloadCompleted(name.concat(": ") + count[1]);
                Log.i(Config.TAG, "Async command execution completed successfully.");
            } else if (returnCode == RETURN_CODE_CANCEL) {
                try {
                    onComplete("Canceled");
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                cancelExecution();
                progressHandler.onErrorThrown(getError(Config.getLastCommandOutput()));
                Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
            }
        });
    }

    private void refreshAdapter() {
        new Handler(Looper.getMainLooper()).post(application::refreshAdapter);
    }


    protected void sendErrorNotification(final String errorMessage) {
        this.notification = new NotificationCompat.Builder(application, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.information).setColor(Color.GREEN)
                .setContentText("Episode download error")
                .setContentTitle(this.getError(errorMessage))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManagerCompat.notify(ThreadLocalRandom.current().nextInt(), notification.build());
    }

    /* Get error message */

    protected String getError(final Exception e) {
        return getError(Objects.requireNonNull(e.getLocalizedMessage()));
    }

    protected String getError(final String e) {
        return "Downloading failed: " + e;
    }

    /**
     * Interface for all download progress callbacks
     */
    public interface EpisodeDownloadProgressHandler {
        void onDownloadCompleted(String result);

        void onDownloadProgress(int newReadBytes);

        void receiveTotalSize(int totalByteSize);

        void onErrorThrown(final String message);
    }

}

