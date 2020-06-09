/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.05.20, 19:46
 */

package net.bplaced.abzzezz.animeapp.activities.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.preference.PreferenceManager;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.extra.CloudList;
import net.bplaced.abzzezz.animeapp.activities.extra.SettingsActivity;
import net.bplaced.abzzezz.animeapp.activities.extra.SplashScreen;
import net.bplaced.abzzezz.animeapp.activities.input.InputDialog;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import org.apache.commons.net.ftp.FTPClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class AnimeListActivity extends AppCompatActivity implements InputDialog.InputDialogListener {


    private AnimeAdapter animeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("dark_mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        setContentView(R.layout.activity_anime_list);
        super.onCreate(savedInstanceState);
        GridView gridView = findViewById(R.id.anime_grid);

        this.animeAdapter = new AnimeAdapter(SplashScreen.saver.getList(), getApplicationContext());
        gridView.setAdapter(animeAdapter);
        /**
         * Set onclick listener, if clicked pass information through to selected anime.
         */
        gridView.setOnItemClickListener((parent, view, position, id) -> new Handler().postDelayed(() -> {
            try {
                Intent intent = new Intent(this, SelectedAnimeActivity.class);
                String[] pass = SplashScreen.saver.getAll(position);
                String[] dataBase = new DataBaseSearch().execute(pass[3]).get();
                intent.putExtra("anime_name", pass[0]);
                intent.putExtra("anime_episodes", dataBase[1]);
                intent.putExtra("anime_cover", pass[2]);
                intent.putExtra("anime_aid", pass[3]);
                intent.putExtra("anime_language", dataBase[4]);
                startActivity(intent);
                finish();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, 100));
        /**
         * Set on long hold listener, then block the old one
         */
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(AnimeListActivity.this)
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setTitle("Remove?")
                    .setMessage("Will remove from list")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        animeAdapter.removeItem(position);
                    }).show();
            return true;
        });
        /**
         * Add toolbar
         */
        Toolbar toolbar = findViewById(R.id.animelist_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.add_aid_item:
                InputDialog input = new InputDialog("Aid ");
                input.show(getSupportFragmentManager(), "Enter AID");
                break;
            case R.id.add_series_cloud:
                Intent intent = new Intent(this, CloudList.class);
                startActivity(intent);
                break;
            case R.id.menu_options:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);
                break;
            case R.id.cloud_save:
                new FTPGetter().execute();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_list_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * add anime to keyset
     *
     * @param aid
     */
    @Override
    public void applyTexts(String aid) {
        animeAdapter.addItem(aid);
    }

    /**
     * Array Picture adapter
     */
    private class AnimeAdapter extends BaseAdapter {

        private final Context context;
        private final List<String> string;

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
            try {
                Picasso.with(context).load(SplashScreen.saver.getAll(position)[2]).resize(ImageUtil.dimensions[0], ImageUtil.dimensions[1]).into(imageView);
                imageView.setAdjustViewBounds(true);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(position);
            }
            return imageView;
        }

        public void removeItem(int index) {
            string.remove(index);
            SplashScreen.saver.remove(index);
            notifyDataSetChanged();
        }

        public void addItem(String item) {
            try {
                String[] all = new DataBaseSearch().execute(item).get();
                string.add(item);
                SplashScreen.saver.add(all);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            notifyDataSetChanged();
        }


        public List<String> getString() {
            return string;
        }
    }

    /**
     * Debug
     */

    class FTPGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                FTPClient ftpClient = new FTPClient();
                ftpClient.connect("abzzezz.bplaced.net");
                ftpClient.login("abzzezz_client", "AzA33EUSgU7KZvbj");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (String line : SplashScreen.saver.getList()) {
                    baos.write((line + "\n").getBytes());
                }
                byte[] bytes = baos.toByteArray();
                Logger.log("File uploaded: " + ftpClient.storeFile("/www/lists/" + new Random().nextInt() + ".txt", new ByteArrayInputStream(bytes)), Logger.LogType.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}



