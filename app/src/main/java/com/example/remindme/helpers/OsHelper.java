package com.example.remindme.helpers;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

public class OsHelper {
    public static boolean isLollipopOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isOreoMr1OrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1;
    }

    public static boolean isPostOreo() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.O;
    }

    public static boolean isOreoOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean isPOrLater() {
        return Build.VERSION.SDK_INT >= 28;
    }

    public static boolean isInteractive(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (isLollipopOrLater()) {
            return powerManager.isInteractive();
        } else {
            return false;
        }
    }
}
