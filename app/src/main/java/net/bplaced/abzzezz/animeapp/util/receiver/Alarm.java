/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.07.20, 18:32
 */

package net.bplaced.abzzezz.animeapp.util.receiver;

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
import net.bplaced.abzzezz.animeapp.util.file.ShowNotifications;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DataBaseTask;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONException;
import org.json.JSONObject;

public class Alarm extends BroadcastReceiver {

    //Databasesearch instance
    private final DataBaseSearch dataBaseSearch = new DataBaseSearch();
    private final ShowNotifications showNotifications = AnimeAppMain.getInstance().getAnimeNotifications();
    //Alarm ID
    private final int alarmID = 1337;

    /**
     * Receiver trigger
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!StringHandler.isOnline(context)) return;

        final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Anime4you:tag");
        wl.acquire();

        if (AnimeAppMain.getInstance().isDebugVersion()) sendNotification(context);
        Logger.log("Checking for new episodes", Logger.LogType.INFO);
        showNotifications.getPreferences().getAll().forEach((key, o) ->
                new TaskExecutor().executeAsync(new DataBaseTask(key.split(StringUtil.splitter)[1], dataBaseSearch),
                        new TaskExecutor.Callback<JSONObject>() {
                            @Override
                            public void onComplete(JSONObject result) throws Exception {
                                int newNumber = result.getInt("episodes");
                                if (newNumber > Integer.parseInt(showNotifications.getPreferences().getString(key, "1"))) {
                                    showNotifications.updateKey(key, newNumber);
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
        final NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.information).setContentText("Alarmmanager fired")
                .setContentTitle("Alarm executed")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(14534, notificationCompat.build());
    }

    /**
     * Send anime available notification
     *
     * @param context notification context
     * @param result  Anime information
     */
    private void sendNotification(final Context context, final JSONObject result) throws JSONException {
        final NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context, AnimeAppMain.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.information).setContentText("New episode for anime: " + result.getString("Name") + " available!")
                .setContentTitle("New episode available!")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(result.getInt("episode"), notificationCompat.build());
    }

    /**
     * Set alarm
     *
     * @param context alarm context
     */
    public void setAlarm(final Context context) {
        Logger.log("Alarm set", Logger.LogType.INFO);
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, Alarm.class);
        final PendingIntent pi = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Triggers every two hours (eg. 12x a day)
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 60 * 2, pi);
    }

    /**
     * Cancel this specific alarm
     *
     * @param context alarm context
     */
    public void cancelAlarm(final Context context) {
        Logger.log("Alarm cancelled", Logger.LogType.INFO);
        final Intent intent = new Intent(context, Alarm.class);
        final PendingIntent sender = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}