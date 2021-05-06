package com.example.remindme.viewModels;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeModel extends ViewModel {

    public enum TimeListModes {
        NONE,
        HOURLY,
        CUSTOM,
    }

    public TimeModel copy() {
        TimeModel instance = new TimeModel(parent);

        instance.time = time;
        instance.scheduledTime = scheduledTime;
        instance.timeListMode = timeListMode;
        instance.setTimeListTimes(getTimeListTimes());
        instance.setTimeListHours(getTimeListHours());

        return instance;
    }

    private final ReminderModel parent;

    public ReminderModel getParent() {
        return parent;
    }

    public TimeModel(final ReminderModel reminderModel) {
        parent = reminderModel;

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        time = calendar.getTime();

    }


    private Date time;

    public Date getTime() {
        return time;
    }

    public void setTime(final Date value, final boolean isReadFromDb) {

        if (value == null) return;

        if (isReadFromDb) {

            time = value;

            isTimeChanged = false;

        } else {

            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(value);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (time != null && !time.equals(calendar.getTime())) { // If new time is different than what was set before.

                isTimeChanged = true;

            }

            time = calendar.getTime();

            setScheduledTime(getParent().getRepeatModel().schedule(this));

        }
    }

    public void setTime(final Date value) {

        setTime(value, false);
    }


    private boolean hasScheduledTime;

    public boolean isHasScheduledTime() {
        return hasScheduledTime;
    }


    private Date scheduledTime;

    public Date getScheduledTime() {

        return scheduledTime;
    }

    public void setScheduledTime(final Date value) {

        if (value == null) {
            hasScheduledTime = false;
            return;
        }

        if (value.compareTo(time) == 0) { // Ignore if intended next schedule is same as time.
            hasScheduledTime = false;
            return;
        }

        hasScheduledTime = true;
        scheduledTime = value;
    }

    private boolean isTimeChanged;

    public boolean isTimeChanged() {
        return isTimeChanged;
    }


    public Date getAlertTime(final boolean isIncludeSnooze) {

        if (isIncludeSnooze && getParent().getSnoozeModel().isEnable() && getParent().getSnoozeModel().isSnoozed()) { // If snoozed then next alert time will use it
            return getParent().getSnoozeModel().getSnoozedTime(getTime());
        }

        if (isHasScheduledTime()) {
            return getScheduledTime();
        }

        return getTime();

    }


    private TimeListModes timeListMode = TimeListModes.NONE;

    public TimeListModes getTimeListMode() {
        return timeListMode;
    }

    public void setTimeListMode(TimeListModes timeListMode) {
        this.timeListMode = timeListMode;
    }


    private final ArrayList<Date> timeListTimes = new ArrayList<>();

    public List<Date> getTimeListTimes() {
        return timeListTimes;
    }

    public void addTimeListTime(final Date time) {
        final int foundIndex = timeListTimes.indexOf(time);
        if (foundIndex < 0) {
            timeListTimes.add(time);
        }
    }

    public void removeTimeListTime(final Date time) {
        timeListTimes.remove(time);
    }

    public void setTimeListTimes(final List<Date> times) {
        timeListTimes.clear();
        timeListTimes.addAll(times);
    }


    private final ArrayList<Integer> timeListHours = new ArrayList<>();

    public ArrayList<Integer> getTimeListHours() {
        return timeListHours;
    }

    public void addTimeListHour(final int hour) {
        final int foundIndex = timeListHours.indexOf(hour);
        if (foundIndex < 0) {
            timeListHours.add(hour);
        }
    }

    public void setTimeListHours(final List<Integer> values) {
        timeListHours.clear();
        timeListHours.addAll(values);
    }


    public static int getIntegerOfTimeListMode(TimeListModes mode) {
        switch (mode) {
            default:
            case NONE:
                return 0;
            case HOURLY:
                return 1;
            case CUSTOM:
                return 2;
        }
    }

    public static TimeListModes getTimeListModeFromInteger(int value) {
        switch (value) {
            default:
            case 0:
                return TimeListModes.NONE;
            case 1:
                return TimeListModes.HOURLY;
            case 2:
                return TimeListModes.CUSTOM;
        }
    }

}
