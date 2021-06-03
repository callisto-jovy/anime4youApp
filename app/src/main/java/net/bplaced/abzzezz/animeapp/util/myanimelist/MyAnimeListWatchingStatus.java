/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.06.21, 16:47
 */

package net.bplaced.abzzezz.animeapp.util.myanimelist;

import net.sandrohc.jikan.model.enums.UserAnimeWatchingStatus;

public enum MyAnimeListWatchingStatus {

    WATCHING(UserAnimeWatchingStatus.WATCHING, "watching", 0),
    COMPLETED(UserAnimeWatchingStatus.COMPLETED, "completed", 1),
    ON_HOLD(UserAnimeWatchingStatus.ON_HOLD, "on_hold", 2),
    DROPPED(UserAnimeWatchingStatus.DROPPED, "dropped", 3),
    PLAN_TO_WATCH(UserAnimeWatchingStatus.PLAN_TO_WATCH, "plan_to_watch", 4);


    public final UserAnimeWatchingStatus userAnimeWatchingStatus;
    public final String string;
    public final int index;

    MyAnimeListWatchingStatus(UserAnimeWatchingStatus userAnimeWatchingStatus, String string, int index) {
        this.userAnimeWatchingStatus = userAnimeWatchingStatus;
        this.string = string;
        this.index = index;
    }

    public static MyAnimeListWatchingStatus getFromIndex(int index) {
        for (MyAnimeListWatchingStatus value : values()) {
            if (value.getIndex() == index) return value;
        }
        return MyAnimeListWatchingStatus.WATCHING;
    }

    public static MyAnimeListWatchingStatus getFromStatus(UserAnimeWatchingStatus status) {
        for (MyAnimeListWatchingStatus value : values()) {
            if (status == value.getUserAnimeWatchingStatus()) return value;
        }
        return MyAnimeListWatchingStatus.WATCHING;
    }

    public int getIndex() {
        return index;
    }

    public String getString() {
        return string;
    }

    public UserAnimeWatchingStatus getUserAnimeWatchingStatus() {
        return userAnimeWatchingStatus;
    }
}
