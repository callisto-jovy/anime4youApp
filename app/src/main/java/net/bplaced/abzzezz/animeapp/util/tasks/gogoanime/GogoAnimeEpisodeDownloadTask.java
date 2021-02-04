/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import android.util.Log;
import com.arthenica.mobileffmpeg.Config;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.tasks.EpisodeDownloadTask;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class GogoAnimeEpisodeDownloadTask extends EpisodeDownloadTask {

    public GogoAnimeEpisodeDownloadTask(SelectedActivity application, String url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    @Override
    public String call() throws Exception {
        if (!outDir.exists()) outDir.mkdir();
        this.outFile = new File(outDir, count[1] + ".mp4");
        final String extension = FilenameUtils.getExtension(url);
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

            final int returnCode = executeFFmpeg(ffmpegArguments);

            if (returnCode == RETURN_CODE_SUCCESS) {
                return name.concat(": ") + count[1];
            } else if (returnCode == RETURN_CODE_CANCEL) {
                return "Download cancelled";
            } else {
                Logger.log(String.format(Locale.ENGLISH,"Command execution failed with returnCode=%d and the output below.", returnCode),  Logger.LogType.ERROR);
                Config.printLastCommandOutput(Log.INFO);
                return "Error";
            }
        } else {
            return "Unexpected video format";
        }
    }
}
