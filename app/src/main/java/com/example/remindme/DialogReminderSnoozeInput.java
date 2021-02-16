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
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import com.example.remindme.util.IReminderSnoozeListener;
import com.example.remindme.util.ReminderSnoozeModel;

public class DialogReminderSnoozeInput extends DialogFragment {
    private IReminderSnoozeListener listener;
    private ReminderSnoozeModel model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (IReminderSnoozeListener) context;
            model = listener.getSnoozeModel();
        }
        catch (ClassCastException e){
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderSnoozeListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_reminder_input_snooze, null);

        final SwitchCompat sw_reminder_snooze_enabled = view.findViewById(R.id.sw_reminder_snooze_enabled);
        final RadioButton rdo_reminder_snooze_m5 = view.findViewById(R.id.rdo_reminder_snooze_m5);
        final RadioButton rdo_reminder_snooze_m10 = view.findViewById(R.id.rdo_reminder_snooze_m10);
        final RadioButton rdo_reminder_snooze_m15 = view.findViewById(R.id.rdo_reminder_snooze_m15);
        final RadioButton rdo_reminder_snooze_m30 = view.findViewById(R.id.rdo_reminder_snooze_m30);
        final RadioButton rdo_reminder_snooze_r3 = view.findViewById(R.id.rdo_reminder_snooze_r3);
        final RadioButton rdo_reminder_snooze_r5 = view.findViewById(R.id.rdo_reminder_snooze_r5);
        final RadioButton rdo_reminder_snooze_rc = view.findViewById(R.id.rdo_reminder_snooze_rc);

        sw_reminder_snooze_enabled.setChecked(model.enabled);

        switch (model.intervalOption){
            default:
            case M5:
                rdo_reminder_snooze_m5.setChecked(true);
                break;
            case M10:
                rdo_reminder_snooze_m10.setChecked(true);
                break;
            case M15:
                rdo_reminder_snooze_m15.setChecked(true);
                break;
            case M30:
                rdo_reminder_snooze_m30.setChecked(true);
                break;
        }

        switch (model.repeatOption){
            default:
            case R3:
                rdo_reminder_snooze_r3.setChecked(true);
                break;
            case R5:
                rdo_reminder_snooze_r5.setChecked(true);
                break;
            case RC:
                rdo_reminder_snooze_rc.setChecked(true);
                break;
        }

        builder.setView(view)
                .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        model.enabled = sw_reminder_snooze_enabled.isChecked();

                        if(rdo_reminder_snooze_m5.isChecked()){
                            model.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M5;
                        }
                        else if(rdo_reminder_snooze_m10.isChecked()){
                            model.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M10;
                        }
                        else if(rdo_reminder_snooze_m15.isChecked()){
                            model.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M15;
                        }
                        else{
                            model.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M30;
                        }

                        if(rdo_reminder_snooze_r3.isChecked()){
                            model.repeatOption = ReminderSnoozeModel.SnoozeRepeatOptions.R3;
                        }
                        else if(rdo_reminder_snooze_r5.isChecked()){
                            model.repeatOption = ReminderSnoozeModel.SnoozeRepeatOptions.R5;
                        }
                        else{
                            model.repeatOption = ReminderSnoozeModel.SnoozeRepeatOptions.RC;
                        }

                        listener.set(model, true);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}