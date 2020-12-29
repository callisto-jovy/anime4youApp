/*
 * Copyright (c) 2020.
 * The code used in this project is entirely owned by Roman P.
 * Code snippets / templates / etc. are mentioned and credited.
 */

package net.bplaced.abzzezz.animeapp.util.tasks.twistmoe;

import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.holders.TwistmoeHolder;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class TwistmoeFetchTask extends TaskExecutor implements Callable<JSONObject>, TwistmoeHolder {

    private final String showName;
    private final String url;

    public TwistmoeFetchTask(final String itemUrl) {
        this.showName = itemUrl;
        this.url = SHOW_API + showName;

    }

    public <R> void executeAsync(final Callback<JSONObject> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public JSONObject call() throws Exception {
        HttpsURLConnection connection = URLUtil.createHTTPSURLConnection(url);
        connection.connect();
        final JSONObject fetchedDetails = new JSONObject(new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining()));

        connection = URLUtil.createHTTPSURLConnection(SHOW_API + showName + "/sources/");
        connection.connect();

        final JSONArray fetchedSources = new JSONArray(new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining()));
        final JSONArray sources = new JSONArray();

        for (int i = 0; i < fetchedSources.length(); i++) {
            final JSONObject item = fetchedSources.getJSONObject(i);
            sources.put(item.getString("source"));
        }

        final JSONObject showDetails = new JSONObject();
        showDetails.put("url", showName)
                .put("title", fetchedDetails.getString("title"))
                .put("id", fetchedDetails.getString("id"))
                .put("sources", sources)
                .put("episodes", sources.length())
                .put("description", fetchedDetails.getString("description"));
        return showDetails;
    }
}
