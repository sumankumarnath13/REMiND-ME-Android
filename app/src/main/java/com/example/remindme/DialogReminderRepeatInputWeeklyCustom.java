package com.example.remindme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.IReminderRepeatListener;
import com.example.remindme.viewModels.ReminderRepeatModel;

public class DialogReminderRepeatInputWeeklyCustom extends DialogFragment {

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
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_weekly_custom, null);

        final CheckBox chk_weekly_1 = view.findViewById(R.id.chk_weekly_1);
        final CheckBox chk_weekly_2 = view.findViewById(R.id.chk_weekly_2);
        final CheckBox chk_weekly_3 = view.findViewById(R.id.chk_weekly_3);
        final CheckBox chk_weekly_4 = view.findViewById(R.id.chk_weekly_4);
        final CheckBox chk_weekly_5 = view.findViewById(R.id.chk_weekly_5);

        final String weekDayName = StringHelper.toWeekday(model.getReminderTime());

        chk_weekly_1.setText("On 1st " + weekDayName + " of the month");
        chk_weekly_2.setText("On 2nd " + weekDayName + " of the month");
        chk_weekly_3.setText("On 3rd " + weekDayName + " of the month");
        chk_weekly_4.setText("On 4th " + weekDayName + " of the month");
        chk_weekly_5.setText("On 5th " + weekDayName + " of the month (if exists)");

        for (int i = 0; i < model.customWeeks.size(); i++) {
            int value = model.customWeeks.get(i);
            switch (value) {
                default:
                case 0:
                    chk_weekly_1.setChecked(true);
                    break;
                case 1:
                    chk_weekly_2.setChecked(true);
                    break;
                case 2:
                    chk_weekly_3.setChecked(true);
                    break;
                case 3:
                    chk_weekly_4.setChecked(true);
                    break;
                case 5:
                    chk_weekly_5.setChecked(true);
                    break;
            }
        }

        builder.setView(view).setTitle("Select weeks to Repeat").setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.customWeeks.clear();
                if (chk_weekly_1.isChecked()) model.customWeeks.add(0);
                if (chk_weekly_2.isChecked()) model.customWeeks.add(1);
                if (chk_weekly_3.isChecked()) model.customWeeks.add(2);
                if (chk_weekly_4.isChecked()) model.customWeeks.add(3);
                if (chk_weekly_5.isChecked()) model.customWeeks.add(4);
                listener.set(model, true);
            }
        }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.set(null, true);
            }
        });

        return builder.create();
    }

}
