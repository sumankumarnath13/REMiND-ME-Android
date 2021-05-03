package com.example.remindme.helpers;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringHelper {

    public static final String TIME_FORMAT_12 = "hh:mm a";
    public static final String TIME_FORMAT_24 = "HH:mm";

    public static final String ALERT_TIME_FORMAT_12 = "h:mm a";
    public static final String ALERT_TIME_FORMAT_24 = "H:mm";

    public static final String TIME_WEEKDAY_DATE_FORMAT_12 = "hh:mm a - EEE %s";
    public static final String TIME_WEEKDAY_DATE_FORMAT_24 = "HH:mm - EEE %s";

    public static final String WEEKDAY_FORMAT = "EEE";
    public static final String WEEKDAY_DATE_FORMAT = "EEE %s";


    public static String toTime(Date value) {
        if (value != null) {
            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                final SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT_24, Locale.ENGLISH);
                return format.format(value).toUpperCase();
            } else {
                final SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT_12, Locale.ENGLISH);
                return format.format(value).toUpperCase();
            }
        } else {
            return "null";
        }
    }

    public static String toAlertTime(Date value) {
        if (value != null) {
            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                final SimpleDateFormat format = new SimpleDateFormat(ALERT_TIME_FORMAT_24, Locale.ENGLISH);
                return format.format(value).toUpperCase();
            } else {
                final SimpleDateFormat format = new SimpleDateFormat(ALERT_TIME_FORMAT_12, Locale.ENGLISH);
                return format.format(value).toUpperCase();
            }
        } else {
            return "null";
        }
    }

    public static String toWeekday(Date value) {
        if (value != null) {
            final SimpleDateFormat format = new SimpleDateFormat(WEEKDAY_FORMAT, Locale.ENGLISH);
            return format.format(value).toUpperCase();
        } else {
            return "null";
        }
    }

    public static String toWeekdayDate(Context context, Date value) {
        if (value != null) {
            final SimpleDateFormat format = new SimpleDateFormat(
                    String.format(WEEKDAY_DATE_FORMAT,
                            AppSettingsHelper.getInstance().getDateFormat(context)),
                    Locale.ENGLISH);
            return format.format(value).toUpperCase();
        } else {
            return "null";
        }
    }

    public static String toTimeWeekdayDate(Context context, Date value) {
        if (value != null) {

            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                final SimpleDateFormat format = new SimpleDateFormat(
                        String.format(TIME_WEEKDAY_DATE_FORMAT_24,
                                AppSettingsHelper.getInstance().getDateFormat(context)),
                        Locale.ENGLISH);
                return format.format(value).toUpperCase();
            } else {
                final SimpleDateFormat format = new SimpleDateFormat(
                        String.format(TIME_WEEKDAY_DATE_FORMAT_12,
                                AppSettingsHelper.getInstance().getDateFormat(context)),
                        Locale.ENGLISH);
                return format.format(value).toUpperCase();
            }

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

    public static String get24(int hour, int min) {
        return String.format(Locale.ENGLISH, "%d:%02d", hour, min);
    }

    public static String get12(int hour, int min) {
        if (hour == 0) {
            return String.format(Locale.ENGLISH, "12:%02d am", min);
        } else if (hour == 12) {
            return String.format(Locale.ENGLISH, "12:%02d pm", min);
        } else if (hour < 12) {
            return String.format(Locale.ENGLISH, "%d:%02d am", hour, min);
        } else {
            return String.format(Locale.ENGLISH, "%d:%02d pm", hour - 11, min);
        }
    }

}
