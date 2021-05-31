package com.example.remindme.helpers;

import com.example.remindme.viewModels.MultipleTimeRepeatModel;
import com.example.remindme.viewModels.PeriodicRepeatModel;
import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.TimeModel;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ScheduleHelper {

    private final int alertYear;
    private final int alertDayOfYear;
    private final int alertDayOfWeek;
    private final int alertDayOfMonth;
    private final int alertHourOfDay;
    private final int alertMinute;

    private final int currentYear;
    private final int currentDayOfYear;
    private final int currentHourOfDay;
    private final int currentMinute;

    private final Calendar calculator;
    private final Date currentTime;

    private final TimeModel timeModel;

    private TimeModel getTimeModel() {
        return timeModel;
    }

    private final RepeatModel repeatModel;

    private RepeatModel getRepeatModel() {
        return repeatModel;
    }

    private PeriodicRepeatModel getPeriodicRepeatModel() {
        return getRepeatModel().getPeriodicRepeatModel();
    }

    private MultipleTimeRepeatModel getMultipleTimeRepeatModel() {
        return getRepeatModel().getMultipleTimeRepeatModel();
    }

    public ScheduleHelper(final TimeModel timeModel, final RepeatModel repeatModel) {

        this.timeModel = timeModel;
        this.repeatModel = repeatModel;

        calculator = Calendar.getInstance();
        currentTime = calculator.getTime();

        currentYear = calculator.get(Calendar.YEAR);
        currentDayOfYear = calculator.get(Calendar.DAY_OF_YEAR);
        currentHourOfDay = calculator.get(Calendar.HOUR_OF_DAY);
        currentMinute = calculator.get(Calendar.MINUTE);

        calculator.setTime(timeModel.getTime());

        alertYear = calculator.get(Calendar.YEAR);
        alertDayOfYear = calculator.get(Calendar.DAY_OF_YEAR);
        alertDayOfMonth = calculator.get(Calendar.DAY_OF_MONTH);
        alertDayOfWeek = calculator.get(Calendar.DAY_OF_WEEK);
        alertHourOfDay = calculator.get(Calendar.HOUR_OF_DAY);
        alertMinute = calculator.get(Calendar.MINUTE);

        // After it took all time fragments to variables bring it to the current time
        if (calculator.getTime().compareTo(currentTime) <= 0) {
            calculator.setTime(currentTime);
        }

        calculator.set(Calendar.SECOND, 0);
        calculator.set(Calendar.MILLISECOND, 0);
        calculator.setFirstDayOfWeek(AppSettingsHelper.getInstance().getFirstDayOfWeek());

    }

    private void scheduleListTime() {

        if (getMultipleTimeRepeatModel().getTimeListMode() != MultipleTimeRepeatModel.TimeListModes.OFF) {

            Date firstTimeListTime = null;

            if (getMultipleTimeRepeatModel().getTimeListMode() == MultipleTimeRepeatModel.TimeListModes.HOURLY) {

                calculator.set(Calendar.MINUTE, alertMinute);

                if (calculator.getTime().compareTo(currentTime) <= 0) {
                    calculator.add(Calendar.HOUR_OF_DAY, 1);
                }

                //calculator.add(Calendar.HOUR_OF_DAY, 1);

            } else if (getMultipleTimeRepeatModel().getTimeListMode() == MultipleTimeRepeatModel.TimeListModes.SELECTED_HOURS
                    && getMultipleTimeRepeatModel().getTimeListHours().size() > 0) {

                calculator.set(Calendar.MINUTE, alertMinute);

                final List<Integer> timeListHours = getMultipleTimeRepeatModel().getTimeListHours();

                Collections.sort(timeListHours);

                for (int i = 0; i < timeListHours.size(); i++) {

                    calculator.set(Calendar.HOUR_OF_DAY, timeListHours.get(i));

                    if (i == 0) {
                        firstTimeListTime = calculator.getTime();
                    }

                    if (calculator.getTime().compareTo(currentTime) > 0) {
                        firstTimeListTime = null;
                        break;
                    }
                }

            } else if (getMultipleTimeRepeatModel().getTimeListMode() == MultipleTimeRepeatModel.TimeListModes.ANYTIME
                    && getMultipleTimeRepeatModel().getTimeListTimes().size() > 0) {

                final List<Date> timeListTimes = getMultipleTimeRepeatModel().getTimeListTimes();

                Collections.sort(timeListTimes);

                final Calendar time = Calendar.getInstance();

                for (int i = 0; i < timeListTimes.size(); i++) {

                    time.setTime(timeListTimes.get(i));

                    final int HOUR = time.get(Calendar.HOUR_OF_DAY);
                    final int MIN = time.get(Calendar.MINUTE);

                    calculator.set(Calendar.HOUR_OF_DAY, HOUR);
                    calculator.set(Calendar.MINUTE, MIN);

                    if (i == 0) {
                        firstTimeListTime = calculator.getTime();
                    }

                    if (calculator.getTime().compareTo(currentTime) > 0) {
                        firstTimeListTime = null;
                        break;
                    }
                }
            }

            // Else, It came this far means no future time found from Time List.
            // SO, use the smallest time to start on next day/week/month/year ...
            if (firstTimeListTime != null) {

                calculator.setTime(firstTimeListTime);

            }
        }
    }

    private Date getNextNoPeriodicRepeat() {

        scheduleListTime(); // Try to find if any tme available from time list for the day

        if (calculator.getTime().compareTo(currentTime) <= 0) { // Then no time found

            return null;

        }

        return calculator.getTime();
    }

//    private Date getNextHour() {
//        calculator.set(Calendar.MINUTE, alertMinute);
//
//        // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
//        if (calculator.getTime().compareTo(currentTime) <= 0) {
//            calculator.add(Calendar.HOUR_OF_DAY, 1);
//        }
//
//        return calculator.getTime();
//    }

    private Date getNextDay() {
        calculator.set(Calendar.HOUR_OF_DAY, alertHourOfDay);
        calculator.set(Calendar.MINUTE, alertMinute);

        scheduleListTime();

        if (calculator.getTime().compareTo(currentTime) <= 0) {

            calculator.add(Calendar.DAY_OF_YEAR, 1);

            scheduleListTime();

        }

        return calculator.getTime();
    }

    private Date getNextDay(final List<Integer> daysOfWeek) {
        calculator.set(Calendar.HOUR_OF_DAY, alertHourOfDay);
        calculator.set(Calendar.MINUTE, alertMinute);

        // Check when is the next closest time from onwards
        Collections.sort(daysOfWeek);

        // Find next schedule this week
        for (int i = 0; i < daysOfWeek.size(); i++) {
            calculator.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i));
            if (calculator.getTime().compareTo(currentTime) >= 0) {
                scheduleListTime();
                if (calculator.getTime().compareTo(currentTime) > 0) {
                    break;
                }
            }
        }

        if (calculator.getTime().compareTo(currentTime) <= 0) { // Still in past. Find schedule on next week
            // Find next schedule next week :
            calculator.set(Calendar.DAY_OF_WEEK, AppSettingsHelper.getInstance().getFirstDayOfWeek());
            calculator.add(Calendar.WEEK_OF_YEAR, 1);
            for (int i = 0; i < daysOfWeek.size(); i++) {
                calculator.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i));
                if (calculator.getTime().compareTo(currentTime) >= 0) {
                    scheduleListTime();
                    if (calculator.getTime().compareTo(currentTime) > 0) {
                        break;
                    }
                }
            }
        }

        return calculator.getTime();
    }

    private Date getNextDayOfWeek() {
        calculator.set(Calendar.DAY_OF_WEEK, alertDayOfWeek);
        calculator.set(Calendar.HOUR_OF_DAY, alertHourOfDay);
        calculator.set(Calendar.MINUTE, alertMinute);

        scheduleListTime();

        if (calculator.getTime().compareTo(currentTime) <= 0) {

            calculator.add(Calendar.WEEK_OF_YEAR, 1);

            scheduleListTime();
        }

        return calculator.getTime();
    }

    private Date getNextDayOfWeek(final List<Integer> weeksOfMonth) {

        calculator.set(Calendar.DAY_OF_WEEK, alertDayOfWeek);
        calculator.set(Calendar.HOUR_OF_DAY, alertHourOfDay);
        calculator.set(Calendar.MINUTE, alertMinute);

        Collections.sort(weeksOfMonth);

        //Find next schedule today
        for (int i = 0; i < weeksOfMonth.size(); i++) {
            calculator.set(Calendar.WEEK_OF_MONTH, weeksOfMonth.get(i) + 1);
            if (calculator.getTime().compareTo(currentTime) >= 0) {
                scheduleListTime();
                if (calculator.getTime().compareTo(currentTime) > 0) {
                    break;
                }
            }
        }

        if (calculator.getTime().compareTo(currentTime) <= 0) { // Still in past
            //Find next schedule next month :
            calculator.set(Calendar.DAY_OF_MONTH, 1);
            calculator.add(Calendar.MONTH, 1);
            for (int i = 0; i < weeksOfMonth.size(); i++) {
                calculator.set(Calendar.WEEK_OF_MONTH, weeksOfMonth.get(i) + 1);
                if (calculator.getTime().compareTo(currentTime) >= 0) {
                    scheduleListTime();
                    if (calculator.getTime().compareTo(currentTime) > 0) {
                        break;
                    }
                }
            }
        }

        return calculator.getTime();
    }

    private Date getNextDayOfMonth() {

        calculator.set(Calendar.DAY_OF_MONTH, alertDayOfMonth);
        calculator.set(Calendar.HOUR_OF_DAY, alertHourOfDay);
        calculator.set(Calendar.MINUTE, alertMinute);

        scheduleListTime();

        if (calculator.getTime().compareTo(currentTime) <= 0) {

            calculator.add(Calendar.MONTH, 1);

            scheduleListTime();

        }

        return calculator.getTime();
    }

    private Date getNextDayOfMonth(final List<Integer> monthsOfYear) {

        calculator.set(Calendar.DAY_OF_MONTH, alertDayOfMonth);
        calculator.set(Calendar.HOUR_OF_DAY, alertHourOfDay);
        calculator.set(Calendar.MINUTE, alertMinute);

        Collections.sort(monthsOfYear);

        //Find next schedule today
        for (int i = 0; i < monthsOfYear.size(); i++) {
            calculator.set(Calendar.MONTH, monthsOfYear.get(i));
            if (calculator.getTime().compareTo(currentTime) >= 0) {
                scheduleListTime();
                if (calculator.getTime().compareTo(currentTime) > 0) {
                    break;
                }
            }
        }

        if (calculator.getTime().compareTo(currentTime) <= 0) { // Still in past
            //Find next schedule next year :

            calculator.set(Calendar.MONTH, Calendar.JANUARY);
            calculator.add(Calendar.YEAR, 1);
            for (int i = 0; i < monthsOfYear.size(); i++) {
                calculator.set(Calendar.MONTH, monthsOfYear.get(i));
                if (calculator.getTime().compareTo(currentTime) >= 0) {
                    scheduleListTime();
                    if (calculator.getTime().compareTo(currentTime) > 0) {
                        break;
                    }
                }
            }
        }

        return calculator.getTime();
    }

    private Date getNextYear() {

        //calculator.set(Calendar.DAY_OF_YEAR, alertDayOfYear);
        calculator.set(Calendar.HOUR_OF_DAY, alertHourOfDay);
        calculator.set(Calendar.MINUTE, alertMinute);

        scheduleListTime();

        if (calculator.getTime().compareTo(currentTime) <= 0) {

            calculator.add(Calendar.YEAR, 1);

            scheduleListTime();

        }

        return calculator.getTime();
    }

    private Date getNextDayCustom(final PeriodicRepeatModel.TimeUnits unit, final int value) {

        //calculator.set(Calendar.DAY_OF_YEAR, alertDayOfYear);
        calculator.set(Calendar.HOUR_OF_DAY, alertHourOfDay);
        calculator.set(Calendar.MINUTE, alertMinute);

        scheduleListTime();

        if (calculator.getTime().compareTo(currentTime) <= 0) {

            switch (unit) {
                case DAYS:
                    calculator.add(Calendar.DAY_OF_YEAR, value);
                    break;
                case WEEKS:
                    calculator.add(Calendar.WEEK_OF_YEAR, value);
                    break;
                case MONTHS:
                    calculator.add(Calendar.MONTH, value);
                    break;
                case YEARS:
                    calculator.add(Calendar.YEAR, value);
                    break;
            }

            scheduleListTime();

        }

        return calculator.getTime();
    }

    public Date getNextSchedule() {
        /*
         * This method will look for next closest date and time to repeat from reminder set time.
         * If the time is in past then it will bring the DAY of YEAR to present and then will look for next possible schedule based on repeat settings.
         * This method will return a non null value only if there is a dat can reached in future.
         * */

        if (getTimeModel().getTime() == null)
            return null;

        if (!getRepeatModel().isEnabled()) {
            // This mean repeat is off but still target time can be reached if in future
            return getNextNoPeriodicRepeat();
        }

        Date nextScheduleTime = null;

        if (getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.OFF) {
            // This mean periodic repeat is off but multiple time repeat still can exists and can reschedule to future
            nextScheduleTime = getNextNoPeriodicRepeat();

        } else if (getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.DAILY) {

            nextScheduleTime = getNextDay();

        } else if (getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.WEEKLY) {

            nextScheduleTime = getNextDayOfWeek();

        } else if (getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.MONTHLY) {

            nextScheduleTime = getNextDayOfMonth();

        } else if (getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.YEARLY) {

            nextScheduleTime = getNextYear();

        } else if (getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.DAILY_CUSTOM) {

            nextScheduleTime = getNextDay(getPeriodicRepeatModel().getCustomDays());

        } else if (getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.WEEKLY_CUSTOM) {

            nextScheduleTime = getNextDayOfWeek(getPeriodicRepeatModel().getCustomWeeks());

        } else if (getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.MONTHLY_CUSTOM) {

            nextScheduleTime = getNextDayOfMonth(getPeriodicRepeatModel().getCustomMonths());

        } else if (getPeriodicRepeatModel().getRepeatOption() == PeriodicRepeatModel.PeriodicRepeatOptions.OTHER) {

            nextScheduleTime = getNextDayCustom(getPeriodicRepeatModel().getCustomTimeUnit(), getPeriodicRepeatModel().getCustomTimeValue());

        }

        if (nextScheduleTime != null && getRepeatModel().getRepeatEndDate() != null) {

            return nextScheduleTime.after(getRepeatModel().getRepeatEndDate()) ? null : nextScheduleTime;

        }

        return nextScheduleTime;
    }
}

