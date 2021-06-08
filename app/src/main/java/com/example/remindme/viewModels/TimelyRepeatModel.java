package com.example.remindme.viewModels;

import java.util.ArrayList;
import java.util.List;

public class TimelyRepeatModel {

    private final RepeatModel parent;

    public RepeatModel getParent() {
        return parent;
    }

    public TimelyRepeatModel(RepeatModel repeatModel) {
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

    private final ArrayList<TimeOfDayModel> timeListTimes = new ArrayList<>();

    public List<TimeOfDayModel> getTimeListTimes() {
        return timeListTimes;
    }

    public void addTimeListTime(int hourOfDay, int minute) {
        final TimeOfDayModel tod = new TimeOfDayModel(hourOfDay, minute);
        final int foundIndex = timeListTimes.indexOf(tod);
        if (foundIndex < 0) {
            timeListTimes.add(tod);
        }
    }

    public void removeTimeListTime(TimeOfDayModel time) {
        timeListTimes.remove(time);
    }

    public void setTimeListTimes(List<TimeOfDayModel> times) {
        timeListTimes.clear();
        timeListTimes.addAll(times);
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
