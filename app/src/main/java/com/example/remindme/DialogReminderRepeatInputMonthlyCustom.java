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
import androidx.fragment.app.Fragment;

import com.example.remindme.ui.main.IReminderRepeatListener;
import com.example.remindme.ui.main.IRepeatInputDialog;
import com.example.remindme.viewModels.ReminderRepeatModel;

import java.util.Calendar;

public class DialogReminderRepeatInputMonthlyCustom extends DialogFragment {

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
            listener.discardChanges();
        }
//        final FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//        transaction.remove(this);
//        transaction.commit();
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
                case Calendar.JANUARY:
                    chk_monthly_jan.setChecked(true);
                    break;
                case Calendar.FEBRUARY:
                    chk_monthly_feb.setChecked(true);
                    break;
                case Calendar.MARCH:
                    chk_monthly_mar.setChecked(true);
                    break;
                case Calendar.APRIL:
                    chk_monthly_apr.setChecked(true);
                    break;
                case Calendar.MAY:
                    chk_monthly_may.setChecked(true);
                    break;
                case Calendar.JUNE:
                    chk_monthly_jun.setChecked(true);
                    break;
                case Calendar.JULY:
                    chk_monthly_jul.setChecked(true);
                    break;
                case Calendar.AUGUST:
                    chk_monthly_aug.setChecked(true);
                    break;
                case Calendar.SEPTEMBER:
                    chk_monthly_sep.setChecked(true);
                    break;
                case Calendar.OCTOBER:
                    chk_monthly_oct.setChecked(true);
                    break;
                case Calendar.NOVEMBER:
                    chk_monthly_nov.setChecked(true);
                    break;
                case Calendar.DECEMBER:
                    chk_monthly_dec.setChecked(true);
                    break;
            }
        }

        builder.setView(view).setTitle("Select months to Repeat").setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.customMonths.clear();
                if (chk_monthly_jan.isChecked()) model.customMonths.add(Calendar.JANUARY);
                if (chk_monthly_feb.isChecked()) model.customMonths.add(Calendar.FEBRUARY);
                if (chk_monthly_mar.isChecked()) model.customMonths.add(Calendar.MARCH);
                if (chk_monthly_apr.isChecked()) model.customMonths.add(Calendar.APRIL);
                if (chk_monthly_may.isChecked()) model.customMonths.add(Calendar.MAY);
                if (chk_monthly_jun.isChecked()) model.customMonths.add(Calendar.JUNE);
                if (chk_monthly_jul.isChecked()) model.customMonths.add(Calendar.JULY);
                if (chk_monthly_aug.isChecked()) model.customMonths.add(Calendar.AUGUST);
                if (chk_monthly_sep.isChecked()) model.customMonths.add(Calendar.SEPTEMBER);
                if (chk_monthly_oct.isChecked()) model.customMonths.add(Calendar.OCTOBER);
                if (chk_monthly_nov.isChecked()) model.customMonths.add(Calendar.NOVEMBER);
                if (chk_monthly_dec.isChecked()) model.customMonths.add(Calendar.DECEMBER);
                //listener.setChanges(model);

                final Fragment fragment = getParentFragmentManager().findFragmentByTag("repeatInput");
                final IRepeatInputDialog hostDialog = (IRepeatInputDialog) fragment;
                hostDialog.setChanges(model);


            }
        }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //listener.setChanges(null, true);
            }
        });

        return builder.create();
    }

}
