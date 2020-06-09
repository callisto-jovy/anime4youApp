/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.05.20, 14:39
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.BackgroundHolder;
import net.bplaced.abzzezz.animeapp.util.file.AnimeSaver;
import net.bplaced.abzzezz.animeapp.util.file.AutoUpdater;
import net.bplaced.abzzezz.animeapp.util.file.EpisodeDownloader;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;


public class SplashScreen extends AppCompatActivity {

    /**
     * Basically the main-class
     */

    public static AnimeSaver saver;
    public static EpisodeDownloader episodeDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Set theme
         */
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        /**
         * Check permissions. If not given prompt to do so
         */
        checkPermissions();
        //Configure handlers
        configureHandlers();
        //Set version text
        TextView versionText = findViewById(R.id.version_text);
        versionText.append("v." + AutoUpdater.version);
        /**
         * Start new intent
         */
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }

    /**
     * Checks permissions and internet connection
     */
    private void checkPermissions() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.BLUETOOTH}, 101);
        if (!URLHandler.isOnline(this)) Toast.makeText(this, "You are not connected to the internet. If Images are not cached they will not show.", Toast.LENGTH_LONG).show();
    }

    /**
     * Configures the handlers and gets a random background
     */
    private void configureHandlers() {
        BackgroundHolder.setup();
        BackgroundHolder.shuffle();
        this.saver = new AnimeSaver(getApplicationContext());
        this.episodeDownloader = new EpisodeDownloader();
        AutoUpdater autoUpdater = new AutoUpdater();
        autoUpdater.execute(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_list_toolbar, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
