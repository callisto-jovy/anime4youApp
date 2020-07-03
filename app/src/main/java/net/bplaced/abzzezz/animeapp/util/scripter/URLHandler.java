/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 20:08
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import android.content.Context;
import android.net.ConnectivityManager;

public class URLHandler {


    /*
    Just for the purpose of keeping the code clean and organized ;)
     */
    public static final String HTTPS_CAPTCHA_ANIME_4_YOU_ONE = "https://captcha.anime4you.one/";
    public static final String NET_APP_UPDATE_APK = "http://abzzezz.bplaced.net/app/update.apk";
    public static final String APP_VERSION_TXT = "http://abzzezz.bplaced.net/app/version.txt";
    public static final String APP_CHANGELOG_TXT = "http://abzzezz.bplaced.net/app/changelog.txt";
    public static String dataBase = "https://www.anime4you.one/speedlist.old.txt";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}

