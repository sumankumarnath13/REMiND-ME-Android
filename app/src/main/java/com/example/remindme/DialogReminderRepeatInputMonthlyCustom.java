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

public class DialogReminderRepeatInputMonthlyCustom extends DialogFragment {

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
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_monthly_custom, null);

        final CheckBox chk_monthly_jan = view.findViewById(R.id.chk_monthly_jan);
        final CheckBox chk_monthly_feb = view.findViewById(R.id.chk_monthly_feb);
        final CheckBox chk_monthly_mar = view.findViewById(R.id.chk_monthly_mar);
        final CheckBox chk_monthly_apr = view.findViewById(R.id.chk_monthly_apr);
        final CheckBox chk_monthly_may = view.findViewById(R.id.chk_monthly_may);
        final CheckBox chk_monthly_jun = view.findViewById(R.id.chk_monthly_jun);
        final CheckBox chk_monthly_jul = view.findViewById(R.id.chk_monthly_jul);
        final CheckBox chk_monthly_aug = view.findViewById(R.id.chk_monthly_aug);
        final CheckBox chk_monthly_sep = view.findViewById(R.id.chk_monthly_sep);
        final CheckBox chk_monthly_oct = view.findViewById(R.id.chk_monthly_oct);
        final CheckBox chk_monthly_nov = view.findViewById(R.id.chk_monthly_nov);
        final CheckBox chk_monthly_dec = view.findViewById(R.id.chk_monthly_dec);

        for (int i = 0; i < model.customMonths.size(); i++) {
            int value = model.customMonths.get(i);
            switch (value) {
                default:
                case 0:
                    chk_monthly_jan.setChecked(true);
                    break;
                case 1:
                    chk_monthly_feb.setChecked(true);
                    break;
                case 2:
                    chk_monthly_mar.setChecked(true);
                    break;
                case 3:
                    chk_monthly_apr.setChecked(true);
                    break;
                case 4:
                    chk_monthly_may.setChecked(true);
                    break;
                case 5:
                    chk_monthly_jun.setChecked(true);
                    break;
                case 6:
                    chk_monthly_jul.setChecked(true);
                    break;
                case 7:
                    chk_monthly_aug.setChecked(true);
                    break;
                case 8:
                    chk_monthly_sep.setChecked(true);
                    break;
                case 9:
                    chk_monthly_oct.setChecked(true);
                    break;
                case 10:
                    chk_monthly_nov.setChecked(true);
                    break;
                case 11:
                    chk_monthly_dec.setChecked(true);
                    break;
            }
        }

        builder.setView(view).setTitle("Select months to Repeat").setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.customMonths.clear();
                if (chk_monthly_jan.isChecked()) model.customMonths.add(0);
                if (chk_monthly_feb.isChecked()) model.customMonths.add(1);
                if (chk_monthly_mar.isChecked()) model.customMonths.add(2);
                if (chk_monthly_apr.isChecked()) model.customMonths.add(3);
                if (chk_monthly_may.isChecked()) model.customMonths.add(4);
                if (chk_monthly_jun.isChecked()) model.customMonths.add(5);
                if (chk_monthly_jul.isChecked()) model.customMonths.add(6);
                if (chk_monthly_aug.isChecked()) model.customMonths.add(7);
                if (chk_monthly_sep.isChecked()) model.customMonths.add(8);
                if (chk_monthly_oct.isChecked()) model.customMonths.add(9);
                if (chk_monthly_nov.isChecked()) model.customMonths.add(10);
                if (chk_monthly_dec.isChecked()) model.customMonths.add(11);
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
