/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.03.21, 22:19
 */

package net.bplaced.abzzezz.animeapp.util.json;

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

}
