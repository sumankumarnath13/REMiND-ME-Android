package com.example.remindme.ui.fragments.dialogFragments;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.controllers.AbstractDialogFragmentController;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.ReminderRepeatModel;

import java.util.Calendar;
import java.util.Date;

public class TimeCalculatorDialog extends AbstractDialogFragmentController {
    public interface ITimeCalculatorListener {
        void setTime(Date newTime);

        Date getTime();
    }

    private ITimeCalculatorListener listener;
    private final Calendar calendar = Calendar.getInstance();
    private final Calendar resultCalendar = Calendar.getInstance();
    private NumberPicker value_picker;
    private NumberPicker unit_picker;
    private TextView tv_reminder_time;
    private TextView tv_reminder_date;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ITimeCalculatorListener) context;
            if (listener.getTime() == null) return;
            calendar.setTime(listener.getTime());
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement ITimeCalculatorListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.time_calculator_dialog, null);

        final Button btn_reminder_time = view.findViewById(R.id.btn_reminder_time);
        btn_reminder_time.setText(StringHelper.toTime(calendar.getTime()));

        final Button btn_reminder_date = view.findViewById(R.id.btn_reminder_date);
        btn_reminder_date.setText(StringHelper.toWeekdayDate(calendar.getTime()));

        btn_reminder_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int mYear, mMonth, mDay;
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT ? R.style.DatePickerDialogLight : R.style.DatePickerDialogBlack,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                refresh();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()); // This will cause extra title on the top of the regular date picker
                datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // This line will try to solve the issue above
                datePickerDialog.setTitle(null); // This line will try to solve the issue above
                datePickerDialog.show();

                if (OsHelper.isLollipopOrLater()) {
                    datePickerDialog.getDatePicker().setFirstDayOfWeek(AppSettingsHelper.getInstance().getFirstDayOfWeek());
                }
            }
        });

        btn_reminder_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int mHour, mMinute;
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
                mMinute = calendar.get(Calendar.MINUTE);

                final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT ? R.style.TimePickerDialogLight : R.style.TimePickerDialogBlack,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                refresh();
                            }
                        }, mHour, mMinute, AppSettingsHelper.getInstance().isUse24hourTime());
                timePickerDialog.show();
                //timePickerDialog.;
            }
        });

        value_picker = view.findViewById(R.id.value_picker);
        value_picker.setMinValue(1);

        final String[] units = new String[]{"Days", "Weeks", "Months", "Years"};
        unit_picker = view.findViewById(R.id.unit_picker);
        unit_picker.setMinValue(0);
        unit_picker.setMaxValue(units.length - 1);
        unit_picker.setDisplayedValues(units);
        unit_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                value_picker.setMaxValue(ReminderRepeatModel.getMaxForTimeUnit(ReminderRepeatModel.transform(newVal)));
                refresh();
            }
        });

        tv_reminder_time = view.findViewById(R.id.tv_reminder_time);
        tv_reminder_date = view.findViewById(R.id.tv_reminder_date);

        value_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                refresh();
            }
        });


        // Value of unit must set first before setting up value of time:
        // 1
        unit_picker.setValue(0);
        value_picker.setMaxValue(ReminderRepeatModel.getMaxForTimeUnit(ReminderRepeatModel.TimeUnits.DAYS));
        //unit_picker.
        // 2
        value_picker.setValue(1);

        builder.setView(view).setPositiveButton("Use Result", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.setTime(resultCalendar.getTime());
            }
        }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DO nothing
            }
        });

        refresh();

        return builder.create();

    }

    @Override
    protected void onUIRefresh() {
        // Do nothing
        resultCalendar.setTime(calendar.getTime());
        switch (ReminderRepeatModel.transform(unit_picker.getValue())) {
            case DAYS:
                resultCalendar.add(Calendar.DAY_OF_YEAR, value_picker.getValue());
                break;
            case WEEKS:
                resultCalendar.add(Calendar.WEEK_OF_YEAR, value_picker.getValue());
                break;
            case MONTHS:
                resultCalendar.add(Calendar.MONTH, value_picker.getValue());
                break;
            case YEARS:
                resultCalendar.add(Calendar.YEAR, value_picker.getValue());
                break;
        }

        tv_reminder_time.setText(StringHelper.toTime(resultCalendar.getTime()));
        tv_reminder_date.setText(StringHelper.toWeekdayDate(resultCalendar.getTime()));
    }

}
