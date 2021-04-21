package com.example.remindme.viewModels;

import androidx.annotation.NonNull;

import com.example.remindme.helpers.StringHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderRepeatModel {
    public enum ReminderRepeatOptions {
        OFF,
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

    private ReminderRepeatOptions repeatOption;

    public ReminderRepeatOptions getRepeatOption() {
        return repeatOption;
    }

    public void setRepeatOption(ReminderRepeatOptions value) {
        repeatOption = value;
        if (repeatOption == ReminderRepeatOptions.OFF) {
            setHasRepeatEnd(false);
        }
    }

    private Date reminderTime;

    public Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Date value) {
        reminderTime = value;
    }

    public void setReminderTime(Calendar value) {
        reminderTime = value.getTime();
    }

    public final List<Integer> customHours;
    public final List<Integer> customDays;
    public final List<Integer> customWeeks;
    public final List<Integer> customMonths;

    private TimeUnits customTimeUnit = TimeUnits.DAYS;
    private int customTimeValue = 3;

    public void setRepeatCustom(TimeUnits unit, int value) {
        customTimeUnit = unit;
        customTimeValue = value;
    }

    public void setRepeatCustom(int unit, int value) {
        customTimeUnit = transform(unit);
        customTimeValue = value;
    }

    public TimeUnits getCustomTimeUnit() {
        return customTimeUnit;
    }

    public int getCustomTimeValue() {
        return customTimeValue;
    }

    private boolean hasRepeatEnd;

    public boolean isHasRepeatEnd() {
        return hasRepeatEnd;
    }

    public void setHasRepeatEnd(boolean isEnabled) {
        if (isEnabled && !isRepeatEndValid()) {
            hasRepeatEnd = false;
            return;
        }

        hasRepeatEnd = isEnabled;
    }

    private boolean isRepeatEndValid() {

        if (getRepeatOption() == ReminderRepeatOptions.OFF) {
            return false;
        }

        if (repeatEndDate == null || reminderTime == null) { // Cannot enable without repeat end date
            return false;
        }

        // End date cannot be same or less than reminder date
        return repeatEndDate.compareTo(reminderTime) > 0;
    }

    private Date repeatEndDate;

    public Date getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(Date value) {
        repeatEndDate = value;
        hasRepeatEnd = isRepeatEndValid();
    }

    public ReminderRepeatModel() {
        repeatOption = ReminderRepeatOptions.OFF;
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
            case OFF:
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
                        builder.append("12:").append(min).append(" am, ");
                    } else if (h == 12) {
                        builder.append("12:").append(min).append(" pm, ");
                    } else if (h < 12) {
                        builder.append(h).append(":").append(min).append(" am, ");
                    } else {
                        builder.append(h - 11).append(":").append(min).append(" pm, ");
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
                        case 4:
                            builder.append("5th (If exists), ");
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

        if (isHasRepeatEnd()) {
            builder.append(" till ").append(StringHelper.toTimeWeekdayDate(getRepeatEndDate()));
        }

        return StringHelper.trimEnd(builder.toString(), ",");
    }

    public String toShortString() {
        StringBuilder builder = new StringBuilder();
        switch (repeatOption) {
            default:
            case OFF:
                builder.append("OFF");
                break;
            case HOURLY:
                builder.append("Hourly");
                break;
            case HOURLY_CUSTOM:
                builder.append("Hourly custom");
                break;
            case DAILY:
                builder.append("Daily");
                break;
            case DAILY_CUSTOM:
                builder.append("Daily custom");
                break;
            case WEEKLY:
                builder.append("Weekly");
                break;
            case WEEKLY_CUSTOM:
                builder.append("Weekly custom");
                break;
            case MONTHLY:
                builder.append("Monthly");
                break;
            case MONTHLY_CUSTOM:
                builder.append("Monthly custom");
                break;
            case YEARLY:
                builder.append("Yearly");
                break;
            case OTHER:
                builder.append("Other");
                break;
        }

        return builder.toString();
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
}
