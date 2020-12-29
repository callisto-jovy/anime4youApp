/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 16:11
 */

package net.bplaced.abzzezz.animeapp.util.connection;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class URLUtil {

    public static HttpsURLConnection createHTTPSURLConnection(final String urlIn, final String requestMethod, final String[]... requestProperties) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(urlIn).openConnection();
        connection.setRequestMethod(requestMethod);
        for (final String[] requestProperty : requestProperties) {
            connection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        return connection;
    }

    public static HttpURLConnection createHTTPURLConnection(final String urlIn, final String requestMethod, final String[]... requestProperties) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(urlIn).openConnection();
        connection.setRequestMethod(requestMethod);
        for (final String[] requestProperty : requestProperties) {
            connection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        return connection;
    }

    public static HttpsURLConnection createHTTPSURLConnection(final String urlIn, final String[]... requestProperties) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(urlIn).openConnection();
        for (final String[] requestProperty : requestProperties) {
            connection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        return connection;
    }

    public static HttpURLConnection createHTTPURLConnection(final String urlIn, final String[]... requestProperties) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(urlIn).openConnection();
        for (final String[] requestProperty : requestProperties) {
            connection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        return connection;
    }

    /**
     * Joins all the lines read from a url together
     *
     * @param src    url to read from
     * @param joiner String to join all read lines together
     * @return all joined lines
     * @throws IOException if reader / url fails, etc.
     */
    public static String collectLines(final URLConnection src, final String joiner) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(src.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line).append(joiner);
        }
        bufferedReader.close();
        return builder.toString();
    }

    /**
     * Joins all the lines read from a url together
     *
     * @param src    url to read from
     * @param joiner String to join all read lines together
     * @return all joined lines
     * @throws IOException if reader / url fails, etc.
     */
    public static String collectLines(final URL src, final String joiner) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(src.openStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line).append(joiner);
        }
        bufferedReader.close();
        return builder.toString();
    }

}
