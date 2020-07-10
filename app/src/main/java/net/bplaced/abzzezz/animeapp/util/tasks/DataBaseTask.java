/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:13
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class DataBaseTask implements Callable<JSONObject> {

    private final String id;
    private final DataBaseSearch dataBaseSearch;

    public DataBaseTask(final String aid, final DataBaseSearch dataBaseSearch) {
        this.id = aid;
        this.dataBaseSearch = dataBaseSearch;
    }


    @Override
    public JSONObject call() throws Exception {
        String realSeries = dataBaseSearch.getSubstringFromDB(id);
        if (realSeries.isEmpty()) return null;
        JSONObject inf = new JSONObject();
        inf.put("id", id);
        inf.put("image_url", StringUtil.getStringFromLong(realSeries, "src=\\\"", "\\\""));
        inf.put("episodes", StringUtil.getStringFromLong(realSeries, "\"Letzte\":\"", "\""));
        inf.put("title", StringUtil.getStringFromLong(realSeries, "\"titel\":\"", "\""));
        inf.put("language", StringUtil.getStringFromLong(realSeries, "\"Untertitel\":\"", "\""));
        inf.put("year", StringUtil.getStringFromLong(realSeries, "\"Jahr\":\"", "\""));
        return inf;
    }
}
