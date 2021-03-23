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

import com.example.remindme.viewModels.IReminderRepeatListener;
import com.example.remindme.viewModels.ReminderRepeatModel;

public class DialogReminderRepeatInputDailyCustom extends DialogFragment {

    private IReminderRepeatListener listener;
    private ReminderRepeatModel model;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_daily_custom, null);

        final CheckBox chk_daily_sun = view.findViewById(R.id.chk_daily_sun);
        final CheckBox chk_daily_mon = view.findViewById(R.id.chk_daily_mon);
        final CheckBox chk_daily_tue = view.findViewById(R.id.chk_daily_tue);
        final CheckBox chk_daily_wed = view.findViewById(R.id.chk_daily_wed);
        final CheckBox chk_daily_thu = view.findViewById(R.id.chk_daily_thu);
        final CheckBox chk_daily_fri = view.findViewById(R.id.chk_daily_fri);
        final CheckBox chk_daily_sat = view.findViewById(R.id.chk_daily_sat);

        for (int i = 0; i < model.customDays.size(); i++) {
            int value = model.customDays.get(i);
            switch (value) {
                default:
                case 0:
                    chk_daily_sun.setChecked(true);
                    break;
                case 1:
                    chk_daily_mon.setChecked(true);
                    break;
                case 2:
                    chk_daily_tue.setChecked(true);
                    break;
                case 3:
                    chk_daily_wed.setChecked(true);
                    break;
                case 4:
                    chk_daily_thu.setChecked(true);
                    break;
                case 5:
                    chk_daily_fri.setChecked(true);
                    break;
                case 6:
                    chk_daily_sat.setChecked(true);
                    break;
            }
        }

        builder.setView(view)
                .setTitle("Select hours to Repeat")
                .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        model.customDays.clear();
                        if (chk_daily_sun.isChecked()) model.customDays.add(0);
                        if (chk_daily_mon.isChecked()) model.customDays.add(1);
                        if (chk_daily_tue.isChecked()) model.customDays.add(2);
                        if (chk_daily_wed.isChecked()) model.customDays.add(3);
                        if (chk_daily_thu.isChecked()) model.customDays.add(4);
                        if (chk_daily_fri.isChecked()) model.customDays.add(5);
                        if (chk_daily_sat.isChecked()) model.customDays.add(6);
                        listener.set(model, true);
                    }
                }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //model.isEnd = true;
                //listener.set(model);
            }
        });

        return builder.create();
    }

}
