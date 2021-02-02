/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.02.21, 19:11
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import android.util.Log;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import net.bplaced.abzzezz.animeapp.util.provider.impl.AnimePaheHolder;
import net.bplaced.abzzezz.animeapp.util.scripter.JsUnpacker;
import net.bplaced.abzzezz.animeapp.util.tasks.EpisodeDownloadTask;
import org.jsoup.Jsoup;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class AnimePaheEpisodeDownloadTask extends EpisodeDownloadTask implements AnimePaheHolder {


    public AnimePaheEpisodeDownloadTask(SelectedActivity application, URL url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    @Override
    public String call() throws Exception {
        //M3u8 download
        if (!outDir.exists()) outDir.mkdir();
        this.outFile = new File(outDir, count[1] + ".mp4");
        final String refererURL = url.toString();

        final String scriptSrc = Jsoup.connect(refererURL).userAgent(RandomUserAgent.getRandomUserAgent()).referrer("https://animepahe.com/").get().getElementsByTag("script").get(5).toString();
        final String unpackedJS = new JsUnpacker(scriptSrc).unpack();

        final Matcher videoSrcMatcher = videoSrcPattern.matcher(unpackedJS);

        System.out.println(unpackedJS);
        String m3u8Src = "";
        while (videoSrcMatcher.find()) {
            final String s = videoSrcMatcher.group();
            m3u8Src = s.substring(s.indexOf('\'') + 1, s.lastIndexOf('\''));
        }

        if (m3u8Src.isEmpty()) {
            this.cancel();
            return "Error extracting";
        }

        List<String> cmdList = new LinkedList<>();
        //Headers
        cmdList.add("-headers");
        cmdList.add("referer:" + refererURL);
        //Input
        cmdList.add("-i");
        cmdList.add(m3u8Src);

        cmdList.add("-vcodec");
        cmdList.add("copy");
        cmdList.add("-c:a");
        cmdList.add("copy");
        cmdList.add("-acodec");
        cmdList.add("mp3");
        //Output
        cmdList.add(outFile.toString());

        final int returnCode = FFmpeg.execute(cmdList.toArray(new String[cmdList.size()]));

        if (returnCode == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
            return name.concat(": ") + count[1];
        } else if (returnCode == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
            return "Download cancelled";
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with returnCode=%d and the output below.", returnCode));
            Config.printLastCommandOutput(Log.INFO);
            return "Error";
        }
    }

    @Override
    public void cancel() {
        FFmpeg.cancel();
        super.cancel();
    }
}
