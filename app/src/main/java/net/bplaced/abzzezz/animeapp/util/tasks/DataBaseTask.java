/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:13
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;

import java.util.concurrent.Callable;

public class DataBaseTask implements Callable<String[]> {

    private final String aid;
    private final DataBaseSearch dataBaseSearch;

    public DataBaseTask(final String aid, final DataBaseSearch dataBaseSearch) {
        this.aid = aid;
        this.dataBaseSearch = dataBaseSearch;
    }


    @Override
    public String[] call() throws Exception {
        String realSeries = dataBaseSearch.getSubstringFromDB(aid);
        String coverURL = realSeries.isEmpty() ? "0" : StringUtil.getStringFromLong(realSeries, "src=\\\"", "\\\"");
        String episodesString = realSeries.isEmpty() ? "0" : StringUtil.getStringFromLong(realSeries, "\"Letzte\":\"", "\"");
        String seriesName = realSeries.isEmpty() ? "ERROR" : StringUtil.getStringFromLong(realSeries, "\"titel\":\"", "\"");
        String language = realSeries.isEmpty() ? "ERROR" : StringUtil.getStringFromLong(realSeries, "\"Untertitel\":\"", "\"");
        return new String[]{seriesName, episodesString, coverURL, aid, language};
    }
}
