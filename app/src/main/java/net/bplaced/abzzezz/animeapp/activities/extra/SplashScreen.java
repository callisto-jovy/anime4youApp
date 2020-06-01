/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.05.20, 14:39
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.BackgroundHolder;
import net.bplaced.abzzezz.animeapp.util.file.AnimeSaver;
import net.bplaced.abzzezz.animeapp.util.file.AutoUpdater;
import net.bplaced.abzzezz.animeapp.util.file.EpisodeDownloader;


public class SplashScreen extends AppCompatActivity {

    public static AnimeSaver saver;
    public static EpisodeDownloader episodeDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("dark_mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        configureHandlers();
        TextView versionText = findViewById(R.id.version_text);
        versionText.append("v." + AutoUpdater.version);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }


    private void configureHandlers() {
        BackgroundHolder.setup();
        BackgroundHolder.shuffle();
        saver = new AnimeSaver(getApplicationContext());
        saver.load();
        episodeDownloader = new EpisodeDownloader();
        AutoUpdater autoUpdater = new AutoUpdater();
        autoUpdater.update(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_list_toolbar, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        SplashScreen.saver.save();
        super.onDestroy();
    }
}
