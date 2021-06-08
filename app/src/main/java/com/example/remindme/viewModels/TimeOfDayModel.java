package com.example.remindme.viewModels;


public class TimeOfDayModel implements Comparable<TimeOfDayModel> {
    private int hourOfDay;

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    private int minute;

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public TimeOfDayModel() {

    }

    public TimeOfDayModel(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    @Override
    public int compareTo(TimeOfDayModel o) {
        return this.hashCode() - o.hashCode();
    }

    @Override
    public int hashCode() {
        return getHourOfDay() * 60 + getMinute();
    }
}
