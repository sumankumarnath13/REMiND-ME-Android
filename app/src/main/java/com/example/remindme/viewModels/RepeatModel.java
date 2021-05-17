package com.example.remindme.viewModels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.remindme.helpers.ScheduleHelper;
import com.example.remindme.helpers.StringHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RepeatModel extends ViewModel {

    private static final int HOURLY = 0;
    private static final int DAILY = 1;
    private static final int DAILY_CUSTOM = 11;
    private static final int WEEKLY = 2;
    private static final int WEEKLY_CUSTOM = 21;
    private static final int MONTHLY = 3;
    private static final int MONTHLY_CUSTOM = 31;
    private static final int YEARLY = 4;
    private static final int OTHER = 9;

    public enum ReminderRepeatOptions {
        HOURLY,
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

        RepeatModel instance = new RepeatModel(parent);

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

    private final ReminderModel parent;

    public ReminderModel getParent() {
        return parent;
    }

    public RepeatModel(final ReminderModel reminderModel) {
        this.parent = reminderModel;
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
        switch (value) {
            case DAILY_CUSTOM:
                if (getCustomDays().size() == 0) {
                    value = ReminderRepeatOptions.DAILY;
                }
                break;
            case WEEKLY_CUSTOM:
                if (getCustomWeeks().size() == 0) {
                    value = ReminderRepeatOptions.WEEKLY;
                }
                break;
            case MONTHLY_CUSTOM:
                if (getCustomMonths().size() == 0) {
                    value = ReminderRepeatOptions.MONTHLY;
                }
                break;
        }

        repeatOption = value;
    }

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

        if (repeatEndDate == null) { // Cannot enable without repeat end date
            return false;
        }

        // End date cannot be same or less than reminder date
        return repeatEndDate.compareTo(getParent().getTimeModel().getAlertTime(false)) > 0;
    }

    private Date repeatEndDate;

    public Date getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(Date value) {
        repeatEndDate = value;
        hasRepeatEnd = isRepeatEndValid();
    }

    private Date validatedScheduledTime;

    public Date getValidatedScheduledTime() {
        if (validatedScheduledTime == null) return null;

        final Date result = validatedScheduledTime;
        validatedScheduledTime = null;
        return result;
    }

    public boolean isValid(final TimeModel timeModel) {
        boolean isValid = false;

        switch (repeatOption) {
            default: //NONE: HOURLY, DAILY: WEEKLY: MONTHLY: YEARLY:
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
                }
                break;
            case WEEKLY_CUSTOM:
                if (customWeeks.size() > 0) {
                    customDays.clear();
                    //customWeeks.clear();
                    customMonths.clear();
                    isValid = true;
                }
                break;
            case MONTHLY_CUSTOM:
                if (customMonths.size() > 0) {
                    customDays.clear();
                    customWeeks.clear();
                    //customMonths.clear();
                    isValid = true;
                }
                break;
            case OTHER:
                if (getCustomTimeValue() > 0 &&
                        getCustomTimeValue() <= getMaxForTimeUnit(getCustomTimeUnit())) {
                    customDays.clear();
                    customWeeks.clear();
                    customMonths.clear();
                    isValid = true;
                }
                break;
        }

        if (isValid && hasRepeatEnd && repeatEndDate == null) {
            isValid = false;
        }

        if (isValid) {
            // Check if any schedule is possible with current settings
            validatedScheduledTime = schedule(timeModel);
            // nextTime is null means no schedule is possible
            isValid = validatedScheduledTime != null;
        }

        return isValid;
    }

    public Date schedule(final TimeModel timeModel) {
        /*
         * This method will look for next closest date and time to repeat from reminder set time.
         * If the time is in past then it will bring the DAY of YEAR to present and then will look for next possible schedule based on repeat settings.
         * This method will return a non null value only if there is a dat can reached in future.
         * */

        if (timeModel.getTime() == null)
            return null;

        if (!isEnabled()) { // If repeat is off then check if the time already passed the current time
            return new ScheduleHelper(timeModel).getNextNoRepeat();
        }

        Date nextScheduleTime = null;

        if (repeatOption == ReminderRepeatOptions.HOURLY) {

            nextScheduleTime = new ScheduleHelper(timeModel).getNextHour();

        } else if (repeatOption == ReminderRepeatOptions.DAILY) {

            nextScheduleTime = new ScheduleHelper(timeModel).getNextDay();

        } else if (repeatOption == ReminderRepeatOptions.WEEKLY) {

            nextScheduleTime = new ScheduleHelper(timeModel).getNextDayOfWeek();

        } else if (repeatOption == ReminderRepeatOptions.MONTHLY) {

            nextScheduleTime = new ScheduleHelper(timeModel).getNextDayOfMonth();

        } else if (repeatOption == ReminderRepeatOptions.YEARLY) {

            nextScheduleTime = new ScheduleHelper(timeModel).getNextYear();

        } else if (repeatOption == RepeatModel.ReminderRepeatOptions.DAILY_CUSTOM) {

            nextScheduleTime = new ScheduleHelper(timeModel).getNextDay(getCustomDays());

        } else if (repeatOption == RepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM) {

            nextScheduleTime = new ScheduleHelper(timeModel).getNextDayOfWeek(getCustomWeeks());

        } else if (repeatOption == RepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM) {

            nextScheduleTime = new ScheduleHelper(timeModel).getNextDayOfMonth(getCustomMonths());

        } else if (repeatOption == RepeatModel.ReminderRepeatOptions.OTHER) {

            nextScheduleTime = new ScheduleHelper(timeModel).getNextDayCustom(getCustomTimeUnit(), getCustomTimeValue());

        }

        if (nextScheduleTime != null && getRepeatEndDate() != null) {

            return nextScheduleTime.after(getRepeatEndDate()) ? null : nextScheduleTime;

        }

        return nextScheduleTime;
    }

    @NonNull
    public String toString(Context context) {

        if (!isEnabled()) {
            return "OFF";
        }

        StringBuilder builder = new StringBuilder();
        switch (repeatOption) {
            default:
            case HOURLY:
                builder.append("Hourly");
                break;
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
            builder.append(" till ").append(StringHelper.toTimeWeekdayDate(context, getRepeatEndDate()));
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
            case HOURLY:
                builder.append("Hourly");
                break;
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
            case HOURLY:
                return HOURLY;
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
            case HOURLY:
                return ReminderRepeatOptions.HOURLY;

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
