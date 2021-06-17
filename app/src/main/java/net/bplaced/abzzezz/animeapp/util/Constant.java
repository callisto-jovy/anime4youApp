/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 07.04.21, 13:38
 */

package net.bplaced.abzzezz.animeapp.util;

import net.bplaced.abzzezz.animeapp.util.connection.RandomUserAgent;

//TODO: Maybe move to an interface for implementation
public class Constant {

    public static final String USER_AGENT = RandomUserAgent.getRandomUserAgent();

    public static final String UPDATE_APK = "http://abzzezz.bplaced.net/app/update.apk";
    public static final String APP_VERSION_TXT = "http://abzzezz.bplaced.net/app/app_version_new.txt";
    public static final String APP_CHANGELOG_TXT = "http://abzzezz.bplaced.net/app/changelog.txt";
    public static final String IMAGE_URL = "http://abzzezz.bplaced.net/app/imgCreate.php?text=";

    public static final String SHOW_ID = "id";
    public static final String SHOW_IMAGE_URL = "image_url";
    public static final String SHOW_EPISODE_COUNT = "episodes";
    public static final String SHOW_TITLE = "title";
    public static final String SHOW_SCORE = "score";
    public static final String SHOW_EPISODES_WATCHED = "episodes_watched_0"; //Ensure backwards compatibility & no weird bugs
    public static final String SHOW_WATCHING_STATUS = "status_watching";
    public static final String SHOW_OWN_SCORE = "score_0";
}
