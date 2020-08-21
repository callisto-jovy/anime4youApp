/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 10.07.20, 15:22
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.extra.PlayerActivity;
import net.bplaced.abzzezz.animeapp.activities.extra.StreamPlayer;
import net.bplaced.abzzezz.animeapp.activities.main.DrawerMainMenu;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.InputDialogBuilder;
import net.bplaced.abzzezz.animeapp.util.InputDialogBuilder.InputDialogListener;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.scripter.ScriptUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.VideoFindTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public class SelectedActivity extends AppCompatActivity {

    public EpisodeAdapter episodeAdapter;
    private String title;
    private int id, episodes;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AnimeAppMain.getInstance().getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_show_layout);

        /*
         * Get intent varaibles
         */
        try {
            final JSONObject inf = new JSONObject(getIntent().getStringExtra("details"));
            this.title = inf.getString(StringHandler.SHOW_TITLE);
            this.episodes = inf.getInt(StringHandler.SHOW_EPISODES_COUNT);
            this.id = inf.getInt(StringHandler.SHOW_ID);
            this.file = new File(getFilesDir(), title);
            final String coverUrl = inf.getString(StringHandler.SHOW_IMAGE_URL);

            //Set text etc.
            ((TextView) findViewById(R.id.selected_anime_name)).setText(title);
            ((TextView) findViewById(R.id.selected_anime_episodes)).append(String.valueOf(episodes));
            ((TextView) findViewById(R.id.selected_anime_aid)).append(String.valueOf(id));
            ((TextView) findViewById(R.id.selected_anime_language)).append(inf.getString("language"));
            ((TextView) findViewById(R.id.selected_anime_year)).append(inf.getString("year"));
            ((TextView) findViewById(R.id.anime_directory_size)).append(FileUtil.calculateFileSize(file));

            final ImageView cover = findViewById(R.id.anime_cover_image);
            final Toolbar toolbar = findViewById(R.id.selected_anime_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(title);
            /*
             * If offline mode is enabled use image offline loader
             */
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("offline_mode", false))
                OfflineImageLoader.loadImage(coverUrl, String.valueOf(id), cover, this);
            else
                Picasso.with(getApplicationContext()).load(coverUrl).resize(ImageUtil.DIMENSIONS[0], ImageUtil.DIMENSIONS[1]).into(cover);


            final ListView listView = findViewById(R.id.anime_episodes_grid);

            /*
             * Set Adapter
             */
            this.episodeAdapter = new EpisodeAdapter(episodes, getApplicationContext());

            listView.setAdapter(episodeAdapter);
            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                final boolean isDownloaded = isEpisodeDownloaded(i);
                new IonAlert(SelectedActivity.this, IonAlert.NORMAL_TYPE)
                        .setConfirmText("Stream")
                        .setConfirmClickListener(ionAlert -> streamEpisode(i))
                        .setCancelText(isDownloaded ? "Play downloaded" : "Cancel")
                        .setCancelClickListener(ionAlert -> {
                            if (isDownloaded)
                                playEpisodeFromSave(i);
                            else
                                ionAlert.dismissWithAnimation();
                        }).show();
            });

            findViewById(R.id.download_anime_button).setOnClickListener(v -> downloadEpisode(getLatestEpisode(), episodes, 0));
        } catch (JSONException e) {
            Logger.log("Error parsing JSON", Logger.LogType.INFO);
            e.printStackTrace();
        }
    }


    /**
     * Toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.anime_selected_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int getLatestEpisode() {
        if (file.list() != null) {
            final OptionalInt highest = Arrays.stream(file.list()).map(s -> StringUtil.extractNumberI(s.substring(0, s.lastIndexOf(".")))).mapToInt(integer -> integer).max();
            if (highest.isPresent()) return highest.getAsInt() + 1;
        }
        return 0;
    }

    public void refreshAdapter() {
        episodeAdapter.notifyDataSetChanged();
    }

    private boolean isEpisodeDownloaded(final int index) {
        if (file.list() != null) {
            return Arrays.stream(file.list()).anyMatch(s -> s.substring(0, s.lastIndexOf(".")).equals(String.valueOf(index)));
        }
        return false;
    }

    /**
     * Items selected
     *
     * @param item selected item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.download_bound:
                final InputDialogBuilder dialogBuilder = new InputDialogBuilder(new InputDialogListener() {
                    @Override
                    public void onDialogInput(final String text) {
                        downloadEpisode(getLatestEpisode(), Integer.parseInt(text), 0);
                    }

                    @Override
                    public void onDialogDenied() {
                    }
                });
                dialogBuilder.showInput("Download bound", "Enter bound", this);
                break;
            case R.id.toogle_notifications_show:
                //Add to notification manager
                AnimeAppMain.getInstance().getAnimeNotifications().add(title.concat(StringUtil.splitter) + id, String.valueOf(episodes));
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
        startActivity(new Intent(this, DrawerMainMenu.class));
        finish();
        super.onBackPressed();
    }

    /**
     * Download method
     *
     * @param start        start
     * @param countMax     max download
     * @param currentCount current episode
     */
    public void downloadEpisode(final int start, final int countMax, final int currentCount) {
        Logger.log("Next episode: " + start, Logger.LogType.INFO);
        int[] count = {currentCount, start};
        /**
         * Check if count is bigger than the max episodes to download
         */
        if (count[0] >= countMax) {
            Logger.log("current episode exceeds max / start exceeds max", Logger.LogType.ERROR);
            return;
        }

        final WebView webView = new WebView(getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(StringHandler.CAPTCHA_ANIME_4_YOU_ONE);

        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();

        new VideoFindTask(id, count[1]).executeAsync(new TaskExecutor.Callback<String>() {
            @Override
            public void onComplete(String result) {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        System.out.println(result);
                        view.evaluateJavascript(result, returnCaptcha -> {
                            if (returnCaptcha.contains("vivo")) {
                                Logger.log("Found link for vivo:" + returnCaptcha, Logger.LogType.INFO);
                                makeText("Found VIVO link");
                                view.loadUrl(URLUtil.toUrl(StringUtil.removeBadCharacters(returnCaptcha, "\\\\", "\""), "https"));
                                view.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        view.evaluateJavascript(ScriptUtil.VIVO_EXPLOIT, value -> {
                                            if (value.contains("node")) {
                                                new DownloadTask(SelectedActivity.this, value.replaceAll("\"", ""), title, new int[]{count[0], count[1], countMax}).executeAsync();
                                                view.destroy();
                                                webView.destroy();
                                            } else makeText("Error getting direct vivo link: " + value);
                                        });
                                        super.onPageFinished(view, url);
                                    }
                                });
                            } else {
                                makeText("Error getting vivo link. Anime4you might be down / Anime done downloading");
                                Logger.log(returnCaptcha, Logger.LogType.WARNING);
                            }
                        });
                        super.onPageFinished(view, url);
                    }
                });
            }

            @Override
            public void preExecute() {

            }
        });
    }
    /**
     * Get download link then stream
     *
     * @param episode
     */

    /**
     * TODO: Merge
     *
     * @param episode
     */
    public void streamEpisode(final int episode) {
        final WebView webView = new WebView(getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(StringHandler.CAPTCHA_ANIME_4_YOU_ONE);

        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();

        new VideoFindTask(id, episode).executeAsync(new TaskExecutor.Callback<String>() {
            @Override
            public void onComplete(String result) {
                webView.getSettings().setJavaScriptEnabled(true);
                /*
                 * Clear all previous data and Cookies, so no code 400 appears: Cookie too large (yummy ;) )
                 */
                WebStorage.getInstance().deleteAllData();
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
                webView.clearCache(true);
                webView.clearFormData();
                webView.clearHistory();
                webView.clearSslPreferences();
                webView.loadUrl(StringHandler.CAPTCHA_ANIME_4_YOU_ONE);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        view.evaluateJavascript(result, returnCaptcha -> {
                            if (returnCaptcha.contains("vivo")) {
                                Logger.log("Found link for vivo:" + returnCaptcha, Logger.LogType.INFO);
                                makeText("Found VIVO link");
                                view.loadUrl(URLUtil.toUrl(StringUtil.removeBadCharacters(returnCaptcha, "\\\\", "\""), "https"));
                                view.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        view.evaluateJavascript(ScriptUtil.VIVO_EXPLOIT, s -> {
                                            if (s.contains("node")) {
                                                final Intent intent = new Intent(SelectedActivity.this, StreamPlayer.class);
                                                intent.putExtra("stream", s.replaceAll("\"", ""));
                                                startActivity(intent);
                                                finish();
                                                webView.destroy();
                                                view.destroy();
                                            } else makeText("Error getting direct vivo link: " + s);
                                        });
                                        super.onPageFinished(view, url);
                                    }
                                });
                            } else {
                                makeText("Error getting vivo link. Anime4you might be down / Anime done downloading");
                                Logger.log(returnCaptcha, Logger.LogType.WARNING);
                            }
                        });
                        super.onPageFinished(view, url);
                    }
                });
            }

            @Override
            public void preExecute() {

            }
        });

    }

    /**
     * Play episode from file
     *
     * @param index episode
     */
    private void playEpisodeFromSave(final int index) {
        Intent intent = null;
        final Optional<File> videoFile = getEpisodeFile(index);

        if (videoFile.isPresent()) {
            final int mode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("video_player_preference", "0"));
            if (mode == 0) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", videoFile.get()), "video/mp4");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (mode == 1) {
                intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("path", videoFile.get().getAbsolutePath());
            }
            startActivity(Objects.requireNonNull(intent));
        }
    }

    /**
     * Make text
     *
     * @param text
     */
    public void makeText(final String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    /**
     * Get episode file
     *
     * @param index
     * @return
     */
    public Optional<File> getEpisodeFile(final int index) {
        if (file.listFiles() != null) {
            return Arrays.stream(file.listFiles()).filter(file -> file.getName().substring(0, file.getName().lastIndexOf(".")).equals(String.valueOf(index))).findFirst();
        }
        return Optional.empty();
    }


    /**
     * Episode adapter
     */
    class EpisodeAdapter extends BaseAdapter {

        private final Context context;
        private final int episodes;

        public EpisodeAdapter(final int episodes, final Context context) {
            this.episodes = episodes;
            this.context = context;
        }

        @Override
        public int getCount() {
            return episodes;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(context).inflate(R.layout.episode_item_layout, parent, false);

            final TextView textView = convertView.findViewById(R.id.episode_name);
            final ImageView actionButton = convertView.findViewById(R.id.download_button);
            textView.setText("Episode: " + position);

            if (isEpisodeDownloaded(position)) {
                textView.setTextColor(0xFF30475e);
                actionButton.setImageResource(R.drawable.delete);
                actionButton.setOnClickListener(view ->
                        new IonAlert(SelectedActivity.this, IonAlert.WARNING_TYPE)
                                .setTitleText("Delete file?")
                                .setContentText("Won't be able to recover this file!")
                                .setConfirmText("Yes, delete it!")
                                .setConfirmClickListener(ionAlert -> {
                                    episodeAdapter.deleteItem(position);
                                    ionAlert.dismissWithAnimation();
                                }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                                .show());
            } else {
                textView.setTextColor(0xFFFFFFF);
                actionButton.setImageResource(R.drawable.download);
                actionButton.setOnClickListener(view -> downloadEpisode(position, 1, 0));
            }
            return convertView;
        }

        /**
         * Delete file
         *
         * @param index
         */
        public void deleteItem(final int index) {
            final Optional<File> videoFile = getEpisodeFile(index);
            if (videoFile.isPresent()) {
                Logger.log("Deleted: " + videoFile.get(), Logger.LogType.INFO);
                notifyDataSetChanged();
            }
        }
    }
}

