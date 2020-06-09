/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 23:44
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import android.content.Context;
import android.net.ConnectivityManager;

public class URLHandler {


    /*
    Just for the purpose of keeping the code clean and organized ;)
     */
    public static final String captchaURL = "https://captcha.anime4you.one/";
    public static final String updateURL = "http://abzzezz.bplaced.net/app/update.apk";
    public static final String dataBase = "https://www.anime4you.one/speedlist.old.txt";
    public static final String checkURL = "http://abzzezz.bplaced.net/app/version.txt";
    public static final String changelogURL = "http://abzzezz.bplaced.net/app/changelog.txt";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}

