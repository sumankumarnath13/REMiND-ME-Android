package com.example.remindme.viewModels;

import androidx.annotation.NonNull;

import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MultipleTimeRepeatModel {

    private final RepeatModel parent;

    public RepeatModel getParent() {
        return parent;
    }

    public MultipleTimeRepeatModel(final RepeatModel repeatModel) {
        this.parent = repeatModel;
    }

    public enum TimeListModes {
        OFF,
        HOURLY,
        SELECTED_HOURS,
        CUSTOM_INTERVAL,
        ANYTIME,
    }

    private TimeListModes timeListMode = TimeListModes.OFF;

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

    @NonNull
    @Override
    public String toString() {
        if (getTimeListMode() == TimeListModes.OFF) {
            return "NONE";
        }

        final StringBuilder builder = new StringBuilder();

        if (getTimeListMode() == TimeListModes.HOURLY) {

            final Calendar c = Calendar.getInstance();
            c.setTime(getParent().getParent().getTimeModel().getTime());
            final int min = c.get(Calendar.MINUTE);

            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                for (int i = 0; i < getTimeListHours().size(); i++) {
                    builder.append(StringHelper.get24(getTimeListHours().get(i), min)).append(", ");
                }
            } else {
                for (int i = 0; i < getTimeListHours().size(); i++) {
                    builder.append(StringHelper.get12(getTimeListHours().get(i), min)).append(", ");
                }
            }
        } else if (getTimeListMode() == TimeListModes.ANYTIME) {

            final Calendar c = Calendar.getInstance();

            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
                for (int i = 0; i < getTimeListTimes().size(); i++) {
                    c.setTime(getTimeListTimes().get(i));
                    builder.append(StringHelper.get24(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))).append(", ");
                }
            } else {
                for (int i = 0; i < getTimeListTimes().size(); i++) {
                    c.setTime(getTimeListTimes().get(i));
                    builder.append(StringHelper.get12(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))).append(", ");
                }
            }
        }

        return StringHelper.trimEnd(builder.toString(), ", ");
    }

    public static int getIntegerOfTimeListMode(TimeListModes mode) {
        switch (mode) {
            default:
            case OFF:
                return 0;
            case HOURLY:
                return 1;
            case SELECTED_HOURS:
                return 2;
            case CUSTOM_INTERVAL:
                return 3;
            case ANYTIME:
                return 4;
        }
    }

    public static TimeListModes getTimeListModeFromInteger(int value) {
        switch (value) {
            default:
            case 0:
                return TimeListModes.OFF;
            case 1:
                return TimeListModes.HOURLY;
            case 2:
                return TimeListModes.SELECTED_HOURS;
            case 3:
                return TimeListModes.CUSTOM_INTERVAL;
            case 4:
                return TimeListModes.ANYTIME;
        }
    }
}
