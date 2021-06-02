/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 09.04.21, 17:42
 */

package net.bplaced.abzzezz.animeapp.util.show;

import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.datatypes.ArrayHelper;
import net.bplaced.abzzezz.animeapp.util.json.JSONHelper;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Optional;

public class Show {

    private final String malID; //the Myanimelist show id
    private final String showTitle; //the show's title
    private final double showScore; //the show's score, mostly rounded to two digits
    private final String imageURL; //the image url
    private final int episodeCount; //the total amount of episodes
    private boolean[] episodesWatched; //Keep track of all the individual episodes watched

    private final JSONObject providers; //Provider information for each provider

    /**
     * Basic Show object constructed from MAL data
     *
     * @param id           MAL ID
     * @param title        Title fetched from MAL
     * @param episodeCount episode count
     * @param imageURL     image url from MAL
     * @param showScore    the year the show was released
     */
    public Show(String id, String title, int episodeCount, String imageURL, final double showScore) {
        this.malID = id;
        this.showTitle = title;
        this.episodeCount = episodeCount;
        this.imageURL = imageURL;
        this.showScore = showScore;
        this.episodesWatched = new boolean[episodeCount];
        this.providers = new JSONObject();
    }

    /**
     * Basic Show object constructed from MAL data with episodes watched
     *
     * @param id           MAL ID
     * @param title        Title fetched from MAL
     * @param episodeCount episode count
     * @param imageURL     image url from MAL
     * @param showScore    the year the show was released
     */
    public Show(String id, String title, int episodeCount, String imageURL, final double showScore, final int episodesWatched) {
        this.malID = id;
        this.showTitle = title;
        this.episodeCount = episodeCount;
        this.imageURL = imageURL;
        this.showScore = showScore;
        this.episodesWatched = new boolean[episodeCount];
        if (episodesWatched > episodeCount)
            Arrays.fill(this.episodesWatched, true);
        else
            Arrays.fill(this.episodesWatched, 0, episodesWatched, true);
        this.providers = new JSONObject();
    }


    /**
     * Retrieve Show from formatted data
     *
     * @param showJSON showJSON to restore the show from
     * @throws JSONException json
     */
    public Show(final JSONObject showJSON) throws JSONException {
        this.malID = showJSON.getString(Constant.SHOW_ID);
        this.showTitle = showJSON.getString(Constant.SHOW_TITLE);
        this.episodeCount = showJSON.optInt(Constant.SHOW_EPISODE_COUNT);
        this.imageURL = showJSON.getString(Constant.SHOW_IMAGE_URL);
        this.showScore = showJSON.optDouble(Constant.SHOW_SCORE, 0);
        this.episodesWatched = JSONHelper.getBooleanArray(showJSON.optJSONArray(Constant.SHOW_EPISODES_WATCHED), episodeCount);
        this.providers = showJSON.getJSONObject("provider_info");
    }

    /**
     * Gets all episodes from a certain provider
     *
     * @param provider provider to get the episodes from
     * @return JSONArray with all episode referrals
     */
    public JSONArray getShowEpisodes(final Provider provider) {
        return getProviderJSON(provider).map(jsonObject -> jsonObject.optJSONArray("episodes")).orElse(new JSONArray());
    }

    /**
     * Calculates the difference between the timestamp of the provider & the current system time
     * Used to calculate the age of the episode referrals
     *
     * @param provider provider to get the timestamp of
     * @return Difference between the current time and the timestamp
     */
    public long getTimestampDifference(final Provider provider) {
        if (AnimeAppMain.getInstance().isDeveloperMode()) return System.currentTimeMillis();
        return this.getProviderJSON(provider)
                .map(jsonObject -> System.currentTimeMillis() - jsonObject.optLong("time"))
                .orElse(System.currentTimeMillis());
    }

    /**
     * Refreshes the
     *
     * @param episodes json array with all the episode referrals for the show
     * @param provider provider to add the json array to
     */
    public void addEpisodesForProvider(final JSONArray episodes, final Provider provider) {
        this.getProviderJSON(provider).ifPresent(providerJSON -> {
            try {
                providerJSON.put("episodes", episodes); //Update episodes
                providerJSON.put("time", System.currentTimeMillis()); //Add the timestamp for later comparison
            } catch (final JSONException e) {
                e.printStackTrace();
            }
            //Finally, update the provider json
            this.updateProviderJSON(provider, providerJSON);
        });
    }

    /**
     * Gets the providers JSON
     *
     * @param provider Provider to get the JSONObject of
     * @return the provider's json object, wrapped in an optional. Returns Optional.empty() if no key was found
     */
    public Optional<JSONObject> getProviderJSON(final Provider provider) {
        return Optional.ofNullable(providers.optJSONObject(provider.getName()) == null ? new JSONObject() : providers.optJSONObject(provider.getName()));
    }

    /**
     * Updates the provider's JSON
     *
     * @param provider     provider to update the JSON of
     * @param providerJSON provider Json to write
     */
    public void updateProviderJSON(final Provider provider, final JSONObject providerJSON) {
        try {
            this.providers.put(provider.getName(), providerJSON);
            this.updateShow();
        } catch (final JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates this show at it's given index
     */
    public void updateShow() {
        final int index = AnimeAppMain.getInstance().getShowSaver().getIndex(this);
        if (index == -1) {
            throw new IndexOutOfBoundsException("Index out of range");
        } else
            AnimeAppMain.getInstance().getShowSaver().refreshShow(this, index);
    }

    /**
     * @return JSONObject with all needed information
     */
    @Override
    public String toString() {
        try {
            return new JSONObject()
                    .put(Constant.SHOW_ID, getID())
                    .put(Constant.SHOW_TITLE, getShowTitle())
                    .put(Constant.SHOW_SCORE, getShowScore())
                    .put(Constant.SHOW_IMAGE_URL, getImageURL())
                    .put(Constant.SHOW_EPISODE_COUNT, getEpisodeCount())
                    .put(Constant.SHOW_EPISODE_COUNT, getEpisodesWatched())
                    .put("provider_info", providers)
                    .toString();
        } catch (final JSONException e) {
            e.printStackTrace();
            return "{}"; //Empty json object
        }
    }

    public double getShowScore() {
        return showScore;
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

    public boolean[] getEpisodesWatched0() {
        return episodesWatched;
    }

    public int getEpisodesWatched() {
        return ArrayHelper.getTotalTruthsInArray(getEpisodesWatched0());
    }

    public void setEpisodesWatched(boolean[] episodesWatched) {
        this.episodesWatched = episodesWatched;
    }

    public void setEpisodesWatched(final int index, final boolean b) {
        this.episodesWatched[index] = b;
    }
}
