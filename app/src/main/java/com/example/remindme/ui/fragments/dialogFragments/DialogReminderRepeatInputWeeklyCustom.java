package com.example.remindme.ui.fragments.dialogFragments;

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
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.ReminderRepeatModel;

public class DialogReminderRepeatInputWeeklyCustom extends DialogFragment {

    private ReminderRepeatModel model;
    private boolean isCancel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            DialogReminderRepeatInput.IRepeatInputDialogListener listener = (DialogReminderRepeatInput.IRepeatInputDialogListener) context;
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
            commitToParent();
        }
    }

    private void commitToParent() {
        final Fragment fragment = getParentFragmentManager().findFragmentByTag("repeatInput");
        if (fragment != null) {
            final IRepeatInputChildDialogListener hostDialog = (IRepeatInputChildDialogListener) fragment;
            hostDialog.setChanges(model);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

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
                case 4:
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

                model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM);
                commitToParent();
            }
        }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                commitToParent();
            }
        });

        return builder.create();
    }

}
