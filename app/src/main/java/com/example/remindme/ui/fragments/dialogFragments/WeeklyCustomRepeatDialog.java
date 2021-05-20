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
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.ui.fragments.dialogFragments.common.CustomRepeatDialogBase;
import com.example.remindme.viewModels.RepeatModel;

public class WeeklyCustomRepeatDialog extends CustomRepeatDialogBase {

    public static final String TAG = "WeeklyCustomRepeatDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_fragment_input_repeat_weeks_of_month, null);

        final AppCompatCheckBox chk_weekly_1 = view.findViewById(R.id.chk_weekly_1);
        final AppCompatCheckBox chk_weekly_2 = view.findViewById(R.id.chk_weekly_2);
        final AppCompatCheckBox chk_weekly_3 = view.findViewById(R.id.chk_weekly_3);
        final AppCompatCheckBox chk_weekly_4 = view.findViewById(R.id.chk_weekly_4);
        final AppCompatCheckBox chk_weekly_5 = view.findViewById(R.id.chk_weekly_5);

        final String weekDayName = StringHelper.toWeekday(getModel().getParent().getTimeModel().getAlertTime(false));

        chk_weekly_1.setText(getString(R.string.repeat_weekly_custom_week1, weekDayName));
        chk_weekly_2.setText(getString(R.string.repeat_weekly_custom_week2, weekDayName));
        chk_weekly_3.setText(getString(R.string.repeat_weekly_custom_week3, weekDayName));
        chk_weekly_4.setText(getString(R.string.repeat_weekly_custom_week4, weekDayName));
        chk_weekly_5.setText(getString(R.string.repeat_weekly_custom_week5, weekDayName));

        for (int i = 0; i < getModel().getCustomWeeks().size(); i++) {
            int value = getModel().getCustomWeeks().get(i);
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
                case 4:
                    chk_weekly_5.setChecked(true);
                    break;
            }
        }

        builder.setView(view).setTitle("Select " + getString(R.string.repeat_weekly_custom))
                .setPositiveButton(getString(R.string.dialog_positive), (dialog, which) -> {
                    getModel().getCustomWeeks().clear();
                    if (chk_weekly_1.isChecked())
                        getModel().getCustomWeeks().add(0);
                    if (chk_weekly_2.isChecked())
                        getModel().getCustomWeeks().add(1);
                    if (chk_weekly_3.isChecked())
                        getModel().getCustomWeeks().add(2);
                    if (chk_weekly_4.isChecked())
                        getModel().getCustomWeeks().add(3);
                    if (chk_weekly_5.isChecked())
                        getModel().getCustomWeeks().add(4);
                    getModel().setEnabled(true);
                    getModel().setRepeatOption(RepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM);
                    ((ICustomRepeatDialogListener) getListener()).setCustomRepeatDialogModel(getModel());
                }).setNegativeButton(getString(R.string.dialog_negative), (dialog, which) -> {

        });

        return builder.create();
    }

}
