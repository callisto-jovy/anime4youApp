/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.04.21, 23:52
 */

package net.bplaced.abzzezz.animeapp.util.tasks.twistmoe;

import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.connection.RBCWrapper;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.tasks.download.EpisodeDownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.channels.Channels;

public class TwistmoeEpisodeDownloadTask extends EpisodeDownloadTask {

    private FileOutputStream fileOutputStream;

    public TwistmoeEpisodeDownloadTask(SelectedActivity application, String url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    @Override
    public String call() throws Exception {
        try {
            final HttpURLConnection connection = URLUtil.createHTTPSURLConnection
                    (
                            url,
                            new String[]{"User-Agent", Constant.USER_AGENT},
                            new String[]{"Range", "f'bytes={pos}-"},
                            new String[]{"Referer", "https://twist.moe/a/"}
                    );

            progressHandler.receiveTotalSize(connection.getContentLength());

            URLUtil.copyFileFromRBC(new RBCWrapper
                            (
                                    Channels.newChannel(connection.getInputStream()),
                                    connection.getContentLength(),
                                    progressHandler::onDownloadProgress
                            ),
                    outFile,
                    fileOutputStream -> this.fileOutputStream = fileOutputStream);

            progressHandler.onDownloadCompleted(name.concat(": ") + count[1]);
            return null;
        } catch (final Exception e) {
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
