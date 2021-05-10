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
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.TimeModel;

import java.util.Calendar;

public class HourlyTimeListDialog extends TimeListDialogBase {

    public static final String TAG = "TimeListInputHourlyDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.time_list_input_hourly_dialog, null);

        final AppCompatCheckBox chk_daily_0 = view.findViewById(R.id.chk_daily_0);
        final AppCompatCheckBox chk_daily_1 = view.findViewById(R.id.chk_daily_1);
        final AppCompatCheckBox chk_daily_2 = view.findViewById(R.id.chk_daily_2);
        final AppCompatCheckBox chk_daily_3 = view.findViewById(R.id.chk_daily_3);
        final AppCompatCheckBox chk_daily_4 = view.findViewById(R.id.chk_daily_4);
        final AppCompatCheckBox chk_daily_5 = view.findViewById(R.id.chk_daily_5);
        final AppCompatCheckBox chk_daily_6 = view.findViewById(R.id.chk_daily_6);
        final AppCompatCheckBox chk_daily_7 = view.findViewById(R.id.chk_daily_7);
        final AppCompatCheckBox chk_daily_8 = view.findViewById(R.id.chk_daily_8);
        final AppCompatCheckBox chk_daily_9 = view.findViewById(R.id.chk_daily_9);
        final AppCompatCheckBox chk_daily_10 = view.findViewById(R.id.chk_daily_10);
        final AppCompatCheckBox chk_daily_11 = view.findViewById(R.id.chk_daily_11);

        final AppCompatCheckBox chk_daily_12 = view.findViewById(R.id.chk_daily_12);
        final AppCompatCheckBox chk_daily_13 = view.findViewById(R.id.chk_daily_13);
        final AppCompatCheckBox chk_daily_14 = view.findViewById(R.id.chk_daily_14);
        final AppCompatCheckBox chk_daily_15 = view.findViewById(R.id.chk_daily_15);
        final AppCompatCheckBox chk_daily_16 = view.findViewById(R.id.chk_daily_16);
        final AppCompatCheckBox chk_daily_17 = view.findViewById(R.id.chk_daily_17);
        final AppCompatCheckBox chk_daily_18 = view.findViewById(R.id.chk_daily_18);
        final AppCompatCheckBox chk_daily_19 = view.findViewById(R.id.chk_daily_19);
        final AppCompatCheckBox chk_daily_20 = view.findViewById(R.id.chk_daily_20);
        final AppCompatCheckBox chk_daily_21 = view.findViewById(R.id.chk_daily_21);
        final AppCompatCheckBox chk_daily_22 = view.findViewById(R.id.chk_daily_22);
        final AppCompatCheckBox chk_daily_23 = view.findViewById(R.id.chk_daily_23);

        final Calendar c = Calendar.getInstance();
        c.setTime(getModel().getTime());
        final int min = c.get(Calendar.MINUTE);

        if (AppSettingsHelper.getInstance().isUse24hourTime()) {

            int hour = 0;

            chk_daily_0.setText(StringHelper.get24(hour++, min));
            chk_daily_1.setText(StringHelper.get24(hour++, min));
            chk_daily_2.setText(StringHelper.get24(hour++, min));
            chk_daily_3.setText(StringHelper.get24(hour++, min));
            chk_daily_4.setText(StringHelper.get24(hour++, min));
            chk_daily_5.setText(StringHelper.get24(hour++, min));
            chk_daily_6.setText(StringHelper.get24(hour++, min));
            chk_daily_7.setText(StringHelper.get24(hour++, min));
            chk_daily_8.setText(StringHelper.get24(hour++, min));
            chk_daily_9.setText(StringHelper.get24(hour++, min));
            chk_daily_10.setText(StringHelper.get24(hour++, min));
            chk_daily_11.setText(StringHelper.get24(hour++, min));

            chk_daily_12.setText(StringHelper.get24(hour++, min));
            chk_daily_13.setText(StringHelper.get24(hour++, min));
            chk_daily_14.setText(StringHelper.get24(hour++, min));
            chk_daily_15.setText(StringHelper.get24(hour++, min));
            chk_daily_16.setText(StringHelper.get24(hour++, min));
            chk_daily_17.setText(StringHelper.get24(hour++, min));
            chk_daily_18.setText(StringHelper.get24(hour++, min));
            chk_daily_19.setText(StringHelper.get24(hour++, min));
            chk_daily_20.setText(StringHelper.get24(hour++, min));
            chk_daily_21.setText(StringHelper.get24(hour++, min));
            chk_daily_22.setText(StringHelper.get24(hour++, min));
            chk_daily_23.setText(StringHelper.get24(hour, min));

        } else {

            int hour = 0;

            chk_daily_0.setText(StringHelper.get12(hour++, min));
            chk_daily_1.setText(StringHelper.get12(hour++, min));
            chk_daily_2.setText(StringHelper.get12(hour++, min));
            chk_daily_3.setText(StringHelper.get12(hour++, min));
            chk_daily_4.setText(StringHelper.get12(hour++, min));
            chk_daily_5.setText(StringHelper.get12(hour++, min));
            chk_daily_6.setText(StringHelper.get12(hour++, min));
            chk_daily_7.setText(StringHelper.get12(hour++, min));
            chk_daily_8.setText(StringHelper.get12(hour++, min));
            chk_daily_9.setText(StringHelper.get12(hour++, min));
            chk_daily_10.setText(StringHelper.get12(hour++, min));
            chk_daily_11.setText(StringHelper.get12(hour++, min));

            chk_daily_12.setText(StringHelper.get12(hour++, min));
            chk_daily_13.setText(StringHelper.get12(hour++, min));
            chk_daily_14.setText(StringHelper.get12(hour++, min));
            chk_daily_15.setText(StringHelper.get12(hour++, min));
            chk_daily_16.setText(StringHelper.get12(hour++, min));
            chk_daily_17.setText(StringHelper.get12(hour++, min));
            chk_daily_18.setText(StringHelper.get12(hour++, min));
            chk_daily_19.setText(StringHelper.get12(hour++, min));
            chk_daily_20.setText(StringHelper.get12(hour++, min));
            chk_daily_21.setText(StringHelper.get12(hour++, min));
            chk_daily_22.setText(StringHelper.get12(hour++, min));
            chk_daily_23.setText(StringHelper.get12(hour, min));

        }

        for (int i = 0; i < getModel().getTimeListHours().size(); i++) {
            int value = getModel().getTimeListHours().get(i);
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
                .setPositiveButton(getString(R.string.dialog_positive), (dialog, which) -> {
                    getModel().getTimeListHours().clear();
                    //int day = 0;
                    if (chk_daily_0.isChecked())
                        getModel().addTimeListHour(0);
                    if (chk_daily_1.isChecked())
                        getModel().addTimeListHour(1);
                    if (chk_daily_2.isChecked())
                        getModel().addTimeListHour(2);
                    if (chk_daily_3.isChecked())
                        getModel().addTimeListHour(3);
                    if (chk_daily_4.isChecked())
                        getModel().addTimeListHour(4);
                    if (chk_daily_5.isChecked())
                        getModel().addTimeListHour(5);
                    if (chk_daily_6.isChecked())
                        getModel().addTimeListHour(6);
                    if (chk_daily_7.isChecked())
                        getModel().addTimeListHour(7);
                    if (chk_daily_8.isChecked())
                        getModel().addTimeListHour(8);
                    if (chk_daily_9.isChecked())
                        getModel().addTimeListHour(9);
                    if (chk_daily_10.isChecked())
                        getModel().addTimeListHour(10);
                    if (chk_daily_11.isChecked())
                        getModel().addTimeListHour(11);
                    if (chk_daily_12.isChecked())
                        getModel().addTimeListHour(12);
                    if (chk_daily_13.isChecked())
                        getModel().addTimeListHour(13);
                    if (chk_daily_14.isChecked())
                        getModel().addTimeListHour(14);
                    if (chk_daily_15.isChecked())
                        getModel().addTimeListHour(15);
                    if (chk_daily_16.isChecked())
                        getModel().addTimeListHour(16);
                    if (chk_daily_17.isChecked())
                        getModel().addTimeListHour(17);
                    if (chk_daily_18.isChecked())
                        getModel().addTimeListHour(18);
                    if (chk_daily_19.isChecked())
                        getModel().addTimeListHour(19);
                    if (chk_daily_20.isChecked())
                        getModel().addTimeListHour(20);
                    if (chk_daily_21.isChecked())
                        getModel().addTimeListHour(21);
                    if (chk_daily_22.isChecked())
                        getModel().addTimeListHour(22);
                    if (chk_daily_23.isChecked())
                        getModel().addTimeListHour(23);

                    if (getModel().getTimeListHours().size() > 0) { // At least one is selected
                        getModel().setTimeListMode(TimeModel.TimeListModes.HOURLY);
                    } else {
                        getModel().setTimeListMode(TimeModel.TimeListModes.NONE);
                    }

                    getListener().setTimeListDialogModel(getModel());

                }).setNegativeButton(getString(R.string.dialog_negative), (dialog, which) -> {

        });

        return builder.create();
    }

}
