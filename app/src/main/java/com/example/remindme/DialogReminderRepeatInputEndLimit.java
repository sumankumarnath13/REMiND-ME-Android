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

import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.IReminderRepeatListener;
import com.example.remindme.viewModels.ReminderRepeatModel;

import java.util.Calendar;

public class DialogReminderRepeatInputEndLimit extends DialogFragment
        //implements  IReminderRepeatListener
{
    private IReminderRepeatListener listener;
    private ReminderRepeatModel model;
    private boolean isCancel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (IReminderRepeatListener) context;
            model = listener.getRepeatModel();
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
            listener.set(null, true);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_end, null);
        builder.setView(view).setTitle("Select Repeat Option").setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.set(null, true);
            }
        });

        tv_end_date_value = view.findViewById(R.id.tv_end_date_value);
        tv_end_date_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar alertTime = Calendar.getInstance();
                //final Calendar currentTime = Calendar.getInstance();
                //alertTime.setTime(reminderModel.getOriginalTime());
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
                                model.setRepeatEndDate(alertTime.getTime());
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
                final Calendar alertTime = Calendar.getInstance();
                //final Calendar currentTime = Calendar.getInstance();
                //alertTime.setTime(reminderModel.getOriginalTime());
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
                                model.setRepeatEndDate(alertTime.getTime());
                                refreshForm();
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        tv_end_date_label = view.findViewById(R.id.tv_end_date_label);
        refreshForm();

        return builder.create();
    }

    private TextView tv_end_date_value;
    private TextView tv_end_time_value;
    private TextView tv_end_date_label;

    private void refreshForm() {

        if (model.isHasRepeatEnd()) {
            tv_end_date_value.setText(StringHelper.toDate(model.getRepeatEndDate()));
            tv_end_time_value.setText(StringHelper.toTime(model.getRepeatEndDate()));
        }
    }
}
