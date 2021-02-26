/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 21.02.21, 13:07
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animecloud;

import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.provider.holders.AnimeCloudHolder;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class AnimeCloudSearchTask extends TaskExecutor implements Callable<List<Show>>, AnimeCloudHolder {

    private final String searchQuery;

    public AnimeCloudSearchTask(final String searchQuery) {
        this.searchQuery = searchQuery;
    }


    public void executeAsync(final Callback<List<Show>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<Show> call() throws Exception {
        final List<Show> showsOut = new ArrayList<>();
        final JSONObject showsIn = new JSONObject(URLUtil.collectLines(URLUtil.createHTTPSURLConnection(String.format(SEARCH_API, searchQuery)), ""));

        Iterator<String> keys = showsIn.keys();

        while(keys.hasNext()) {
            final String key = keys.next();
            if (showsIn.get(key) instanceof JSONObject) {
                final JSONObject showJSON = showsIn.getJSONObject(key);
                showsOut.add(Providers.ANIMECLOUD.getProvider().getShowFromSave(showJSON));
            }
        }

        return showsOut;
    }
}
