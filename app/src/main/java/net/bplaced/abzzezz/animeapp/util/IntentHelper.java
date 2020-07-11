/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 04.07.20, 00:16
 */

package net.bplaced.abzzezz.animeapp.util;

import java.util.Hashtable;

/**
 * Curtsy of https://stackoverflow.com/users/917553/roger-sanoli
 * I fuCKING LOVE YOU. FUCK YOU ANDROID FOR NOT LETTING ME PASS OBJECTS THROUGH INTENTS!!!!!!!!!!
 */
public class IntentHelper {

    private static IntentHelper _instance;
    private final Hashtable<String, Object> _hash;

    private IntentHelper() {
        _hash = new Hashtable<>();
    }

    private static IntentHelper getInstance() {
        if (_instance == null) {
            _instance = new IntentHelper();
        }
        return _instance;
    }

    public static void addObjectForKey(Object object, String key) {
        getInstance()._hash.put(key, object);
    }

    public static Object getObjectForKey(String key) {
        final IntentHelper helper = getInstance();
        final Object data = helper._hash.get(key);
        helper._hash.remove(key);
        return data;
    }
}