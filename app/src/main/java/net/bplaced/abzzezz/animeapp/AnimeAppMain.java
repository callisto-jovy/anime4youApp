/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 17:59
 */

package net.bplaced.abzzezz.animeapp;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.myanimelist.MyAnimeList;
import net.bplaced.abzzezz.animeapp.util.show.ShowSaver;
import net.bplaced.abzzezz.animeapp.util.tasks.UpdateTask;

import java.io.File;

public class AnimeAppMain {
    /**
     * Notification Channel Identification for android's notification service
     */
    public static final String NOTIFICATION_CHANNEL_ID = "Anime Channel";
    /**
     * App Main instance
     */
    public static final AnimeAppMain INSTANCE = new AnimeAppMain();
    /**
     * The app's version, determined by the Build-Config
     */
    private final float version;
    /**
     * Developer mode toggle
     * (Set to true whenever the app is under development, is set to false in final builds)
     */
    private final boolean developerMode = true;

    private boolean versionOutdated;

    private File internalImageStorage;

    private ShowSaver showSaver;

    private MyAnimeList myAnimeList; //MyAnimeList class instance

    public AnimeAppMain() {
        this.version = Float.parseFloat(BuildConfig.VERSION_NAME.replace(".", ""));
    }

    /**
     * Initialize service handlers, check the version & set local storage
     */
    public void configureHandlers(final Application application) {
        Logger.log("Starting app's internal services", Logger.LogType.INFO);
        this.showSaver = new ShowSaver(application);
        this.myAnimeList = new MyAnimeList(application);

        this.internalImageStorage = new File(application.getDataDir(), "StoredImagesOffline");
        if (!internalImageStorage.exists())
            Logger.log("Image file created: " + internalImageStorage.mkdir(), Logger.LogType.INFO);

        new UpdateTask(application).executeAsync(); //Check version
    }

    /**
     * Checks permissions and internet connection
     *
     * @param activity Activity to make toast on & request permissions
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void checkAndroidPermissions(final Activity activity) {
        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.SET_ALARM, Manifest.permission.WAKE_LOCK, Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.FOREGROUND_SERVICE};

        activity.requestPermissions(permissions, 101);

        if (URLUtil.isOffline(activity))
            Toast.makeText(activity, "You are not connected to the internet. If Images are not cached they will not show.", Toast.LENGTH_LONG).show();
    }

    /**
     * Creates notification channel
     *
     * @param context context to register service to
     */
    public void createNotificationChannel(final Context context) {
        Logger.log("Creating new notification channel", Logger.LogType.INFO);

        final NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, context.getString(R.string.app_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(context.getString(R.string.app_notification_channel_description));
        channel.setLightColor(Color.MAGENTA);
        final NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        if (isDeveloperMode())
            notificationManager.cancelAll();
    }

    public MyAnimeList getMyAnimeList() {
        return myAnimeList;
    }

    public File getInternalImageStorage() {
        return internalImageStorage;
    }

    public ShowSaver getShowSaver() {
        return showSaver;
    }

    public boolean isDeveloperMode() {
        return developerMode;
    }

    public float getVersion() {
        return version;
    }

    public boolean isVersionOutdated() {
        return versionOutdated;
    }

    public void setVersionOutdated(boolean versionOutdated) {
        this.versionOutdated = versionOutdated;
    }
}
