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

    private boolean wasAccountAdded;
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    private MyAnimeListToken myToken;
    private String username;

    @SuppressLint("CommitPrefEdits")
    public MyAnimeList(final Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = preferences.edit();
        this.wasAccountAdded = preferences.getBoolean("account_added", false);
        this.myToken = Optional.of(preferences.getString("token", "")).map(s -> {
            try {
                return new MyAnimeListToken(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }).orElse(null);
    }

    public void setupSync(final String username, final String password, final Consumer<Boolean> onReturn) {
        Logger.log("Setting up sync", Logger.LogType.INFO);
        //If the account was added directly sync

        if (isWasAccountAdded() && myToken != null) {
            startSync(onReturn);
            return;
        }

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


    public void startSync(final Consumer<Boolean> onReturn) {
        if (!isSyncAccount()) return;

        if (!isWasAccountAdded() || myToken == null) {
            onReturn.accept(false);
            return;
        }

        if (checkSync()) {
            refreshToken(unused -> startSync(onReturn));
            return;
        }

        sync(onReturn);
    }

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

    public void updateShowEpisodes(final Show show) {
        if (checkSync()) {
            refreshToken(unused -> updateShowEpisodes(show));
            return;
        }

        if (isSyncable())
            new MyAnimeListUpdateEntryTask(show.getID(), myToken, new String[]{"num_episodes_watched", String.valueOf(show.getEpisodesWatched())}).executeAsync(new TaskExecutor.Callback<Integer>() {
                @Override
                public void onComplete(Integer result) {
                    Logger.log("Status code:" + result, Logger.LogType.INFO);
                }

                @Override
                public void preExecute() {

                }
            });
    }

    public void updateShowState(final Show show, final String status) {
        if (checkSync()) {
            refreshToken(unused -> updateShowEpisodes(show));
            return;
        }

        if (isSyncable())
            new MyAnimeListUpdateEntryTask(show.getID(), myToken, new String[]{"status", status}).executeAsync(new TaskExecutor.Callback<Integer>() {
                @Override
                public void onComplete(Integer result) {
                    Logger.log("Status code:" + result, Logger.LogType.INFO);
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
