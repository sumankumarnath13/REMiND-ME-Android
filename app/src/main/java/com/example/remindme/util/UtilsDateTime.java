package com.example.remindme.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsDateTime {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat timeDateFormat = new SimpleDateFormat("hh:mm a - dd MMM yy");
    public static final SimpleDateFormat uniqueDateTimeFormat = new SimpleDateFormat("yyMMddHHmm");

    public static String toTimeString(Date value) {
        return timeFormat.format(value);
    }

    public static String toDateString(Date value) {
        return dateFormat.format(value);
    }

    public static String toTimeDateString(Date value) {
        return timeDateFormat.format(value);
    }

}
