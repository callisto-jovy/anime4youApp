/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.07.20, 16:07
 */

package net.bplaced.abzzezz.animeapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import net.bplaced.abzzezz.animeapp.util.animenotifications.AnimeNotificationService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, AnimeNotificationService.class);
            context.startService(serviceIntent);
            Toast.makeText(context, "Anime Notification service has started", Toast.LENGTH_SHORT).show();
        }
    }
}
