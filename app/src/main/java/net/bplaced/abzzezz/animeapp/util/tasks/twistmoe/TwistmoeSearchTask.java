/*
 * Copyright (c) 2020.
 * The code used in this project is entirely owned by Roman P.
 * Code snippets / templates / etc. are mentioned and credited.
 */

package net.bplaced.abzzezz.animeapp.util.tasks.twistmoe;

import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.provider.impl.TwistmoeHolder;
import net.bplaced.abzzezz.animeapp.util.provider.providers.Twistmoe;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.ricecode.similarity.JaroStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TwistmoeSearchTask extends TaskExecutor implements Callable<List<Show>>, TwistmoeHolder {

    private final String searchQuery;
    private final SimilarityStrategy stringSimilarity = new JaroStrategy();

    public TwistmoeSearchTask(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public <R> void executeAsync(final Callback<List<Show>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<Show> call() throws Exception {
        final List<Show> showsOut = new ArrayList<>();

        final HttpsURLConnection connection = URLUtil.createHTTPSURLConnection(SHOW_API, new String[]{"x-access-token", getRequestToken()}, new String[]{"User-Agent", StringHandler.USER_AGENT});
        connection.connect();
        final JSONArray shows = new JSONArray(URLUtil.collectLines(connection, ""));

        final Twistmoe decoder = (Twistmoe) Providers.TWISTMOE.getProvider();

        for (int i = 0; i < shows.length(); i++) {
            final JSONObject showJSON = shows.getJSONObject(i);
            if (stringSimilarity.score(showJSON.getString("title"), searchQuery) > 0.8 || stringSimilarity.score(showJSON.getString("alt_title"), searchQuery) > 0.8) {
                final String slug = showJSON.getJSONObject("slug").getString("slug");
                showsOut.add(decoder.getShow(new TwistmoeFetchCallable(slug).call()));
            }
        }

        return showsOut;
    }
}
