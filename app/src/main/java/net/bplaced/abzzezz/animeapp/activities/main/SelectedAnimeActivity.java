/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.05.20, 19:54
 */

package net.bplaced.abzzezz.animeapp.activities.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.extra.PlayerActivity;
import net.bplaced.abzzezz.animeapp.activities.extra.SplashScreen;
import net.bplaced.abzzezz.animeapp.activities.input.InputDialog;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.ScriptUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.io.File;

public class SelectedAnimeActivity extends AppCompatActivity implements InputDialog.InputDialogListener {

    private String animeName, animeCover, language;
    private int position, aid, animeEpisodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("dark_mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_selected);
        /**
         * Get intent varaibles
         */
        this.animeName = getIntent().getStringExtra("anime_name");
        this.animeEpisodes = Integer.valueOf(getIntent().getStringExtra("anime_episodes"));
        this.animeCover = getIntent().getStringExtra("anime_cover");
        this.position = getIntent().getIntExtra("list_position", -1);
        this.aid = Integer.valueOf(getIntent().getStringExtra("anime_aid"));
        this.language = getIntent().getStringExtra("anime_language");

        /**
         * Set text
         */
        TextView selected_anime_name = findViewById(R.id.selected_anime_name);
        TextView selected_anime_episodes = findViewById(R.id.selected_anime_episodes);
        TextView selected_anime_aid = findViewById(R.id.selected_anime_aid);
        TextView selected_anime_size = findViewById(R.id.anime_directory_size);
        TextView selected_anime_language = findViewById(R.id.selected_anime_language);
        selected_anime_aid.append(String.valueOf(aid));
        selected_anime_name.setText(animeName);
        selected_anime_episodes.append(String.valueOf(animeEpisodes));
        selected_anime_language.append(language);
        /**
         * Toolbar and Image
         */
        Toolbar toolbar = findViewById(R.id.selected_anime_toolbar);
        ImageView cover = findViewById(R.id.anime_cover_image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(animeName);
        Picasso.with(getApplicationContext()).load(animeCover).resize(ImageUtil.dimensions[0], ImageUtil.dimensions[1]).into(cover);
        /**
         * GridView and Download Button
         */
        GridView episodeGrid = findViewById(R.id.anime_episodes_grid);
        File animeFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), animeName);
        String[] episodes = animeFile.list();
        selected_anime_size.append(FileUtil.calculateFileSize(animeFile));

        AnimeEpisodeAdapter animeEpisodeAdapter = new AnimeEpisodeAdapter(episodes == null || episodes.length == 0 ? new String[1] : episodes, getApplicationContext());
        episodeGrid.setAdapter(animeEpisodeAdapter);
        /**
         Configure grid
         */
        episodeGrid.setOnItemClickListener((parent, view, position, id) -> {
            File episodeFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + animeName, animeEpisodeAdapter.episodes[position]);
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("file_path", episodeFile.getPath());
            startActivity(intent);
            finish();
        });

        episodeGrid.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(SelectedAnimeActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete?").setMessage("Will delete File").setPositiveButton("Yes", (dialogInterface, i) -> {
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + animeName, animeEpisodeAdapter.episodes[position]);
                Logger.log("Deleted: " + f.delete(), Logger.LogType.INFO);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }).show();
            return true;
        });

        /**
         * Button
         */
        FloatingActionButton downloadAnime = findViewById(R.id.download_anime_button);
        int nextStart = (episodes != null && episodes.length != 0) ? StringUtil.extractNumberI(episodes[episodes.length - 1].replaceAll(".mp4", "")) + 1 : 1;
        downloadAnime.setOnClickListener(v -> downloadEpisode(nextStart, animeEpisodes, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_selected_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.delete_aid_item:
                SplashScreen.saver.getList().remove(position);
                break;
            case R.id.download_specific_episode:
                InputDialog input = new InputDialog("Episode to download");
                input.show(getSupportFragmentManager(), "Download specific");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        SplashScreen.saver.save();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AnimeListActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public void downloadEpisode(int start, int countMax, int currentCount) {
        int[] count = {currentCount, start};
        /**
         * Check if count is bigger than the max episodes to download
         */
        if (count[0] >= countMax) {
            Logger.log("current episode exceeds max / start exceeds max", Logger.LogType.ERROR);
            return;
        }
        /**
         * Load captcha URL
         */
        WebView webView = new WebView(getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(URLHandler.captchaURL);
        /**
         * Clear all previous data and Cookies, so no code 400 appears: Cookie too large (yummy ;) )
         */
        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(ScriptUtil.getRequest(aid, count[1]), returnCaptcha -> {
                    if (returnCaptcha.contains("vivo")) {
                        Logger.log("Found link for vivo:" + returnCaptcha, Logger.LogType.INFO);
                        makeText("Found vivo link");
                        view.loadUrl(URLUtil.toUrl(StringUtil.removeBadCharacters(returnCaptcha, "\\\\", "\""), "https"));
                        view.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                view.evaluateJavascript(ScriptUtil.vivoExploit, value -> {
                                    if (value.contains("node")) {
                                        SplashScreen.episodeDownloader.download(value.replaceAll("\"", ""), animeName + "::" + count[1], SelectedAnimeActivity.this, "mp4");
                                        view.destroy();
                                        if (count[0] < countMax) {
                                            count[0]++;
                                            count[1]++;
                                            downloadEpisode(count[1], countMax, count[0]);
                                        }
                                    } else makeText("Error getting direct vivo link: " + value);
                                });
                                super.onPageFinished(view, url);
                            }
                        });
                    } else {
                        makeText("Error getting vivo link. Anime4you might be down / Anime done downloading");
                    }
                });
                super.onPageFinished(view, url);
            }
        });
    }

    public void makeText(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void applyTexts(String start) {
        try {
            downloadEpisode(Integer.valueOf(start), 1, 0);
        } catch (NumberFormatException e) {
            makeText("Not possible to convert number, please check your input");
        }
    }

    class AnimeEpisodeAdapter extends BaseAdapter {

        private final String[] episodes;
        private final Context context;

        public AnimeEpisodeAdapter(String[] episodes, Context context) {
            this.episodes = episodes;
            this.context = context;
        }

        @Override
        public int getCount() {
            return episodes.length;
        }

        @Override
        public Object getItem(int position) {
            return episodes[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(context);
            textView.setTextSize(15);
            textView.setTextColor(ColorStateList.valueOf(Color.WHITE));
            textView.setText(episodes[position]);
            return textView;
        }
    }

}
