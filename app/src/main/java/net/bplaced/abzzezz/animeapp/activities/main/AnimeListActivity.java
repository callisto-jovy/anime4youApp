/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 23:42
 */

package net.bplaced.abzzezz.animeapp.activities.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.extra.CloudList;
import net.bplaced.abzzezz.animeapp.activities.extra.SplashScreen;
import net.bplaced.abzzezz.animeapp.input.AIDInput;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;

import java.io.File;
import java.util.List;

public class AnimeListActivity extends AppCompatActivity implements AIDInput.AIDInputDialogListener {

    private DataBaseSearch dataBaseSearch = new DataBaseSearch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_anime_list);
        super.onCreate(savedInstanceState);
        GridView gridView = findViewById(R.id.anime_grid);
        AnimeAdapter animeAdapter = new AnimeAdapter(SplashScreen.saver.getList(), getApplicationContext());
        gridView.setAdapter(animeAdapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, SelectedAnimeActivity.class);
            String[] pass = SplashScreen.saver.getAll(animeAdapter.getString().get(position));
            String[] dataBase = dataBaseSearch.getAll(Integer.valueOf(pass[3]));
            intent.putExtra("anime_name", pass[0]);
            intent.putExtra("anime_episodes", dataBase[1]);
            intent.putExtra("anime_cover", pass[2]);
            intent.putExtra("anime_aid", pass[3]);
            intent.putExtra("list_position", position);
            intent.putExtra("anime_language", dataBase[4]);
            startActivity(intent);
            finish();
        }, 100));

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(AnimeListActivity.this)
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setTitle("Remove?")
                    .setMessage("Will remove from list")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        SplashScreen.saver.getList().remove(position);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                    }).show();
            return true;
        });

        Toolbar toolbar = findViewById(R.id.animelist_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.add_aid_item) {
            AIDInput input = new AIDInput();
            input.show(getSupportFragmentManager(), "Enter AID");
        } else if (itemID == R.id.add_series_cloud) {
            Intent intent = new Intent(this, CloudList.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_list_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void applyTexts(String aid) {
        int aid_int = Integer.valueOf(aid);
        String[] all = dataBaseSearch.getAll(aid_int);
        SplashScreen.saver.add(all[0], all[1], all[2], all[3]);

        File animeDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), all[0]);
        if (!animeDirectory.exists()) animeDirectory.mkdir();


        SplashScreen.saver.save();
    }

    @Override
    protected void onDestroy() {
        SplashScreen.saver.save();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        SplashScreen.saver.save();
        super.onBackPressed();
    }

    class AnimeAdapter extends BaseAdapter {

        private Context context;
        private List<String> string;

        public AnimeAdapter(List<String> string, Context context) {
            this.string = string;
            this.context = context;
        }

        @Override
        public int getCount() {
            return string.size();
        }

        @Override
        public Object getItem(int position) {
            return string.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(ImageUtil.getImageBitmap(SplashScreen.saver.getAll(string.get(position))[2], ImageUtil.dimensions[0], ImageUtil.dimensions[1]));
            return imageView;
        }


        public List<String> getString() {
            return string;
        }
    }
}



