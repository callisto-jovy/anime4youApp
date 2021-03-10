/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import com.arthenica.mobileffmpeg.FFmpeg;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.tasks.download.EpisodeDownloadTask;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class GogoAnimeEpisodeDownloadTask extends EpisodeDownloadTask {

    private long ffmpegTask;

    public GogoAnimeEpisodeDownloadTask(SelectedActivity application, String url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    @Override
    public String call() throws Exception {
        if (!outDir.exists()) outDir.mkdir();
        this.outFile = new File(outDir, count[1] + ".mp4");
        try {
            final String extension = url.substring(url.lastIndexOf(".") + 1);
            if (extension.equals("mp4")) {
                return super.call();
            } else if (extension.equals("m3u8")) {
                final List<String> ffmpegArguments = new LinkedList<>();
                //Input
                ffmpegArguments.add("-i");
                ffmpegArguments.add(url);

                ffmpegArguments.add("-vcodec");
                ffmpegArguments.add("copy");
                ffmpegArguments.add("-c:a");
                ffmpegArguments.add("copy");
                ffmpegArguments.add("-acodec");
                ffmpegArguments.add("mp3");
                //Output
                ffmpegArguments.add(outFile.toString());

                this.ffmpegTask = this.startFFDefaultTask(ffmpegArguments, url);
                return null;
            } else {
                progressHandler.onErrorThrown(getError("Unexpected video format"));
                return null;
            }

        } catch (final StringIndexOutOfBoundsException e) {
            progressHandler.onErrorThrown(getError(e));
            return null;
        }
    }

    @Override
    public void cancel() {
        FFmpeg.cancel(ffmpegTask);
        super.cancel();
    }

}
