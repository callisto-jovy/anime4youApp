/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:37
 */

package net.bplaced.abzzezz.animeapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.file.ShowSaver;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.PermissionTask;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.io.File;

public class AnimeAppMain {

    public static final String NOTIFICATION_CHANNEL_ID = "Anime Channel";
    private static final AnimeAppMain INSTANCE = new AnimeAppMain();

    private final float version;
    private final boolean developerMode;
    private final String notificationChannelName;
    private int themeId;
    private boolean versionOutdated;

    //Private identifier
    private String androidID;

    private File imageStorage;
    //Show utilities
    private ShowSaver showSaver;

    public AnimeAppMain() {
        this.version = Float.parseFloat(BuildConfig.VERSION_NAME.replace(".", ""));
        this.developerMode = false;
        this.notificationChannelName = "AnimeChannel";

    }

    public static AnimeAppMain getInstance() {
        return INSTANCE;
    }

    /**
     * Configures the handlers and gets a random background
     */
    @SuppressLint("HardwareIds")
    public void configureHandlers(final Application application) {
        this.androidID = generateID();

        this.showSaver = new ShowSaver(application);

        this.imageStorage = new File(application.getDataDir(), "StoredImagesOffline");
        if (!imageStorage.exists()) Logger.log("Image file created: " + imageStorage.mkdir(), Logger.LogType.INFO);

        if (PreferenceManager.getDefaultSharedPreferences(application).getBoolean("dark_mode", true))
            this.themeId = R.style.DarkTheme;
        else
            this.themeId = R.style.LightTheme;
    }

    /**
     * Check if user has access to the app
     * @param context context to make toast on
     */
    public void checkPermission(final Context context) {
        new PermissionTask().executeAsync(new TaskExecutor.Callback<Boolean>() {
            @Override
            public void onComplete(final Boolean permit) {
                if (permit) {
                    Logger.log("Unregistered device", Logger.LogType.INFO);

                    final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    final ClipData clip = ClipData.newPlainText("ID", androidID);
                    clipboard.setPrimaryClip(clip);

                    System.exit(0);
                    Toast.makeText(context, "You are not registered. Please contact the developer and give him your clipboard id", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void preExecute() {
            }
        });
    }

    /**
     * Checks permissions and internet connection
     * @param activity Activity to make toast on & request permissions
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void checkAndroidPermissions(final Activity activity) {
        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.BLUETOOTH, Manifest.permission.SET_ALARM, Manifest.permission.WAKE_LOCK, Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.FOREGROUND_SERVICE};

        activity.requestPermissions(permissions, 101);
        if (StringHandler.isOffline(activity))
            Toast.makeText(activity, "You are not connected to the internet. If Images are not cached they will not show.", Toast.LENGTH_LONG).show();
    }

    /**
     * Creates notification channel
     * @param context context to register service to
     */
    public void createNotificationChannel(final Context context) {
        Logger.log("Creating new notification channel", Logger.LogType.INFO);
        final NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, notificationChannelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notification channel to display download notification");
        channel.setLightColor(Color.MAGENTA);
        final NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Generates a pseudo random id to the device's id
     * @return the generated pseudo id
     */
    private String generateID() {
        return "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits;
    }

    public String getAndroidID() {
        return androidID;
    }

    public File getImageStorage() {
        return imageStorage;
    }

    public int getThemeId() {
        return themeId;
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
