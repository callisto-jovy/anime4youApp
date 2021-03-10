/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 04.02.21, 08:31
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.BuildConfig;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.DrawerMainMenu;
import net.bplaced.abzzezz.animeapp.util.tasks.UpdateTask;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AnimeAppMain.getInstance().getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        AnimeAppMain.getInstance().createNotificationChannel(getApplication());
        AnimeAppMain.getInstance().checkAndroidPermissions(this);
        //Configure handlers
        AnimeAppMain.getInstance().configureHandlers(getApplication());

        new UpdateTask(getApplication()).executeAsync(); //Check version

        ((TextView) findViewById(R.id.version_text)).append("v." + BuildConfig.VERSION_NAME);   //Set version text

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            //TODO: Reimplement
            /*
            for (int i = 0; i < AnimeAppMain.getInstance().getShowSaver().getShowSize(); i++) {
                int finalI = i;
                AnimeAppMain.getInstance().getShowSaver().getShow(i).ifPresent(show -> show.getProvider().refreshShow(show, refreshedShow -> {
                    AnimeAppMain.getInstance().getShowSaver().refreshShow(refreshedShow, finalI);
                    Toast.makeText(getApplicationContext(), "Refreshed show:" + refreshedShow.getShowTitle(), Toast.LENGTH_SHORT).show();
                }));
            }

             */
            //Start menu
            startActivity(new Intent(this, DrawerMainMenu.class));
            finish();
        }, AnimeAppMain.getInstance().isDeveloperMode() ? 10 : 2500);
    }

}
