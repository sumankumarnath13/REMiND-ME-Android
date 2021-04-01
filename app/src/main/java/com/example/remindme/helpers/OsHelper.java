package com.example.remindme.helpers;

import android.os.Build;

public class OsHelper {
    public static boolean isForegroundServiceRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean isHeadsUpSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
