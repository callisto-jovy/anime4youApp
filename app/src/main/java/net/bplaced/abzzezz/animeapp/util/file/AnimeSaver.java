/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.05.20, 19:54
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import ga.abzzezz.util.array.ArrayUtil;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class AnimeSaver {

    private final File file;
    private List<String> animeList;

    public AnimeSaver(Context context) {
        this.animeList = new ArrayList<>();
        this.file = new File(context.getFilesDir(), "animes.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        try {
            animeList = FileUtil.getFileContentAsList(file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (!animeList.isEmpty()) {
            FileUtil.writeArrayListToFile(animeList, file, false, true);
            Logger.log("Saved list!", Logger.LogType.INFO);
        }
    }

    public void add(String[] all) {
        animeList.add(all[0].replaceAll(":", "") + StringUtil.splitter + all[1] + StringUtil.splitter + all[2] + StringUtil.splitter + all[3]);
    }

    public void add(String string) {
        String[] split = string.split(StringUtil.splitter);
        animeList.add(split[0].replaceAll(":", "") + StringUtil.splitter + split[1] + StringUtil.splitter + split[2] + StringUtil.splitter + split[3]);
    }

    public boolean containsAid(String aid) {
        return getList().stream().filter(s -> s.split(StringUtil.splitter)[3].equalsIgnoreCase(aid)).count() > 0;
    }

    /**
     * Name: 0
     * Episodes: 1
     * ImageURL: 2
     * AID: 3
     *
     * @param anime
     * @return
     */
    public String[] getAll(String anime) {
        return animeList.get(ArrayUtil.indexOfKey(animeList, anime)).split(StringUtil.splitter);
    }

    public List<String> getList() {
        return animeList;
    }
}
