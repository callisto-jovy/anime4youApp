/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 18:46
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import ga.abzzezz.util.array.ArrayUtil;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimeSaver {

    private File file;
    private List<String> list;

    public AnimeSaver(Context context) {
        this.list = new ArrayList<>();
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
        list = FileUtil.getFileContentAsList(file);
    }

    public void save() {
        Logger.log("Saving", Logger.LogType.INFO);
        if(!list.isEmpty()) FileUtil.writeArrayListToFile(list, file, false, true);
    }

    public void add(String name, String episode, String url, String aid) {
        list.add(name.replaceAll(":", "") + StringUtil.splitter + episode + StringUtil.splitter + url + StringUtil.splitter + aid);
    }

    public void add(String string) {
        String split[] = string.split(StringUtil.splitter);
        list.add(split[0].replaceAll(":", "") + StringUtil.splitter + split[1] + StringUtil.splitter + split[2] + StringUtil.splitter + split[3]);
    }

    /**
     * Name: 1
     * Episodes: 2
     * ImageURL: 3
     * AID: 4
     *
     * @param anime
     * @return
     */
    public String[] getAll(String anime) {
        return list.get(ArrayUtil.indexOfKey(list, anime)).split(StringUtil.splitter);
    }

    public List<String> getList() {
        return list;
    }
}
