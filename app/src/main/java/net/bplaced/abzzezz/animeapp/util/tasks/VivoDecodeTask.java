/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.11.20, 20:00
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VivoDecodeTask extends TaskExecutor implements Callable<String> {

    private final String url;

    public VivoDecodeTask(final String url) {
        this.url = url;
    }

    public <R> void executeAsync(Callback<String> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public String call() throws Exception {
        final StringBuilder finalUrl = new StringBuilder();
        final Pattern pattern = Pattern.compile("Core\\.InitializeStream\\s*\\(\\s*\\{[^)}]*source\\s*:\\s*'(.*?)',\\n");
        final Document document = Jsoup.connect("https://vivo.sx/605301b221").userAgent("Mozilla/5.0 (Linux; Android 7.0; Moto C Plus) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.73 Mobile Safari/537.36").get();
        final Element body = document.body();
        
        String source = body.getElementsByClass("vivo-website-wrapper").first().getElementsByTag("script").get(2).data();

        final Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            source = matcher.group(1);
            source = URLDecoder.decode(source, StandardCharsets.UTF_8.toString());

            for (int i = 0; i < source.toCharArray().length; i++) {
                char c = source.charAt(i);
                if (c != ' ') {
                    c += '/';
                    if (126 < c) {
                        c -= 94;
                    }
                    finalUrl.append(c);
                }
            }
        }
        return finalUrl.toString();
    }
}
