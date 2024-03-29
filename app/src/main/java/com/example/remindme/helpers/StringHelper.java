package com.example.remindme.helpers;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringHelper {

    public static final String TIME_FORMAT_12 = "hh:mm";
    public static final String TIME_FORMAT_AM_PM = "a";
    public static final String TIME_FORMAT_12_AM_PM = "hh:mm a";
    public static final String TIME_FORMAT_24 = "HH:mm";

    public static final String ALERT_TIME_FORMAT_12 = "h:mm:ss a";
    public static final String ALERT_TIME_FORMAT_24 = "H:mm:ss";

    public static final String TIME_WEEKDAY_DATE_FORMAT_12 = "hh:mm a - EEE, %s";
    public static final String TIME_WEEKDAY_DATE_FORMAT_24 = "HH:mm - EEE, %s";

    public static final String WEEKDAY_FORMAT = "EEE";
    public static final String FULL_WEEKDAY_FORMAT = "EEEE";
    public static final String WEEKDAY_DATE_FORMAT = "EEE, %s";

    public static String toTime(Date value) {
        if (value != null) {
            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                final SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT_24, Locale.getDefault());
                return format.format(value);
            } else {
                final SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT_12, Locale.getDefault());
                return format.format(value);
            }
        } else {
            return "null";
        }
    }

    public static String toAmPm(Date value) {
        if (value != null) {
            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                return "";
            } else {
                final SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT_AM_PM, Locale.getDefault());
                return format.format(value).toUpperCase();
            }
        } else {
            return "null";
        }
    }

    public static String toTimeAmPm(Date value) {
        if (value != null) {
            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                final SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT_24, Locale.getDefault());
                return format.format(value);
            } else {
                final SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT_12_AM_PM, Locale.getDefault());
                return format.format(value).toUpperCase();
            }
        } else {
            return "null";
        }
    }

    public static String toAlertTime(Date value) {
        if (value != null) {
            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                final SimpleDateFormat format = new SimpleDateFormat(ALERT_TIME_FORMAT_24, Locale.getDefault());
                return format.format(value);
            } else {
                final SimpleDateFormat format = new SimpleDateFormat(ALERT_TIME_FORMAT_12, Locale.getDefault());
                return format.format(value).toUpperCase();
            }
        } else {
            return "null";
        }
    }

    public static String toWeekday(Date value) {
        if (value != null) {
            final SimpleDateFormat format = new SimpleDateFormat(WEEKDAY_FORMAT, Locale.getDefault());
            return format.format(value);
        } else {
            return "null";
        }
    }

    public static String toFullWeekday(Date value) {
        if (value != null) {
            final SimpleDateFormat format = new SimpleDateFormat(FULL_WEEKDAY_FORMAT, Locale.getDefault());
            return format.format(value);
        } else {
            return "null";
        }
    }

    public static String toWeekdayDate(Context context, Date value) {
        if (value != null) {
            final SimpleDateFormat format = new SimpleDateFormat(
                    String.format(Locale.getDefault(), WEEKDAY_DATE_FORMAT,
                            AppSettingsHelper.getInstance().getDateFormat(context)),
                    Locale.getDefault());
            return format.format(value);
        } else {
            return "null";
        }
    }

    public static String toTimeWeekdayDate(Context context, Date value) {
        if (value != null) {

            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                final SimpleDateFormat format = new SimpleDateFormat(
                        String.format(Locale.getDefault(), TIME_WEEKDAY_DATE_FORMAT_24,
                                AppSettingsHelper.getInstance().getDateFormat(context)),
                        Locale.getDefault());
                return format.format(value);
            } else {
                final SimpleDateFormat format = new SimpleDateFormat(
                        String.format(Locale.getDefault(), TIME_WEEKDAY_DATE_FORMAT_12,
                                AppSettingsHelper.getInstance().getDateFormat(context)),
                        Locale.getDefault());
                return format.format(value);
            }

        } else {
            return "null";
        }
    }

    public static String trimEnd(String value, String valueToTrim) {
        if (!isNullOrEmpty(value) && !isNullOrEmpty(valueToTrim)) {
            final StringBuilder stringBuilder = new StringBuilder(value);
            int lastIndex = stringBuilder.lastIndexOf(valueToTrim);
            int length = stringBuilder.length();

            if (lastIndex >= 0 && length > 0) {
                if (length - lastIndex == valueToTrim.length()) {
                    stringBuilder.replace(lastIndex, lastIndex + 1, "");
                }
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
        return String.format(Locale.getDefault(), "%d:%02d", hour, min);
    }

    public static String get12(int hour, int min) {
        if (hour == 0) {
            return String.format(Locale.getDefault(), "12:%02d am", min);
        } else if (hour == 12) {
            return String.format(Locale.getDefault(), "12:%02d pm", min);
        } else if (hour < 12) {
            return String.format(Locale.getDefault(), "%d:%02d am", hour, min);
        } else {
            return String.format(Locale.getDefault(), "%d:%02d pm", hour - 12, min);
        }
    }

}
