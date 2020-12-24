/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:23
 */

package net.bplaced.abzzezz.animeapp.util.show;

import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.ProviderType;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class Show {

    private String id;
    private String year = "";
    private String title;
    private String episodes;
    private String imageURL;
    private String language;

    private Provider provider;
    private JSONObject showJSON;

    public Show(String id, String title, String episodes, String imageURL, String language, Provider provider) {
        this.id = id;
        this.title = title;
        this.episodes = episodes;
        this.imageURL = imageURL;
        this.language = language;
        this.provider = provider;
    }

    public Show(JSONObject showJSON) throws JSONException {
        this.id = showJSON.getString(StringHandler.SHOW_ID);
        this.title = showJSON.getString(StringHandler.SHOW_TITLE);
        this.episodes = showJSON.getString(StringHandler.SHOW_EPISODE_COUNT);
        this.imageURL = showJSON.getString(StringHandler.SHOW_IMAGE_URL);
        this.language = showJSON.getString(StringHandler.SHOW_LANG);
        this.provider = ProviderType.getProvider(showJSON.getString(StringHandler.SHOW_PROVIDER));
    }

    public Show(JSONObject showJSON, ProviderType provider) throws JSONException {
        this.id = showJSON.getString(StringHandler.SHOW_ID);
        this.title = showJSON.getString(StringHandler.SHOW_TITLE);
        this.episodes = showJSON.getString(StringHandler.SHOW_EPISODE_COUNT);
        this.imageURL = showJSON.getString(StringHandler.SHOW_IMAGE_URL);
        this.language = showJSON.getString(StringHandler.SHOW_LANG);
        this.provider = provider.getProvider();
    }

    @Override
    public String toString() {
        try {
            return provider.format(this).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public JSONObject getShowJSON() {
        return showJSON;
    }

    public void setShowJSON(JSONObject showJSON) {
        this.showJSON = showJSON;
    }

    public String getYear() {
        return year;
    }

    public String getID() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String episodes) {
        this.episodes = episodes;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

}
