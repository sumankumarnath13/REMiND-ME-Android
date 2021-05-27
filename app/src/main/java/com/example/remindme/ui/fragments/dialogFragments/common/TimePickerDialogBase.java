package com.example.remindme.ui.fragments.dialogFragments.common;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.remindme.helpers.ToastHelper;

import java.util.Calendar;
import java.util.Date;

public abstract class TimePickerDialogBase
        extends DialogFragmentBase {

    public static final String TAG = "RemindMeTimePickerDialog";

    public interface ITimePickerListener {
        void onSetListenerTime(Date dateTime);

        Date onGetListenerTime();
    }

    private ITimePickerListener listener;

    protected ITimePickerListener getListener() {
        return listener;
    }

    public void setListener(ITimePickerListener listener) {
        this.listener = listener;
    }

    private Calendar calendar;

    protected Calendar getCalendar() {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        return calendar;
    }

    protected void setCalendar(Date date) {
        getCalendar().setTime(date);
    }

    protected int getHourOfDay() {
        return getCalendar().get(Calendar.HOUR_OF_DAY);
    }

    protected int getMin() {
        return getCalendar().get(Calendar.MINUTE);
    }

    protected Date getTime() {
        return getCalendar().getTime();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Dialog listener is not set!");
            dismiss();
            return;
        }

        setCalendar(getListener().onGetListenerTime());
    }

    @Override
    protected void onUIRefresh() {

    }

}