package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.viewModels.SnoozeModel;
import com.example.remindme.viewModels.factories.SnoozeViewModelFactory;

public class SnoozeDialog extends RefreshableDialogFragmentBase {
    public static final String TAG = "SnoozeDialog";

    private ISnoozeInputDialogListener listener;
    private SnoozeModel model;
    private AppCompatRadioButton rdo_reminder_snooze_m5;
    private AppCompatRadioButton rdo_reminder_snooze_m10;
    private AppCompatRadioButton rdo_reminder_snooze_m15;
    private AppCompatRadioButton rdo_reminder_snooze_m30;
    private AppCompatRadioButton rdo_reminder_snooze_r3;
    private AppCompatRadioButton rdo_reminder_snooze_r5;
    private AppCompatRadioButton rdo_reminder_snooze_rc;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ISnoozeInputDialogListener) context;
            model = new ViewModelProvider(this, new SnoozeViewModelFactory(listener.getSnoozeDialogModel().getParent())).get(SnoozeModel.class);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement ISnoozeInputDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.reminder_snooze_dialog, null);

        final SwitchCompat sw_reminder_snooze_enabled = view.findViewById(R.id.sw_reminder_snooze);
        sw_reminder_snooze_enabled.setChecked(model.isEnable());
        sw_reminder_snooze_enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isRefreshing()) {
                    model.setEnable(isChecked);
                    refresh();
                }
            }
        });


        rdo_reminder_snooze_m5 = view.findViewById(R.id.rdo_reminder_snooze_m5);
        rdo_reminder_snooze_m5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    model.setInterval(SnoozeModel.SnoozeIntervals.M5);
                }
            }
        });

        rdo_reminder_snooze_m10 = view.findViewById(R.id.rdo_reminder_snooze_m10);
        rdo_reminder_snooze_m10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    model.setInterval(SnoozeModel.SnoozeIntervals.M10);
                }
            }
        });

        rdo_reminder_snooze_m15 = view.findViewById(R.id.rdo_reminder_snooze_m15);
        rdo_reminder_snooze_m15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    model.setInterval(SnoozeModel.SnoozeIntervals.M15);
                }
            }
        });

        rdo_reminder_snooze_m30 = view.findViewById(R.id.rdo_reminder_snooze_m30);
        rdo_reminder_snooze_m30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    model.setInterval(SnoozeModel.SnoozeIntervals.M30);
                }
            }
        });

        rdo_reminder_snooze_r3 = view.findViewById(R.id.rdo_reminder_snooze_r3);
        rdo_reminder_snooze_r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    model.setLimit(SnoozeModel.SnoozeLimits.R3);
                }
            }
        });

        rdo_reminder_snooze_r5 = view.findViewById(R.id.rdo_reminder_snooze_r5);
        rdo_reminder_snooze_r5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    model.setLimit(SnoozeModel.SnoozeLimits.R5);
                }
            }
        });

        rdo_reminder_snooze_rc = view.findViewById(R.id.rdo_reminder_snooze_rc);
        rdo_reminder_snooze_rc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    model.setLimit(SnoozeModel.SnoozeLimits.RC);
                }
            }
        });


        builder.setView(view)
                .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.setSnoozeDialogModel(model);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        refresh();
        return builder.create();
    }

    @Override
    protected void onUIRefresh() {
        rdo_reminder_snooze_m5.setEnabled(model.isEnable());
        rdo_reminder_snooze_m10.setEnabled(model.isEnable());
        rdo_reminder_snooze_m15.setEnabled(model.isEnable());
        rdo_reminder_snooze_m30.setEnabled(model.isEnable());
        rdo_reminder_snooze_r3.setEnabled(model.isEnable());
        rdo_reminder_snooze_r5.setEnabled(model.isEnable());
        rdo_reminder_snooze_rc.setEnabled(model.isEnable());
    }

    public interface ISnoozeInputDialogListener {

        void setSnoozeDialogModel(SnoozeModel model);

        SnoozeModel getSnoozeDialogModel();

    }
}
