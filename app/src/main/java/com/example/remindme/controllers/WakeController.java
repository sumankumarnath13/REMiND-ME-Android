package com.example.remindme.controllers;

import android.content.Context;
import android.os.PowerManager;

import com.example.remindme.viewModels.ReminderModel;

public class WakeController {
    private static final String WAKE_TAG = ":p58DBS2fay";
    private static PowerManager.WakeLock wakeLock;


    public static void acquireWake(Context context) {
        if (wakeLock != null) {
            return;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_TAG);
        wakeLock.acquire(ReminderModel.RING_DURATION);

    }

    public static void releaseWake() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

}
