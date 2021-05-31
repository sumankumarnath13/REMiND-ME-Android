package com.example.remindme.viewModels;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.ScheduleHelper;
import com.example.remindme.helpers.StringHelper;

import java.util.Calendar;
import java.util.Date;

public class TimeModel extends ViewModel {

//    public enum TimeListModes {
//        NONE,
//        HOURLY,
//        INTERVAL,
//        ANYTIME,
//    }

    public TimeModel copy() {
        TimeModel instance = new TimeModel(parent);

        instance.time = time;
        instance.scheduledTime = scheduledTime;
//        instance.timeListMode = timeListMode;
//        instance.setTimeListTimes(getTimeListTimes());
//        instance.setTimeListHours(getTimeListHours());

        return instance;
    }

    private final AlertModel parent;

    public AlertModel getParent() {
        return parent;
    }

    public TimeModel(final AlertModel alertModel) {
        parent = alertModel;

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

        if (value == null)
            return;

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

            final ScheduleHelper scheduleHelper = new ScheduleHelper(this, getParent().getRepeatModel());

            setScheduledTime(scheduleHelper.getNextSchedule());

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

//    private TimeListModes timeListMode = TimeListModes.NONE;
//
//    public TimeListModes getTimeListMode() {
//        return timeListMode;
//    }
//
//    public void setTimeListMode(TimeListModes timeListMode) {
//        this.timeListMode = timeListMode;
//    }
//
//    private final ArrayList<Date> timeListTimes = new ArrayList<>();
//
//    public List<Date> getTimeListTimes() {
//        return timeListTimes;
//    }
//
//    public void addTimeListTime(final Date time) {
//        final int foundIndex = timeListTimes.indexOf(time);
//        if (foundIndex < 0) {
//            timeListTimes.add(time);
//        }
//    }
//
//    public void removeTimeListTime(final Date time) {
//        timeListTimes.remove(time);
//    }
//
//    public void setTimeListTimes(final List<Date> times) {
//        timeListTimes.clear();
//        timeListTimes.addAll(times);
//    }
//
//    private final ArrayList<Integer> timeListHours = new ArrayList<>();
//
//    public ArrayList<Integer> getTimeListHours() {
//        return timeListHours;
//    }
//
//    public void addTimeListHour(final int hour) {
//        final int foundIndex = timeListHours.indexOf(hour);
//        if (foundIndex < 0) {
//            timeListHours.add(hour);
//        }
//    }
//
//    public void setTimeListHours(final List<Integer> values) {
//        timeListHours.clear();
//        timeListHours.addAll(values);
//    }

    @NonNull
    @Override
    public String toString() {
//        if (getTimeListMode() == TimeListModes.NONE) {
//            return "NONE";
//        }
//
//        final StringBuilder builder = new StringBuilder();
//
//        if (getTimeListMode() == TimeListModes.HOURLY) {
//
//            final Calendar c = Calendar.getInstance();
//            c.setTime(getTime());
//            final int min = c.get(Calendar.MINUTE);
//
//            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
//                for (int i = 0; i < getTimeListHours().size(); i++) {
//                    builder.append(StringHelper.get24(getTimeListHours().get(i), min)).append(", ");
//                }
//            } else {
//                for (int i = 0; i < getTimeListHours().size(); i++) {
//                    builder.append(StringHelper.get12(getTimeListHours().get(i), min)).append(", ");
//                }
//            }
//        } else if (getTimeListMode() == TimeListModes.ANYTIME) {
//
//            final Calendar c = Calendar.getInstance();
//
//            if (AppSettingsHelper.getInstance().isUse24hourTime()) {
//                for (int i = 0; i < getTimeListTimes().size(); i++) {
//                    c.setTime(getTimeListTimes().get(i));
//                    builder.append(StringHelper.get24(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))).append(", ");
//                }
//            } else {
//                for (int i = 0; i < getTimeListTimes().size(); i++) {
//                    c.setTime(getTimeListTimes().get(i));
//                    builder.append(StringHelper.get12(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))).append(", ");
//                }
//            }
//        }

        //return StringHelper.trimEnd(builder.toString(), ", ");

        return "";
    }

    public Spannable toSpannableString(int highlightColor) {
        final Calendar c = Calendar.getInstance();
        c.setTime(isHasScheduledTime() ? getScheduledTime() : getTime());

        String foFind;
        if (AppSettingsHelper.getInstance().isUse24hourTime()) {
            foFind = StringHelper.get24(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        } else {
            foFind = StringHelper.get12(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        }

        final String timeList = toString();
        final Spannable spannable = new SpannableString(timeList);

        final int startIndex = timeList.indexOf(foFind);
        if (startIndex < 0) {
            return spannable;
        }

        final int endIndex = startIndex + foFind.length();
        spannable.setSpan(new ForegroundColorSpan(highlightColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }


}
