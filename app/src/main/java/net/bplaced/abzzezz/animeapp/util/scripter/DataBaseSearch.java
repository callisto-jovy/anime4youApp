/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.05.20, 15:29
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import android.os.AsyncTask;
import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;

import java.net.MalformedURLException;
import java.net.URL;


public class DataBaseSearch extends AsyncTask<String, Void, String[]> {

    
    /**
     * @param aid
     * @return
     */

    public String getSubstringFromDB(String aid) {
        String realSeries = "";
        try {
            String line = URLUtil.getURLContentAsString(new URL(URLHandler.dataBase));
            realSeries = StringUtil.getStringFromLong(line, "\"aid\"" + ":" + "\"" + aid + "\"", "}");
        } catch (StringIndexOutOfBoundsException | MalformedURLException e) {
            e.printStackTrace();
            Logger.log("Checking Database: " + e.getMessage(), Logger.LogType.ERROR);
        }
        return realSeries;
    }

    @Override
    protected String[] doInBackground(String... aid) {
        String realSeries = getSubstringFromDB(aid[0]);
        String coverURL = realSeries.isEmpty() ? "0" : StringUtil.getStringFromLong(realSeries, "src=\\\"", "\\\"");
        String episodesString = realSeries.isEmpty() ? "0" : StringUtil.getStringFromLong(realSeries, "\"Letzte\":\"", "\"");
        String seriesName = realSeries.isEmpty() ? "ERROR" : StringUtil.getStringFromLong(realSeries, "\"titel\":\"", "\"");
        String language = realSeries.isEmpty() ? "ERROR" : StringUtil.getStringFromLong(realSeries, "\"Untertitel\":\"", "\"");
        return new String[]{seriesName, episodesString, coverURL, String.valueOf(aid[0]), language};
    }

    @Override
    protected void onCancelled() {
        Logger.log("Cancelled DataBase Task", Logger.LogType.INFO);
        super.onCancelled();
    }
}
