/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.07.20, 18:32
 */

package net.bplaced.abzzezz.animeapp.util.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import net.bplaced.abzzezz.animeapp.util.tasks.UpdateTask;

public class BootReceiver extends BroadcastReceiver {

    /**
     * Gets triggered whenever the phone is restarted, restarts alarm
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            new UpdateTask(context).executeAsync();
            Toast.makeText(context, "Anime Notification service has started", Toast.LENGTH_SHORT).show();
        }
    }
}
