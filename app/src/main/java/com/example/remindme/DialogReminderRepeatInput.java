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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.IReminderRepeatListener;
import com.example.remindme.viewModels.ReminderRepeatModel;

import java.util.Calendar;

public class DialogReminderRepeatInput extends DialogFragment
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
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat, null);
        builder.setView(view).setTitle("Select Repeat Option").setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.set(null, true);
            }
        });

        final RadioButton rdo_reminder_repeat_none = view.findViewById(R.id.rdo_reminder_repeat_none);
        rdo_reminder_repeat_none.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.NONE;
                listener.set(model, true);
            }
        });

        final RadioButton rdo_reminder_repeat_hourly = view.findViewById(R.id.rdo_reminder_repeat_hourly);
        rdo_reminder_repeat_hourly.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.HOURLY;
                listener.set(model, true);
            }
        });

        final RadioButton rdo_reminder_repeat_hourly_custom = view.findViewById(R.id.rdo_reminder_repeat_hourly_custom);
        rdo_reminder_repeat_hourly_custom.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.HOURLY_CUSTOM;
                listener.set(model, false);
            }
        });

        final RadioButton rdo_reminder_repeat_daily = view.findViewById(R.id.rdo_reminder_repeat_daily);
        rdo_reminder_repeat_daily.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.DAILY;
                listener.set(model, true);
            }
        });

        final RadioButton rdo_reminder_repeat_daily_custom = view.findViewById(R.id.rdo_reminder_repeat_daily_custom);
        rdo_reminder_repeat_daily_custom.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.DAILY_CUSTOM;
                listener.set(model, false);
            }
        });

        final RadioButton rdo_reminder_repeat_weekly = view.findViewById(R.id.rdo_reminder_repeat_weekly);
        rdo_reminder_repeat_weekly.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.WEEKLY;
                listener.set(model, true);
            }
        });

        final RadioButton rdo_reminder_repeat_weekly_custom = view.findViewById(R.id.rdo_reminder_repeat_weekly_custom);
        rdo_reminder_repeat_weekly_custom.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM;
                listener.set(model, false);
            }
        });

        final RadioButton rdo_reminder_repeat_monthly = view.findViewById(R.id.rdo_reminder_repeat_monthly);
        rdo_reminder_repeat_monthly.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.MONTHLY;
                listener.set(model, true);
            }
        });

        final RadioButton rdo_reminder_repeat_monthly_custom = view.findViewById(R.id.rdo_reminder_repeat_monthly_custom);
        rdo_reminder_repeat_monthly_custom.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM;
                listener.set(model, false);
            }
        });

        final RadioButton rdo_reminder_repeat_yearly = view.findViewById(R.id.rdo_reminder_repeat_yearly);
        rdo_reminder_repeat_yearly.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.YEARLY;
                listener.set(model, true);
            }
        });

        final RadioButton rdo_reminder_repeat_other = view.findViewById(R.id.rdo_reminder_repeat_other);
        rdo_reminder_repeat_other.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.OTHER;
                listener.set(model, false);
            }
        });

        // No radio group wont work for the given layout. So resetting programmatically is required.
        rdo_reminder_repeat_none.setChecked(false);
        rdo_reminder_repeat_hourly.setChecked(false);
        rdo_reminder_repeat_hourly_custom.setChecked(false);
        rdo_reminder_repeat_daily.setChecked(false);
        rdo_reminder_repeat_daily_custom.setChecked(false);
        rdo_reminder_repeat_weekly.setChecked(false);
        rdo_reminder_repeat_weekly_custom.setChecked(false);
        rdo_reminder_repeat_monthly.setChecked(false);
        rdo_reminder_repeat_monthly_custom.setChecked(false);
        rdo_reminder_repeat_yearly.setChecked(false);
        rdo_reminder_repeat_other.setChecked(false);

        switch (model.repeatOption) {
            default:
            case NONE:
                rdo_reminder_repeat_none.setChecked(true);
                break;
            case HOURLY:
                rdo_reminder_repeat_hourly.setChecked(true);
                break;
            case HOURLY_CUSTOM:
                rdo_reminder_repeat_hourly_custom.setChecked(true);
                break;
            case DAILY:
                rdo_reminder_repeat_daily.setChecked(true);
                break;
            case DAILY_CUSTOM:
                rdo_reminder_repeat_daily_custom.setChecked(true);
                break;
            case WEEKLY:
                rdo_reminder_repeat_weekly.setChecked(true);
                break;
            case WEEKLY_CUSTOM:
                rdo_reminder_repeat_weekly_custom.setChecked(true);
                break;
            case MONTHLY:
                rdo_reminder_repeat_monthly.setChecked(true);
                break;
            case MONTHLY_CUSTOM:
                rdo_reminder_repeat_monthly_custom.setChecked(true);
                break;
            case YEARLY:
                rdo_reminder_repeat_yearly.setChecked(true);
                break;
            case OTHER:
                rdo_reminder_repeat_other.setChecked(true);
                break;
        }

        tv_end_date_value = view.findViewById(R.id.tv_end_date_value);
        tv_end_date_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar alertTime = Calendar.getInstance();
                if (model.isHasRepeatEnd()) {
                    alertTime.setTime(model.getRepeatEndDate());
                } else {
                    alertTime.setTime(model.getReminderTime());
                }

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
                if (model.isHasRepeatEnd()) {
                    alertTime.setTime(model.getRepeatEndDate());
                } else {
                    alertTime.setTime(model.getReminderTime());
                }

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
