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
        /**
         * Set theme
         */
        setTheme(AnimeAppMain.getInstance().getThemeID());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        /**
         * Check permissions. If not given prompt to do so
         */

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
                    File outDic = new File(Environment.DIRECTORY_DOWNLOADS, "Anime4you-Update");
                    String fileName = "AutoUpdate.apk";
                    Downloader.download(StringHandler.UPDATE_APK, outDic, fileName, getParent());
                    Toast.makeText(SplashScreen.this, "New update available. Please install the new version.", Toast.LENGTH_LONG).show();
                }
            }
        });


        //Set version text
        TextView versionText = findViewById(R.id.version_text);
        versionText.append("v." + AnimeAppMain.getInstance().getVersion());
        /**
         * Start new intent
         */
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(this, DrawerMainMenu.class);
            startActivity(intent);
            finish();
        }, AnimeAppMain.getInstance().isDebugVersion() ? 10 : 2500);
    }

}
