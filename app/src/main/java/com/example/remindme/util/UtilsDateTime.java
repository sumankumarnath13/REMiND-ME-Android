package com.example.remindme.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsDateTime {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat timeDateFormat = new SimpleDateFormat("hh:mm a - dd MMM yy");
    public static final SimpleDateFormat timeStampFormat = new SimpleDateFormat("hh:mm:ss a - dd MMM yy");
    public static final SimpleDateFormat uniqueDateTimeFormat = new SimpleDateFormat("yyMMddHHmm");

    public static String toTimeString(Date value) {
        if (value != null) {
            return timeFormat.format(value);
        } else {
            return "null";
        }
    }

    public static String toDateString(Date value) {
        if (value != null) {
            return dateFormat.format(value);
        } else {
            return "null";
        }
    }

    public static String toTimeDateString(Date value) {
        if (value != null) {
            return timeDateFormat.format(value);
        } else {
            return "null";
        }
    }

    public static String toTimeStamp(Date value) {
        if (value != null) {
            return timeStampFormat.format(value);
        } else {
            return "null";
        }
    }

}
