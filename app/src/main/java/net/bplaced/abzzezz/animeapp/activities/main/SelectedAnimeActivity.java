/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:37
 */

package net.bplaced.abzzezz.animeapp.activities.main;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.OptionalInt;

public class SelectedAnimeActivity extends AppCompatActivity {

    public AnimeEpisodeAdapter animeEpisodeAdapter;
    private String animeName;
    private int aid, animeEpisodes;
    private File animeFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AnimeAppMain.getInstance().getThemeID());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_selected_layout);

        /*
         * Get intent varaibles
         */
        try {
            final JSONObject inf = new JSONObject(getIntent().getStringExtra("anime_details"));
            this.animeName = inf.getString("name");
            this.animeEpisodes = inf.getInt("episodes");
            this.aid = inf.getInt("id");
            final String animeCover = inf.getString("image_url");
            this.animeFile = new File(getFilesDir(), animeName);

            //Set text etc.
            ((TextView) findViewById(R.id.selected_anime_name)).setText(animeName);
            ((TextView) findViewById(R.id.selected_anime_episodes)).append(String.valueOf(animeEpisodes));
            ((TextView) findViewById(R.id.selected_anime_aid)).append(String.valueOf(aid));
            ((TextView) findViewById(R.id.selected_anime_language)).append(inf.getString("language"));
            ((TextView) findViewById(R.id.selected_anime_year)).append(inf.getString("year"));
            ((TextView) findViewById(R.id.anime_directory_size)).append(FileUtil.calculateFileSize(animeFile));

            final ImageView cover = findViewById(R.id.anime_cover_image);
            final Toolbar toolbar = findViewById(R.id.selected_anime_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(animeName);
            /*
             * If offline mode is enabled use image offline loader
             */
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("offline_mode", false))
                OfflineImageLoader.loadImage(animeCover, String.valueOf(aid), cover, this);
            else
                Picasso.with(getApplicationContext()).load(animeCover).resize(ImageUtil.DIMENSIONS[0], ImageUtil.DIMENSIONS[1]).into(cover);


            final ListView listView = findViewById(R.id.anime_episodes_grid);

            /*
             * Set Adapter
             */
            this.animeEpisodeAdapter = new AnimeEpisodeAdapter(animeEpisodes, getApplicationContext());

            listView.setAdapter(animeEpisodeAdapter);
            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                final boolean isDownloaded = isEpisodeDownloaded(i);
                new IonAlert(SelectedAnimeActivity.this, IonAlert.NORMAL_TYPE)
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

            /*
             * Button
             */
            FloatingActionButton downloadAnime = findViewById(R.id.download_anime_button);
        /*
        "Calculate" next start
         */
            downloadAnime.setOnClickListener(v -> downloadEpisode(getLatestEpisode(), animeEpisodes, 0));
        } catch (JSONException e) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_selected_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int getLatestEpisode() {
        if (animeFile.list() != null) {
            final OptionalInt highest = Arrays.stream(animeFile.list()).map(s -> StringUtil.extractNumberI(s.substring(0, s.lastIndexOf(".")))).mapToInt(integer -> integer).max();
            if (highest.isPresent())
                return highest.getAsInt() + 1;
        }
        return 1;
    }

    public void resetAdapter() {
        animeEpisodeAdapter.notifyDataSetChanged();
    }

    private boolean isEpisodeDownloaded(final int index) {
        if (animeFile.list() != null) {
            return Arrays.stream(animeFile.list()).anyMatch(s -> s.substring(0, s.lastIndexOf(".")).equals(String.valueOf(index)));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.download_bound:
                InputDialogBuilder dialogBuilder = new InputDialogBuilder(new InputDialogListener() {
                    @Override
                    public void onDialogInput(String text) {
                        downloadEpisode(getLatestEpisode(), Integer.parseInt(text), 0);
                    }

                    @Override
                    public void onDialogDenied() {
                    }
                });
                dialogBuilder.showInput("Download bound", "Enter bound", this);
                break;
            case R.id.toogle_notifications_anime:
                //Add to notification manager
                AnimeAppMain.getInstance().getAnimeNotifications().add(animeName + StringUtil.splitter + aid, String.valueOf(animeEpisodes));
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
        webView.loadUrl(StringHandler.HTTPS_CAPTCHA_ANIME_4_YOU_ONE);

        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();

        new VideoFindTask(aid, count[1], this).executeAsync(new TaskExecutor.Callback<String>() {
            @Override
            public void onComplete(String result) throws Exception {
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
                                                new DownloadTask(SelectedAnimeActivity.this, value.replaceAll("\"", ""), animeName, new int[]{count[0], count[1], countMax}).executeAsync();
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
        webView.loadUrl(StringHandler.HTTPS_CAPTCHA_ANIME_4_YOU_ONE);

        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();

        new VideoFindTask(aid, episode, this).executeAsync(new TaskExecutor.Callback<String>() {
            @Override
            public void onComplete(String result) throws Exception {
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
                webView.loadUrl(StringHandler.HTTPS_CAPTCHA_ANIME_4_YOU_ONE);
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
                                                final Intent intent = new Intent(SelectedAnimeActivity.this, StreamPlayer.class);
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
        final File file = getEpisodeFile(index);
        int mode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("video_player_preference", "0"));
        if (mode == 0) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file), "video/mp4");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else if (mode == 1) {
            intent = new Intent(getApplicationContext(), PlayerActivity.class);
            intent.putExtra("path", file.getAbsolutePath());
        }
        startActivity(Objects.requireNonNull(intent));
        finish();
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
    public File getEpisodeFile(final int index) {
        if (animeFile.listFiles() != null) {
            return Arrays.stream(animeFile.listFiles()).filter(file -> file.getName().substring(0, file.getName().lastIndexOf(".")).equals(String.valueOf(index))).findFirst().get();
        }
        return null;
    }

    /*
    Method no longer needed
    public static List<String> sortWithNumberInName(List<String> in) {
        in.sort(Comparator.comparingInt((o) -> StringUtil.extractNumberI(o.substring(o.indexOf("::") + 2, o.indexOf(".mp4")))));
        return in;
    }

     */

    /*
    private BroadcastReceiver broadcastReceiver;
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
    */

    /**
     * Episode adapter
     */
    class AnimeEpisodeAdapter extends BaseAdapter {

        private final Context context;
        private final int episodes;

        public AnimeEpisodeAdapter(int episodes, Context context) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.episode_layout, parent, false);
            final TextView textView = convertView.findViewById(R.id.episode_name);
            final ImageView actionButton = convertView.findViewById(R.id.download_button);
            textView.setText("Episode: " + position);
            if (isEpisodeDownloaded(position)) {
                textView.setTextColor(0xFF30475e);
                actionButton.setImageResource(R.drawable.delete);
                actionButton.setOnClickListener(view ->
                        new IonAlert(SelectedAnimeActivity.this, IonAlert.WARNING_TYPE)
                                .setTitleText("Delete file?")
                                .setContentText("Won't be able to recover this file!")
                                .setConfirmText("Yes, delete it!")
                                .setConfirmClickListener(ionAlert -> {
                                    animeEpisodeAdapter.deleteItem(position);
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
            Logger.log("Deleted: " + getEpisodeFile(index).delete(), Logger.LogType.INFO);
            notifyDataSetChanged();
        }
    }
}

