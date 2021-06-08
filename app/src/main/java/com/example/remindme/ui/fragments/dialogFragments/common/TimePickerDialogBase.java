package com.example.remindme.ui.fragments.dialogFragments.common;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remindme.helpers.ToastHelper;

import java.util.Calendar;
import java.util.Date;

public abstract class TimePickerDialogBase
        extends DialogFragmentBase {

    public static final String TAG = "RemindMeTimePickerDialog";

    public interface ITimePickerListener {
        void onSetListenerTime(int hourOfDay, int minute);

        Date onGetListenerTime();
    }

    private ITimePickerListener listener;

    protected ITimePickerListener getListener() {
        if (listener == null) {
            listener = super.getListener(ITimePickerListener.class);
        }
        return listener;
    }

    private Calendar calendar;

    protected Calendar getCalendar() {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        return calendar;
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

    private static final String MODEL_KEY = "MODEL_KEY";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(MODEL_KEY, getCalendar().getTimeInMillis());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Dialog listener is not set!");
            dismiss();
            return;
        }

        if (savedInstanceState == null) {
            getCalendar().setTime(getListener().onGetListenerTime());
        } else {
            getCalendar().setTimeInMillis(savedInstanceState.getLong(MODEL_KEY, 0));
        }
    }

    @Override
    protected void onUIRefresh() {

    }

}