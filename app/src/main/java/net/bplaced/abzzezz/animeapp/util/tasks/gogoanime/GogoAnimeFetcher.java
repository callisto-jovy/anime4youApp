/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 01.02.21, 10:32
 */

package net.bplaced.abzzezz.animeapp.util.tasks.gogoanime;

import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import org.json.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

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

    private final JSONArray fetchedDirectURLs;
    private final Document showDocument;

    public GogoAnimeFetcher(final String urlIn) throws IOException {
        if (!CACHE_DIRECTORY.exists()) CACHE_DIRECTORY.mkdir();
        if (!DOWNLOAD_DIRECTORY.exists()) DOWNLOAD_DIRECTORY.mkdir();

        this.showDocument = createGogoConnection(urlIn, RandomUserAgent.getRandomUserAgent()).get();
        this.fetchedDirectURLs = this.fetchReferrals();
    }

    public static JSONArray fetchReferrals(final String idIn, final int epiStart, final int epiEnd) throws IOException {
        final String userAgent = RandomUserAgent.getRandomUserAgent();

        final int id = Integer.parseInt(idIn); //Parse id

        final String episodesURL = String.format(Locale.ENGLISH, EPISODE_API_URL, epiStart, epiEnd, id); //Format episode request URL
        final Document episodesDocument = createGogoCdn(episodesURL, userAgent).get(); //Create httpsurlconnection

        return episodesDocument.body().getElementById("episode_related").children().stream()
                .map(element -> BASE_URL + element.selectFirst("a").attr("href").trim())
                .collect(
                        Collector.of(
                                JSONArray::new, //init accumulator
                                JSONArray::put, //processing each element
                                JSONArray::put  //confluence 2 accumulators in parallel execution
                        ));
    }

    /**
     * Fetches the id to an episode using it's site's referral
     *
     * @param referral referral to the site
     * @return the
     */
    public static Optional<String> fetchIDLink(final String referral) {
        try {
            final Document episodeDocument = createGogoCdn(referral, Constant.USER_AGENT).get();

            final String src = episodeDocument.selectFirst("iframe").attr("src");
            final Matcher matcher = PATTERN.matcher(src);

            if (matcher.find()) {
                return Optional.of(String.format(API_URL, matcher.group().substring(3, matcher.group().length() - 1)));
            } else return Optional.empty();
        } catch (final IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Calls gogoanime's search api
     *
     * @param searchQuery query to search for
     * @return String array with all url results
     * @throws IOException url error
     */
    public static String[] getURLsFromSearch(final String searchQuery) throws IOException {
        final Document document = Jsoup.connect(String.format(SEARCH_URL, searchQuery)).userAgent(Constant.USER_AGENT).get();
        return document.getElementsByClass("name").stream().map(element -> BASE_URL + element.select("a").attr("href")).toArray(String[]::new);
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
    private JSONArray fetchReferrals() throws IOException {
        final String userAgent = RandomUserAgent.getRandomUserAgent();

        final Element body = showDocument.body();

        final int id = Integer.parseInt(body.selectFirst("input#movie_id").val());
        final int epiStart = Integer.parseInt(body.selectFirst("#episode_page a.active").attr("ep_start"));
        final int epiEnd = Integer.parseInt(body.selectFirst("#episode_page a.active").attr("ep_end"));

        final String episodesURL = String.format(Locale.ENGLISH, EPISODE_API_URL, epiStart, epiEnd, id);

        final Document episodesDocument = createGogoCdn(episodesURL, userAgent).get();
        return episodesDocument
                .body()
                .getElementById("episode_related")
                .children()
                .stream()
                .map(element -> BASE_URL + element.selectFirst("a").attr("href").trim())
                .collect(
                        Collector.of
                                (
                                        JSONArray::new, //init accumulator
                                        JSONArray::put, //processing each element
                                        JSONArray::put  //confluence 2 accumulators in parallel execution
                                )
                );
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

    public JSONArray getFetchedDirectURLs() {
        return fetchedDirectURLs;
    }
}
