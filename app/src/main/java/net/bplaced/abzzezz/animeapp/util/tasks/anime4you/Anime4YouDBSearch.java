/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 08.02.21, 18:39
 */

package net.bplaced.abzzezz.animeapp.util.tasks.anime4you;

import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.holders.Anime4YouHolder;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;


public class Anime4YouDBSearch implements Anime4YouHolder {

    public final String getShowDetails(final String search) {
        final StringBuilder builder = new StringBuilder(getDataBase());
        final int start = builder.indexOf(search);
        return builder.substring(start, builder.indexOf("}", start) + 1);
    }

    public final String getDataBase() {
        try {
            final HttpsURLConnection urlConnection = URLUtil.createHTTPSURLConnection(DATABASE, new String[]{"User-Agent", StringHandler.USER_AGENT});
            urlConnection.connect();
            return new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining());
        } catch (final Exception e) {
            e.printStackTrace();
            Logger.log("Timeout/MalformedURL/IOException identified. Requesting Backup database", Logger.LogType.INFO);
            try {
                final HttpURLConnection urlConnection = URLUtil.createHTTPURLConnection(BACKUP_DATABASE, new String[]{"User-Agent", StringHandler.USER_AGENT});
                urlConnection.connect();
                return new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining());
            } catch (final IOException ioException) {
                Logger.log("Exception thrown while requesting backup database. Return is null", Logger.LogType.WARNING);
                return "";
            }
        }
    }

}
