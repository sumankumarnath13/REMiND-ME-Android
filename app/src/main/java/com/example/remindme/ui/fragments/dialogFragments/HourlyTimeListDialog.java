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
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.TimeViewModel;

import java.util.Calendar;

public class HourlyTimeListDialog extends TimeListDialogBase {

    public static final String TAG = "TimeListInputHourlyDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.time_list_input_hourly_dialog, null);

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

        final Calendar c = Calendar.getInstance();
        c.setTime(getModel().getUpdatedTime());
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

        for (int i = 0; i < getModel().getHourlyTimes().size(); i++) {
            int value = getModel().getHourlyTimes().get(i);
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
                        getModel().getHourlyTimes().clear();
                        //int day = 0;
                        if (chk_daily_0.isChecked()) getModel().getHourlyTimes().add(0);
                        if (chk_daily_1.isChecked()) getModel().getHourlyTimes().add(1);
                        if (chk_daily_2.isChecked()) getModel().getHourlyTimes().add(2);
                        if (chk_daily_3.isChecked()) getModel().getHourlyTimes().add(3);
                        if (chk_daily_4.isChecked()) getModel().getHourlyTimes().add(4);
                        if (chk_daily_5.isChecked()) getModel().getHourlyTimes().add(5);
                        if (chk_daily_6.isChecked()) getModel().getHourlyTimes().add(6);
                        if (chk_daily_7.isChecked()) getModel().getHourlyTimes().add(7);
                        if (chk_daily_8.isChecked()) getModel().getHourlyTimes().add(8);
                        if (chk_daily_9.isChecked()) getModel().getHourlyTimes().add(9);
                        if (chk_daily_10.isChecked()) getModel().getHourlyTimes().add(10);
                        if (chk_daily_11.isChecked()) getModel().getHourlyTimes().add(11);
                        if (chk_daily_12.isChecked()) getModel().getHourlyTimes().add(12);
                        if (chk_daily_13.isChecked()) getModel().getHourlyTimes().add(13);
                        if (chk_daily_14.isChecked()) getModel().getHourlyTimes().add(14);
                        if (chk_daily_15.isChecked()) getModel().getHourlyTimes().add(15);
                        if (chk_daily_16.isChecked()) getModel().getHourlyTimes().add(16);
                        if (chk_daily_17.isChecked()) getModel().getHourlyTimes().add(17);
                        if (chk_daily_18.isChecked()) getModel().getHourlyTimes().add(18);
                        if (chk_daily_19.isChecked()) getModel().getHourlyTimes().add(19);
                        if (chk_daily_20.isChecked()) getModel().getHourlyTimes().add(20);
                        if (chk_daily_21.isChecked()) getModel().getHourlyTimes().add(21);
                        if (chk_daily_22.isChecked()) getModel().getHourlyTimes().add(22);
                        if (chk_daily_23.isChecked()) getModel().getHourlyTimes().add(23);

                        getModel().setTimeListMode(TimeViewModel.TimeListModes.HOURLY);
                        getListener().timeListDialogSetTimeViewModel(getModel());

                    }
                }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

}
