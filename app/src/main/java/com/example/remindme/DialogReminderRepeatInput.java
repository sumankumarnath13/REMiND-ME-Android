package com.example.remindme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.remindme.viewModels.IReminderRepeatListener;
import com.example.remindme.viewModels.ReminderRepeatModel;

public class DialogReminderRepeatInput extends DialogFragment {
    private IReminderRepeatListener listener;
    private ReminderRepeatModel model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (IReminderRepeatListener) context;
            model = listener.getRepeatModel();
        }
        catch (ClassCastException e){
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderRepeatListener");
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

            }
        });

        final RadioButton rdo_reminder_repeat_none = view.findViewById(R.id.rdo_reminder_repeat_none);
        rdo_reminder_repeat_none.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.None;
                listener.set(model, true);
            }
        });

        final RadioButton rdo_reminder_repeat_hourly = view.findViewById(R.id.rdo_reminder_repeat_hourly);
        rdo_reminder_repeat_hourly.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Hourly;
                listener.set(model, true);
            }
        });

        final RadioButton rdo_reminder_repeat_daily = view.findViewById(R.id.rdo_reminder_repeat_daily);
        rdo_reminder_repeat_daily.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Daily;
                listener.set(model, false);
            }
        });

        final RadioButton rdo_reminder_repeat_weekly = view.findViewById(R.id.rdo_reminder_repeat_weekly);
        rdo_reminder_repeat_weekly.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Weekly;
                listener.set(model, true);
            }
        });

        final RadioButton rdo_reminder_repeat_monthly = view.findViewById(R.id.rdo_reminder_repeat_monthly);
        rdo_reminder_repeat_monthly.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Monthly;
                listener.set(model, false);
            }
        });

        final RadioButton rdo_reminder_repeat_yearly = view.findViewById(R.id.rdo_reminder_repeat_yearly);
        rdo_reminder_repeat_yearly.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Yearly;
                listener.set(model, true);
            }
        });

        switch (model.repeatOption){
            default:
            case None:
                rdo_reminder_repeat_none.setChecked(true);
                break;
            case Hourly:
                rdo_reminder_repeat_hourly.setChecked(true);
                break;
            case Daily:
                rdo_reminder_repeat_daily.setChecked(true);
                break;
            case Weekly:
                rdo_reminder_repeat_weekly.setChecked(true);
                break;
            case Monthly:
                rdo_reminder_repeat_monthly.setChecked(true);
                break;
            case Yearly:
                rdo_reminder_repeat_yearly.setChecked(true);
                break;
        }

        return builder.create();
    }
}
