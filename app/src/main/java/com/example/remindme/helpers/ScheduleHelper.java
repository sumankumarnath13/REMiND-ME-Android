package com.example.remindme.helpers;

import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.TimeModel;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ScheduleHelper {

    final int alertYear;
    final int alertDayOfYear;
    final int alertDayOfWeek;
    final int alertDayOfMonth;
    final int alertHourOfDay;
    final int alertMinute;

    final int currentYear;
    final int currentDayOfYear;
    final int currentHourOfDay;
    final int currentMinute;

    final Calendar calculator;
    final Date currentTime;
    final TimeModel timeModel;

    public ScheduleHelper(final TimeModel timeModel) {

        this.timeModel = timeModel;
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

        if (timeModel.getTimeListMode() != TimeModel.TimeListModes.NONE) {

            Date firstTimeListTime = null;

            if (timeModel.getTimeListMode() == TimeModel.TimeListModes.HOURLY && timeModel.getTimeListHours().size() > 0) {

                calculator.set(Calendar.MINUTE, alertMinute);

                final List<Integer> timeListHours = timeModel.getTimeListHours();

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

            } else if (timeModel.getTimeListMode() == TimeModel.TimeListModes.CUSTOM && timeModel.getTimeListTimes().size() > 0) {

                final List<Date> timeListTimes = timeModel.getTimeListTimes();

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

    public Date getNextNoRepeat() {

        scheduleListTime(); // Try to find if any tme available from time list for the day

        if (calculator.getTime().compareTo(currentTime) <= 0) { // Then no time found

            return null;

        }

        return calculator.getTime();
    }

    public Date getNextHour() {
        calculator.set(Calendar.MINUTE, alertMinute);

        // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
        if (calculator.getTime().compareTo(currentTime) <= 0) {
            calculator.add(Calendar.HOUR_OF_DAY, 1);
        }

        return calculator.getTime();
    }

    public Date getNextDay() {
        calculator.set(Calendar.HOUR_OF_DAY, alertHourOfDay);
        calculator.set(Calendar.MINUTE, alertMinute);

        scheduleListTime();

        if (calculator.getTime().compareTo(currentTime) <= 0) {

            calculator.add(Calendar.DAY_OF_YEAR, 1);

            scheduleListTime();

        }

        return calculator.getTime();
    }

    public Date getNextDay(final List<Integer> daysOfWeek) {
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

    public Date getNextDayOfWeek() {
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

    public Date getNextDayOfWeek(final List<Integer> weeksOfMonth) {

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

    public Date getNextDayOfMonth() {

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

    public Date getNextDayOfMonth(final List<Integer> monthsOfYear) {

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

    public Date getNextYear() {

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

    public Date getNextDayCustom(final RepeatModel.TimeUnits unit, final int value) {

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

}

