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
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.animenotifications.AnimeNotificationService;
import net.bplaced.abzzezz.animeapp.util.file.AnimeNotifications;
import net.bplaced.abzzezz.animeapp.util.file.AnimeSaver;
import net.bplaced.abzzezz.animeapp.util.file.DownloadTracker;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.io.File;

public class AnimeAppMain {

    public static final String NOTIFICATION_CHANNEL_ID = "Anime Channel";
    /**
     * Version and variables
     */
    private static final AnimeAppMain INSTANCE = new AnimeAppMain();
    private final float version;
    private final boolean debugVersion;
    private final String notificationChannelName;
    private boolean darkMode;
    private int themeID;
    private File imageStorage;

    /**
     * Handlers
     */
    private AnimeSaver animeSaver;
    private DownloadTracker downloadTracker;
    private AnimeNotifications animeNotifications;

    public AnimeAppMain() {
        this.version = 44;
        this.debugVersion = false;
        this.notificationChannelName = "AnimeChannel";
    }

    public static AnimeAppMain getInstance() {
        return INSTANCE;
    }

    /**
     * Configures the handlers and gets a random background
     */
    public void configureHandlers(final Application application) {
        this.animeSaver = new AnimeSaver(application);
        this.downloadTracker = new DownloadTracker(application);
        this.animeNotifications = new AnimeNotifications(application);
        this.imageStorage = new File(application.getDataDir(), "StoredImagesOffline");
        if (!imageStorage.exists()) Logger.log("Image file created: " + imageStorage.mkdir(), Logger.LogType.INFO);
        this.darkMode = PreferenceManager.getDefaultSharedPreferences(application).getBoolean("dark_mode", true);
        if (darkMode)
            this.themeID = R.style.DarkTheme;
        else
            this.themeID = R.style.LightTheme;

        Intent animeAlarm = new Intent(application, AnimeNotificationService.class);
        animeAlarm.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        application.startService(animeAlarm);

    }

    /**
     * Checks permissions and internet connection
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void checkPermissions(final Activity activity) {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.BLUETOOTH, Manifest.permission.SET_ALARM, Manifest.permission.WAKE_LOCK, Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.FOREGROUND_SERVICE};

        activity.requestPermissions(permissions, 101);
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
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(application);
            managerCompat.cancelAll();
        }
    }

    public File getImageStorage() {
        return imageStorage;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public int getThemeID() {
        return themeID;
    }

    public DownloadTracker getDownloadTracker() {
        return downloadTracker;
    }

    public AnimeNotifications getAnimeNotifications() {
        return animeNotifications;
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
