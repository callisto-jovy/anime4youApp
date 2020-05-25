/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 23:42
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.AnimeListActivity;
import net.bplaced.abzzezz.animeapp.util.BackgroundHolder;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ConstraintLayout constraintLayout = findViewById(R.id.main_meu_layout);
        constraintLayout.setBackgroundResource(BackgroundHolder.background);
        FloatingActionButton floatingActionButton = findViewById(R.id.goto_anime_list);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(BackgroundHolder.color));
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AnimeListActivity.class);
            startActivity(intent);
        });
    }
}
