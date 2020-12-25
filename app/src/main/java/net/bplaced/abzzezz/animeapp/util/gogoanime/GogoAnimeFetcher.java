package net.bplaced.abzzezz.animeapp.util.gogoanime;

import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uses Jsoup and JSON
 */
public class GogoAnimeFetcher {

    public static final String BASE_URL = "https://gogoanime.so";

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
    private final String urlIn;

    public GogoAnimeFetcher(final String urlIn) throws IOException {
        if (!CACHE_DIRECTORY.exists()) CACHE_DIRECTORY.mkdir();
        if (!DOWNLOAD_DIRECTORY.exists()) DOWNLOAD_DIRECTORY.mkdir();

        this.urlIn = urlIn;
        this.showDocument = Jsoup.connect(urlIn).userAgent(RandomUserAgent.getRandomUserAgent()).get();
        this.showTitle = this.sanitizeString(showDocument.title());

        this.fetchedDirectURLs = this.fetchIDs();
    }

    /**
     * Fetches all ids from the given url
     *
     * @param urlIn url to first get id from
     * @return String array containing all ids for the direct url
     * @throws IOException some connection goes wrong
     */
    public static String[] fetchIDs(final String urlIn) throws IOException {
        final String userAgent = RandomUserAgent.getRandomUserAgent();

        final Document showDocument = Jsoup.connect(urlIn).userAgent(userAgent).get();
        final int id = Integer.parseInt(showDocument.body().selectFirst("input#movie_id").val());
        final int epiStart = Integer.parseInt(showDocument.body().selectFirst("#episode_page a.active").attr("ep_start"));
        final int epiEnd = Integer.parseInt(showDocument.body().selectFirst("#episode_page a.active").attr("ep_end"));

        /*
         * Grab episodes & fetch ids
         */

        final String episodesURL = String.format(EPISODE_API_URL, epiStart, epiEnd, id);
        final Document episodesDocument = Jsoup.connect(episodesURL).userAgent(userAgent).get();

        return episodesDocument.body().getElementById("episode_related").children().stream()
                .map(element -> BASE_URL + element.selectFirst("a").attr("href").trim())
                .map(episodeURL -> {
                    try {
                        final Document episodeDocument = Jsoup.connect(episodeURL).userAgent(userAgent).get();
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

    public static String[] fetchIDs(final String idIn, final int epiStart, final int epiEnd) throws IOException {
        final String userAgent = RandomUserAgent.getRandomUserAgent();

        final int id = Integer.parseInt(idIn);

        /*
         * Grab episodes & fetch ids
         */

        final String episodesURL = String.format(EPISODE_API_URL, epiStart, epiEnd, id);
        final Document episodesDocument = Jsoup.connect(episodesURL).userAgent(userAgent).get();

        return episodesDocument.body().getElementById("episode_related").children().stream()
                .map(element -> BASE_URL + element.selectFirst("a").attr("href").trim())
                .map(episodeURL -> {
                    try {
                        final Document episodeDocument = Jsoup.connect(episodeURL).userAgent(userAgent).get();
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

    public static String[] getURLsFromSearch(final String searchQuery) throws IOException {
        final Document document = Jsoup.connect(String.format(SEARCH_URL, searchQuery)).userAgent(StringHandler.USER_AGENT).get();
        return document.getElementsByClass("name").stream().map(element -> BASE_URL + element.select("a").attr("href")).toArray(String[]::new);
    }

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
     * Fetches the show's image from it's url
     *
     * @return the file's location
     * @throws IOException if copy process goes wrong
     */
    public File fetchImage() throws IOException {
        final URL imageURL = new URL(fetchImage0());
        final File cacheFile = new File(CACHE_DIRECTORY, sanitizeString(imageURL.getFile()));
        this.copyFileFromURL(imageURL, cacheFile);
        return cacheFile;
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
     * @return
     */
    public String getID() {
        return showDocument.body().selectFirst("input#movie_id").val();
    }

    public String getEpisodeStart() {
        return showDocument.body().selectFirst("#episode_page a.active").attr("ep_start");
    }

    public String getEpisodeEnd() {
        return showDocument.body().selectFirst("#episode_page a.active").attr("ep_end");
    }

    /**
     * Fetch videos
     *
     * @param start start
     * @param end   end
     * @throws IOException if something goes wrong
     */
    public void fetch(final int start, final int end) throws IOException, JSONException {
        //Create output directory
        final File outputDirectory = new File(DOWNLOAD_DIRECTORY, showTitle);
        if (!outputDirectory.exists()) outputDirectory.mkdir();

        for (int i = start; i < end; i++) {
            //Get direct video url
            final String formatted = String.format(API_URL, fetchedDirectURLs[i]);
            final URL vidURL = new URL(getVidURL(collectLines(new URL(formatted), "")));
            //Copy file from url (Download)
            copyFileFromURL(vidURL, new File(outputDirectory, showTitle.concat(" " + i).concat(".mp4")));
            System.out.println("Done downloading " + i + "/" + end);
        }
        System.out.println("Done downloading!");
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
        final Document episodesDocument = Jsoup.connect(episodesURL).userAgent(userAgent).get();

        return episodesDocument.body().getElementById("episode_related").children().stream()
                .map(element -> BASE_URL + element.selectFirst("a").attr("href").trim())
                .map(episodeURL -> {
                    try {
                        final Document episodeDocument = Jsoup.connect(episodeURL).userAgent(userAgent).get();
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
     * Copies file from url
     *
     * @param src  to copy from
     * @param dest destination to copy to
     * @throws IOException @
     */
    private void copyFileFromURL(final URL src, final File dest) throws IOException {
        final FileOutputStream fileOutputStream = new FileOutputStream(dest);
        fileOutputStream.getChannel().transferFrom(Channels.newChannel(src.openStream()), 0, Long.MAX_VALUE);
        fileOutputStream.close();
    }

    /**
     * Joins all the lines read from a url together
     *
     * @param src    url to read from
     * @param joiner String to join all read lines together
     * @return all joined lines
     * @throws IOException if reader / url fails, etc.
     */
    private String collectLines(final URL src, final String joiner) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(src.openStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line).append(joiner);
        }
        bufferedReader.close();
        return builder.toString();
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
