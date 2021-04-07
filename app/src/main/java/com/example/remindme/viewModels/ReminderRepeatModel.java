package com.example.remindme.viewModels;

import androidx.annotation.NonNull;

import com.example.remindme.helpers.StringHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReminderRepeatModel {
    public enum ReminderRepeatOptions {
        NONE,
        HOURLY,
        HOURLY_CUSTOM,
        DAILY,
        DAILY_CUSTOM,
        WEEKLY,
        WEEKLY_CUSTOM,
        MONTHLY,
        MONTHLY_CUSTOM,
        YEARLY,
        OTHER
    }

    public enum TimeUnits {
        DAYS,
        WEEKS,
        MONTHS,
        YEARS
    }

    public int customMinute;
    public List<Integer> customHours;
    public List<Integer> customDays;
    public List<Integer> customWeeks;
    public String weekDayName;
    public List<Integer> customMonths;

    public TimeUnits customTimeUnit = TimeUnits.DAYS;
    public int customTimeValue;
    public int customTimeHourValue;
    public int customTimeMinuteValue;

    public ReminderRepeatOptions repeatOption;

    public ReminderRepeatModel() {
        repeatOption = ReminderRepeatOptions.DAILY;
        customHours = new ArrayList<>();
        customDays = new ArrayList<>();
        customWeeks = new ArrayList<>();
        customMonths = new ArrayList<>();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        switch (repeatOption) {
            default:
            case NONE:
                builder.append("OFF");
                break;
            case HOURLY:
                builder.append("Hourly");
                break;
            case HOURLY_CUSTOM:
                builder.append("On ");
                for (int i = 0; i < customHours.size(); i++) {
                    int h = customHours.get(i);
                    if (h == 0) {
                        builder.append("12:" + customMinute + " am, ");
                    } else if (h == 12) {
                        builder.append("12:" + customMinute + " pm, ");
                    } else if (h < 12) {
                        builder.append(h + ":" + customMinute + " am, ");
                    } else {
                        builder.append(h - 11 + ":" + customMinute + " pm, ");
                    }
                }
                //builder.replace(builder.lastIndexOf(", "), builder.length(), "");
                builder.append("of every day");
                break;
            case DAILY:
                builder.append("Daily");
                break;
            case DAILY_CUSTOM:
                builder.append("On ");
                for (int i = 0; i < customDays.size(); i++) {
                    int value = customDays.get(i);
                    switch (value) {
                        default:
                        case Calendar.SUNDAY:
                            builder.append("Sun, ");
                            break;
                        case Calendar.MONDAY:
                            builder.append("Mon, ");
                            break;
                        case Calendar.TUESDAY:
                            builder.append("Tue, ");
                            break;
                        case Calendar.WEDNESDAY:
                            builder.append("Wed, ");
                            break;
                        case Calendar.THURSDAY:
                            builder.append("Thu, ");
                            break;
                        case Calendar.FRIDAY:
                            builder.append("Fri, ");
                            break;
                        case Calendar.SATURDAY:
                            builder.append("Sat, ");
                            break;
                    }
                }
                //builder.replace(builder.lastIndexOf(", "), builder.length(), "");
                builder.append(" of every week");
                break;
            case WEEKLY:
                builder.append("Weekly");
                break;
            case WEEKLY_CUSTOM:
                builder.append("On ");
                for (int i = 0; i < customWeeks.size(); i++) {
                    int value = customWeeks.get(i);
                    switch (value) {
                        default:
                        case 0:
                            builder.append("1st, ");
                            break;
                        case 1:
                            builder.append("2nd, ");
                            break;
                        case 2:
                            builder.append("3rd, ");
                            break;
                        case 3:
                            builder.append("4th, ");
                            break;
                    }
                }
                //builder.replace(builder.lastIndexOf(", "), builder.length(), "");
                builder.append("week of every month");
                break;
            case MONTHLY:
                builder.append("Monthly");
                break;
            case MONTHLY_CUSTOM:
                builder.append("On ");
                for (int i = 0; i < customMonths.size(); i++) {
                    int value = customMonths.get(i);
                    switch (value) {
                        default:
                        case Calendar.JANUARY:
                            builder.append("Jan, ");
                            break;
                        case Calendar.FEBRUARY:
                            builder.append("Feb, ");
                            break;
                        case Calendar.MARCH:
                            builder.append("Mar, ");
                            break;
                        case Calendar.APRIL:
                            builder.append("Apr, ");
                            break;
                        case Calendar.MAY:
                            builder.append("May, ");
                            break;
                        case Calendar.JUNE:
                            builder.append("Jun, ");
                            break;
                        case Calendar.JULY:
                            builder.append("Jul, ");
                            break;
                        case Calendar.AUGUST:
                            builder.append("Aug, ");
                            break;
                        case Calendar.SEPTEMBER:
                            builder.append("Sep, ");
                            break;
                        case Calendar.OCTOBER:
                            builder.append("Oct, ");
                            break;
                        case Calendar.NOVEMBER:
                            builder.append("Nov, ");
                            break;
                        case Calendar.DECEMBER:
                            builder.append("Dec, ");
                            break;
                    }
                }
                //builder.replace(builder.lastIndexOf(", "), builder.length(), "");
                builder.append("of every year");
                break;
            case YEARLY:
                builder.append("Yearly");
                break;
            case OTHER:
                builder.append("On every ");
                builder.append(customTimeValue);
                switch (customTimeUnit) {
                    case DAYS:
                        builder.append(" days");
                        break;
                    case WEEKS:
                        builder.append(" weeks");
                        break;
                    case MONTHS:
                        builder.append(" months");
                        break;
                    case YEARS:
                        builder.append(" years");
                        break;
                }
                break;
        }

        return StringHelper.trimEnd(builder.toString(), ",");
    }


}
