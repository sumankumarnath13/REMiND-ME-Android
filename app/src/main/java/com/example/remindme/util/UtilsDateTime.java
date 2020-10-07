package com.example.remindme.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsDateTime {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    public static SimpleDateFormat timeDateFormat = new SimpleDateFormat("hh:mm a - dd MMM yy");
    public static SimpleDateFormat uniqueDateTimeFormat = new SimpleDateFormat("yyMMddHHmm");

    public static String toTimeString(Date value){
        return timeFormat.format(value);
    }

    public static String toDateString(Date value){
        return dateFormat.format(value);
    }

    public static String toTimeDateString(Date value){
        return timeDateFormat.format(value);
    }

    public static int toInt(Date value){
        int id = Integer.parseInt(uniqueDateTimeFormat.format(value));
        return  id;
    }

    public static Date toDate(Integer value) throws ParseException {
        Date d = uniqueDateTimeFormat.parse(value.toString());
        return  d;
    }
}
