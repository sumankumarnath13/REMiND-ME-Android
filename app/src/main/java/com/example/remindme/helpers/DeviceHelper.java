package com.example.remindme.helpers;

import android.os.Build;

import java.util.Locale;

public class DeviceHelper {

    private DeviceHelper() {
    }

    private static DeviceHelper instance;

    public static DeviceHelper getInstance() {
        if (instance == null) {
            instance = new DeviceHelper();
        }
        return instance;
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getBrand() {
        return Build.BRAND;
    }

    public String getOperatingSystemSignature() {
        return String.format(Locale.ENGLISH, "Android %s (%d)", Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
    }

    public String getOperatingSystemUpdateSignature() {
        if (OsHelper.isMarshmallowOrLater()) {
            return String.format(Locale.ENGLISH, "Update-%s-%s", Build.VERSION.SECURITY_PATCH, Build.VERSION.INCREMENTAL);
        } else {
            return String.format(Locale.ENGLISH, "Update-%s", Build.VERSION.INCREMENTAL);
        }
    }

}
