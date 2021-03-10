/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:23
 */

package net.bplaced.abzzezz.animeapp.util.show;

import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Show {

    private final String malID;
    private final String showTitle;
    private final String showReleaseYear;
    private final String imageURL;
    private final int episodeCount;

    private final JSONObject providers;

    /**
     * Basic Show object constructed from MAL data
     *
     * @param id              MAL ID
     * @param title           Title fetched from MAL
     * @param episodeCount    episode count
     * @param imageURL        image url from MAL
     * @param showReleaseYear the year the show was released
     */
    public Show(String id, String title, int episodeCount, String imageURL, final String showReleaseYear) {
        this.malID = id;
        this.showTitle = title;
        this.episodeCount = episodeCount;
        this.imageURL = imageURL;
        this.showReleaseYear = showReleaseYear;
        this.providers = new JSONObject();
    }

    /**
     * Retrieve Show from formatted data
     *
     * @param showJSON showJSON to restore the show from
     * @throws JSONException json
     */
    public Show(JSONObject showJSON) throws JSONException {
        this.malID = showJSON.getString(StringHandler.SHOW_ID);
        this.showTitle = showJSON.getString(StringHandler.SHOW_TITLE);
        this.episodeCount = showJSON.getInt(StringHandler.SHOW_EPISODE_COUNT);
        this.imageURL = showJSON.getString(StringHandler.SHOW_IMAGE_URL);
        this.showReleaseYear = showJSON.getString(StringHandler.SHOW_YEAR);
        this.providers = showJSON.getJSONObject("provider_info");
    }

    /**
     * @param provider
     * @return
     */
    public JSONArray getShowEpisodes(final Provider provider) {
        try {
            final String providerName = provider.getName();
            if (!providers.has(providerName)) {
                Logger.log("Cannot retrieve provider information. No json object with the given provider was found", Logger.LogType.ERROR);
                return new JSONArray();
            }

            final JSONObject providerJSON = providers.getJSONObject(providerName); //Retrieve information
            return providerJSON.getJSONArray("episodes");
        } catch (final JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public long getTimestampDifference(final Provider provider) {
        try {
            final JSONObject providerJSON = getProviderJSON(provider);
            if (providerJSON.has("time")) {
                return System.currentTimeMillis() - providerJSON.getLong("time");
            } else return System.currentTimeMillis();
        } catch (JSONException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    public void addEpisodesForProvider(final JSONArray episodes, final Provider provider) {
        try {
            final JSONObject providerJSON = providers.getJSONObject(provider.getName()); //Retrieve information
            providerJSON.put("episodes", episodes); //Update
            providerJSON.put("time", System.currentTimeMillis());
            this.updateProviderJSON(provider, providerJSON);
        } catch (final JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getProviderJSON(final Provider provider) throws JSONException {
        if (providers.has(provider.getName())) {
            return providers.getJSONObject(provider.getName());
        } else {
            return new JSONObject();
        }
    }

    public void updateProviderJSON(final Provider provider, final JSONObject providerJSON) {
        try {
            this.providers.put(provider.getName(), providerJSON);
            this.updateShow();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateShow() {
        final int index = AnimeAppMain.getInstance().getShowSaver().getIndex(this);
        if (index == -1) {
            throw new IndexOutOfBoundsException("Index out of range");
        } else
            AnimeAppMain.getInstance().getShowSaver().refreshShow(this, index);
    }

    @Override
    public String toString() {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put(StringHandler.SHOW_ID, getID())
                    .put(StringHandler.SHOW_TITLE, getShowTitle())
                    .put(StringHandler.SHOW_YEAR, getShowReleaseYear())
                    .put(StringHandler.SHOW_IMAGE_URL, getImageURL())
                    .put(StringHandler.SHOW_EPISODE_COUNT, getEpisodeCount())
                    .put("provider_info", providers);
            return jsonObject.toString();
        } catch (final JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getShowReleaseYear() {
        return showReleaseYear;
    }

    public String getID() {
        return malID;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public String getImageURL() {
        return imageURL;
    }

    public JSONObject getProviders() {
        return providers;
    }
}
