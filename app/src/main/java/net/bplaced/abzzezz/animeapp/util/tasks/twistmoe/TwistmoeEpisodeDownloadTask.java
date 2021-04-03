/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.12.20, 19:41
 */

package net.bplaced.abzzezz.animeapp.util.tasks.twistmoe;

import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.connection.RBCWrapper;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.tasks.download.EpisodeDownloadTask;

import java.io.File;
import java.net.HttpURLConnection;
import java.nio.channels.Channels;

public class TwistmoeEpisodeDownloadTask extends EpisodeDownloadTask {

    public TwistmoeEpisodeDownloadTask(SelectedActivity application, String url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    @Override
    public String call() throws Exception {
        if (!outDir.exists()) outDir.mkdir();
        this.outFile = new File(outDir, count[1] + ".mp4");
        try {
            final HttpURLConnection connection = URLUtil.createHTTPSURLConnection
                    (
                            url,
                            new String[]{"User-Agent", Constant.USER_AGENT},
                            new String[]{"Range", "f'bytes={pos}-"},
                            new String[]{"Referer", "https://twist.moe/a/"}
                    );

            progressHandler.receiveTotalSize(connection.getContentLength());

            URLUtil.copyFileFromRBC(new RBCWrapper(
                            Channels.newChannel(connection.getInputStream()),
                            connection.getContentLength(),
                            progressHandler::onDownloadProgress),
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
}
