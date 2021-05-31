package com.example.remindme.viewModels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.remindme.helpers.ScheduleHelper;
import com.example.remindme.helpers.StringHelper;

import java.util.Calendar;
import java.util.Date;

public class RepeatModel extends ViewModel {

    public boolean isEnabled() {
        return !(getMultipleTimeRepeatModel().getTimeListMode() == MultipleTimeRepeatModel.TimeListModes.OFF &&
                getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.OFF);
    }

    public void setEnable(boolean value) {
        if (value) {
            if (!isEnabled()) {
                getPeriodicRepeatModel().setRepeatOption(PeriodicRepeatModel.PeriodicRepeatOptions.DAILY);
            }
        } else {
            getMultipleTimeRepeatModel().setTimeListMode(MultipleTimeRepeatModel.TimeListModes.OFF);
            getPeriodicRepeatModel().setRepeatOption(PeriodicRepeatModel.PeriodicRepeatOptions.OFF);
        }
    }

    private MultipleTimeRepeatModel multipleTimeRepeatModel;

    public MultipleTimeRepeatModel getMultipleTimeRepeatModel() {
        return multipleTimeRepeatModel;
    }

    public void setMultipleTimeRepeatModel(MultipleTimeRepeatModel multipleTimeRepeatModel) {
        this.multipleTimeRepeatModel = multipleTimeRepeatModel;
    }

    private PeriodicRepeatModel periodicRepeatModel;

    public PeriodicRepeatModel getPeriodicRepeatModel() {
        return periodicRepeatModel;
    }

    public void setPeriodicRepeatModel(PeriodicRepeatModel periodicRepeatModel) {
        this.periodicRepeatModel = periodicRepeatModel;
    }

    public RepeatModel copy() {

        RepeatModel instance = new RepeatModel(parent);

        instance.getMultipleTimeRepeatModel().setTimeListMode(getMultipleTimeRepeatModel().getTimeListMode());
        instance.getMultipleTimeRepeatModel().setTimeListHours(getMultipleTimeRepeatModel().getTimeListHours());
        instance.getMultipleTimeRepeatModel().setTimeListTimes(getMultipleTimeRepeatModel().getTimeListTimes());


        instance.getPeriodicRepeatModel().setCustomDays(getPeriodicRepeatModel().getCustomDays());
        instance.getPeriodicRepeatModel().setCustomWeeks(getPeriodicRepeatModel().getCustomWeeks());
        instance.getPeriodicRepeatModel().setCustomMonths(getPeriodicRepeatModel().getCustomMonths());
        instance.getPeriodicRepeatModel().setRepeatCustom(getPeriodicRepeatModel().getCustomTimeUnit(), getPeriodicRepeatModel().getCustomTimeValue());
        instance.getPeriodicRepeatModel().setRepeatOption(getPeriodicRepeatModel().getRepeatOption());

        instance.setHasRepeatEnd(isHasRepeatEnd());
        instance.setRepeatEndDate(getRepeatEndDate());

        return instance;

    }

    private final AlertModel parent;

    public AlertModel getParent() {
        return parent;
    }

    public RepeatModel(final AlertModel parent) {
        this.parent = parent;
        this.periodicRepeatModel = new PeriodicRepeatModel(this);
        this.multipleTimeRepeatModel = new MultipleTimeRepeatModel(this);
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

        //    if (!isEnabled()) {
        //        return false;
        //    }

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
        if (validatedScheduledTime == null)
            return null;

        final Date result = validatedScheduledTime;
        validatedScheduledTime = null;
        return result;
    }

    public boolean isValid(final TimeModel timeModel, final RepeatModel repeatModel) {
        boolean isValid = false;

        switch (repeatModel.getMultipleTimeRepeatModel().getTimeListMode()) {
            default:
                repeatModel.getMultipleTimeRepeatModel().getTimeListTimes().clear();
                repeatModel.getMultipleTimeRepeatModel().getTimeListHours().clear();
                isValid = true;
                break;
            case SELECTED_HOURS:
                if (repeatModel.getMultipleTimeRepeatModel().getTimeListHours().size() > 0) {
                    repeatModel.getMultipleTimeRepeatModel().getTimeListTimes().clear();
                    isValid = true;
                }
                break;
            case ANYTIME:
                if (repeatModel.getMultipleTimeRepeatModel().getTimeListTimes().size() > 0) {
                    repeatModel.getMultipleTimeRepeatModel().getTimeListHours().clear();
                    isValid = true;
                }
                break;
        }

        if (isValid) {
            switch (repeatModel.getPeriodicRepeatModel().getRepeatOption()) {
                default: //NONE: HOURLY, DAILY: WEEKLY: MONTHLY: YEARLY:
                    repeatModel.getPeriodicRepeatModel().getCustomDays().clear();
                    repeatModel.getPeriodicRepeatModel().getCustomWeeks().clear();
                    repeatModel.getPeriodicRepeatModel().getCustomMonths().clear();
                    // Calculate only if its not snoozed already. This calculations will occur when snooze get off or when it will be dismissed
                    isValid = true;
                    break;
                case DAILY_CUSTOM:
                    if (repeatModel.getPeriodicRepeatModel().getCustomDays().size() > 0) {
                        //customDays.clear();
                        repeatModel.getPeriodicRepeatModel().getCustomWeeks().clear();
                        repeatModel.getPeriodicRepeatModel().getCustomMonths().clear();
                        isValid = true;
                    }
                    break;
                case WEEKLY_CUSTOM:
                    if (repeatModel.getPeriodicRepeatModel().getCustomWeeks().size() > 0) {
                        repeatModel.getPeriodicRepeatModel().getCustomDays().clear();
                        //customWeeks.clear();
                        repeatModel.getPeriodicRepeatModel().getCustomMonths().clear();
                        isValid = true;
                    }
                    break;
                case MONTHLY_CUSTOM:
                    if (repeatModel.getPeriodicRepeatModel().getCustomMonths().size() > 0) {
                        repeatModel.getPeriodicRepeatModel().getCustomDays().clear();
                        repeatModel.getPeriodicRepeatModel().getCustomWeeks().clear();
                        //customMonths.clear();
                        isValid = true;
                    }
                    break;
                case OTHER:
                    if (repeatModel.getPeriodicRepeatModel().getCustomTimeValue() > 0 &&
                            repeatModel.getPeriodicRepeatModel().getCustomTimeValue() <= PeriodicRepeatModel.getMaxForTimeUnit(repeatModel.getPeriodicRepeatModel().getCustomTimeUnit())) {
                        repeatModel.getPeriodicRepeatModel().getCustomDays().clear();
                        repeatModel.getPeriodicRepeatModel().getCustomWeeks().clear();
                        repeatModel.getPeriodicRepeatModel().getCustomMonths().clear();
                        isValid = true;
                    }
                    break;
            }
        }

        if (isValid && hasRepeatEnd && repeatEndDate == null) {
            isValid = false;
        }

        if (isValid) {
            // Check if any schedule is possible with current settings
            ScheduleHelper scheduleHelper = new ScheduleHelper(timeModel, repeatModel);
            validatedScheduledTime = scheduleHelper.getNextSchedule();
            // nextTime is null means no schedule is possible
            isValid = validatedScheduledTime != null;
        }

        return isValid;
    }


    @NonNull
    public String toString(Context context) {

        if (!isEnabled()) {
            return "OFF";
        }

        final StringBuilder builder = new StringBuilder();

        switch (getMultipleTimeRepeatModel().getTimeListMode()) {
            default:
                builder.append("Once, ");
                break;
            case HOURLY:
                builder.append("On every hour ");
                break;
            case SELECTED_HOURS:
                builder.append("On selected hours ");
                break;
            case ANYTIME:
                builder.append("On different times ");
                break;
            case CUSTOM_INTERVAL:
                builder.append("On every interval ");
                break;
        }

        switch (getPeriodicRepeatModel().getRepeatOption()) {
            default:
            case OFF:
                return "for selected day ";
            case DAILY:
                builder.append("of everyday ");
                break;
            case DAILY_CUSTOM:
                builder.append("of every ");
                for (int i = 0; i < getPeriodicRepeatModel().getCustomDays().size(); i++) {
                    int value = getPeriodicRepeatModel().getCustomDays().get(i);
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
                builder.append("of every week ");
                break;
            case WEEKLY:
                builder.append("of every week ");
                break;
            case WEEKLY_CUSTOM:
                builder.append("of every ");
                for (int i = 0; i < getPeriodicRepeatModel().getCustomWeeks().size(); i++) {
                    int value = getPeriodicRepeatModel().getCustomWeeks().get(i);
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
                builder.append("week of every month ");
                break;
            case MONTHLY:
                builder.append("of every month ");
                break;
            case MONTHLY_CUSTOM:
                builder.append("of every ");
                for (int i = 0; i < getPeriodicRepeatModel().getCustomMonths().size(); i++) {
                    int value = getPeriodicRepeatModel().getCustomMonths().get(i);
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
                builder.append("of every year ");
                break;
            case YEARLY:
                builder.append("of every year ");
                break;
            case OTHER:
                builder.append("of every ");
                switch (getPeriodicRepeatModel().getCustomTimeUnit()) {
                    case DAYS:
                        if (getPeriodicRepeatModel().getCustomTimeValue() == 1)
                            builder.append("day");
                        else
                            builder.append(getPeriodicRepeatModel().getCustomTimeValue());
                        builder.append(" days");
                        break;
                    case WEEKS:
                        if (getPeriodicRepeatModel().getCustomTimeValue() == 1)
                            builder.append("week");
                        else
                            builder.append(getPeriodicRepeatModel().getCustomTimeValue());
                        builder.append(" weeks");
                        break;
                    case MONTHS:
                        if (getPeriodicRepeatModel().getCustomTimeValue() == 1)
                            builder.append("month");
                        else
                            builder.append(getPeriodicRepeatModel().getCustomTimeValue());
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

        switch (getMultipleTimeRepeatModel().getTimeListMode()) {
            default:
                builder.append("Once, ");
                break;
            case HOURLY:
                builder.append("Hourly, ");
                break;
            case SELECTED_HOURS:
                builder.append("Selected hours, ");
                break;
            case ANYTIME:
                builder.append("Different times, ");
                break;
            case CUSTOM_INTERVAL:
                builder.append("Interval, ");
                break;
        }

        switch (getPeriodicRepeatModel().getRepeatOption()) {
            default:
            case OFF:
                return "One day only";
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

}
