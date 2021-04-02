package com.example.remindme.helpers;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

public class OsHelper {
    //region Private functions
    private static boolean isL() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private static boolean isO() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.O;
    }

    private static boolean isOOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
    //endregion


    public static boolean isForegroundServiceRequired() {
        return isO();
    }

    public static boolean isHeadsUpSupported() {
        return isL();
    }

    public static boolean isHeadsUpContinued() {
        return isOOrLater();
    }

    public static boolean isInteractive(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (isL()) {
            return powerManager.isInteractive();
        } else {
            return false;
        }
    }
}
