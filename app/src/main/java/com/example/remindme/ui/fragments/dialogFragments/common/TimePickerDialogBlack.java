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

        timePicker.setCurrentHour(getHourOfDay());
        timePicker.setCurrentMinute(getMin());
        timePicker.setIs24HourView(AppSettingsHelper.getInstance().isUse24hourTime());

        builder.setView(view).setPositiveButton(getString(R.string.acton_dialog_positive), (dialog, which) -> {

            final Calendar calendar = Calendar.getInstance();

            calendar.setTime(getTime());
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            getListener().onSetListenerTime(calendar.getTime());

        }).setNegativeButton(getString(R.string.acton_dialog_negative), (dialog, which) -> {
        });

        return builder.create();
    }
}