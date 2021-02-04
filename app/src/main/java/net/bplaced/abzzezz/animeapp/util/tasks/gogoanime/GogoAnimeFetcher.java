/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 01.02.21, 10:32
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uses Jsoup and JSON
 */
public class GogoAnimeFetcher {

    public static final String BASE_URL = "https://gogoanime.sh";

    public static final String API_URL = "https://gogo-play.net/ajax.php?id=%s";
    //Start, end, anime-id, Returns a "list" containing all redirects to the other episodes
    public static final String EPISODE_API_URL = "https://ajax.gogocdn.net/ajax/load-list-episode?ep_start=%d&ep_end=%d&id=%d";

    public static final String SEARCH_URL = "https://gogoanime.so/search.html?keyword=%s";

    public static final Pattern PATTERN = Pattern.compile("id=.+&");

    public static final File CACHE_DIRECTORY = new File(System.getProperty("user.home"), "Anime Cache");

    public static final File DOWNLOAD_DIRECTORY = new File(System.getProperty("user.home") + "/Desktop", "Gogo Anime Downloader");

    private final String showTitle;
    private final String[] fetchedDirectURLs;
    private final Document showDocument;

    public GogoAnimeFetcher(final String urlIn) throws IOException {
        if (!CACHE_DIRECTORY.exists()) CACHE_DIRECTORY.mkdir();
        if (!DOWNLOAD_DIRECTORY.exists()) DOWNLOAD_DIRECTORY.mkdir();

        this.showDocument = createGogoConnection(urlIn, RandomUserAgent.getRandomUserAgent()).get();
        this.showTitle = this.sanitizeString(showDocument.title());

        this.fetchedDirectURLs = this.fetchIDs();
    }

    public static String[] fetchIDs(final String idIn, final int epiStart, final int epiEnd) throws IOException {
        final String userAgent = RandomUserAgent.getRandomUserAgent();

        final int id = Integer.parseInt(idIn);

        /*
         * Grab episodes & fetch ids
         */

        final String episodesURL = String.format(Locale.ENGLISH, EPISODE_API_URL, epiStart, epiEnd, id);
        final Document episodesDocument = createGogoCdn(episodesURL, userAgent).get();

        return episodesDocument.body().getElementById("episode_related").children().stream()
                .map(element -> BASE_URL + element.selectFirst("a").attr("href").trim())
                .map(episodeURL -> {
                    try {
                        final Document episodeDocument = createGogoCdn(episodesURL, userAgent).get();
                        final String src = episodeDocument.selectFirst("iframe").attr("src");

                        System.out.println(src);

                        final Matcher matcher = PATTERN.matcher(src);
                        if (matcher.find()) {
                            System.out.println(matcher.group());
                            return matcher.group().substring(3, matcher.group().length() - 1);
                        }
                        else return "";
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "";
                    }
                }).toArray(String[]::new);
    }

    /**
     * Calls gogoanime's search api
     *
     * @param searchQuery query to search for
     * @return String array with all url results
     * @throws IOException url error
     */
    public static String[] getURLsFromSearch(final String searchQuery) throws IOException {
        final Document document = Jsoup.connect(String.format(SEARCH_URL, searchQuery)).userAgent(StringHandler.USER_AGENT).get();
        return document.getElementsByClass("name").stream().map(element -> BASE_URL + element.select("a").attr("href")).toArray(String[]::new);
    }

    /**
     * @param urlIn url to extract from
     * @return extracted direct url
     * @throws JSONException when bad json is parsed
     */
    public static String getDirectVideoURL(final String urlIn) throws JSONException {
        return getVidURL(String.format(API_URL, urlIn));
    }

    /**
     * Gets the direct video url from the formatted api link
     *
     * @param in read in lines
     * @return url to mp4
     */
    private static String getVidURL(final String in) throws JSONException {
        return new JSONObject(in).getJSONArray("source").getJSONObject(0).getString("file");
    }

    /**
     * Create jsoup connection
     *
     * @param url       url in
     * @param userAgent user agent to use
     * @return pre constructed connection
     */
    private static Connection createGogoConnection(final String url, final String userAgent) {
        return Jsoup.connect(url).userAgent(userAgent).header("authority", "gogoanime.so").referrer("https://gogoanime.so/search.html");
    }

    /**
     * Create jsoup connection
     *
     * @param url       url in
     * @param userAgent user agent to use
     * @return pre constructed connection
     */
    private static Connection createGogoCdn(final String url, final String userAgent) {
        return Jsoup.connect(url).userAgent(userAgent).header("authority", "ajax.gogocdn.net");
    }

    /**
     * Fetches the show's image from it's url
     *
     * @return the image's url
     */
    public String fetchImage0() {
        return showDocument.selectFirst("meta[property=og:image]").attr("content");
    }

    /**
     * Returns the show's id
     *
     * @return the shod id
     */
    public String getID() {
        return showDocument.body().selectFirst("input#movie_id").val();
    }

    /**
     * @return episode start from local document
     */
    public String getEpisodeStart() {
        return showDocument.body().selectFirst("#episode_page a.active").attr("ep_start");
    }

    /**
     * @return episode end from local document
     */
    public String getEpisodeEnd() {
        return showDocument.body().selectFirst("#episode_page a.active").attr("ep_end");
    }

    /**
     * Fetches all ids from the given url
     *
     * @return String array containing all ids for the direct url
     * @throws IOException some connection goes wrong
     */
    private String[] fetchIDs() throws IOException {
        final String userAgent = RandomUserAgent.getRandomUserAgent();

        final Element body = showDocument.body();

        final int id = Integer.parseInt(body.selectFirst("input#movie_id").val());
        final int epiStart = Integer.parseInt(body.selectFirst("#episode_page a.active").attr("ep_start"));
        final int epiEnd = Integer.parseInt(body.selectFirst("#episode_page a.active").attr("ep_end"));

        /*
         * Grab episodes & fetch ids
         */

        final String episodesURL = String.format(EPISODE_API_URL, epiStart, epiEnd, id);
        final Document episodesDocument = createGogoCdn(episodesURL, userAgent).get();

        return episodesDocument.body().getElementById("episode_related").children().stream()
                .map(element -> BASE_URL + element.selectFirst("a").attr("href").trim())
                .map(episodeURL -> {
                    try {
                        final Document episodeDocument = createGogoCdn(episodeURL, userAgent).get();
                        final String src = episodeDocument.selectFirst("iframe").attr("src");

                        final Matcher matcher = PATTERN.matcher(src);
                        if (matcher.find())
                            return matcher.group().substring(3, matcher.group().length() - 1);
                        else return "";
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "";
                    }
                }).toArray(String[]::new);
    }

    /**
     * Removes windows disallowed characters
     *
     * @param string String to be sanitized
     * @return sanitized string
     */
    private String sanitizeString(String string) {
        if (string == null) return "";
        return string.replaceAll("[\u0000-\u001f<>:\"/\\\\|?*\u007f]+", "").trim();
    }

    public String getShowTitle() {
        return showTitle;
    }

    public String[] getFetchedDirectURLs() {
        return fetchedDirectURLs;
    }
}
