/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 30.05.21, 17:27
 */

package net.bplaced.abzzezz.animeapp.util.myanimelist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.provider.holders.MyAnimeListHolder;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.myanimelist.MyAnimeListSyncTask;
import net.bplaced.abzzezz.animeapp.util.tasks.myanimelist.MyAnimeListTokenRefreshTask;
import net.bplaced.abzzezz.animeapp.util.tasks.myanimelist.MyAnimeListTokenTask;
import net.bplaced.abzzezz.animeapp.util.tasks.myanimelist.MyAnimeListUpdateEntryTask;
import net.sandrohc.jikan.Jikan;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;
import java.util.function.Consumer;

public class MyAnimeList implements MyAnimeListHolder {

    /**
     * TODO: Directly sync new additions
     */
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private boolean wasAccountAdded;
    private MyAnimeListToken myToken;
    private String username;

    @SuppressLint("CommitPrefEdits")
    public MyAnimeList(final Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = preferences.edit();
        this.wasAccountAdded = preferences.getBoolean("account_added", false);
        if (wasAccountAdded) this.username = preferences.getString("username", "");

        this.myToken = Optional.of(preferences.getString("token", "")).map(s -> {
            try {
                return new MyAnimeListToken(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }).orElse(null);
    }

    /**
     * Method called when setting up the sync for the first time
     *
     * @param username myanimelist username
     * @param password myanimelist password
     * @param onReturn consumer boolean, acts as a return statement
     */
    public void setupSync(final String username, final String password, final Consumer<Boolean> onReturn) {
        Logger.log("Setting up sync", Logger.LogType.INFO);

        //If the account was added directly sync
        if (isWasAccountAdded() && myToken != null) {
            startSync(onReturn);
            return;
        }
        //Retrieve the token for the first time & add it, then start the sync
        retrieveToken(username, password, tokenRetrieved -> {
            if (tokenRetrieved) {
                this.username = username;

                editor.putString("token", myToken.toString())
                        .putBoolean("account_added", wasAccountAdded = true)
                        .putString("username", username)
                        .commit();

                startSync(onReturn);
            } else onReturn.accept(false);
        });
    }

    /**
     * Method that is called whenever the sync is started, checks if the account was added, the token is valid, etc.
     *
     * @param onReturn consumer that acts like a return statement
     */
    public void startSync(final Consumer<Boolean> onReturn) {
        if (!isSyncAccount()) return;

        if (!isWasAccountAdded() || myToken == null) {
            onReturn.accept(false);
            return;
        }
        //Check if the token needs a refresh
        checkToken(unused -> startSync(onReturn));

        sync(onReturn); //Start the "real" sync
    }

    /**
     * Starts a new Sync-Task to add all the user's shows to the local list
     *
     * @param onReturn Consumer that acts as an callback
     */
    private void sync(final Consumer<Boolean> onReturn) {
        new MyAnimeListSyncTask(getJikan(), username).executeAsync(new TaskExecutor.Callback<Boolean>() {
            @Override
            public void onComplete(final Boolean result) {
                onReturn.accept(result);
            }

            @Override
            public void preExecute() {
                Logger.log("Syncing with mal", Logger.LogType.INFO);
            }
        });
    }

    /**
     * Starts a new async task to update the show's episodes
     *
     * @param show show to update
     */
    public void updateShowEpisodes(final Show show) {
        this.checkToken(unused -> updateShowEpisodes(show));

        if (isSyncable())
            new MyAnimeListUpdateEntryTask(show.getID(), myToken, new String[]{"num_episodes_watched", String.valueOf(show.getEpisodesWatched())}).executeAsync(new TaskExecutor.Callback<Integer>() {
                @Override
                public void onComplete(final Integer responseCode) {
                    Logger.log("Status code:" + responseCode, Logger.LogType.INFO);
                }

                @Override
                public void preExecute() {

                }
            });
    }

    /**
     * Starts a new async task to update the show's watching status
     *
     * @param show   show to apply the update to
     * @param status myanimelistwatchingstatus to get the status
     */
    public void updateShowState(final Show show, final MyAnimeListWatchingStatus status) {
        this.checkToken(unused -> updateShowState(show, status));

        if (isSyncable())
            new MyAnimeListUpdateEntryTask(show.getID(), myToken, new String[]{"status", status.getString()}).executeAsync(new TaskExecutor.Callback<Integer>() {
                @Override
                public void onComplete(final Integer responseCode) {
                    Logger.log("Status code:" + responseCode, Logger.LogType.INFO);
                    show.setWatchingStatus(status.getUserAnimeWatchingStatus());
                }

                @Override
                public void preExecute() {

                }
            });
    }

    public void updateShowScore(final Show show, final int score) {
        this.checkToken(unused -> updateShowScore(show, score));

        if (isSyncable())
            new MyAnimeListUpdateEntryTask(show.getID(), myToken, new String[]{"score", String.valueOf(score)}).executeAsync(new TaskExecutor.Callback<Integer>() {
                @Override
                public void onComplete(final Integer responseCode) {
                    Logger.log("Status code:" + responseCode, Logger.LogType.INFO);
                    show.setShowScore(score);
                }

                @Override
                public void preExecute() {

                }
            });
    }

    /**
     * Retrieves the token using the user's username & password
     *
     * @param username the user's username
     * @param password the user's password
     * @param onReturn consumer which acts as an return statement
     */
    private void retrieveToken(final String username, final String password, final Consumer<Boolean> onReturn) {
        new MyAnimeListTokenTask(username, password).executeAsync(new TaskExecutor.Callback<Optional<MyAnimeListToken>>() {
            @Override
            public void onComplete(Optional<MyAnimeListToken> result) {
                result.ifPresent(myAnimeListToken -> myToken = myAnimeListToken);
                onReturn.accept(result.isPresent());
            }

            @Override
            public void preExecute() {
                Logger.log("Retrieving token", Logger.LogType.INFO);
            }
        });
    }

    /**
     * Refreshes the token if it's not valid anymore
     */
    private void refreshToken(final Consumer<Void> toDo) {
        new MyAnimeListTokenRefreshTask(myToken).executeAsync(new TaskExecutor.Callback<Optional<MyAnimeListToken>>() {
            @Override
            public void onComplete(final Optional<MyAnimeListToken> result) {
                result.ifPresent(myAnimeListToken -> {
                    myToken = myAnimeListToken;
                    toDo.accept(null);
                });
            }

            @Override
            public void preExecute() {
                Logger.log("Refreshing token", Logger.LogType.INFO);
            }
        });
    }

    /**
     * Checks if the token is still valid and if not starts a new task to refresh it
     *
     * @param afterCheck consumer with a void accepted, used as an callback, to wait for the refresh to be completed
     */
    private void checkToken(final Consumer<Void> afterCheck) {
        if (checkSync()) refreshToken(afterCheck);
    }

    /**
     * Checks if the token is still valid
     *
     * @return is the current time bigger than the time the token expires?
     */
    private boolean checkSync() {
        return System.currentTimeMillis() >= myToken.getExpiration();
    }

    public boolean isSyncable() {
        return isSyncAccount() && isWasAccountAdded() && myToken != null;
    }

    public MyAnimeListToken getMyToken() {
        return myToken;
    }

    public boolean isSyncAccount() {
        return preferences.getBoolean("sync_mal", false);
    }

    public boolean isWasAccountAdded() {
        return wasAccountAdded;
    }

    public Jikan getJikan() {
        return new Jikan();
    }
}
