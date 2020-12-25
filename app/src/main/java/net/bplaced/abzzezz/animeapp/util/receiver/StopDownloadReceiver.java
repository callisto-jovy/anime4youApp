/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.07.20, 23:35
 */

package net.bplaced.abzzezz.animeapp.util.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.tasks.IntentHelper;
import net.bplaced.abzzezz.animeapp.util.tasks.anime4you.Anime4YouDownloadTask;

public class StopDownloadReceiver extends BroadcastReceiver {

    /**
     * Gets called if stop download is triggered
     *
     * @param context
     * @param intent
     */


    @Override
    public void onReceive(Context context, Intent intent) {
        final Anime4YouDownloadTask trigger = (Anime4YouDownloadTask) IntentHelper.getObjectForKey(intent.getData().toString());
        if (!trigger.isCancelled()) {
            Logger.log("Further downloading cancelled", Logger.LogType.INFO);
            Toast.makeText(context, "Download cancelled", Toast.LENGTH_SHORT).show();
            trigger.cancel();
        }
    }
}