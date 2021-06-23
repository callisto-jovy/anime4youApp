/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.03.21, 19:41
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.connection.RBCWrapper;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.tasks.download.EpisodeDownloadTask;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.channels.Channels;

public class GogoAnimeEpisodeDownloadTask extends EpisodeDownloadTask {

    private FileOutputStream fileOutputStream;

    public GogoAnimeEpisodeDownloadTask(SelectedActivity application, String url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    @Override
    public String call() throws Exception {
        try {
            final HttpsURLConnection connection = URLUtil.createHTTPSURLConnection(url, 0, 0,
                    new String[]{"User-Agent", Constant.USER_AGENT}, new String[]{"Range", "bytes=0-"});

            connection.setInstanceFollowRedirects(true);

            progressHandler.receiveTotalSize(connection.getContentLength());

            URLUtil.copyFileFromRBC(new RBCWrapper(
                            Channels.newChannel(connection.getInputStream()),
                            connection.getContentLength(),
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
    public void cancelExecution() {
        //Flush and close the stream if needed
        if (this.fileOutputStream != null) {
            try {
                this.fileOutputStream.flush();
                this.fileOutputStream.close();
            } catch (final IOException e) {
                sendErrorNotification(e.getLocalizedMessage());
            }
        }
        super.cancelExecution();
    }

}
