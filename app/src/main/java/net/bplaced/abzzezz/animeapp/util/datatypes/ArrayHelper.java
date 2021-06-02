/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.06.21, 21:56
 */

package net.bplaced.abzzezz.animeapp.util.datatypes;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ArrayHelper {

    public static int getTotalTruthsInArray(final boolean[] array) {
        int i = 0;
        for (final boolean b : array) {
            if (b) i++;
        }
        return i;
    }

    public static Map<String, String> stringArrayToMap(final String[]... requestProperties) {
        return Arrays.stream(requestProperties)
                .collect(
                        Collectors.toMap(
                                key -> key[0],
                                value -> value[1])
                );
    }

}
