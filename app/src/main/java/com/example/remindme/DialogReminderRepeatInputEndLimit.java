package com.example.remindme;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.remindme.helpers.StringHelper;
import com.example.remindme.ui.main.IReminderRepeatListener;
import com.example.remindme.ui.main.IRepeatInputDialog;
import com.example.remindme.viewModels.ReminderRepeatModel;

import java.util.Calendar;

public class DialogReminderRepeatInputEndLimit extends DialogFragment {
    private ReminderRepeatModel model;
    private boolean isCancel;
    private boolean isSetTime;
    private TextView tv_end_date_value;
    private TextView tv_end_time_value;
    private final Calendar alertTime = Calendar.getInstance();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            final IReminderRepeatListener listener = (IReminderRepeatListener) context;
            model = listener.getRepeatModel();
            if (model.isHasRepeatEnd()) {
                alertTime.setTime(model.getRepeatEndDate());
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderRepeatListener");
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        isCancel = true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (isCancel) {
            commitToParent();
        }
    }

    private void commitToParent() {
        final Fragment fragment = getParentFragmentManager().findFragmentByTag("repeatInput");
        if (fragment != null) {
            final IRepeatInputDialog hostDialog = (IRepeatInputDialog) fragment;
            hostDialog.setChanges(model);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_end, null);
        builder.setView(view).setTitle("Set when to stop repeating")
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isSetTime) {
                            model.setRepeatEndDate(alertTime.getTime());
                            model.setHasRepeatEnd(true);
                        }

                        commitToParent();
                    }
                })
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        model.setHasRepeatEnd(false);
                        commitToParent();
                    }
                });

        tv_end_date_value = view.findViewById(R.id.tv_end_date_value);
        tv_end_date_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int mYear, mMonth, mDay;
                mYear = alertTime.get(Calendar.YEAR);
                mMonth = alertTime.get(Calendar.MONTH);
                mDay = alertTime.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), R.style.DatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                alertTime.set(Calendar.YEAR, year);
                                alertTime.set(Calendar.MONTH, monthOfYear);
                                alertTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                isSetTime = true;
                                refreshForm();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()); // This will cause extra title on the top of the regular date picker
                datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // This line will try to solve the issue above
                datePickerDialog.setTitle(null); // This line will try to solve the issue above
                datePickerDialog.show();
            }
        });

        tv_end_time_value = view.findViewById(R.id.tv_end_time_value);
        tv_end_time_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int mHour, mMinute;
                mHour = alertTime.get(Calendar.HOUR_OF_DAY);
                mMinute = alertTime.get(Calendar.MINUTE);
                final TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), R.style.TimePickerDialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                alertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                alertTime.set(Calendar.MINUTE, minute);
                                alertTime.set(Calendar.SECOND, 0); // Setting second to 0 is important.
                                isSetTime = true;
                                refreshForm();
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        refreshForm();

        return builder.create();
    }

    private void refreshForm() {
        if (model.isHasRepeatEnd() || isSetTime) {
            tv_end_date_value.setText(StringHelper.toDate(alertTime.getTime()));
            tv_end_time_value.setText(StringHelper.toTime(alertTime.getTime()));
        }
    }
}
