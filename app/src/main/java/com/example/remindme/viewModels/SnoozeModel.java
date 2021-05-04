package com.example.remindme.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.Date;

public class SnoozeModel extends ViewModel {

    public enum SnoozeIntervals {
        M5,
        M10,
        M15,
        M30
    }

    public enum SnoozeLimits {
        R3,
        R5,
        RC,
    }

    public SnoozeModel copy() {
        SnoozeModel instance = new SnoozeModel(parent);

        instance.setEnable(isEnable());
        instance.setInterval(getInterval());
        instance.setLimit(getLimit());
        instance.setCount(getCount());

        return instance;
    }

    private final ReminderModel parent;

    public ReminderModel getParent() {
        return parent;
    }

    public SnoozeModel(final ReminderModel reminderModel) {
        this.parent = reminderModel;
    }

    private SnoozeIntervals interval = SnoozeIntervals.M5;

    public SnoozeIntervals getInterval() {
        return interval;
    }

    public void setInterval(SnoozeIntervals intervalOption) {
        this.interval = intervalOption;
    }

    private SnoozeLimits limit = SnoozeLimits.R3;

    public SnoozeLimits getLimit() {
        return limit;
    }

    public void setLimit(SnoozeLimits limit) {
        this.limit = limit;
    }

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int value) {
        count = value;
    }

    public void addCount() {
        count++;
    }

    public void clearCount() {
        count = 0;
    }

    private boolean isEnable = true;

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean value) {
        if (value) {
            getParent().setNotification(false);
        }

        isEnable = value;
    }

    public boolean isSnoozed() {
        return count > 0;
    }

    public Date getSnoozedTime(Date fromTime) {

        Date nextTime = null;

        if (isSnoozed()) {

            final Calendar nextSnoozeOff = Calendar.getInstance();

            nextSnoozeOff.setTime(fromTime);

            switch (interval) {
                default:
                case M5:
                    nextSnoozeOff.add(Calendar.MINUTE, 5 * getCount());
                    break;
                case M10:
                    nextSnoozeOff.add(Calendar.MINUTE, 10 * getCount());
                    break;
                case M15:
                    nextSnoozeOff.add(Calendar.MINUTE, 15 * getCount());
                    break;
                case M30:
                    nextSnoozeOff.add(Calendar.MINUTE, 30 * getCount());
                    break;
            }

            nextTime = nextSnoozeOff.getTime();
        }

        return nextTime;
    }

    public Date getNextSnoozeTime(Date fromTime) {

        Date nextTime = null;

        int iteration = 0;

        switch (limit) {
            default:
            case R3:
                if (getCount() < 3) {
                    iteration = getCount() + 1;
                }
                break;
            case R5:
                if (getCount() < 5) {
                    iteration = getCount() + 1;
                }
                break;
            case RC:
                iteration = getCount() + 1;
                break;
        }

        if (iteration > 0) {

            final Calendar nextSnoozeOff = Calendar.getInstance();

            nextSnoozeOff.setTime(fromTime);

            switch (interval) {
                default:
                case M5:
                    nextSnoozeOff.add(Calendar.MINUTE, 5 * iteration);
                    break;
                case M10:
                    nextSnoozeOff.add(Calendar.MINUTE, 10 * iteration);
                    break;
                case M15:
                    nextSnoozeOff.add(Calendar.MINUTE, 15 * iteration);
                    break;
                case M30:
                    nextSnoozeOff.add(Calendar.MINUTE, 30 * iteration);
                    break;
            }

            nextTime = nextSnoozeOff.getTime();
        }

        return nextTime;
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (isEnable) {
            builder.append("Interval ");

            switch (interval) {
                default:
                case M5:
                    builder.append("5 min");
                    break;
                case M10:
                    builder.append("10 min");
                    break;
                case M15:
                    builder.append("15 min");
                    break;
                case M30:
                    builder.append("30 min");
                    break;
            }

            builder.append(", Repeat ");

            switch (limit) {
                default:
                case R3:
                    builder.append("3 times");
                    break;
                case R5:
                    builder.append("5 times");
                    break;
                case RC:
                    builder.append("Continuously");
                    break;
            }
        } else {
            builder.append("OFF");
        }

        return builder.toString();
    }

    public static int getIntegerOfSnoozeInterval(SnoozeIntervals option) {
        switch (option) {
            default:
            case M5:
                return 5;
            case M10:
                return 10;
            case M15:
                return 15;
            case M30:
                return 30;
        }
    }

    public static SnoozeIntervals getSnoozeIntervalFromInteger(int value) {
        switch (value) {
            default:
            case 5:
                return SnoozeIntervals.M5;

            case 10:
                return SnoozeIntervals.M10;

            case 15:
                return SnoozeIntervals.M15;

            case 30:
                return SnoozeIntervals.M30;
        }
    }

    public static int getIntegerOfSnoozeLimit(SnoozeLimits limit) {
        switch (limit) {
            default:
            case R3:
                return 3;
            case R5:
                return 5;
            case RC:
                return -1;
        }
    }

    public static SnoozeLimits getSnoozeLimitFromInteger(int value) {
        switch (value) {
            default:
            case 3:
                return SnoozeLimits.R3;

            case 5:
                return SnoozeLimits.R5;

            case -1:
                return SnoozeLimits.RC;
        }
    }
}
