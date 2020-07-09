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
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.util.animenotifications.AnimeNotificationService;
import net.bplaced.abzzezz.animeapp.util.file.AnimeNotifications;
import net.bplaced.abzzezz.animeapp.util.file.AnimeSaver;
import net.bplaced.abzzezz.animeapp.util.file.DownloadTracker;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

//TODO: Recode project
public class AnimeAppMain {

    public static final String NOTIFICATION_CHANNEL_ID = "Anime Channel";
    public String androidId;
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
        this.version = 46;
        this.debugVersion = true;
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
        this.androidId = Settings.Secure.getString(application.getContentResolver(), Settings.Secure.ANDROID_ID);
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

    public void checkRequest(final Context context) {
        new TaskExecutor().executeAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                URL url = new URL("http://abzzezz.bplaced.net/app/user.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.addRequestProperty("Referer", androidId);
                connection.connect();
                final InputStream inputStream = connection.getInputStream();
                final String response = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
                return response.equals("200");
            }
        }, new TaskExecutor.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) throws Exception {
                if (!result) {
                    Logger.log("Unregistered device", Logger.LogType.INFO);
                    new IonAlert(context).setTitleText("You are not registered").setContentText("You are not registered. Please contact the developer and give him your clipboard id").setConfirmText("Exit").setConfirmClickListener(new IonAlert.ClickListener() {
                        @Override
                        public void onClick(IonAlert ionAlert) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("ID", androidId);
                            clipboard.setPrimaryClip(clip);
                            System.exit(0);
                        }
                    }).show();
                }
            }

            @Override
            public void preExecute() {
            }
        });
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
        if (!StringHandler.isOnline(activity))
            Toast.makeText(activity, "You are not connected to the internet. If Images are not cached they will not show.", Toast.LENGTH_LONG).show();
    }

    public void createNotificationChannel(final Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Logger.log("Creating new notification channel", Logger.LogType.INFO);
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, notificationChannelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notification channel to display download notification");
            channel.setLightColor(Color.MAGENTA);
            NotificationManager notificationManager = application.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(application);
            managerCompat.cancelAll();
        }
    }

    public String getAndroidId() {
        return androidId;
    }

    public File getImageStorage() {
        return imageStorage;
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

    public boolean isDebugVersion() {
        return debugVersion;
    }

    public float getVersion() {
        return version;
    }
}
