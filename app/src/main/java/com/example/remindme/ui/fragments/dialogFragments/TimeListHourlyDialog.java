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
import com.example.remindme.ui.fragments.dialogFragments.common.TimeListDialogBase;
import com.example.remindme.viewModels.TimelyRepeatModel;

import java.util.Calendar;

public class TimeListHourlyDialog extends TimeListDialogBase {

    public static final String TAG = "TimeListHourlyDialog";

    private Calendar alertTimeCalendar;

    public Calendar getAlertTimeCalendar() {
        if (alertTimeCalendar == null) {
            alertTimeCalendar = Calendar.getInstance();
        }

        if (getModel() != null) {
            alertTimeCalendar.setTime(getModel().getParent().getTimeModel().getTime());
        }

        return alertTimeCalendar;
    }

    private int getMinute() {
        return getAlertTimeCalendar().get(Calendar.MINUTE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_fragment_input_time_list_hourly, null);

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

        final int min = getMinute();

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

        for (int i = 0; i < getModel().getTimelyRepeatModel().getTimeListTimes().size(); i++) {
            int value = getModel().getTimelyRepeatModel().getTimeListTimes().get(i).getHourOfDay();
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
                .setTitle(getString(R.string.format_heading_time_list, "Select", "hours"))
                .setPositiveButton(getString(R.string.acton_dialog_positive), (dialog, which) -> {
                    getModel().getTimelyRepeatModel().getTimeListTimes().clear();
                    //int day = 0;
                    if (chk_daily_0.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(0, min);
                    if (chk_daily_1.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(1, min);
                    if (chk_daily_2.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(2, min);
                    if (chk_daily_3.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(3, min);
                    if (chk_daily_4.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(4, min);
                    if (chk_daily_5.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(5, min);
                    if (chk_daily_6.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(6, min);
                    if (chk_daily_7.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(7, min);
                    if (chk_daily_8.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(8, min);
                    if (chk_daily_9.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(9, min);
                    if (chk_daily_10.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(10, min);
                    if (chk_daily_11.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(11, min);
                    if (chk_daily_12.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(12, min);
                    if (chk_daily_13.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(13, min);
                    if (chk_daily_14.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(14, min);
                    if (chk_daily_15.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(15, min);
                    if (chk_daily_16.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(16, min);
                    if (chk_daily_17.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(17, min);
                    if (chk_daily_18.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(18, min);
                    if (chk_daily_19.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(19, min);
                    if (chk_daily_20.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(20, min);
                    if (chk_daily_21.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(21, min);
                    if (chk_daily_22.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(22, min);
                    if (chk_daily_23.isChecked())
                        getModel().getTimelyRepeatModel().addTimeListTime(23, min);

                    if (getModel().getTimelyRepeatModel().getTimeListTimes().size() > 0) { // At least one is selected
                        getModel().getTimelyRepeatModel().setTimeListMode(TimelyRepeatModel.TimeListModes.SELECTED_HOURS);
                        getListener().setTimeListDialogModel(getModel());
                    }

                }).setNegativeButton(getString(R.string.acton_dialog_negative), (dialog, which) -> {

        });

        return builder.create();
    }

}
