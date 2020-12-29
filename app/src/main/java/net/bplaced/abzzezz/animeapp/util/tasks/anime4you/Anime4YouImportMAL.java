/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:05
 */

package net.bplaced.abzzezz.animeapp.util.tasks.anime4you;

import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.provider.impl.Anime4YouHolder;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.ricecode.similarity.JaroStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Anime4YouImportMAL extends TaskExecutor implements Callable<String>, Anime4YouHolder {

    private final String url;
    private final SimilarityStrategy stringSimilarity = new JaroStrategy();
    private String dataBase;

    public Anime4YouImportMAL(final String url) {
        this.url = url;
    }

    @Override
    public String call() throws Exception {
        dataBase = URLUtil.getURLContentAsString(new URL(DATABASE));
        for (final String[] strings : getSimilar()) {
            AnimeAppMain.getInstance().getShowSaver().addShow(getPrefDub(strings));
        }
        return null;
    }

    public <R> void executeAsync(final Callback<String> callback) {
        super.executeAsync(this, callback);
    }

    private JSONObject getPrefDub(final String[] show) {
        final String showName = show[0];
        final Collection<JSONObject> shows = new ArrayList<>();
        int showIndex = dataBase.indexOf("\"titel\":\"" + showName + "\"");

        while (showIndex != -1) {
            try {
                final JSONObject listObject = new JSONObject(dataBase.substring(dataBase.lastIndexOf("{", showIndex), dataBase.indexOf("}", showIndex) + 1));
                final JSONObject converted = new JSONObject();

                converted.put("id", listObject.getString("aid"));
                converted.put("image_url", COVER_DATABASE.concat(listObject.getString("image_id")));
                converted.put("episodes", listObject.getString("Letzte"));
                converted.put("title", listObject.getString("titel"));
                converted.put("language", listObject.getString("Untertitel"));
                converted.put("year", listObject.getString("Jahr"));
                shows.add(converted);
            } catch (JSONException e) {
                e.printStackTrace();
                break;
            }
            showIndex = dataBase.indexOf("\"titel\":\"" + showName + "\"", showIndex + 1);
        }
        final JSONObject gerSub = shows.stream().findAny().get();

        return shows.stream().filter(jsonObject -> {
            try {
                return jsonObject.getString("language").equals("gerdub");
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }).findAny().orElse(gerSub);
    }

    private Collection<String[]> getSimilar() throws MalformedURLException {
        final List<String> allAvailable = getAllTitles();
        final List<String> animeMAL = getMalTitles();

        return allAvailable.parallelStream().map(s -> s.split(StringUtil.splitter)).filter(strings -> {
            final Optional<String[]> contains = animeMAL.stream().map(s -> s.split(StringUtil.splitter)).filter(s -> stringSimilarity.score(strings[0], s[0]) > 0.9F).findFirst();
            return contains.isPresent() && contains.get()[1].equals(strings[1]);
        }).collect(Collectors.toList());
    }

    private List<String> getAllTitles() {
        final List<String> titleList = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder(dataBase);
        final String title = "\"titel\":\"";
        final String episode = "\"Letzte\":\"";
        while (stringBuilder.indexOf(title) != -1) {
            final int start = stringBuilder.indexOf(title);
            final int end = stringBuilder.indexOf("\"", start + title.length());
            final int smallStart = stringBuilder.lastIndexOf("{", start);
            final int smallEnd = stringBuilder.indexOf("}", start);
            final String smallString = stringBuilder.substring(smallStart, smallEnd);
            final String name = stringBuilder.substring(start + title.length(), end);
            final String episodes = StringUtil.getStringFromLong(smallString, episode, "\"");
            final String s = name + StringUtil.splitter + episodes;
            if (!titleList.contains(s)) titleList.add(s);
            stringBuilder.delete(smallStart, smallEnd);
        }
        Logger.log("Done getting Anime4you.", Logger.LogType.INFO);
        return titleList;
    }

    private List<String> getMalTitles() throws MalformedURLException {
        final List<String> titles = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder();

        for (final String line : URLUtil.getURLContentAsArray(new URL(url))) {
            if (line.contains("<table class=\"list-table\" data-items=\"")) {
                stringBuilder.append(line, line.indexOf("<table class=\"list-table\" data-items=\""), line.indexOf(">"));
                break;
            }
        }

        final String title = "anime_title&quot;:&quot;";
        final String episode = "anime_num_episodes&quot;:";

        while (stringBuilder.indexOf(title) != -1) {
            final int start = stringBuilder.indexOf(title) + title.length();
            final int end = stringBuilder.indexOf("&", start);
            final String smallString = stringBuilder.substring(start, stringBuilder.indexOf("}", start));
            final int episodeStart = smallString.indexOf(episode) + episode.length();

            final String name = stringBuilder.substring(start, end);
            final String episodes = smallString.substring(episodeStart, smallString.indexOf(",", episodeStart));
            titles.add(name + StringUtil.splitter + episodes);

            stringBuilder.delete(start - title.length(), start + smallString.length());
        }

        Logger.log("Got all titles from MAL", Logger.LogType.INFO);
        return titles;
    }

}
