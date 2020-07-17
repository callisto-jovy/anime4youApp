/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:05
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;


public class DataBaseSearch {


    /**
     * @param search
     * @param url
     * @return
     * @throws MalformedURLException
     */
    public final String getShowDetails(final String search, final String url) throws IOException {
        final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("User-Agent", StringHandler.USER_AGENT);
        urlConnection.setConnectTimeout(4000);
        urlConnection.connect();

        final StringBuilder builder = new StringBuilder(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining()));
        final int start = builder.indexOf(search);
        return builder.substring(start, builder.indexOf("}", start) + 1);
    }
}
