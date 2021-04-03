/*
 * Copyright (c) 2020.
 * The code used in this project is entirely owned by Roman P.
 * Code snippets / templates / etc. are mentioned and credited.
 */

package net.bplaced.abzzezz.animeapp.util.tasks.twistmoe;

import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.holders.TwistmoeHolder;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.ricecode.similarity.JaroStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TwistmoeSearchTask extends TaskExecutor implements Callable<List<JSONObject>>, TwistmoeHolder {

    private final String showMALID, showTitle;
    private final SimilarityStrategy stringSimilarity = new JaroStrategy();

    public TwistmoeSearchTask(final String showMALID, final String showTitle) {
        this.showMALID = showMALID;
        this.showTitle = showTitle;
    }

    public void executeAsync(final Callback<List<JSONObject>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<JSONObject> call() throws Exception {
        final List<JSONObject> showsOut = new ArrayList<>();

        final HttpsURLConnection connection = URLUtil.createHTTPSURLConnection(SHOW_API, new String[]{"x-access-token", getRequestToken()}, new String[]{"User-Agent", Constant.USER_AGENT}, new String[]{"Referer", "https://twist.moe/"});
        connection.connect();
        final JSONArray shows = new JSONArray(URLUtil.collectLines(connection, ""));

        final int malID = Integer.parseInt(showMALID);

        for (int i = 0; i < shows.length(); i++) {
            final JSONObject showJSON = shows.getJSONObject(i);
            //TODO: Maybe sort array with the string similarity?? Only ofc if it's size is more than one
            if (showJSON.isNull("mal_id")) {
                if (stringSimilarity.score(showJSON.getString("title"), showTitle) > 0.8 || stringSimilarity.score(showJSON.getString("alt_title"), showTitle) > 0.8) {
                    showsOut.add(new TwistmoeFetchCallable(showJSON.getJSONObject("slug").getString("slug")).call());
                }
            } else {
                if (showJSON.getInt("mal_id") == malID)
                    showsOut.add(new TwistmoeFetchCallable(showJSON.getJSONObject("slug").getString("slug")).call());
            }
        }
        return showsOut;
    }
}
