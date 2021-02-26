/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 08.02.21, 18:24
 */

package net.bplaced.abzzezz.animeapp.util.tasks.twistmoe;

import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.provider.holders.TwistmoeHolder;
import net.bplaced.abzzezz.animeapp.util.provider.providers.Twistmoe;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TwistmoeImportMALTask extends TaskExecutor implements Callable<List<Show>>, TwistmoeHolder {

    private final String malURL;

    public TwistmoeImportMALTask(final String malURL) {
        this.malURL = malURL;
    }

    public void executeAsync(Callback<List<Show>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<Show> call() throws Exception {
        final List<Show> foundShows = new ArrayList<>();

        final HttpsURLConnection connection = URLUtil.createHTTPSURLConnection(SHOW_API, new String[]{"x-access-token", getRequestToken()}, new String[]{"User-Agent", StringHandler.USER_AGENT}, new String[]{"Referer", "https://twist.moe/"});
        final JSONArray shows = new JSONArray(URLUtil.collectLines(connection, ""));

        final Twistmoe decoder = (Twistmoe) Providers.TWISTMOE.getProvider();

        //TODO: Check if works
        final int[] malIDs = new int[0];

        for (int i = 0; i < shows.length(); i++) {
            final JSONObject showJSON = shows.getJSONObject(i);
            if (!showJSON.has("malID")) continue;

            for (final int malID : malIDs) {
                if (malID == showJSON.getInt("malID")) {
                    foundShows.add(decoder.getShowFromSave(new TwistmoeFetchCallable(showJSON.getJSONObject("slug").getString("slug")).call()));
                }
            }
        }
        return foundShows;
    }
    //TODO: Get all MAL titles.


}
