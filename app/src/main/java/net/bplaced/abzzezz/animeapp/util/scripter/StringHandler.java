/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 20:08
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import android.content.Context;
import android.net.ConnectivityManager;
import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;

public class StringHandler {

    public static final String USER_AGENT = RandomUserAgent.getRandomUserAgent();

    public static final String UPDATE_APK = "http://abzzezz.bplaced.net/app/update.apk";
    public static final String APP_VERSION_TXT = "http://abzzezz.bplaced.net/app/app_version_new.txt";
    public static final String APP_CHANGELOG_TXT = "http://abzzezz.bplaced.net/app/changelog.txt";
    public static final String USER_URL = "http://abzzezz.bplaced.net/app/user.php";
    public static final String IMAGE_URL = "http://abzzezz.bplaced.net/app/imgCreate.php?text=";

    /*
    JSON keys
     */
    public static final String SHOW_ID = "id";
    public static final String SHOW_IMAGE_URL = "image_url";
    public static final String SHOW_EPISODE_COUNT = "episodes";
    public static final String SHOW_TITLE = "title";
    public static final String SHOW_LANG = "language";
    public static final String SHOW_YEAR = "year";

    public static final String SHOW_PROVIDER = "provider";

    public static final String VIDOZA_SCRIPT = "call(); function call() { vidoza = $.ajax({type: 'POST',url: '/check_video.php',async: false,success: function(data){},data: {vidhash:\"%s\"}}).responseJSON[\"url\"];return vidoza;}";

    public static boolean isOffline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}

