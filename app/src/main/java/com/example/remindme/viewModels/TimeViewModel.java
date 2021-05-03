package com.example.remindme.viewModels;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeViewModel extends ViewModel {

    public enum TimeListModes {
        NONE,
        HOURLY,
        CUSTOM,
    }

    public TimeViewModel copy() {
        TimeViewModel instance = new TimeViewModel(reminderModel);

        instance.time = time;
        instance.updatedTime = updatedTime;
        instance.timeListMode = timeListMode;
        instance.addTimes(getCustomTimes());
        instance.setHourlyTimes(getHourlyTimes());

        return instance;
    }

    private final ReminderModel reminderModel;

    public TimeViewModel(final ReminderModel parent) {
        reminderModel = parent;
    }

    private Date time;

    public void setTime(final Date value) {
        if (value == null) return;

        final Date userTime = purifyTime(value); // Remove second and milli(s) from time.

        if (time != null && !time.equals(userTime)) { // If new time is different than what was set before.

            isTimeChanged = true;
        }

        time = userTime;

        if (Calendar.getInstance().getTime().compareTo(time) >= 0) { // If the user value "effectively" is in past then calculate next schedule.
            updatedTime = reminderModel.getRepeatModel().getNextScheduleTime(time); // Given_time will be used if its not null.
        } else {
            // If not then no need of calculated time. The given time will be used.
            updatedTime = null;
        }

        purifyTimes(getUpdatedTime(), getCustomTimes());
    }

    public Date getTime() {

        if (time == null) {
            final Calendar _c = Calendar.getInstance();
            _c.add(Calendar.HOUR_OF_DAY, 1);
            return purifyTime(_c);
        }

        return time;
    }

    private Date updatedTime;

    public Date getUpdatedTime() {

        return updatedTime == null ? getTime() : updatedTime;
    }

    public void setUpdatedTime(final Date value) {
        if (value == null) return;

        updatedTime = value;

        purifyTimes(getUpdatedTime(), getCustomTimes());
    }

    private boolean isTimeChanged;

    public boolean isTimeChanged() {
        return isTimeChanged;
    }

    public boolean isTimeUpdated() {
        return updatedTime != null && time != null && !updatedTime.equals(time);
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

    public void clearTimes() {
        customTimes.clear();
    }

    public void addTime(final Date time) {
        customTimes.add(time);
        purifyTimes(getUpdatedTime(), getCustomTimes());
    }

    public void addTimes(final List<Date> values) {
        customTimes.clear();
        customTimes.addAll(values);
        purifyTimes(getUpdatedTime(), getCustomTimes());
    }

    private final ArrayList<Integer> hourlyTimes = new ArrayList<>();

    public ArrayList<Integer> getHourlyTimes() {
        return hourlyTimes;
    }

    public void setHourlyTimes(final List<Integer> values) {
        hourlyTimes.clear();
        hourlyTimes.addAll(values);
    }

    private static Date purifyTime(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return purifyTime(calendar);
    }

    private static Date purifyTime(final Calendar calendar) {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
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
