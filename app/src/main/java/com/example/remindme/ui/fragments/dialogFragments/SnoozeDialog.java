package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.common.DialogFragmentBase;
import com.example.remindme.viewModels.SnoozeModel;
import com.example.remindme.viewModels.factories.SnoozeViewModelFactory;

public class SnoozeDialog extends DialogFragmentBase {

    public static final String TAG = "SnoozeDialog";

    public interface ISnoozeInputDialogListener {

        void onSetListenerSnoozeModel(SnoozeModel model);

        SnoozeModel onGetListenerSnoozeModel();

    }

    private ISnoozeInputDialogListener listener;

    private ISnoozeInputDialogListener getListener() {
        if (listener == null) {
            listener = super.getListener(ISnoozeInputDialogListener.class);
        }
        return listener;
    }

    private SnoozeModel model;
    private AppCompatRadioButton rdo_reminder_snooze_m5;
    private AppCompatRadioButton rdo_reminder_snooze_m10;
    private AppCompatRadioButton rdo_reminder_snooze_m15;
    private AppCompatRadioButton rdo_reminder_snooze_m30;
    private AppCompatRadioButton rdo_reminder_snooze_r3;
    private AppCompatRadioButton rdo_reminder_snooze_r5;
    private AppCompatRadioButton rdo_reminder_snooze_rc;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Dialog listener is not set!");
            dismiss();
            return;
        }

        model = new ViewModelProvider(this, new SnoozeViewModelFactory(getListener().onGetListenerSnoozeModel())).get(SnoozeModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_fragment_input_snooze, null);

        final SwitchCompat sw_reminder_snooze_enabled = view.findViewById(R.id.sw_reminder_snooze);
        sw_reminder_snooze_enabled.setChecked(model.isEnable());
        sw_reminder_snooze_enabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing()) {
                model.setEnable(isChecked);
                refresh();
            }
        });


        rdo_reminder_snooze_m5 = view.findViewById(R.id.rdo_reminder_snooze_m5);
        rdo_reminder_snooze_m5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                model.setInterval(SnoozeModel.SnoozeIntervals.M5);
            }
        });

        rdo_reminder_snooze_m10 = view.findViewById(R.id.rdo_reminder_snooze_m10);
        rdo_reminder_snooze_m10.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                model.setInterval(SnoozeModel.SnoozeIntervals.M10);
            }
        });

        rdo_reminder_snooze_m15 = view.findViewById(R.id.rdo_reminder_snooze_m15);
        rdo_reminder_snooze_m15.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                model.setInterval(SnoozeModel.SnoozeIntervals.M15);
            }
        });

        rdo_reminder_snooze_m30 = view.findViewById(R.id.rdo_reminder_snooze_m30);
        rdo_reminder_snooze_m30.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                model.setInterval(SnoozeModel.SnoozeIntervals.M30);
            }
        });

        rdo_reminder_snooze_r3 = view.findViewById(R.id.rdo_reminder_snooze_r3);
        rdo_reminder_snooze_r3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                model.setLimit(SnoozeModel.SnoozeLimits.R3);
            }
        });

        rdo_reminder_snooze_r5 = view.findViewById(R.id.rdo_reminder_snooze_r5);
        rdo_reminder_snooze_r5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                model.setLimit(SnoozeModel.SnoozeLimits.R5);
            }
        });

        rdo_reminder_snooze_rc = view.findViewById(R.id.rdo_reminder_snooze_rc);
        rdo_reminder_snooze_rc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                model.setLimit(SnoozeModel.SnoozeLimits.RC);
            }
        });


        builder.setView(view)
                .setPositiveButton(getString(R.string.acton_dialog_positive), (dialog, which) -> getListener().onSetListenerSnoozeModel(model))
                .setNegativeButton(getString(R.string.acton_dialog_negative), (dialog, which) -> {

                });

        refresh();
        return builder.create();
    }

    @Override
    protected void onUIRefresh() {

        switch (model.getInterval()) {
            default:
            case M5:
                rdo_reminder_snooze_m5.setChecked(true);
                break;
            case M10:
                rdo_reminder_snooze_m10.setChecked(true);
                break;
            case M15:
                rdo_reminder_snooze_m15.setChecked(true);
                break;
            case M30:
                rdo_reminder_snooze_m30.setChecked(true);
                break;
        }

        switch (model.getLimit()) {
            default:
            case R3:
                rdo_reminder_snooze_r3.setChecked(true);
                break;
            case R5:
                rdo_reminder_snooze_r5.setChecked(true);
                break;
            case RC:
                rdo_reminder_snooze_rc.setChecked(true);
                break;
        }

        rdo_reminder_snooze_m5.setEnabled(model.isEnable());
        rdo_reminder_snooze_m10.setEnabled(model.isEnable());
        rdo_reminder_snooze_m15.setEnabled(model.isEnable());
        rdo_reminder_snooze_m30.setEnabled(model.isEnable());
        rdo_reminder_snooze_r3.setEnabled(model.isEnable());
        rdo_reminder_snooze_r5.setEnabled(model.isEnable());
        rdo_reminder_snooze_rc.setEnabled(model.isEnable());
    }

}
