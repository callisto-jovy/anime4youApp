/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 20:07
 */

package net.bplaced.abzzezz.animeapp.activities.main;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.*;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.array.ArrayUtil;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.ScriptUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectedAnimeActivity extends AppCompatActivity {

    private String animeName, animeCover, language;
    private int aid, animeEpisodes;
    public AnimeEpisodeAdapter animeEpisodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("dark_mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_selected_layout);
        /**
         * Get intent varaibles
         */
        this.animeName = getIntent().getStringExtra("anime_name");
        this.animeEpisodes = Integer.valueOf(getIntent().getStringExtra("anime_episodes"));
        this.animeCover = getIntent().getStringExtra("anime_cover");
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
        /**
         * Get anime file
         */
        File animeFile = new File(getFilesDir(), animeName);
        /**
         * If it does not exist then create new one
         */
        if (!animeFile.exists()) animeFile.mkdir();
        /**
         * Convert file array to list
         */
        List<String> episodes = Arrays.asList(animeFile.list());
        selected_anime_size.append(FileUtil.calculateFileSize(animeFile));
        /**
         * Set Adapter
         */
        this.animeEpisodeAdapter = new AnimeEpisodeAdapter(episodes, getApplicationContext());
        episodeGrid.setAdapter(animeEpisodeAdapter);
        /**
         Configure grid
         */
        episodeGrid.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getEpisodeFile(position)));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        });

        episodeGrid.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(SelectedAnimeActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete?").setMessage("Will delete File").setPositiveButton("Yes", (dialogInterface, i) -> {
                animeEpisodeAdapter.deleteItem(position);
            }).show();
            return true;
        });

        /**
         * Button
         */
        FloatingActionButton downloadAnime = findViewById(R.id.download_anime_button);
        String episodeString = (episodes != null && episodes.size() != 0) ? episodes.get(episodes.size() - 1) : "1";
        int nextStart = (episodes != null && episodes.size() != 0) ? StringUtil.extractNumberI(episodeString.substring(episodeString.indexOf("::") + 2, episodeString.indexOf(".mp4"))) + 1 : 1;
        downloadAnime.setOnClickListener(v -> downloadEpisode(nextStart, animeEpisodes, 0));
    }

    /**
     * Toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_selected_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Items selected
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        EditText editText = new EditText(this);
        switch (itemID) {
            case R.id.download_specific_episode:
                new AlertDialog.Builder(this).setTitle("Download specific").setMessage("Enter episode to download").setPositiveButton("Enter", (dialogInterface, i) -> {
                    downloadEpisode(Integer.valueOf(editText.getText().toString()), 1, 0);
                }).setView(editText).show();
                break;
            case R.id.download_bound:
                String[] episodes = new File(getFilesDir(), animeName).list();
                String episodeString = (episodes != null && episodes.length != 0) ? episodes[episodes.length- 1] : "1";
                int nextStart = (episodes != null && episodes.length != 0) ? StringUtil.extractNumberI(episodeString.substring(episodeString.indexOf("::") + 2, episodeString.indexOf(".mp4"))) + 1 : 1;
                new AlertDialog.Builder(this).setTitle("Download bound").setMessage("Enter download bound").setPositiveButton("Enter", (dialogInterface, i) -> {
                    downloadEpisode(nextStart, Integer.parseInt(editText.getText().toString()), 0);
                }).setView(editText).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On Back pressed
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DrawerMainMenu.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    /**
     * Download method
     *
     * @param start
     * @param countMax
     * @param currentCount
     */
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
        final WebView webView = new WebView(getApplicationContext());
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
                        makeText("Found VIVO link");
                        view.loadUrl(URLUtil.toUrl(StringUtil.removeBadCharacters(returnCaptcha, "\\\\", "\""), "https"));
                        view.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                view.evaluateJavascript(ScriptUtil.vivoExploit, value -> {
                                    if (value.contains("node")) {
                                        download(value.replaceAll("\"", ""), animeName + "::" + count[1]);
                                        view.destroy();
                                        new Handler().postDelayed(() -> {
                                            if (count[0] < countMax) {
                                                count[0]++;
                                                count[1]++;
                                                downloadEpisode(count[1], countMax, count[0]);
                                            }
                                            /**
                                             * Wait
                                             */
                                        }, Long.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("download_delay", "0")) * 1000);
                                    } else makeText("Error getting direct vivo link: " + value);
                                });
                                super.onPageFinished(view, url);
                            }
                        });
                    } else makeText("Error getting vivo link. Anime4you might be down / Anime done downloading");
                });
                super.onPageFinished(view, url);
            }
        });
    }

    /**
     * Make text
     *
     * @param text
     */
    public void makeText(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    /**
     * Get episode file
     *
     * @param index
     * @return
     */
    public File getEpisodeFile(int index) {
        File animeFile = new File(getFilesDir(), animeName);
        return new File(animeFile, animeFile.list()[index]);
    }

    private BroadcastReceiver broadcastReceiver;

    /**
     * Download
     *
     * @param url
     * @param fileName
     */
    public void download(String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        File file = new File(Environment.DIRECTORY_DOWNLOADS, "Move");
        request.setDescription("Downloading File: " + fileName);
        request.setTitle(fileName);
        request.setDestinationInExternalPublicDir(file.getAbsolutePath(), fileName + ".mp4");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
                    if (cursor.moveToFirst()) {
                        /**
                         * Move file from download to internal storage
                         */
                        File src = new File(Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath());
                        File newOut = new File(getFilesDir(), fileName.substring(0, fileName.indexOf("::")));
                        if (!newOut.exists()) newOut.mkdir();
                        FileUtil.copyFile(src, new File(newOut, src.getName()), true);
                    }

                    cursor.close();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    class AnimeEpisodeAdapter extends BaseAdapter {

        private final List<String> episodes;
        private final Context context;

        public AnimeEpisodeAdapter(List<String> episodes, Context context) {
            this.episodes = new ArrayList<>(episodes);
            this.context = context;
        }

        @Override
        public int getCount() {
            return episodes.size();
        }

        @Override
        public Object getItem(int position) {
            return episodes.get(position);
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
            textView.setText(episodes.get(position));
            return textView;
        }

        public void deleteItem(int index) {
            Logger.log("Deleted: " + getEpisodeFile(index).delete(), Logger.LogType.INFO);
            episodes.remove(index);
            notifyDataSetChanged();
        }
    }

}
