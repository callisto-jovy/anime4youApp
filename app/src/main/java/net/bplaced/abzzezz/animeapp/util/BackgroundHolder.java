/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 18:40
 */

package net.bplaced.abzzezz.animeapp.util;


import net.bplaced.abzzezz.animeapp.R;

import java.util.ArrayList;
import java.util.Random;

public class BackgroundHolder {

    private static final ArrayList<Integer> backgroundAndColor = new ArrayList<>();
    public static int background;

    public static void setup() {
        backgroundAndColor.add(R.drawable.darwisgame);
        backgroundAndColor.add(R.drawable.akamegakill);
        backgroundAndColor.add(R.drawable.demonslayer);
        backgroundAndColor.add(R.drawable.classroomofthelite);
        backgroundAndColor.add(R.drawable.blackbutler);
        backgroundAndColor.add(R.drawable.darlinginthefranxx);
        backgroundAndColor.add(R.drawable.gardenofsinners1);
        backgroundAndColor.add(R.drawable.sevendeadlysins);
        backgroundAndColor.add(R.drawable.gardenofsinners2);
        backgroundAndColor.add(R.drawable.violetevergarden);
        backgroundAndColor.add(R.drawable.nger);
        backgroundAndColor.add(R.drawable.deathnote);
        backgroundAndColor.add(R.drawable.gardenofsinners);
        backgroundAndColor.add(R.drawable.mirainikki);
        backgroundAndColor.add(R.drawable.striketheblood);
        backgroundAndColor.add(R.drawable.rddob);
        backgroundAndColor.add(R.drawable.yourlieinapril);
        backgroundAndColor.add(R.drawable.steinsgate);
        backgroundAndColor.add(R.drawable.trinityseven);
        backgroundAndColor.add(R.drawable.blackclover);
        backgroundAndColor.add(R.drawable.ditf);
        backgroundAndColor.add(R.drawable.loveiswar);
        backgroundAndColor.add(R.drawable.another);
        backgroundAndColor.add(R.drawable.another);
        backgroundAndColor.add(R.drawable.bakemonogatari);
        backgroundAndColor.add(R.drawable.cowboybebop);
        backgroundAndColor.add(R.drawable.mushishi);
    }

    public static void shuffle() {
        int randomNumber = new Random().nextInt(backgroundAndColor.size());
        background = backgroundAndColor.get(randomNumber);
    }

}
