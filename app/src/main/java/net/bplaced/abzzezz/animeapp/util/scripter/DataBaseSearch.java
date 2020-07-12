/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:05
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;


public class DataBaseSearch {


    /**
     * @param aid
     * @return
     */

    public JSONObject getShowDetails(final String aid) {
        final String search = "{\"aid\":\"" + aid + "\"";
        try {
            return new JSONObject(getShowDetails(search, StringHandler.DATABASE));
        } catch (StringIndexOutOfBoundsException | MalformedURLException | JSONException e) {
            Logger.log("Checking Database: " + e.getMessage(), Logger.LogType.ERROR);
            try {
                return new JSONObject(getShowDetails(search, StringHandler.BACKUP_DATABASE));
            } catch (JSONException | MalformedURLException jsonException) {
                jsonException.printStackTrace();
                return null;
            }
        }
    }

    /**
     *
     * @param search
     * @param url
     * @return
     * @throws MalformedURLException
     */
    private String getShowDetails(final String search, final String url) throws MalformedURLException {
        final StringBuilder builder = new StringBuilder(URLUtil.getURLContentAsString(new URL(url)));
        final int start = builder.indexOf(search);
        return builder.substring(start, builder.indexOf("}", start) + 1);
    }
}
