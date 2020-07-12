/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:13
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class DataBaseTask implements Callable<JSONObject> {

    private final String id;
    private final DataBaseSearch dataBaseSearch;

    public DataBaseTask(String id, DataBaseSearch dataBaseSearch) {
        this.id = id;
        this.dataBaseSearch = dataBaseSearch;
    }


    @Override
    public JSONObject call() throws Exception {
        final JSONObject showDetails = dataBaseSearch.getShowDetails(id);
        if (showDetails == null) return null;
        final JSONObject inf = new JSONObject();
        inf.put("id", id);
        inf.put("image_url", StringHandler.COVER_DATABASE.concat(showDetails.getString("image_id")));
        inf.put("episodes", showDetails.getString("Letzte"));
        inf.put("title", showDetails.getString("titel"));
        inf.put("language", showDetails.getString("Untertitel"));
        inf.put("year", showDetails.getString("Jahr"));
        return inf;
    }
}
