/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.02.21, 16:06
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.holders.AnimePaheHolder;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.util.concurrent.Callable;

public class AnimePaheFetchDirectTask extends TaskExecutor implements Callable<String>, AnimePaheHolder {

    private final String episodeJSON;

    public AnimePaheFetchDirectTask(final String episodeJSON) {
        this.episodeJSON = episodeJSON;
    }

    public void executeAsync(Callback<String> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public String call() throws Exception {
        final JSONObject episodeJSONObject = new JSONObject(this.episodeJSON);
        final String id = episodeJSONObject.getString("anime_id");
        final String session = episodeJSONObject.getString("session");

        final HttpsURLConnection episodeAPIConnection = URLUtil.createHTTPSURLConnection(String.format(STREAM_API, id, session), new String[]{"User-Agent", RandomUserAgent.getRandomUserAgent()});
        final JSONArray availableStreams = new JSONObject(URLUtil.collectLines(episodeAPIConnection, "")).getJSONArray("data");

        String link = "";

        for (int i = 0; i < availableStreams.length(); i++) {
            final JSONObject above = availableStreams.getJSONObject(i);

            if (above.has("1080")) {
                link = above.getJSONObject("1080").getString("kwik");
                break;
            } else if (above.has("720")) {
                link = above.getJSONObject("720").getString("kwik");
            } else if (above.has("360")) {
                link = above.getJSONObject("360").getString("kwik");
            }
        }
        return link;
    }
}
