/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:28
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;
import com.htetznaing.lowcostvideo.Sites.Vidoza;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.scripter.Anime4YouDBSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.VivoDecodeTask;
import net.bplaced.abzzezz.animeapp.util.tasks.anime4you.Anime4YouDataBaseTask;
import net.bplaced.abzzezz.animeapp.util.tasks.anime4you.Anime4YouDirectVideoTask;
import net.bplaced.abzzezz.animeapp.util.tasks.anime4you.Anime4YouDownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.anime4you.Anime4YouSearchDBTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Anime4you extends Provider {

    public static final Anime4YouDBSearch ANIME_4_YOU_DB_SEARCH = new Anime4YouDBSearch();

    public Anime4you() {
        super("ANIME4YOU");
    }


    @Override
    public void refreshShow(Show show, Consumer<Show> updatedShow) {
        new TaskExecutor().executeAsync(new Anime4YouDataBaseTask(show.getID(), ANIME_4_YOU_DB_SEARCH), new TaskExecutor.Callback<Show>() {
            @Override
            public void onComplete(Show result) {
                updatedShow.accept(result);
            }

            @Override
            public void preExecute() {
                Logger.log("Fetching anime information", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void handleSearch(java.lang.String searchQuery, Consumer<List<Show>> searchResults) {
        new Anime4YouSearchDBTask(searchQuery).executeAsync(new TaskExecutor.Callback<List<Show>>() {
            @Override
            public void onComplete(final List<Show> result) {
                searchResults.accept(result);
            }

            @Override
            public void preExecute() {
                Log.i("Search", "Staring search");
            }
        });
    }

    @Override
    public JSONObject format(final Show show) throws JSONException {
        return new JSONObject()
                .put(StringHandler.SHOW_ID, show.getID())
                .put(StringHandler.SHOW_IMAGE_URL, show.getImageURL())
                .put(StringHandler.SHOW_EPISODE_COUNT, show.getEpisodes())
                .put(StringHandler.SHOW_TITLE, show.getTitle())
                .put(StringHandler.SHOW_LANG, show.getLanguage())
                .put(StringHandler.SHOW_YEAR, show.getYear())
                .put(StringHandler.SHOW_PROVIDER, Providers.ANIME4YOU.name());
    }

    @Override
    public Show getShow(final JSONObject data) throws JSONException {
        return new Show(
                data.getString("aid"),
                data.getString("titel"),
                data.getString("Letzte"),
                StringHandler.COVER_DATABASE.concat(data.getString("image_id")),
                data.getString("Untertitel"),
                Providers.ANIME4YOU.getProvider());
    }


    @Override
    public Show decode(JSONObject showJSON) throws JSONException {
        return new Show(
                showJSON.getString(StringHandler.SHOW_ID),
                showJSON.getString(StringHandler.SHOW_TITLE),
                showJSON.getString(StringHandler.SHOW_EPISODE_COUNT),
                showJSON.getString(StringHandler.SHOW_IMAGE_URL),
                showJSON.getString(StringHandler.SHOW_LANG),
                Providers.ANIME4YOU.getProvider());
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<URL>> resultURL, int... ints) {
        final WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(StringHandler.CAPTCHA_ANIME_4_YOU_ONE);

        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();

        AtomicReference<URL> url = new AtomicReference<>();

        new Anime4YouDirectVideoTask(show.getID(), ints[1]).executeAsync(new TaskExecutor.Callback<java.lang.String>() {
            @Override
            public void onComplete(java.lang.String foundEntry) {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(final WebView view, final java.lang.String u) {
                        view.evaluateJavascript(foundEntry, resultFromCaptcha -> {
                            try {
                                final JSONArray resultJSON = new JSONObject(resultFromCaptcha).getJSONArray("hosts");
                                final java.lang.String vivoURLEncoded = resultJSON.getJSONObject(2).getString("href");
                                final java.lang.String vidozaHash = resultJSON.getJSONObject(1).getString("crypt");

                                final java.lang.String[] urls = new java.lang.String[2];

                                final Consumer<java.lang.String> onDone = vivoURLDecoded -> {
                                    urls[0] = vivoURLDecoded;
                                    webView.destroy();
                                    view.destroy();

                                    java.lang.String finalURL = urls[0];
                                    if (urls[0] == null && urls[1] == null) {
                                        makeText("No link found for requested video", context);
                                        return;
                                    } else if (urls[0] == null) {
                                        finalURL = urls[1];
                                        makeText("Downloading from vidoza", context);
                                    } else if (urls[0].isEmpty()) {
                                        finalURL = urls[1];
                                        makeText("Downloading from vidoza", context);
                                    }

                                    try {
                                        resultURL.accept(Optional.of(new URL(finalURL.replace("\"", ""))));
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                };

                                view.evaluateJavascript(java.lang.String.format(StringHandler.VIDOZA_SCRIPT, vidozaHash), vidozaURL -> {
                                    Vidoza.fetch(vidozaURL.replace("\"", ""), new LowCostVideo.OnTaskCompleted() {
                                        @Override
                                        public void onTaskCompleted(final ArrayList<XModel> vidURL, final boolean multiple_quality) {
                                            vidURL.stream().max(XModel::compareTo).ifPresent(xModel -> urls[1] = xModel.getUrl());
                                            decodeVivo(vivoURLEncoded, onDone);
                                        }

                                        @Override
                                        public void onError() {
                                            System.out.println("Error vidoza");
                                            decodeVivo(vivoURLEncoded, onDone);
                                        }
                                    });
                                });
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                        });
                        super.onPageFinished(view, u);
                    }
                });
            }

            @Override
            public void preExecute() {
            }
        });
    }


    public void makeText(final java.lang.String text, final Context context) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    private void decodeVivo(final java.lang.String vivoURL, final Consumer<java.lang.String> url) {
        new VivoDecodeTask(vivoURL).executeAsync(new TaskExecutor.Callback<java.lang.String>() {
            @Override
            public void onComplete(java.lang.String result) {
                url.accept(result);
            }

            @Override
            public void preExecute() {
            }
        });
    }

    @Override
    public void handleDownload(SelectedActivity activity, URL url, Show show, File outDirectory, int... ints) {
        new Anime4YouDownloadTask(activity, url.toString(), show.getTitle(), outDirectory, new int[]{ints[0], ints[1], ints[2]}).executeAsync();
    }
}
