/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.05.20, 14:39
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.AnimeListActivity;
import net.bplaced.abzzezz.animeapp.util.BackgroundHolder;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

public class MainMenuActivity extends AppCompatActivity {

    boolean showLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("dark_mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ConstraintLayout constraintLayout = findViewById(R.id.main_meu_layout);
        constraintLayout.setBackgroundResource(BackgroundHolder.background);
        FloatingActionButton floatingActionButton = findViewById(R.id.anime_list_mext_button);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AnimeListActivity.class);
            startActivity(intent);
        });

        WebView changelog = findViewById(R.id.changelog_webview);
        changelog.loadUrl(URLHandler.changelogURL);

        FloatingActionButton changelogButton = findViewById(R.id.changelog_button);
        changelogButton.setOnClickListener(v -> {
            showLog = !showLog;
            changelog.setVisibility(showLog ? View.VISIBLE : View.INVISIBLE);
        });
    }
}
