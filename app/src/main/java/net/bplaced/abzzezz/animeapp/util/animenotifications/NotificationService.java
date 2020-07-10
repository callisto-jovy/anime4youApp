/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.07.20, 20:35
 */

package net.bplaced.abzzezz.animeapp.util.animenotifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.receiver.Alarm;


public class NotificationService extends Service {

    private final Alarm alarm = new Alarm();

    /**
     * Start alarm, cancel if set and debugging is enabled
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.log(getClass().getName() + " started", Logger.LogType.INFO);
        if (AnimeAppMain.getInstance().isDebugVersion()) alarm.cancelAlarm(getApplicationContext());
        alarm.setAlarm(getApplicationContext());
        return START_STICKY;
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.log(getClass().getName() + "ended", Logger.LogType.INFO);
    }

    /**
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}