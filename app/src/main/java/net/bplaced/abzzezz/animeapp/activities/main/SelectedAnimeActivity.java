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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.extra.PlayerActivity;
import net.bplaced.abzzezz.animeapp.activities.extra.SplashScreen;
import net.bplaced.abzzezz.animeapp.input.DownloadSpecificInput;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.ScriptUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.io.File;

public class SelectedAnimeActivity extends AppCompatActivity implements DownloadSpecificInput.SpecificDownloadListener {

    private String animeName, animeCover, language;
    private int position, aid, animeEpisodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
         *
         */
        /**
         * Toolbar and Image
         */
        Toolbar toolbar = findViewById(R.id.selected_anime_toolbar);
        ImageView cover = findViewById(R.id.anime_cover_image);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(animeName);
        cover.setImageBitmap(ImageUtil.getImageBitmap(animeCover, ImageUtil.dimensions[0], ImageUtil.dimensions[1]));
        /**
         *
         */
        /**
         * GridView and Download Button
         */
        GridView episodeGrid = findViewById(R.id.anime_episodes_grid);
        File animeFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), animeName);
        String[] episodes = animeFile.list();
        selected_anime_size.append(FileUtil.calculateFileSize(animeFile));

        AnimeEpisodeAdapter animeEpisodeAdapter = new AnimeEpisodeAdapter(episodes == null || episodes.length == 0 ? new String[]{} : episodes, getApplicationContext());
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
            new AlertDialog.Builder(SelectedAnimeActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete?")
                    .setMessage("Will delete File")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + animeName, animeEpisodeAdapter.episodes[position]);
                        f.deleteOnExit();
                        System.out.println("Deleted: " + f.delete());
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                    })
                    .show();
            return true;
        });
        /**
         * Button
         */
        FloatingActionButton downloadAnime = findViewById(R.id.download_anime_button);
        SplashScreen.episodeDownloader.setButton(downloadAnime);

        int nextStart = (episodes != null && episodes.length != 0) ? StringUtil.extractNumberI(episodes[episodes.length - 1].replaceAll(".mp4", "")) + 1 : 1;
        downloadAnime.setOnClickListener(v -> download(nextStart));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_selected_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.delete_aid_item) {
            SplashScreen.saver.getList().remove(position);
        } else if (itemID == R.id.download_specific_episode) {
            DownloadSpecificInput input = new DownloadSpecificInput();
            input.show(getSupportFragmentManager(), "Download specific");
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

    public void download(int start) {
            /*
                If series is downloaded, reset index to 0
            */
        if (SplashScreen.episodeDownloader.currentIndex > SplashScreen.episodeDownloader.episodesTotal) {
            Logger.log("Resetting episode counter", Logger.LogType.INFO);
            SplashScreen.episodeDownloader.currentIndex = 0;
        }

            /*
            If 0 then configure or reconfigure
             */
        if (SplashScreen.episodeDownloader.currentIndex == 0) {
            SplashScreen.episodeDownloader.currentIndex = start;
            SplashScreen.episodeDownloader.downloadAID = aid;
            SplashScreen.episodeDownloader.episodesTotal = animeEpisodes;
        }

        if (start > animeEpisodes) {
            makeText("All episodes downloaded already");
            return;
        }

        WebView webView = new WebView(getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(URLHandler.captchaURL);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(ScriptUtil.getRequest(SplashScreen.episodeDownloader.downloadAID, SplashScreen.episodeDownloader.currentIndex), returnJS -> {
                    if (returnJS.contains("vivo")) {
                        makeText("Found link for Episode:" + SplashScreen.episodeDownloader.currentIndex);
                        SplashScreen.episodeDownloader.currentLink = URLUtil.toUrl(StringUtil.removeWindowsChars(returnJS), "https") + StringUtil.splitter + animeName + "::" + SplashScreen.episodeDownloader.currentIndex;
                        Logger.log("Got vivo link: " + returnJS, Logger.LogType.INFO);

                            /*
                            Check if script has failed / or series is done downloading
                             */
                        if (SplashScreen.episodeDownloader.currentLink.isEmpty() || SplashScreen.episodeDownloader.currentIndex > SplashScreen.episodeDownloader.episodesTotal) {
                            makeText("Please get links first! / Series download done");
                            return;
                        }

                            /*
                            Load vivo page and run script
                             */
                        String[] urlComplete = SplashScreen.episodeDownloader.currentLink.split(StringUtil.splitter);
                        view.loadUrl(urlComplete[0]);
                        view.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                view.evaluateJavascript(ScriptUtil.vivoExploit, returnJS -> {
                                    if (returnJS.contains("node")) {
                                            /*
                                            Debug purposes
                                             */
                                        Logger.log("Got vivo video url: " + returnJS, Logger.LogType.INFO);
                                            /*
                                            Download from Vivo url
                                             */
                                        SplashScreen.episodeDownloader.download(returnJS.replaceAll("\"", ""), urlComplete[1], SelectedAnimeActivity.this, "mp4");
                                            /*
                                            Destroy old web view
                                             */
                                        view.destroy();
                                        /**
                                         * Add to index and if button checked then call on click again
                                         */
                                        if (SplashScreen.episodeDownloader.currentIndex <= SplashScreen.episodeDownloader.episodesTotal) {
                                            SplashScreen.episodeDownloader.currentIndex++;
                                        }
                                    } else {
                                        /**
                                         * If the JS return of the vivo page does not equal node (vivo direct video url)
                                         * then return
                                         */
                                        Logger.log(returnJS, Logger.LogType.INFO);
                                        makeText("Error getting link, returning");
                                        return;
                                    }
                                });
                            }
                        });
                    } else {
                        /**
                         * If JS return does not equals vivo then return
                         */
                        Logger.log("Error getting vivo link: " + returnJS, Logger.LogType.ERROR);
                        makeText("Error getting Episode:" + SplashScreen.episodeDownloader.currentIndex);
                        return;
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
            download(Integer.valueOf(start));
        } catch (NumberFormatException e) {
            makeText("Not possible to convert number, please check your input");
        }
    }

    class AnimeEpisodeAdapter extends BaseAdapter {

        private String[] episodes;
        private Context context;

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
            textView.setTextColor(-1);
            textView.setAlpha(0.87F);
            textView.setText(episodes[position]);
            return textView;
        }
    }

}
