package com.example.remindme.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringHelper {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yy");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat ALERT_TIME_FORMAT = new SimpleDateFormat("h:mm a");
    public static final SimpleDateFormat WEEKDAY_FORMAT = new SimpleDateFormat("EEE");
    public static final SimpleDateFormat WEEKDAY_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yy");
    public static final SimpleDateFormat TIME_DATE_FORMAT = new SimpleDateFormat("hh:mm a - EEE, dd MMM yy");
    //public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("hh:mm:ss a - dd MMM yy");
    //public static final SimpleDateFormat uniqueDateTimeFormat = new SimpleDateFormat("yyMMddHHmm");

    public static String toTime(Date value) {
        if (value != null) {
            return TIME_FORMAT.format(value);
        } else {
            return "null";
        }
    }

    public static String toAlertTime(Date value) {
        if (value != null) {
            return ALERT_TIME_FORMAT.format(value);
        } else {
            return "null";
        }
    }

    public static String toDate(Date value) {
        if (value != null) {
            return DATE_FORMAT.format(value);
        } else {
            return "null";
        }
    }

    public static String toWeekday(Date value) {
        if (value != null) {
            return WEEKDAY_FORMAT.format(value);
        } else {
            return "null";
        }
    }

    public static String toWeekdayDate(Date value) {
        if (value != null) {
            return WEEKDAY_DATE_FORMAT.format(value);
        } else {
            return "null";
        }
    }

    public static String toTimeDate(Date value) {
        if (value != null) {
            return TIME_DATE_FORMAT.format(value);
        } else {
            return "null";
        }
    }

    public static String trimEnd(String value, String x) {
        if (!isEmpty(value) && !isEmpty(x)) {
            StringBuilder stringBuilder = new StringBuilder(value);
            int lastIndex = stringBuilder.lastIndexOf(x);
            int length = stringBuilder.length();
            if (lastIndex >= 0 && length > 0) {
                stringBuilder.replace(lastIndex, length, "");
            }
            return stringBuilder.toString();
        } else {
            return value;
        }
    }

    public static boolean isEmpty(String value) {
        if (value == null) {
            return true;
        }

        value = value.replace(" ", ""); // Remove blank spaces

        if (value.length() == 0) {
            return true;
        }

        return false;
    }

}
