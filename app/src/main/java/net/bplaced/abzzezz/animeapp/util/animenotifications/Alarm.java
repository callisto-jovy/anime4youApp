/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.07.20, 17:07
 */

package net.bplaced.abzzezz.animeapp.util.animenotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.file.AnimeNotifications;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DataBaseTask;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

public class Alarm extends BroadcastReceiver {

    private final DataBaseSearch dataBaseSearch = new DataBaseSearch();
    private final AnimeNotifications animeNotifications = AnimeAppMain.getInstance().getAnimeNotifications();
    private final int alarmID = 1337;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!URLHandler.isOnline(context)) return;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Anime4you:tag");
        wl.acquire();

        if (AnimeAppMain.getInstance().isDebugVersion()) sendNotification(context);
        Logger.log("Checking for new episodes", Logger.LogType.INFO);
        animeNotifications.getPreferences().getAll().forEach((key, o) ->
                new TaskExecutor().executeAsync(new DataBaseTask(key.split(StringUtil.splitter)[1], dataBaseSearch),
                        new TaskExecutor.Callback<String[]>() {
                            @Override
                            public void onComplete(String[] result) {
                                if (Integer.parseInt(result[1]) > Integer.parseInt(animeNotifications.getPreferences().getString(key, "1"))) {
                                    animeNotifications.updateKey(result[0] + StringUtil.splitter + result[3], result[1]);
                                    sendNotification(context, result);
                                }
                            }

                            @Override
                            public void preExecute() {
                            }
                        }));
        wl.release();
    }

    /*
    For debug purposes
     */
    private void sendNotification(final Context context) {
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.information).setContentText("Alarmmanager fired")
                .setContentTitle("Alarm executed")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(14534, notificationCompat.build());
    }

    /**
     * Send anime available notification
     *
     * @param context notification context
     * @param result  Anime information
     */
    private void sendNotification(final Context context, final String[] result) {
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.information).setContentText("New episode for anime: " + result[0] + " available!")
                .setContentTitle("New episode available!")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(Integer.parseInt(result[3]), notificationCompat.build());
    }

    /**
     * Set alarm
     *
     * @param context alarm context
     */
    public void setAlarm(final Context context) {
        Logger.log("Alarm set", Logger.LogType.INFO);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //TODO: Set real time
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 60 * 2, pi);
    }

    /**
     * Cancel this specific alarm
     *
     * @param context alarm context
     */
    public void cancelAlarm(final Context context) {
        Logger.log("Alarm cancelled", Logger.LogType.INFO);
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}