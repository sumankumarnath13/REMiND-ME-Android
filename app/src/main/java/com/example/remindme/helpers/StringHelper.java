package com.example.remindme.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringHelper {
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    public static final SimpleDateFormat TIME_FORMAT_24 = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    public static final SimpleDateFormat ALERT_TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    public static final SimpleDateFormat ALERT_TIME_FORMAT_24 = new SimpleDateFormat("H:mm", Locale.ENGLISH);

    public static final SimpleDateFormat TIME_WEEKDAY_DATE_FORMAT = new SimpleDateFormat("hh:mm a - EEE dd MMM yy", Locale.ENGLISH);
    public static final SimpleDateFormat TIME_WEEKDAY_DATE_FORMAT_24 = new SimpleDateFormat("HH:mm - EEE dd MMM yy", Locale.ENGLISH);

    public static final SimpleDateFormat WEEKDAY_FORMAT = new SimpleDateFormat("EEE", Locale.ENGLISH);
    public static final SimpleDateFormat WEEKDAY_DATE_FORMAT = new SimpleDateFormat("EEE dd MMM yy", Locale.ENGLISH);


    public static String toTime(Date value) {
        if (value != null) {
            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                return TIME_FORMAT_24.format(value).toUpperCase();
            } else {
                return TIME_FORMAT.format(value).toUpperCase();
            }
        } else {
            return "null";
        }
    }

    public static String toAlertTime(Date value) {
        if (value != null) {
            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                return ALERT_TIME_FORMAT_24.format(value).toUpperCase();
            } else {
                return ALERT_TIME_FORMAT.format(value).toUpperCase();
            }
        } else {
            return "null";
        }
    }

    public static String toWeekday(Date value) {
        if (value != null) {
            return WEEKDAY_FORMAT.format(value).toUpperCase();
        } else {
            return "null";
        }
    }

    public static String toWeekdayDate(Date value) {
        if (value != null) {
            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                return TIME_WEEKDAY_DATE_FORMAT_24.format(value).toUpperCase();
            } else {
                return WEEKDAY_DATE_FORMAT.format(value).toUpperCase();
            }
        } else {
            return "null";
        }
    }

    public static String toTimeWeekdayDate(Date value) {
        if (value != null) {
            return TIME_WEEKDAY_DATE_FORMAT.format(value).toUpperCase();
        } else {
            return "null";
        }
    }

    public static String trimEnd(String value, String valueToTrim) {
        if (!isNullOrEmpty(value) && !isNullOrEmpty(valueToTrim)) {
            StringBuilder stringBuilder = new StringBuilder(value);
            int lastIndex = stringBuilder.lastIndexOf(valueToTrim);
            int length = stringBuilder.length();
            if (lastIndex >= 0 && length > 0) {
                stringBuilder.replace(lastIndex, lastIndex + 1, "");
            }
            return stringBuilder.toString();
        } else {
            return value;
        }
    }

    public static boolean isNullOrEmpty(String value) {
        if (value == null) {
            return true;
        }

        if (value.length() == 0) {
            return true;
        }

        value = value.replace(" ", ""); // Remove blank spaces

        return value.length() == 0;
    }

}
