/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.04.21, 18:02
 */

package net.bplaced.abzzezz.animeapp.util.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class JSONHelper {
    /**
     * Warps a json object using an optional
     *
     * @param jsonObject jsonobject to get items from
     * @param key        the corresponding key
     * @return optional with a jsonobject (empty if null)
     */
    public static Optional<JSONObject> getJSONObject(final JSONObject jsonObject, final String key) {
        return Optional.ofNullable(jsonObject.optJSONObject(key));
    }

    public static boolean[] getBooleanArray(final JSONArray jsonArray, final int fallback) throws JSONException {
        if (jsonArray == null) return new boolean[fallback];

        final boolean[] booleans = new boolean[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            booleans[i] = jsonArray.getBoolean(i);
        }

        return booleans;
    }

    public static JSONArray getBooleanArrayAsJSONArray(final boolean[] object) {
        final JSONArray jsonArray = new JSONArray();
        for (boolean o : object) {
            jsonArray.put(o);
        }
        return jsonArray;
    }

}
