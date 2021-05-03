package com.example.remindme.helpers;

import android.content.Context;
import android.os.PowerManager;

import com.example.remindme.viewModels.RingingModel;

public class WakeLockHelper {
    private static final String WAKE_TAG = "dingDong::p58DBS2fay";
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context context) {
        if (wakeLock != null && wakeLock.isHeld()) {
            return;
        }

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_TAG);
        wakeLock.acquire(RingingModel.MAX_RING_DURATION);
    }

    public static void release() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
