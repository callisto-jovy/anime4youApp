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

    private String year;
    private String id;
    private String title;
    private String episodes;
    private String imageURL;
    private String language;

    private Provider provider;
    private JSONObject showAdditional;

    public Show(String id, String title, String episodes, String imageURL, String language, Provider provider, final JSONObject... showAdditional) {
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
        final Show thisShow = provider.getShowFromSave(showJSON);

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
    public String toString() {
        try {
            return provider.formatShowForSave(this).toString();
        } catch (final JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setYear(String year) {
        this.year = year;
    }

    public JSONObject getShowAdditional() {
        return showAdditional;
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
