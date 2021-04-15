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
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.ReminderRepeatModel;

import java.util.Calendar;

public class DialogReminderRepeatInput extends DialogFragment implements IRepeatInputChildDialogListener {
    private IRepeatInputDialogListener listener;
    private ReminderRepeatModel model;
    private boolean isCancel;

    private RadioButton rdo_reminder_repeat_none;
    private RadioButton rdo_reminder_repeat_hourly;
    private RadioButton rdo_reminder_repeat_hourly_custom;
    private RadioButton rdo_reminder_repeat_daily;
    private RadioButton rdo_reminder_repeat_daily_custom;
    private RadioButton rdo_reminder_repeat_weekly;
    private RadioButton rdo_reminder_repeat_weekly_custom;
    private RadioButton rdo_reminder_repeat_monthly;
    private RadioButton rdo_reminder_repeat_monthly_custom;
    private RadioButton rdo_reminder_repeat_yearly;
    private RadioButton rdo_reminder_repeat_other;
    private TextView tv_end_date_value;
    private TextView tv_end_time_value;
    private SwitchCompat sw_has_repeat_end;
    private LinearLayout lv_repeat_end_date;
    private LinearLayout lv_repeat_end_time;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (IRepeatInputDialogListener) context;
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
            listener.discardChanges();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat, null);
        builder.setView(view).setTitle("Select Repeat Option")
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.commitChanges(model);
                    }
                })
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.discardChanges();
                    }
                });

        rdo_reminder_repeat_none = view.findViewById(R.id.rdo_reminder_repeat_none);
        rdo_reminder_repeat_none.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.NONE);
                setChanges();
            }
        });

        rdo_reminder_repeat_hourly = view.findViewById(R.id.rdo_reminder_repeat_hourly);
        rdo_reminder_repeat_hourly.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.HOURLY);
                setChanges();
            }
        });

        rdo_reminder_repeat_hourly_custom = view.findViewById(R.id.rdo_reminder_repeat_hourly_custom);
        rdo_reminder_repeat_hourly_custom.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogReminderRepeatInputHourlyCustom ting = new DialogReminderRepeatInputHourlyCustom();
                ting.show(getParentFragmentManager(), "ting");
            }
        });

        rdo_reminder_repeat_daily = view.findViewById(R.id.rdo_reminder_repeat_daily);
        rdo_reminder_repeat_daily.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.DAILY);
                setChanges();
            }
        });

        rdo_reminder_repeat_daily_custom = view.findViewById(R.id.rdo_reminder_repeat_daily_custom);
        rdo_reminder_repeat_daily_custom.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogReminderRepeatInputDailyCustom ting = new DialogReminderRepeatInputDailyCustom();
                ting.show(getParentFragmentManager(), "ting");
            }
        });

        rdo_reminder_repeat_weekly = view.findViewById(R.id.rdo_reminder_repeat_weekly);
        rdo_reminder_repeat_weekly.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.WEEKLY);
                setChanges();
            }
        });

        rdo_reminder_repeat_weekly_custom = view.findViewById(R.id.rdo_reminder_repeat_weekly_custom);
        rdo_reminder_repeat_weekly_custom.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogReminderRepeatInputWeeklyCustom ting = new DialogReminderRepeatInputWeeklyCustom();
                ting.show(getParentFragmentManager(), "ting");
            }
        });

        rdo_reminder_repeat_monthly = view.findViewById(R.id.rdo_reminder_repeat_monthly);
        rdo_reminder_repeat_monthly.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.MONTHLY);
                setChanges();
            }
        });

        rdo_reminder_repeat_monthly_custom = view.findViewById(R.id.rdo_reminder_repeat_monthly_custom);
        rdo_reminder_repeat_monthly_custom.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogReminderRepeatInputMonthlyCustom ting = new DialogReminderRepeatInputMonthlyCustom();
                ting.show(getParentFragmentManager(), "ting");
            }
        });

        rdo_reminder_repeat_yearly = view.findViewById(R.id.rdo_reminder_repeat_yearly);
        rdo_reminder_repeat_yearly.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.YEARLY);
                setChanges();
            }
        });

        rdo_reminder_repeat_other = view.findViewById(R.id.rdo_reminder_repeat_other);
        rdo_reminder_repeat_other.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogReminderRepeatInputCustom ting = new DialogReminderRepeatInputCustom();
                ting.show(getParentFragmentManager(), "ting");
            }
        });

        sw_has_repeat_end = view.findViewById(R.id.sw_has_repeat_end);
        sw_has_repeat_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_has_repeat_end.isChecked() && !model.isHasRepeatEnd()) { // Use a default repeat end to next month
                    Calendar c = Calendar.getInstance();
                    c.setTime(model.getReminderTime());
                    c.add(Calendar.MONTH, 1);
                    model.setRepeatEndDate(c.getTime());
                }

                model.setHasRepeatEnd(sw_has_repeat_end.isChecked());
                setChanges();
            }
        });

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
                                setChanges();
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
                                setChanges();
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        lv_repeat_end_date = view.findViewById(R.id.lv_repeat_end_date);
        lv_repeat_end_time = view.findViewById(R.id.lv_repeat_end_time);

        setChanges();

        return builder.create();
    }

    private void setChanges() {
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

        switch (model.getRepeatOption()) {
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

        sw_has_repeat_end.setChecked(model.isHasRepeatEnd());

        if (model.isHasRepeatEnd()) {
            lv_repeat_end_date.setVisibility(View.VISIBLE);
            lv_repeat_end_time.setVisibility(View.VISIBLE);
            tv_end_date_value.setText(StringHelper.toWeekdayDate(model.getRepeatEndDate()));
            tv_end_time_value.setText(StringHelper.toTime(model.getRepeatEndDate()));
        } else {
            lv_repeat_end_date.setVisibility(View.GONE);
            lv_repeat_end_time.setVisibility(View.GONE);
        }

    }

    public void setChanges(ReminderRepeatModel m) {
        model = m;
        setChanges();
    }

    public interface IRepeatInputDialogListener {

        void commitChanges(ReminderRepeatModel repeatModel);

        void discardChanges();

        ReminderRepeatModel getRepeatModel();
    }
}
