/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 23:42
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.file.AnimeSaver;
import net.bplaced.abzzezz.animeapp.util.file.AutoUpdater;
import net.bplaced.abzzezz.animeapp.util.BackgroundHolder;
import net.bplaced.abzzezz.animeapp.util.file.EpisodeDownloader;


public class SplashScreen extends AppCompatActivity {

    public static AnimeSaver saver;
    public static EpisodeDownloader episodeDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        configureHandlers();

        TextView versionText = findViewById(R.id.version_text);
        versionText.append("" + AutoUpdater.version);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }

    private void configureHandlers() {
        BackgroundHolder backgroundHolder = new BackgroundHolder();
        backgroundHolder.shuffle();
        this.saver = new AnimeSaver(getApplicationContext());
        saver.load();

        this.episodeDownloader = new EpisodeDownloader();
        AutoUpdater autoUpdater = new AutoUpdater();
        autoUpdater.update(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_list_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        SplashScreen.saver.save();
        super.onDestroy();
    }
}
