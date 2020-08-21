/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:37
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ga.abzzezz.util.data.URLUtil;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.BuildConfig;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.DrawerMainMenu;
import net.bplaced.abzzezz.animeapp.util.file.Downloader;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.io.File;
import java.net.URL;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AnimeAppMain.getInstance().getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        AnimeAppMain.getInstance().createNotificationChannel(getApplication());
        AnimeAppMain.getInstance().checkPermissions(this);
        //Configure handlers
        AnimeAppMain.getInstance().configureHandlers(getApplication());

        /*
        Check version
         */
        new TaskExecutor().executeAsync(() -> AnimeAppMain.getInstance().getVersion() < Float.parseFloat(URLUtil.getURLContentAsString(new URL(StringHandler.APP_VERSION_TXT))), new TaskExecutor.Callback<Boolean>() {
            @Override
            public void preExecute() {
            }

            @Override
            public void onComplete(Boolean result) {
                if (result) {
                    AnimeAppMain.getInstance().setVersionOutdated(true);
                    Downloader.download(StringHandler.UPDATE_APK, new File(Environment.DIRECTORY_DOWNLOADS, "Anime4you-Update"), "AutoUpdate.apk", getParent());
                    Toast.makeText(SplashScreen.this, "New update available. Please install the new version.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Set version text
        ((TextView) findViewById(R.id.version_text)).append("v." + BuildConfig.VERSION_NAME);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //Start menu
            startActivity(new Intent(this, DrawerMainMenu.class));
            finish();
        }, AnimeAppMain.getInstance().isDebugVersion() ? 10 : 2500);
    }

}
