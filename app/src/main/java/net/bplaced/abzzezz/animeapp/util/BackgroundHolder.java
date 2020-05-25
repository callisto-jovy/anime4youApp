/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 19.05.20, 22:26
 */

package net.bplaced.abzzezz.animeapp.util;


import net.bplaced.abzzezz.animeapp.R;

import java.util.HashMap;
import java.util.Random;

public class BackgroundHolder {

    public static final HashMap<Integer, Integer> backgroundAndColor = new HashMap<>();
    public static int background, color;

    public BackgroundHolder() {
        backgroundAndColor.put(R.drawable.strelizia, 0xFFB7E2EF);
        backgroundAndColor.put(R.drawable.ichigo, 0xFF6074CD);
        backgroundAndColor.put(R.drawable.darwisgame, 0xFFEA535C);
        backgroundAndColor.put(R.drawable.arifureta, 0xFFC3C6D2);
        backgroundAndColor.put(R.drawable.elfenlied, 0xFF733507);
        backgroundAndColor.put(R.drawable.akamegakill, 0xFFD51D2D);
        backgroundAndColor.put(R.drawable.highschooldxd, 0xFF072E0E);
        backgroundAndColor.put(R.drawable.demonslayer, 0xFF76C9E1);
        backgroundAndColor.put(R.drawable.classroomofthelite, 0xFF8D1C30);
        backgroundAndColor.put(R.drawable.blackbutler, 0xFF531A4A);
        backgroundAndColor.put(R.drawable.darlinginthefranxx, 0xFFEFCDC6);
        backgroundAndColor.put(R.drawable.gardenofsinners1, 0xFF194570);
        backgroundAndColor.put(R.drawable.sevendeadlysins, 0xFF501152);
        backgroundAndColor.put(R.drawable.gardenofsinners2, 0xFF7F97C0);
        backgroundAndColor.put(R.drawable.violetevergarden, 0xFFD4BAA9);
        backgroundAndColor.put(R.drawable.nger, 0xFFF6AA70);
        backgroundAndColor.put(R.drawable.deathnote, 0xFFB7A58F);
        backgroundAndColor.put(R.drawable.gardenofsinners, 0xFFB6C9D0);
        backgroundAndColor.put(R.drawable.mirainikki, 0xFFC81225);
        backgroundAndColor.put(R.drawable.striketheblood, 0xFF56B7D2);
        backgroundAndColor.put(R.drawable.rddob, 0xFF7474AC);
        backgroundAndColor.put(R.drawable.yourlieinapril, 0xFFE7B38C);
        backgroundAndColor.put(R.drawable.steinsgate, 0xFFD6B86D);
        backgroundAndColor.put(R.drawable.trinityseven, 0xFF484848);
        backgroundAndColor.put(R.drawable.blackclover, 0xFF60080A);
        backgroundAndColor.put(R.drawable.ditf, 0xFFC75D7E);
        backgroundAndColor.put(R.drawable.loveiswar, 0xFFD94B62);
        backgroundAndColor.put(R.drawable.kanjo, 0xFFFACFB9);
        backgroundAndColor.put(R.drawable.another, 0xFFE87479);
    }

    public static void shuffle() {
        int randomNumber = new Random().nextInt(backgroundAndColor.size());
        background = (int) backgroundAndColor.keySet().toArray()[randomNumber];
        color = Integer.valueOf(backgroundAndColor.get(background));
    }

}
