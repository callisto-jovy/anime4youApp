/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.02.21, 16:50
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.impl.AnimePaheHolder;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.util.concurrent.Callable;

public class AnimePaheFetchCallable implements Callable<JSONObject>, AnimePaheHolder {

    private final JSONObject showJSON;

    public AnimePaheFetchCallable(final JSONObject showJSON) {
        this.showJSON = showJSON;
    }


    @Override
    public JSONObject call() throws Exception {
        final HttpsURLConnection httpsURLConnection = URLUtil.createHTTPSURLConnection(String.format(EPISODE_API, showJSON.getString("id")), new String[]{"User-Agent", RandomUserAgent.getRandomUserAgent()});
        final JSONArray collectedLines = new JSONObject(URLUtil.collectLines(httpsURLConnection, "")).getJSONArray("data");
        httpsURLConnection.disconnect();
        showJSON.put("src", collectedLines);
        return showJSON;
    }
}
