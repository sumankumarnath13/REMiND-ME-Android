package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.viewModels.TimeViewModel;

import java.util.Calendar;

public class CustomTimeListDialog extends TimeListDialogBase {

    public static final String TAG = "CustomTimeListDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.custom_time_list_dialog, null);


        final Calendar c = Calendar.getInstance();
        c.setTime(getModel().getUpdatedTime());
        final int min = c.get(Calendar.MINUTE);


        builder.setView(view)
                .setTitle("Select hours to Repeat")
                .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getModel().getHourlyTimes().clear();

                        getModel().clearTimes();
                        getModel().addTime(c.getTime());
                        getModel().setTimeListMode(TimeViewModel.TimeListModes.CUSTOM);
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
