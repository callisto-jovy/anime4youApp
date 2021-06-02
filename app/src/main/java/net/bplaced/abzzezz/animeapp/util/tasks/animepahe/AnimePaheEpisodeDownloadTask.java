/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 07.04.21, 13:10
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import com.arthenica.mobileffmpeg.FFmpeg;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.provider.holders.AnimePaheHolder;
import net.bplaced.abzzezz.animeapp.util.string.JsUnpacker;
import net.bplaced.abzzezz.animeapp.util.tasks.download.EpisodeDownloadTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

public class AnimePaheEpisodeDownloadTask extends EpisodeDownloadTask implements AnimePaheHolder {

    private long ffmpegTask;

    public AnimePaheEpisodeDownloadTask(SelectedActivity application, String url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    @Override
    public String call() throws Exception {
        //M3u8 download
        if (!outDir.exists()) outDir.mkdir();
        this.outFile = new File(outDir, count[1] + ".mp4");
        //Fetch document
        if (url.isEmpty()) {
            this.progressHandler.onErrorThrown(getError("Video resolution"));
            return null;
        }

        final Document document = Jsoup.connect(url).userAgent(Constant.USER_AGENT).referrer(ANIME_PAHE_REFERER).get();
        final Elements javascriptElements = document.getElementsByTag("script");

        final JsUnpacker jsUnpacker = new JsUnpacker(javascriptElements.get(javascriptElements.size() - 2).toString());        //Get second to last javascript & unpack it
        if (!jsUnpacker.detect()) {
            this.progressHandler.onErrorThrown(getError("Finding P.A.C.K.E.R"));
            return null;
        }

        final Matcher videoSrcMatcher = VIDEO_SRC_PATTERN.matcher(jsUnpacker.unpack()); //Prepare matcher with unpacked javascript

        String m3u8Src = "";
        while (videoSrcMatcher.find()) {
            final String s = videoSrcMatcher.group();
            m3u8Src = s.substring(s.indexOf('\'') + 1, s.lastIndexOf('\''));
        }

        if (m3u8Src.isEmpty()) {
            this.progressHandler.onErrorThrown(getError("Extracting source"));
            return null;
        }

        final List<String> ffmpegArguments = new LinkedList<>();
        //Headers
        ffmpegArguments.add("-headers");
        ffmpegArguments.add("referer:" + url);
        //Input
        ffmpegArguments.add("-i");
        ffmpegArguments.add(m3u8Src);

        ffmpegArguments.add("-vcodec");
        ffmpegArguments.add("copy");
        ffmpegArguments.add("-c:a");
        ffmpegArguments.add("copy");
        ffmpegArguments.add("-acodec");
        ffmpegArguments.add("mp3");
        //Output
        ffmpegArguments.add(outFile.getPath());

        this.ffmpegTask = this.startFFDefaultTask(ffmpegArguments, m3u8Src, new String[]{"Referer", url}, new String[]{"User-Agent", Constant.USER_AGENT});
        return null;
    }

    @Override
    public void cancelExecution() {
        FFmpeg.cancel(ffmpegTask);
        super.cancelExecution();
    }
}
