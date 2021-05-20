package com.example.remindme.ui.fragments.dialogFragments.common;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.remindme.helpers.ToastHelper;

import java.util.Calendar;
import java.util.Date;

public abstract class DateTimePickerDialogBase
        extends DialogFragmentBase {

    private Date dateTime;

    private int hourOfDay;

    public int getHourOfDay() {
        return hourOfDay;
    }

    private int min;

    public int getMin() {
        return min;
    }

    private int dayOfMonth;

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    private int monthOfYear;

    public int getMonthOfYear() {
        return monthOfYear;
    }

    private int year;

    public int getYear() {
        return year;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((IDateTimePickerListener) getListener() == null) {
            ToastHelper.showError(getContext(), "Listener incompatible!");
            dismiss();
        }

        final Date x = ((IDateTimePickerListener) getListener()).getDateTimePicker(getTag());
        setDateTime(x);
    }

    @Override
    protected void onUIRefresh() {

    }

}