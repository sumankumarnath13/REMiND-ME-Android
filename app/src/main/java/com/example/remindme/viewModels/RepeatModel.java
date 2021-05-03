package com.example.remindme.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.remindme.helpers.StringHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RepeatModel extends ViewModel {

    private static final int DAILY = 1;
    private static final int DAILY_CUSTOM = 11;
    private static final int WEEKLY = 2;
    private static final int WEEKLY_CUSTOM = 21;
    private static final int MONTHLY = 3;
    private static final int MONTHLY_CUSTOM = 31;
    private static final int YEARLY = 4;
    private static final int OTHER = 9;

    public enum ReminderRepeatOptions {
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
        YEARS,
    }


    public RepeatModel copy() {

        RepeatModel instance = new RepeatModel(reminderModel);

        instance.setEnabled(isEnabled());
        instance.setCustomDays(getCustomDays());
        instance.setCustomWeeks(getCustomWeeks());
        instance.setCustomMonths(getCustomMonths());

        instance.setHasRepeatEnd(isHasRepeatEnd());
        instance.setRepeatCustom(getCustomTimeUnit(), getCustomTimeValue());
        instance.setRepeatEndDate(getRepeatEndDate());
        instance.setRepeatOption(getRepeatOption());

        return instance;

    }

    private final ReminderModel reminderModel;

    public RepeatModel(ReminderModel parent) {
        reminderModel = parent;
    }

    private boolean isEnabled;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        if (!enabled) {
            setHasRepeatEnd(false);
        }
    }

    private ReminderRepeatOptions repeatOption = ReminderRepeatOptions.DAILY; // DEFAULT

    public ReminderRepeatOptions getRepeatOption() {
        return repeatOption;
    }

    public void setRepeatOption(ReminderRepeatOptions value) {
        repeatOption = value;
    }

    public Date getReminderTime() {
        return reminderModel.getTimeViewModel().getUpdatedTime();
    }

//    private final List<Integer> customHours = new ArrayList<>();
//
//    public List<Integer> getCustomHours() {
//        return customHours;
//    }
//
//    public void setCustomHours(List<Integer> hours) {
//        customHours.clear();
//        customHours.addAll(hours);
//    }

    private final List<Integer> customDays = new ArrayList<>();

    public List<Integer> getCustomDays() {
        return customDays;
    }

    public void setCustomDays(final List<Integer> values) {
        customDays.clear();
        customDays.addAll(values);
    }

    private final List<Integer> customWeeks = new ArrayList<>();

    public List<Integer> getCustomWeeks() {
        return customWeeks;
    }

    public void setCustomWeeks(final List<Integer> values) {
        customWeeks.clear();
        customWeeks.addAll(values);
    }

    private final List<Integer> customMonths = new ArrayList<>();

    public List<Integer> getCustomMonths() {
        return customMonths;
    }

    public void setCustomMonths(final List<Integer> values) {
        customMonths.clear();
        customMonths.addAll(values);
    }

    private TimeUnits customTimeUnit = TimeUnits.DAYS;
    private int customTimeValue = 3;

    public void setRepeatCustom(final TimeUnits unit, final int value) {
        customTimeUnit = unit;
        customTimeValue = value;
    }

    public void setRepeatCustom(final int unit, final int value) {
        customTimeUnit = getTimeUnitFromInteger(unit);
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

        if (!isEnabled()) {
            return false;
        }

        if (repeatEndDate == null || getReminderTime() == null) { // Cannot enable without repeat end date
            return false;
        }

        // End date cannot be same or less than reminder date
        return repeatEndDate.compareTo(getReminderTime()) > 0;
    }

    private Date repeatEndDate;

    public Date getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(Date value) {
        repeatEndDate = value;
        hasRepeatEnd = isRepeatEndValid();
    }

    private Date validatedNextScheduledTime;

    public Date getValidatedNextScheduledTime() {
        if (validatedNextScheduledTime == null) return null;

        final Date result = validatedNextScheduledTime;
        validatedNextScheduledTime = null;
        return result;
    }

    public boolean isValid() {
        boolean isValid = false;

        switch (repeatOption) {
            default: //NONE: DAILY: WEEKLY: MONTHLY: YEARLY:
                customDays.clear();
                customWeeks.clear();
                customMonths.clear();
                // Calculate only if its not snoozed already. This calculations will occur when snooze get off or when it will be dismissed
                isValid = true;
                break;
            case DAILY_CUSTOM:
                if (customDays.size() > 0) {
                    //customDays.clear();
                    customWeeks.clear();
                    customMonths.clear();
                    isValid = true;
                    break;

                }
            case WEEKLY_CUSTOM:
                if (customWeeks.size() > 0) {
                    customDays.clear();
                    //customWeeks.clear();
                    customMonths.clear();
                    isValid = true;
                    break;
                }
            case MONTHLY_CUSTOM:
                if (customMonths.size() > 0) {
                    customDays.clear();
                    customWeeks.clear();
                    //customMonths.clear();
                    isValid = true;
                    break;

                }
            case OTHER:
                if (getCustomTimeValue() > 0 &&
                        getCustomTimeValue() <= getMaxForTimeUnit(getCustomTimeUnit())) {
                    customDays.clear();
                    customWeeks.clear();
                    customMonths.clear();
                    isValid = true;
                    break;
                }
        }


        if (isValid && hasRepeatEnd && repeatEndDate == null) {
            isValid = false;
        }

        if (isValid) {
            // Check if any schedule is possible with current settings
            validatedNextScheduledTime = getNextScheduleTime(reminderModel.getTimeViewModel().getTime());
            // nextTime is null means no schedule is possible
            isValid = validatedNextScheduledTime != null;
        }

        return isValid;
    }

    public Date getNextScheduleTime(final Date fromTime) {
        /*
         * This method will look for next closest date and time to repeat from reminder set time.
         * If the time is in past then it will bring the DAY of YEAR to present and then will look for next possible schedule based on repeat settings.
         * This method will return a non null value only if there is a dat can reached in future.
         * */


        final Calendar currentTimeCalendar = Calendar.getInstance();

        if (fromTime == null) return null;

        Date nextScheduleTime = null;
        final Calendar reminderCal = Calendar.getInstance();
        //Calculation to find next schedule will begin from current time
        reminderCal.setTime(fromTime);

        final int MINUTE = reminderCal.get(Calendar.MINUTE);
        final int HOUR_OF_DAY = reminderCal.get(Calendar.HOUR_OF_DAY);
        final int DAY_OF_YEAR = reminderCal.get(Calendar.DAY_OF_YEAR);

        // If the time from which it needs to calculate is in past then use current time as start point
        final Calendar baseCl = Calendar.getInstance();
        if (reminderCal.before(currentTimeCalendar)) {
            //Reminder was in past: Start calculation from present.
            baseCl.setTime(currentTimeCalendar.getTime());
        } else {
            //Reminder is in future. Start from there then.
            baseCl.setTime(fromTime);
        }

        final Calendar newScheduleCl = Calendar.getInstance();
        // Any reminder time be it base or calculated will not take seconds/mil.s. into consideration. However, current time must contain it to compare if its in past or future precisely.
        newScheduleCl.set(Calendar.SECOND, 0);
        newScheduleCl.set(Calendar.MILLISECOND, 0);

//        if (repeatOption == ReminderRepeatOptions.HOURLY) {
//            // Set alarm values to current time onwards
//            newScheduleCl.set(Calendar.MINUTE, MINUTE);
//            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
//            if (newScheduleCl.compareTo(baseCl) < 0) {
//                newScheduleCl.add(Calendar.HOUR_OF_DAY, 1);
//            }
//            nextScheduleTime = newScheduleCl.getTime();
//        } else


//        else if (repeatOption == ReminderRepeatOptions.HOURLY_CUSTOM) {
//            // Set alarm values to current time onwards
//            newScheduleCl.set(Calendar.MINUTE, MINUTE);
//            // Check when is the next closest time from onwards
//            Collections.sort(customHours);
//            //Find next schedule today
//            //nextScheduleCal.set(Calendar.HOUR_OF_DAY, 0);
//            for (int i = 0; i < customHours.size(); i++) {
//                newScheduleCl.set(Calendar.HOUR_OF_DAY, customHours.get(i));
//                if (newScheduleCl.compareTo(baseCl) >= 0) {
//                    nextScheduleTime = newScheduleCl.getTime();
//                    break;
//                }
//            }
//            if (nextScheduleTime == null) {
//                //Reset and Find next schedule tomorrow :
//                newScheduleCl.set(Calendar.HOUR_OF_DAY, 0);
//                newScheduleCl.add(Calendar.DATE, 1);
//                for (int i = 0; i < customHours.size(); i++) {
//                    newScheduleCl.set(Calendar.HOUR_OF_DAY, customHours.get(i));
//                    if (newScheduleCl.compareTo(baseCl) >= 0) {
//                        nextScheduleTime = newScheduleCl.getTime();
//                        break;
//                    }
//                }
//            }
//        }
//
        if (repeatOption == ReminderRepeatOptions.DAILY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.DAY_OF_YEAR, 1);
            }
            nextScheduleTime = newScheduleCl.getTime();
        } else if (repeatOption == ReminderRepeatOptions.WEEKLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.WEEK_OF_YEAR, 1);
            }
            nextScheduleTime = newScheduleCl.getTime();
        } else if (repeatOption == ReminderRepeatOptions.MONTHLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.MONTH, 1);
            }
            nextScheduleTime = newScheduleCl.getTime();
        } else if (repeatOption == ReminderRepeatOptions.YEARLY) {
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.YEAR, 1);
            }
            nextScheduleTime = newScheduleCl.getTime();
        } else if (repeatOption == RepeatModel.ReminderRepeatOptions.DAILY_CUSTOM) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);

            // Check when is the next closest time from onwards
            Collections.sort(customDays);
            //Find next schedule today
            //nextScheduleCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            for (int i = 0; i < customDays.size(); i++) {
                newScheduleCl.set(Calendar.DAY_OF_WEEK, customDays.get(i));
                if (newScheduleCl.compareTo(baseCl) >= 0) {
                    nextScheduleTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextScheduleTime == null) {
                //Find next schedule next week :
                newScheduleCl.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                newScheduleCl.add(Calendar.WEEK_OF_YEAR, 1);
                for (int i = 0; i < customDays.size(); i++) {
                    newScheduleCl.set(Calendar.DAY_OF_WEEK, customDays.get(i));
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextScheduleTime = newScheduleCl.getTime();
                        break;
                    }
                }
            }
        } else if (repeatOption == RepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_WEEK, Calendar.DAY_OF_WEEK);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);

            // Check when is the next closest time from onwards
            Collections.sort(customWeeks);
            //nextScheduleCal.set(Calendar.DAY_OF_MONTH, 1);
            //Find next schedule today
            for (int i = 0; i < customWeeks.size(); i++) {
                newScheduleCl.set(Calendar.WEEK_OF_MONTH, customWeeks.get(i) + 1);
                if (newScheduleCl.compareTo(baseCl) >= 0) {
                    nextScheduleTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextScheduleTime == null) {
                //Find next schedule next month :
                newScheduleCl.set(Calendar.DAY_OF_MONTH, 1);
                newScheduleCl.add(Calendar.MONTH, 1);
                for (int i = 0; i < customWeeks.size(); i++) {
                    newScheduleCl.set(Calendar.WEEK_OF_MONTH, customWeeks.get(i) + 1);
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextScheduleTime = newScheduleCl.getTime();
                        break;
                    }
                }
            }
        } else if (repeatOption == RepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);

            // Check when is the next closest time from onwards
            Collections.sort(customMonths);
            //Find next schedule today
            //nextScheduleCal.set(Calendar.MONTH, Calendar.JANUARY);
            for (int i = 0; i < customMonths.size(); i++) {
                newScheduleCl.set(Calendar.MONTH, customMonths.get(i));
                if (newScheduleCl.compareTo(baseCl) >= 0) {
                    nextScheduleTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextScheduleTime == null) {
                //Find next schedule next year :
                newScheduleCl.set(Calendar.MONTH, Calendar.JANUARY);
                newScheduleCl.add(Calendar.YEAR, 1);
                for (int i = 0; i < customMonths.size(); i++) {
                    newScheduleCl.set(Calendar.MONTH, customMonths.get(i));
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextScheduleTime = newScheduleCl.getTime();
                        break;
                    }
                }
            }
        } else if (repeatOption == RepeatModel.ReminderRepeatOptions.OTHER) {
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                switch (getCustomTimeUnit()) {
                    case DAYS:
                        newScheduleCl.add(Calendar.DAY_OF_YEAR, getCustomTimeValue());
                        break;
                    case WEEKS:
                        newScheduleCl.add(Calendar.WEEK_OF_YEAR, getCustomTimeValue());
                        break;
                    case MONTHS:
                        newScheduleCl.add(Calendar.MONTH, getCustomTimeValue());
                        break;
                    case YEARS:
                        newScheduleCl.add(Calendar.YEAR, getCustomTimeValue());
                        break;
                }
            }
            nextScheduleTime = newScheduleCl.getTime();
        }

        if (nextScheduleTime != null && getRepeatEndDate() != null) {
            return nextScheduleTime.after(getRepeatEndDate()) ? null : nextScheduleTime;
        }

        return nextScheduleTime;
    }


    @NonNull
    @Override
    public String toString() {

        if (!isEnabled()) {
            return "OFF";
        }

        StringBuilder builder = new StringBuilder();
        switch (repeatOption) {
            default:
//            case HOURLY:
//                builder.append("Hourly");
//                break;
//            case HOURLY_CUSTOM:
//                builder.append("On ");
//                for (int i = 0; i < customHours.size(); i++) {
//                    final int h = customHours.get(i);
//                    final Calendar c = Calendar.getInstance();
//                    if (getReminderTime() != null) {
//                        c.setTime(getReminderTime());
//                    }
//                    final int min = c.get(Calendar.MINUTE);
//
//                    if (AppSettingsHelper.getInstance().isUse24hourTime()) {
//                        builder.append(StringHelper.get24(h, min)).append(", ");
//                    } else {
//                        builder.append(StringHelper.get12(h, min)).append(", ");
//                    }
//                }
//                builder.append("of every day");
//                break;
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
        if (!isEnabled()) {
            return "OFF";
        }

        StringBuilder builder = new StringBuilder();
        switch (repeatOption) {
            default:
//            case HOURLY:
//                builder.append("Hourly");
//                break;
//            case HOURLY_CUSTOM:
//                builder.append("Hourly custom");
//                break;
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

    public static int getIntegerFromTimeUnit(TimeUnits unit) {
        switch (unit) {
            default:
            case DAYS:
                return 0;
            case WEEKS:
                return 1;
            case MONTHS:
                return 2;
            case YEARS:
                return 3;
        }
    }

    public static int getIntegerOfRepeatOption(ReminderRepeatOptions option) {
        switch (option) {
            default:
            case DAILY:
                return DAILY;
            case DAILY_CUSTOM:
                return DAILY_CUSTOM;
            case WEEKLY:
                return WEEKLY;
            case WEEKLY_CUSTOM:
                return WEEKLY_CUSTOM;
            case MONTHLY:
                return MONTHLY;
            case MONTHLY_CUSTOM:
                return MONTHLY_CUSTOM;
            case YEARLY:
                return YEARLY;
            case OTHER:
                return OTHER;
        }
    }

    public static ReminderRepeatOptions getRepeatOptionFromInteger(int value) {
        switch (value) {
            default:
            case DAILY:
                return ReminderRepeatOptions.DAILY;

            case DAILY_CUSTOM:
                return ReminderRepeatOptions.DAILY_CUSTOM;

            case WEEKLY:
                return ReminderRepeatOptions.WEEKLY;

            case WEEKLY_CUSTOM:
                return ReminderRepeatOptions.WEEKLY_CUSTOM;

            case MONTHLY:
                return ReminderRepeatOptions.MONTHLY;

            case MONTHLY_CUSTOM:
                return ReminderRepeatOptions.MONTHLY_CUSTOM;

            case YEARLY:
                return ReminderRepeatOptions.YEARLY;

            case OTHER:
                return ReminderRepeatOptions.OTHER;
        }
    }

    public static TimeUnits getTimeUnitFromInteger(int unit) {
        switch (unit) {
            default:
            case 0:
                return TimeUnits.DAYS;
            case 1:
                return TimeUnits.WEEKS;
            case 2:
                return TimeUnits.MONTHS;
            case 3:
                return TimeUnits.YEARS;
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
            case YEARS:
                return 3;
        }
    }
}
