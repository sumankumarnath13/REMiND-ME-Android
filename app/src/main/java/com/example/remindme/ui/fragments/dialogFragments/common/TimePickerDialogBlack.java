package com.example.remindme.ui.fragments.dialogFragments.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;

import java.util.Calendar;

public class TimePickerDialogBlack extends TimePickerDialogBase {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.common_dialog_time_picker_black, null);

        final TimePicker timePicker = view.findViewById(R.id.time_picker_black);

        timePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            getCalendar().set(Calendar.HOUR_OF_DAY, hourOfDay);
            getCalendar().set(Calendar.MINUTE, minute);
            getCalendar().set(Calendar.SECOND, 0);
            getCalendar().set(Calendar.MILLISECOND, 0);
        });

        timePicker.setIs24HourView(AppSettingsHelper.getInstance().isUse24hourTime());
        timePicker.setCurrentHour(getHourOfDay());
        timePicker.setCurrentMinute(getMin());

        builder.setView(view).setPositiveButton(getString(R.string.acton_dialog_positive), (dialog, which) -> {

            getListener().onSetListenerTime(getHourOfDay(), getMin());

        }).setNegativeButton(getString(R.string.acton_dialog_negative), (dialog, which) -> {
        });

        return builder.create();
    }
}