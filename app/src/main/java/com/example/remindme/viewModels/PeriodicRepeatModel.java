package com.example.remindme.viewModels;

import java.util.ArrayList;
import java.util.List;

public class PeriodicRepeatModel {

    private static final int OFF = 0;
    private static final int DAILY = 1;
    private static final int DAILY_CUSTOM = 11;
    private static final int WEEKLY = 2;
    private static final int WEEKLY_CUSTOM = 21;
    private static final int MONTHLY = 3;
    private static final int MONTHLY_CUSTOM = 31;
    private static final int YEARLY = 4;
    private static final int OTHER = 9;

    private final RepeatModel parent;

    public RepeatModel getParent() {
        return parent;
    }

    public PeriodicRepeatModel(final RepeatModel repeatModel) {
        this.parent = repeatModel;
    }

    public enum PeriodicRepeatOptions {
        OFF,
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

    private PeriodicRepeatOptions repeatOption = PeriodicRepeatOptions.OFF; // DEFAULT

    public PeriodicRepeatOptions getRepeatOption() {
        return repeatOption;
    }

    public void setRepeatOption(PeriodicRepeatOptions value) {
        switch (value) {
            case DAILY_CUSTOM:
                if (getCustomDays().size() == 0) {
                    value = PeriodicRepeatOptions.DAILY;
                }
                break;
            case WEEKLY_CUSTOM:
                if (getCustomWeeks().size() == 0) {
                    value = PeriodicRepeatOptions.WEEKLY;
                }
                break;
            case MONTHLY_CUSTOM:
                if (getCustomMonths().size() == 0) {
                    value = PeriodicRepeatOptions.MONTHLY;
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

    public static int getIntegerOfRepeatOption(PeriodicRepeatOptions option) {
        switch (option) {
            default:
            case OFF:
                return OFF;
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

    public static PeriodicRepeatOptions getRepeatOptionFromInteger(int value) {
        switch (value) {
            default:
            case OFF:
                return PeriodicRepeatOptions.OFF;

            case DAILY:
                return PeriodicRepeatOptions.DAILY;

            case DAILY_CUSTOM:
                return PeriodicRepeatOptions.DAILY_CUSTOM;

            case WEEKLY:
                return PeriodicRepeatOptions.WEEKLY;

            case WEEKLY_CUSTOM:
                return PeriodicRepeatOptions.WEEKLY_CUSTOM;

            case MONTHLY:
                return PeriodicRepeatOptions.MONTHLY;

            case MONTHLY_CUSTOM:
                return PeriodicRepeatOptions.MONTHLY_CUSTOM;

            case YEARLY:
                return PeriodicRepeatOptions.YEARLY;

            case OTHER:
                return PeriodicRepeatOptions.OTHER;
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
