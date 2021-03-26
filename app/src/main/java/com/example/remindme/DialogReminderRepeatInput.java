package com.example.remindme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.remindme.viewModels.IReminderRepeatListener;
import com.example.remindme.viewModels.ReminderRepeatModel;

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

//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }

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


        /* WEEKLY custom system isn't ready yet
         *
         *
         *  */
        rdo_reminder_repeat_weekly_custom.setVisibility(View.GONE);


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
        }

        return builder.create();
    }
}
