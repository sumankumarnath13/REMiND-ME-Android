package com.example.remindme.viewModels;

import androidx.annotation.NonNull;

import com.example.remindme.helpers.StringHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    }

    public Date reminderTime;
    public List<Integer> customHours;
    public List<Integer> customDays;
    public List<Integer> customWeeks;
    //public String weekDayName;
    public List<Integer> customMonths;

    public TimeUnits customTimeUnit = TimeUnits.DAYS;
    public int customTimeValue;

    private boolean hasRepeatEnd;

    public boolean isHasRepeatEnd() {
        return hasRepeatEnd;
    }

    private Date repeatEndDate;

    public Date getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(Date value) {
        repeatEndDate = value;
        hasRepeatEnd = repeatEndDate != null;
    }

    public static int transform(TimeUnits unit) {
        switch (unit) {
            default:
            case DAYS:
                return 0;
            case WEEKS:
                return 1;
            case MONTHS:
                return 2;
        }
    }

    public static TimeUnits transform(int unit) {
        switch (unit) {
            default:
            case 0:
                return TimeUnits.DAYS;
            case 1:
                return TimeUnits.WEEKS;
            case 2:
                return TimeUnits.MONTHS;
        }
    }

    public static int getMaxForTimeUnit(TimeUnits unit) {
        switch (unit) {
            default:
            case DAYS:
                return 1095;
            case WEEKS:
                return 156;
            case MONTHS:
                return 36;
        }
    }

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
                    final int h = customHours.get(i);
                    final Calendar c = Calendar.getInstance();
                    if (reminderTime != null) {
                        c.setTime(reminderTime);
                    }
                    final int min = c.get(Calendar.MINUTE);

                    if (h == 0) {
                        builder.append("12:" + min + " am, ");
                    } else if (h == 12) {
                        builder.append("12:" + min + " pm, ");
                    } else if (h < 12) {
                        builder.append(h + ":" + min + " am, ");
                    } else {
                        builder.append(h - 11 + ":" + min + " pm, ");
                    }
                }
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
                builder.append("of every year");
                break;
            case YEARLY:
                builder.append("Yearly");
                break;
            case OTHER:
                builder.append("On every ");
                switch (customTimeUnit) {
                    case DAYS:
                        if (customTimeValue == 1)
                            builder.append("day");
                        else
                            builder.append(customTimeValue);
                        builder.append(" days");
                        break;
                    case WEEKS:
                        if (customTimeValue == 1)
                            builder.append("week");
                        else
                            builder.append(customTimeValue);
                        builder.append(" weeks");
                        break;
                    case MONTHS:
                        if (customTimeValue == 1)
                            builder.append("month");
                        else
                            builder.append(customTimeValue);
                        builder.append(" months");
                        break;
                }
                break;
        }

        if (getRepeatEndDate() != null) {
            builder.append(" till " + StringHelper.toTimeDate(getRepeatEndDate()));
        }

        return StringHelper.trimEnd(builder.toString(), ",");
    }


}
