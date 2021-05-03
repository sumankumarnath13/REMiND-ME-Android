package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.viewModels.RepeatModel;

import java.util.Calendar;

public class DailyCustomRepeatDialog extends CustomRepeatDialogBase {

    public static final String TAG = "DailyCustomRepeatDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_daily_custom, null);

        final CheckBox chk_daily_sun = view.findViewById(R.id.chk_daily_sun);
        final CheckBox chk_daily_mon = view.findViewById(R.id.chk_daily_mon);
        final CheckBox chk_daily_tue = view.findViewById(R.id.chk_daily_tue);
        final CheckBox chk_daily_wed = view.findViewById(R.id.chk_daily_wed);
        final CheckBox chk_daily_thu = view.findViewById(R.id.chk_daily_thu);
        final CheckBox chk_daily_fri = view.findViewById(R.id.chk_daily_fri);
        final CheckBox chk_daily_sat = view.findViewById(R.id.chk_daily_sat);

        for (int i = 0; i < getModel().getCustomDays().size(); i++) {
            int value = getModel().getCustomDays().get(i);
            switch (value) {
                default:
                case Calendar.SUNDAY:
                    chk_daily_sun.setChecked(true);
                    break;
                case Calendar.MONDAY:
                    chk_daily_mon.setChecked(true);
                    break;
                case Calendar.TUESDAY:
                    chk_daily_tue.setChecked(true);
                    break;
                case Calendar.WEDNESDAY:
                    chk_daily_wed.setChecked(true);
                    break;
                case Calendar.THURSDAY:
                    chk_daily_thu.setChecked(true);
                    break;
                case Calendar.FRIDAY:
                    chk_daily_fri.setChecked(true);
                    break;
                case Calendar.SATURDAY:
                    chk_daily_sat.setChecked(true);
                    break;
            }
        }

        builder.setView(view)
                .setTitle("Select hours to Repeat")
                .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getModel().getCustomDays().clear();
                        if (chk_daily_sun.isChecked())
                            getModel().getCustomDays().add(Calendar.SUNDAY);
                        if (chk_daily_mon.isChecked())
                            getModel().getCustomDays().add(Calendar.MONDAY);
                        if (chk_daily_tue.isChecked())
                            getModel().getCustomDays().add(Calendar.TUESDAY);
                        if (chk_daily_wed.isChecked())
                            getModel().getCustomDays().add(Calendar.WEDNESDAY);
                        if (chk_daily_thu.isChecked())
                            getModel().getCustomDays().add(Calendar.THURSDAY);
                        if (chk_daily_fri.isChecked())
                            getModel().getCustomDays().add(Calendar.FRIDAY);
                        if (chk_daily_sat.isChecked())
                            getModel().getCustomDays().add(Calendar.SATURDAY);
                        getModel().setEnabled(true);
                        getModel().setRepeatOption(RepeatModel.ReminderRepeatOptions.DAILY_CUSTOM);
                        getListener().customRepeatDialogSetRepeatModel(getModel());

                    }
                }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

}
