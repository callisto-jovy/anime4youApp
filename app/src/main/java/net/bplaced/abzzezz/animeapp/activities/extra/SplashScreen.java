/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 07.11.20, 20:32
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

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
        AnimeAppMain.getInstance().checkPermissions(this);
        //Configure handlers
        AnimeAppMain.getInstance().configureHandlers(getApplication());

        /*
        Check version
         */

        new UpdateTask(getApplication()).executeAsync();

        //Set version text
        ((TextView) findViewById(R.id.version_text)).append("v." + BuildConfig.VERSION_NAME);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //Start menu
            startActivity(new Intent(this, DrawerMainMenu.class));
            finish();
        }, AnimeAppMain.getInstance().isDebugVersion() ? 10 : 2500);
    }

}
