/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.02.21, 16:06
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.json.JSONHelper;
import net.bplaced.abzzezz.animeapp.util.provider.holders.AnimePaheHolder;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

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
        final JSONObject episodeJSONObject = new JSONObject(this.episodeJSON); //Supply the json from the episode
        final String id = episodeJSONObject.getString("anime_id");
        final String session = episodeJSONObject.getString("session");
        //Establish a new connection to the episode api with the id & session. Add a user-agent as a confirmation of authenticity
        final HttpsURLConnection episodeAPIConnection = URLUtil.createHTTPSURLConnection(String.format(STREAM_API, id, session), new String[]{"User-Agent", RandomUserAgent.getRandomUserAgent()});
        final JSONArray availableStreams = new JSONObject(URLUtil.collectLines(episodeAPIConnection, "")).getJSONArray("data"); //Collect all data from the output, read it into a new jsonobject and get the jsonarray called "data"
        //Get the highest resolution available
        return IntStream.range(0, availableStreams.length())
                .mapToObj(value ->
                        Optional.ofNullable(availableStreams.optJSONObject(value).names()) //Warp the json object's names (ie. the resolution) using an optional
                                .map(jsonArray -> { //Map the optionals value (if the optionals's value exists)
                                    try {
                                        String opt = jsonArray.optString(0, "0"); //Get the name json array's first element, use an 0 as a fallback (also so it doesn't throw unnecessary exceptions)
                                        return new JSONObject() //Return a new json object with the resolution & direct url
                                                .put("res", Integer.parseInt(opt))
                                                .put("url", JSONHelper.getJSONObject(availableStreams.optJSONObject(value), opt) //Warp the possibility of the json object requested being NULL with an optional
                                                        .map(map -> map.optString("kwik")) //Map the optional's value to the "kwik url" or else an empty string
                                                        .orElse(""));
                                    } catch (final JSONException e) { //Catch the exception :/
                                        e.printStackTrace();
                                        return null;
                                    }
                                }))
                .max(Comparator.comparingInt(value -> //Get the highest value from the int "res" (0 if it doesn't exist)
                        value.map(jsonObject -> jsonObject.optInt("res"))
                                .orElse(0)))
                .map(optional -> //Map the result to the corresponding url
                        optional.map(jsonObject -> jsonObject.optString("url"))
                                .orElse(""))
                .orElse(""); //Return nothing if no max is found, etc.

    }
}
