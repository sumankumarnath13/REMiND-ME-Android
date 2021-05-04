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
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.RepeatModel;

public class WeeklyCustomRepeatDialog extends CustomRepeatDialogBase {

    public static final String TAG = "WeeklyCustomRepeatDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.reminder_repeat_weekly_custom_dialog, null);

        final CheckBox chk_weekly_1 = view.findViewById(R.id.chk_weekly_1);
        final CheckBox chk_weekly_2 = view.findViewById(R.id.chk_weekly_2);
        final CheckBox chk_weekly_3 = view.findViewById(R.id.chk_weekly_3);
        final CheckBox chk_weekly_4 = view.findViewById(R.id.chk_weekly_4);
        final CheckBox chk_weekly_5 = view.findViewById(R.id.chk_weekly_5);

        final String weekDayName = StringHelper.toWeekday(getModel().getParent().getTimeModel().getAlertTime(false));

        chk_weekly_1.setText("On 1st " + weekDayName + " of the month");
        chk_weekly_2.setText("On 2nd " + weekDayName + " of the month");
        chk_weekly_3.setText("On 3rd " + weekDayName + " of the month");
        chk_weekly_4.setText("On 4th " + weekDayName + " of the month");
        chk_weekly_5.setText("On 5th " + weekDayName + " of the month (if exists)");

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

        builder.setView(view).setTitle("Select weeks to Repeat").setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getModel().getCustomWeeks().clear();
                if (chk_weekly_1.isChecked()) getModel().getCustomWeeks().add(0);
                if (chk_weekly_2.isChecked()) getModel().getCustomWeeks().add(1);
                if (chk_weekly_3.isChecked()) getModel().getCustomWeeks().add(2);
                if (chk_weekly_4.isChecked()) getModel().getCustomWeeks().add(3);
                if (chk_weekly_5.isChecked()) getModel().getCustomWeeks().add(4);
                getModel().setEnabled(true);
                getModel().setRepeatOption(RepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM);
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
