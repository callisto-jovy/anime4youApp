/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.02.21, 19:11
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import android.util.Log;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import net.bplaced.abzzezz.animeapp.util.provider.impl.AnimePaheHolder;
import net.bplaced.abzzezz.animeapp.util.scripter.JsUnpacker;
import net.bplaced.abzzezz.animeapp.util.tasks.EpisodeDownloadTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class AnimePaheEpisodeDownloadTask extends EpisodeDownloadTask implements AnimePaheHolder {

    public AnimePaheEpisodeDownloadTask(SelectedActivity application, String url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    @Override
    public String call() throws Exception {
        //M3u8 download
        if (!outDir.exists()) outDir.mkdir();
        this.outFile = new File(outDir, count[1] + ".mp4");
        //Fetch document
        final Document document = Jsoup.connect(url).userAgent(RandomUserAgent.getRandomUserAgent()).referrer(ANIME_PAHE_REFERER).get();
        final Elements javascriptElements = document.getElementsByTag("script");

        //Get second to last javascript
        final String unpackedJS = new JsUnpacker(javascriptElements.get(javascriptElements.size() - 2).toString()).unpack();

        final Matcher videoSrcMatcher = videoSrcPattern.matcher(unpackedJS);

        String m3u8Src = "";
        while (videoSrcMatcher.find()) {
            final String s = videoSrcMatcher.group();
            m3u8Src = s.substring(s.indexOf('\'') + 1, s.lastIndexOf('\''));
        }

        if (m3u8Src.isEmpty()) {
            this.cancel();
            return "Error extracting";
        }

        final List<String> cmdList = new LinkedList<>();
        //Headers
        cmdList.add("-headers");
        cmdList.add("referer:" + url);
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

        final int returnCode = executeFFmpeg(cmdList);

        if (returnCode == RETURN_CODE_SUCCESS) {
            return name.concat(": ") + count[1];
        } else if (returnCode == RETURN_CODE_CANCEL) {
            return "Download cancelled";
        } else {
            Logger.log(String.format(Locale.ENGLISH,"Command execution failed with returnCode=%d and the output below.", returnCode),  Logger.LogType.ERROR);
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
