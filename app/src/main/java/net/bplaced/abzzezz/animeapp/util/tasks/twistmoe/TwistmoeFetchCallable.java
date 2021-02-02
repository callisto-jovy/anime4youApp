/*
 * Copyright (c) 2020.
 * The code used in this project is entirely owned by Roman P.
 * Code snippets / templates / etc. are mentioned and credited.
 */

package net.bplaced.abzzezz.animeapp.util.tasks.twistmoe;

import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.impl.TwistmoeHolder;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class TwistmoeFetchCallable implements Callable<JSONObject>, TwistmoeHolder {

    private final String slug;

    public TwistmoeFetchCallable(final String slug) {
        this.slug = slug;
    }

    @Override
    public JSONObject call() throws Exception {
        HttpsURLConnection connection = URLUtil.createHTTPSURLConnection(SHOW_API + slug, requestHeaders);
        connection.connect();
        final JSONObject fetchedDetails = new JSONObject(new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining()));

        connection = URLUtil.createHTTPSURLConnection(SHOW_API + slug + "/sources/", requestHeaders);
        connection.connect();

        final JSONArray fetchedSources = new JSONArray(new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining()));
        final JSONArray sources = new JSONArray();

        for (int i = 0; i < fetchedSources.length(); i++) {
            final JSONObject item = fetchedSources.getJSONObject(i);
            sources.put(item.getString("source"));
        }

        final JSONObject showDetails = new JSONObject();
        showDetails.put("url", slug)
                .put("title", fetchedDetails.getString("title"))
                .put("id", fetchedDetails.getString("id"))
                .put("sources", sources)
                .put("episodes", sources.length());
        return showDetails;
    }
}
