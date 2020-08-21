/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:13
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class DataBaseTask implements Callable<JSONObject> {

    private final JSONObject details;
    private final DataBaseSearch dataBaseSearch;
    private String id;

    public DataBaseTask(final JSONObject details, final DataBaseSearch dataBaseSearch) {
        this.details = details;
        this.dataBaseSearch = dataBaseSearch;
        try {
            this.id = details.getString(StringHandler.SHOW_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public DataBaseTask(String id, DataBaseSearch dataBaseSearch) {
        this.dataBaseSearch = dataBaseSearch;
        this.id = id;
        this.details = null;
    }


    @Override
    public JSONObject call() {
        final String search = "{\"aid\":\"" + id.concat("\"");
        try {
            return getDetails(new JSONObject(dataBaseSearch.getShowDetails(search, StringHandler.DATABASE)));
        } catch (Exception e) {
            Logger.log("Exception thrown in DataBaseTask", Logger.LogType.WARNING);

            if (e instanceof JSONException) {
                Logger.log("JSON exception identified", Logger.LogType.INFO);
                if (details != null) {
                    return details;
                } else {
                    Logger.log("Show details null, requesting backup database", Logger.LogType.INFO);
                    try {
                        return getDetails(new JSONObject(dataBaseSearch.getShowDetails(search, StringHandler.BACKUP_DATABASE)));
                    } catch (Exception exception) {
                        e.printStackTrace();
                        return null;
                    }
                }
            } else {
                Logger.log("Timeout/MalformedURL/IoException identified. Requesting Backup database", Logger.LogType.INFO);
                try {
                    return getDetails(new JSONObject(dataBaseSearch.getShowDetails(search, StringHandler.BACKUP_DATABASE)));
                } catch (Exception exception) {
                    Logger.log("Requesting backup database exception thrown. Result might be null", Logger.LogType.WARNING);
                    return details;
                }
            }
        }
    }


    private JSONObject getDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject inf = new JSONObject();
        inf.put(StringHandler.SHOW_ID, id);
        inf.put(StringHandler.SHOW_IMAGE_URL, StringHandler.COVER_DATABASE.concat(jsonObject.getString(StringHandler.SHOW_IMAGE_URL)));
        inf.put(StringHandler.SHOW_EPISODES_COUNT, jsonObject.getString("Letzte"));
        inf.put(StringHandler.SHOW_TITLE, jsonObject.getString("titel"));
        inf.put(StringHandler.SHOW_LANG, jsonObject.getString("Untertitel"));
        inf.put(StringHandler.SHOW_YEAR, jsonObject.getString("Jahr"));
        return inf;
    }
}
