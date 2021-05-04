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
        instance.addTimes(getCustomTimes());
        instance.setHourlyTimes(getHourlyTimes());

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

    public Date getAlertTime(final boolean isIncludeSnooze) {

        if (isIncludeSnooze && getParent().getSnoozeModel().isEnable() && getParent().getSnoozeModel().isSnoozed()) { // If snoozed then next alert time will use it
            return getParent().getSnoozeModel().getSnoozedTime(getTime());
        }

        if (isHasScheduledTime()) {
            return getScheduledTime();
        }

        return getTime();

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

            final Calendar currentTimeCalendar = Calendar.getInstance();

            // if alert time is already in past then find next schedule
            if (currentTimeCalendar.getTime().compareTo(time) >= 0) {
                final Date nextSchedule = getParent().getRepeatModel().getNextScheduleTime(time);
                setScheduledTime(nextSchedule);
            }
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

        if (value == null) return;

        if (value.compareTo(time) == 0) { // Ignore if intended next schedule is same as time.
            return;
        }

        scheduledTime = value;

        hasScheduledTime = true;
    }

    private boolean isTimeChanged;

    public boolean isTimeChanged() {
        return isTimeChanged;
    }

    private TimeListModes timeListMode = TimeListModes.NONE;

    public TimeListModes getTimeListMode() {
        return timeListMode;
    }

    public void setTimeListMode(TimeListModes timeListMode) {
        this.timeListMode = timeListMode;
    }

    private final ArrayList<Date> customTimes = new ArrayList<>();

    public List<Date> getCustomTimes() {
        return customTimes;
    }

    public void addCustomTime(final Date time) {
        final int foundIndex = customTimes.indexOf(time);
        if (foundIndex < 0) {
            customTimes.add(time);
        }
    }

    public void removeCustomTime(final Date time) {
        customTimes.remove(time);
    }

    public void addTimes(final List<Date> values) {
        customTimes.clear();
        customTimes.addAll(values);
    }

    private final ArrayList<Integer> hourlyTimes = new ArrayList<>();

    public ArrayList<Integer> getHourlyTimes() {
        return hourlyTimes;
    }

    public void setHourlyTimes(final List<Integer> values) {
        hourlyTimes.clear();
        hourlyTimes.addAll(values);
    }

    private static void purifyTimes(final Date primaryTime, List<Date> times) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(primaryTime);
        if (times.size() > 0) {
            final int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int year = calendar.get(Calendar.YEAR);

            for (int i = 0; i < times.size(); i++) {
                calendar.setTime(times.get(i));
                calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                times.get(i).setTime(calendar.getTime().getTime());
            }
        }
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
