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
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.ui.main.IReminderRepeatListener;
import com.example.remindme.ui.main.IRepeatInputDialog;
import com.example.remindme.viewModels.ReminderRepeatModel;

import java.util.Calendar;

public class DialogReminderRepeatInputHourlyCustom extends DialogFragment {

    private ReminderRepeatModel model;
    private boolean isCancel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            IReminderRepeatListener listener = (IReminderRepeatListener) context;
            model = listener.getRepeatModel();
            //transaction = listener.getTransaction();
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
            final IRepeatInputDialog hostDialog = (IRepeatInputDialog) fragment;
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

        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_hourly_custom, null);

        final CheckBox chk_daily_0 = view.findViewById(R.id.chk_daily_0);
        final CheckBox chk_daily_1 = view.findViewById(R.id.chk_daily_1);
        final CheckBox chk_daily_2 = view.findViewById(R.id.chk_daily_2);
        final CheckBox chk_daily_3 = view.findViewById(R.id.chk_daily_3);
        final CheckBox chk_daily_4 = view.findViewById(R.id.chk_daily_4);
        final CheckBox chk_daily_5 = view.findViewById(R.id.chk_daily_5);
        final CheckBox chk_daily_6 = view.findViewById(R.id.chk_daily_6);
        final CheckBox chk_daily_7 = view.findViewById(R.id.chk_daily_7);
        final CheckBox chk_daily_8 = view.findViewById(R.id.chk_daily_8);
        final CheckBox chk_daily_9 = view.findViewById(R.id.chk_daily_9);
        final CheckBox chk_daily_10 = view.findViewById(R.id.chk_daily_10);
        final CheckBox chk_daily_11 = view.findViewById(R.id.chk_daily_11);

        final CheckBox chk_daily_12 = view.findViewById(R.id.chk_daily_12);
        final CheckBox chk_daily_13 = view.findViewById(R.id.chk_daily_13);
        final CheckBox chk_daily_14 = view.findViewById(R.id.chk_daily_14);
        final CheckBox chk_daily_15 = view.findViewById(R.id.chk_daily_15);
        final CheckBox chk_daily_16 = view.findViewById(R.id.chk_daily_16);
        final CheckBox chk_daily_17 = view.findViewById(R.id.chk_daily_17);
        final CheckBox chk_daily_18 = view.findViewById(R.id.chk_daily_18);
        final CheckBox chk_daily_19 = view.findViewById(R.id.chk_daily_19);
        final CheckBox chk_daily_20 = view.findViewById(R.id.chk_daily_20);
        final CheckBox chk_daily_21 = view.findViewById(R.id.chk_daily_21);
        final CheckBox chk_daily_22 = view.findViewById(R.id.chk_daily_22);
        final CheckBox chk_daily_23 = view.findViewById(R.id.chk_daily_23);

        Calendar c = Calendar.getInstance();
        c.setTime(model.getReminderTime());
        final int min = c.get(Calendar.MINUTE);

        chk_daily_5.setText("5:" + min + " am");
        chk_daily_6.setText("6:" + min + " am");
        chk_daily_7.setText("7:" + min + " am");
        chk_daily_8.setText("8:" + min + " am");
        chk_daily_9.setText("9:" + min + " am");
        chk_daily_10.setText("10:" + min + " am");
        chk_daily_11.setText("11:" + min + " am");
        chk_daily_12.setText("12:" + min + " pm");
        chk_daily_13.setText("1:" + min + " pm");
        chk_daily_14.setText("2:" + min + " pm");
        chk_daily_15.setText("3:" + min + " pm");
        chk_daily_16.setText("4:" + min + " pm");

        chk_daily_17.setText("5:" + min + " pm");
        chk_daily_18.setText("6:" + min + " pm");
        chk_daily_19.setText("7:" + min + " pm");
        chk_daily_20.setText("8:" + min + " pm");
        chk_daily_21.setText("9:" + min + " pm");
        chk_daily_22.setText("10:" + min + " pm");
        chk_daily_23.setText("11:" + min + " pm");
        chk_daily_0.setText("12:" + min + " am");
        chk_daily_1.setText("1:" + min + " am");
        chk_daily_2.setText("2:" + min + " am");
        chk_daily_3.setText("3:" + min + " am");
        chk_daily_4.setText("4:" + min + " am");

        for (int i = 0; i < model.customHours.size(); i++) {
            int value = model.customHours.get(i);
            switch (value) {
                default:
                case 0:
                    chk_daily_0.setChecked(true);
                    break;
                case 1:
                    chk_daily_1.setChecked(true);
                    break;
                case 2:
                    chk_daily_2.setChecked(true);
                    break;
                case 3:
                    chk_daily_3.setChecked(true);
                    break;
                case 4:
                    chk_daily_4.setChecked(true);
                    break;
                case 5:
                    chk_daily_5.setChecked(true);
                    break;
                case 6:
                    chk_daily_6.setChecked(true);
                    break;
                case 7:
                    chk_daily_7.setChecked(true);
                    break;
                case 8:
                    chk_daily_8.setChecked(true);
                    break;
                case 9:
                    chk_daily_9.setChecked(true);
                    break;
                case 10:
                    chk_daily_10.setChecked(true);
                    break;
                case 11:
                    chk_daily_11.setChecked(true);
                    break;


                case 12:
                    chk_daily_12.setChecked(true);
                    break;
                case 13:
                    chk_daily_13.setChecked(true);
                    break;
                case 14:
                    chk_daily_14.setChecked(true);
                    break;
                case 15:
                    chk_daily_15.setChecked(true);
                    break;
                case 16:
                    chk_daily_16.setChecked(true);
                    break;
                case 17:
                    chk_daily_17.setChecked(true);
                    break;
                case 18:
                    chk_daily_18.setChecked(true);
                    break;
                case 19:
                    chk_daily_19.setChecked(true);
                    break;
                case 20:
                    chk_daily_20.setChecked(true);
                    break;
                case 21:
                    chk_daily_21.setChecked(true);
                    break;
                case 22:
                    chk_daily_22.setChecked(true);
                    break;
                case 23:
                    chk_daily_23.setChecked(true);
                    break;
            }
        }

        builder.setView(view)
                .setTitle("Select hours to Repeat")
                .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        model.customHours.clear();
                        //int day = 0;
                        if (chk_daily_0.isChecked()) model.customHours.add(0);
                        if (chk_daily_1.isChecked()) model.customHours.add(1);
                        if (chk_daily_2.isChecked()) model.customHours.add(2);
                        if (chk_daily_3.isChecked()) model.customHours.add(3);
                        if (chk_daily_4.isChecked()) model.customHours.add(4);
                        if (chk_daily_5.isChecked()) model.customHours.add(5);
                        if (chk_daily_6.isChecked()) model.customHours.add(6);
                        if (chk_daily_7.isChecked()) model.customHours.add(7);
                        if (chk_daily_8.isChecked()) model.customHours.add(8);
                        if (chk_daily_9.isChecked()) model.customHours.add(9);
                        if (chk_daily_10.isChecked()) model.customHours.add(10);
                        if (chk_daily_11.isChecked()) model.customHours.add(11);
                        if (chk_daily_12.isChecked()) model.customHours.add(12);
                        if (chk_daily_13.isChecked()) model.customHours.add(13);
                        if (chk_daily_14.isChecked()) model.customHours.add(14);
                        if (chk_daily_15.isChecked()) model.customHours.add(15);
                        if (chk_daily_16.isChecked()) model.customHours.add(16);
                        if (chk_daily_17.isChecked()) model.customHours.add(17);
                        if (chk_daily_18.isChecked()) model.customHours.add(18);
                        if (chk_daily_19.isChecked()) model.customHours.add(19);
                        if (chk_daily_20.isChecked()) model.customHours.add(20);
                        if (chk_daily_21.isChecked()) model.customHours.add(21);
                        if (chk_daily_22.isChecked()) model.customHours.add(22);
                        if (chk_daily_23.isChecked()) model.customHours.add(23);

                        model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.HOURLY_CUSTOM);
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
