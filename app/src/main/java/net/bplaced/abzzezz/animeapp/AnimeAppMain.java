/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:37
 */

package net.bplaced.abzzezz.animeapp;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.file.AnimeSaver;
import net.bplaced.abzzezz.animeapp.util.file.DownloadTracker;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

public class AnimeAppMain {

    /**
     * Version and variables
     */
    private static final AnimeAppMain inst = new AnimeAppMain();

    private final float version;
    private final boolean debugVersion;
    private final String notificationChannelName;
    private boolean darkMode;
    public static final String NOTIFICATION_CHANNEL_ID = "Anime Channel";
    private int themeID;

    /**
     * Handlers
     */
    private AnimeSaver animeSaver;
    private DownloadTracker downloadTracker;

    public AnimeAppMain() {
        this.version = 40;
        this.debugVersion = false;
        this.notificationChannelName = "AnimeChannel";
    }

    /**
     * Configures the handlers and gets a random background
     */
    public void configureHandlers(final Application application) {
        this.animeSaver = new AnimeSaver(application);
        this.downloadTracker = new DownloadTracker(application);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
        this.darkMode = sharedPreferences.getBoolean("dark_mode", false);
        if (darkMode)
            themeID = R.style.DarkTheme;
        else
            themeID = R.style.LightTheme;

    }

    /**
     * Checks permissions and internet connection
     */
    public void checkPermissions(final Activity activity) {
        activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.BLUETOOTH}, 101);
        if (!URLHandler.isOnline(activity))
            Toast.makeText(activity, "You are not connected to the internet. If Images are not cached they will not show.", Toast.LENGTH_LONG).show();
    }

    public void createNotificationChannel(final Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Logger.log("Creating new notification channel", Logger.LogType.INFO);
            String description = "Notification channel to display download notification";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, notificationChannelName, importance);
            channel.setDescription(description);
            channel.setLightColor(Color.MAGENTA);
            NotificationManager notificationManager = application.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public int getThemeID() {
        return themeID;
    }

    public static AnimeAppMain getInstance() {
        return inst;
    }

    public DownloadTracker getDownloadTracker() {
        return downloadTracker;
    }

    public AnimeSaver getAnimeSaver() {
        return animeSaver;
    }

    public String getNotificationChannelName() {
        return notificationChannelName;
    }

    public boolean isDebugVersion() {
        return debugVersion;
    }

    public float getVersion() {
        return version;
    }
}
