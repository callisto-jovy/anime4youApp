/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 22:51
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


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AnimeAppMain.INSTANCE.createNotificationChannel(getApplication());
        AnimeAppMain.INSTANCE.checkAndroidPermissions(this);
        AnimeAppMain.INSTANCE.configureHandlers(getApplication());

        ((TextView) findViewById(R.id.version_text)).append("v." + BuildConfig.VERSION_NAME);   //Set version text

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //Start menu
            startActivity(new Intent(this, DrawerMainMenu.class));
            finish();
        }, AnimeAppMain.INSTANCE.isDeveloperMode() ? 10 : 2500);
    }

}
