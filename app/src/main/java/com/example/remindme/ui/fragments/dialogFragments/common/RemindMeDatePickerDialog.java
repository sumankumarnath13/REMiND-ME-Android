package com.example.remindme.ui.fragments.dialogFragments.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.OsHelper;

import java.util.Calendar;

public class RemindMeDatePickerDialog extends DateTimePickerDialogBase {

    public static final String TAG = "DatePickerDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.common_dialog_date_picker, null);

        final DatePicker datePicker = view.findViewById(R.id.date_picker);

        if (OsHelper.isLollipopOrLater()) {
            datePicker.setFirstDayOfWeek(AppSettingsHelper.getInstance().getFirstDayOfWeek());
        }

        datePicker.init(getYear(), getMonthOfYear(), getDayOfMonth(),
                (view1, year, monthOfYear, dayOfMonth) -> {

                });
        final Calendar calendar = Calendar.getInstance();
        if (getListener().getMinimumDateTime(getTag()) != null) {
            calendar.setTime(getListener().getMinimumDateTime(getTag()));
            datePicker.setMinDate(calendar.getTimeInMillis());
        }

        builder.setView(view)
                .setPositiveButton(getString(R.string.acton_dialog_positive), (dialog, which) -> {

                    calendar.setTime(getDateTime());
                    calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                    calendar.set(Calendar.MONTH, datePicker.getMonth());
                    calendar.set(Calendar.YEAR, datePicker.getYear());

                    getListener().setDateTimePicker(getTag(), calendar.getTime());

                }).setNegativeButton(getString(R.string.acton_dialog_negative), (dialog, which) -> {
        });

        return builder.create();
    }
}