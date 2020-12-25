/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:23
 */

package net.bplaced.abzzezz.animeapp.util.show;

import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class Show {

    private final java.lang.String year = "";
    private java.lang.String id;
    private java.lang.String title;
    private java.lang.String episodes;
    private java.lang.String imageURL;
    private java.lang.String language;

    private Provider provider;
    private JSONObject showAdditional;

    public Show(java.lang.String id, java.lang.String title, java.lang.String episodes, java.lang.String imageURL, java.lang.String language, Provider provider, final JSONObject... showAdditional) {
        this.id = id;
        this.title = title;
        this.episodes = episodes;
        this.imageURL = imageURL;
        this.language = language;
        this.provider = provider;
        for (final JSONObject jsonObject : showAdditional) {
            this.showAdditional = jsonObject;
        }
    }

    public Show(JSONObject showJSON) throws JSONException {
        this.provider = Providers.getProvider(showJSON.getString(StringHandler.SHOW_PROVIDER));
        final Show thisShow = provider.decode(showJSON);

        this.id = thisShow.getID();
        this.title = thisShow.getTitle();
        this.episodes = thisShow.getEpisodes();
        this.imageURL = thisShow.getImageURL();
        this.language = thisShow.getLanguage();
        this.showAdditional = thisShow.getShowAdditional();
    }

    public Show(JSONObject showJSON, Providers provider) throws JSONException {
        this.id = showJSON.getString(StringHandler.SHOW_ID);
        this.title = showJSON.getString(StringHandler.SHOW_TITLE);
        this.episodes = showJSON.getString(StringHandler.SHOW_EPISODE_COUNT);
        this.imageURL = showJSON.getString(StringHandler.SHOW_IMAGE_URL);
        this.language = showJSON.getString(StringHandler.SHOW_LANG);
        this.provider = provider.getProvider();
    }

    @Override
    public java.lang.String toString() {
        try {
            return provider.format(this).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public JSONObject getShowAdditional() {
        return showAdditional;
    }

    public void setShowAdditional(JSONObject showAdditional) {
        this.showAdditional = showAdditional;
    }

    public java.lang.String getYear() {
        return year;
    }

    public java.lang.String getID() {
        return id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public java.lang.String getTitle() {
        return title;
    }

    public void setTitle(java.lang.String title) {
        this.title = title;
    }

    public java.lang.String getEpisodes() {
        return episodes;
    }

    public void setEpisodes(java.lang.String episodes) {
        this.episodes = episodes;
    }

    public java.lang.String getImageURL() {
        return imageURL;
    }

    public void setImageURL(java.lang.String imageURL) {
        this.imageURL = imageURL;
    }

    public java.lang.String getLanguage() {
        return language;
    }

    public void setLanguage(java.lang.String language) {
        this.language = language;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

}
