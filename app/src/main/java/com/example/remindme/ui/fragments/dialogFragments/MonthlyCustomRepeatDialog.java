package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.viewModels.RepeatModel;

import java.util.Calendar;

public class MonthlyCustomRepeatDialog extends CustomRepeatDialogBase {

    public static final String TAG = "MonthlyCustomRepeatDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.reminder_repeat_monthly_custom_dialog, null);

        final AppCompatCheckBox chk_monthly_jan = view.findViewById(R.id.chk_monthly_jan);
        final AppCompatCheckBox chk_monthly_feb = view.findViewById(R.id.chk_monthly_feb);
        final AppCompatCheckBox chk_monthly_mar = view.findViewById(R.id.chk_monthly_mar);
        final AppCompatCheckBox chk_monthly_apr = view.findViewById(R.id.chk_monthly_apr);
        final AppCompatCheckBox chk_monthly_may = view.findViewById(R.id.chk_monthly_may);
        final AppCompatCheckBox chk_monthly_jun = view.findViewById(R.id.chk_monthly_jun);
        final AppCompatCheckBox chk_monthly_jul = view.findViewById(R.id.chk_monthly_jul);
        final AppCompatCheckBox chk_monthly_aug = view.findViewById(R.id.chk_monthly_aug);
        final AppCompatCheckBox chk_monthly_sep = view.findViewById(R.id.chk_monthly_sep);
        final AppCompatCheckBox chk_monthly_oct = view.findViewById(R.id.chk_monthly_oct);
        final AppCompatCheckBox chk_monthly_nov = view.findViewById(R.id.chk_monthly_nov);
        final AppCompatCheckBox chk_monthly_dec = view.findViewById(R.id.chk_monthly_dec);

        for (int i = 0; i < getModel().getCustomMonths().size(); i++) {
            int value = getModel().getCustomMonths().get(i);
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

        builder.setView(view).setTitle("Select months to Repeat").setPositiveButton(getString(R.string.dialog_positive), (dialog, which) -> {
            getModel().getCustomMonths().clear();
            if (chk_monthly_jan.isChecked())
                getModel().getCustomMonths().add(Calendar.JANUARY);
            if (chk_monthly_feb.isChecked())
                getModel().getCustomMonths().add(Calendar.FEBRUARY);
            if (chk_monthly_mar.isChecked())
                getModel().getCustomMonths().add(Calendar.MARCH);
            if (chk_monthly_apr.isChecked())
                getModel().getCustomMonths().add(Calendar.APRIL);
            if (chk_monthly_may.isChecked())
                getModel().getCustomMonths().add(Calendar.MAY);
            if (chk_monthly_jun.isChecked())
                getModel().getCustomMonths().add(Calendar.JUNE);
            if (chk_monthly_jul.isChecked())
                getModel().getCustomMonths().add(Calendar.JULY);
            if (chk_monthly_aug.isChecked())
                getModel().getCustomMonths().add(Calendar.AUGUST);
            if (chk_monthly_sep.isChecked())
                getModel().getCustomMonths().add(Calendar.SEPTEMBER);
            if (chk_monthly_oct.isChecked())
                getModel().getCustomMonths().add(Calendar.OCTOBER);
            if (chk_monthly_nov.isChecked())
                getModel().getCustomMonths().add(Calendar.NOVEMBER);
            if (chk_monthly_dec.isChecked())
                getModel().getCustomMonths().add(Calendar.DECEMBER);

            getModel().setEnabled(true);
            getModel().setRepeatOption(RepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM);
            getListener().setCustomRepeatDialogModel(getModel());

        }).setNegativeButton(getString(R.string.dialog_negative), (dialog, which) -> {

        });

        return builder.create();
    }

}
