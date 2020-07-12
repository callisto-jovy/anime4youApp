/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 20:08
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import android.content.Context;
import android.net.ConnectivityManager;

public class StringHandler {


    /*
    Just for the purpose of keeping the code clean and organized ;)
     */
    public static final String CAPTCHA_ANIME_4_YOU_ONE = "https://captcha.anime4you.one";
    public static final String UPDATE_APK = "http://abzzezz.bplaced.net/app/update.apk";
    public static final String APP_VERSION_TXT = "http://abzzezz.bplaced.net/app/version.txt";
    public static final String APP_CHANGELOG_TXT = "http://abzzezz.bplaced.net/app/changelog.txt";
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 7.0; Moto C Plus) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.73 Mobile Safari/537.36";
    public static final String REQUEST_URL = "http://abzzezz.bplaced.net/app/request.php";
    public static final String USER_URL = "http://abzzezz.bplaced.net/app/user.php";
    public static final String DATABASE = "https://www.anime4you.one/speedlist.old.txt";
    public static final String BACKUP_DATABASE = "http://abzzezz.bplaced.net/list.txt";
    public static final String COVER_DATABASE = "https://cdn.anime4you.one/covers/";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


}

