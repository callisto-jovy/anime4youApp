/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:21
 */

package net.bplaced.abzzezz.animeapp.util.provider;

import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Provider {

    private ProviderType type;
    private String baseURL;

    public Provider(ProviderType type, String baseURL) {
        this.type = type;
        this.baseURL = baseURL;
    }

    public abstract JSONObject format(final Show show) throws JSONException;

    public abstract Show getShow(final JSONObject data) throws JSONException;

    public abstract void handleDownload();


    public ProviderType getType() {
        return type;
    }

    public void setType(ProviderType type) {
        this.type = type;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
