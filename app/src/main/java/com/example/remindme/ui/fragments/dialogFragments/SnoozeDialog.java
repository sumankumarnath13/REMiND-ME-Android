package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.viewModels.SnoozeModel;
import com.example.remindme.viewModels.factories.SnoozeViewModelFactory;

public class SnoozeDialog extends DialogFragment {
    public static final String TAG = "SnoozeDialog";

    private ISnoozeInputDialogListener listener;
    private SnoozeModel model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ISnoozeInputDialogListener) context;
            model = new ViewModelProvider(this, new SnoozeViewModelFactory(listener.getSnoozeDialogModel().getParent())).get(SnoozeModel.class);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderSnoozeListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_reminder_input_snooze, null);

        final RadioButton rdo_reminder_snooze_m5 = view.findViewById(R.id.rdo_reminder_snooze_m5);
        final RadioButton rdo_reminder_snooze_m10 = view.findViewById(R.id.rdo_reminder_snooze_m10);
        final RadioButton rdo_reminder_snooze_m15 = view.findViewById(R.id.rdo_reminder_snooze_m15);
        final RadioButton rdo_reminder_snooze_m30 = view.findViewById(R.id.rdo_reminder_snooze_m30);
        final RadioButton rdo_reminder_snooze_r3 = view.findViewById(R.id.rdo_reminder_snooze_r3);
        final RadioButton rdo_reminder_snooze_r5 = view.findViewById(R.id.rdo_reminder_snooze_r5);
        final RadioButton rdo_reminder_snooze_rc = view.findViewById(R.id.rdo_reminder_snooze_rc);

        final SwitchCompat sw_reminder_snooze_enabled = view.findViewById(R.id.sw_reminder_snooze);
        sw_reminder_snooze_enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rdo_reminder_snooze_m5.setEnabled(isChecked);
                rdo_reminder_snooze_m10.setEnabled(isChecked);
                rdo_reminder_snooze_m15.setEnabled(isChecked);
                rdo_reminder_snooze_m30.setEnabled(isChecked);
                rdo_reminder_snooze_r3.setEnabled(isChecked);
                rdo_reminder_snooze_r5.setEnabled(isChecked);
                rdo_reminder_snooze_rc.setEnabled(isChecked);

                if (OsHelper.isLollipopOrLater()) {
                    if (isChecked) {
                        rdo_reminder_snooze_m5.setButtonTintList(getResources().getColorStateList(R.color.bg_success));
                        rdo_reminder_snooze_m10.setButtonTintList(getResources().getColorStateList(R.color.bg_warning));
                        rdo_reminder_snooze_m15.setButtonTintList(getResources().getColorStateList(R.color.bg_info));
                        rdo_reminder_snooze_m30.setButtonTintList(getResources().getColorStateList(R.color.bg_danger));
                        rdo_reminder_snooze_r3.setButtonTintList(getResources().getColorStateList(R.color.bg_success));
                        rdo_reminder_snooze_r5.setButtonTintList(getResources().getColorStateList(R.color.bg_warning));
                        rdo_reminder_snooze_rc.setButtonTintList(getResources().getColorStateList(R.color.bg_info));
                    } else {

                        if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT) {
                            rdo_reminder_snooze_m5.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                            rdo_reminder_snooze_m10.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                            rdo_reminder_snooze_m15.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                            rdo_reminder_snooze_m30.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                            rdo_reminder_snooze_r3.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                            rdo_reminder_snooze_r5.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                            rdo_reminder_snooze_rc.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                        } else {
                            rdo_reminder_snooze_m5.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                            rdo_reminder_snooze_m10.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                            rdo_reminder_snooze_m15.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                            rdo_reminder_snooze_m30.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                            rdo_reminder_snooze_r3.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                            rdo_reminder_snooze_r5.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                            rdo_reminder_snooze_rc.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                        }
                    }
                }
            }
        });

        sw_reminder_snooze_enabled.setChecked(model.isEnable());

        if (OsHelper.isLollipopOrLater()) {
            if (model.isEnable()) {
                rdo_reminder_snooze_m5.setButtonTintList(getResources().getColorStateList(R.color.bg_success));
                rdo_reminder_snooze_m10.setButtonTintList(getResources().getColorStateList(R.color.bg_warning));
                rdo_reminder_snooze_m15.setButtonTintList(getResources().getColorStateList(R.color.bg_info));
                rdo_reminder_snooze_m30.setButtonTintList(getResources().getColorStateList(R.color.bg_danger));
                rdo_reminder_snooze_r3.setButtonTintList(getResources().getColorStateList(R.color.bg_success));
                rdo_reminder_snooze_r5.setButtonTintList(getResources().getColorStateList(R.color.bg_warning));
                rdo_reminder_snooze_rc.setButtonTintList(getResources().getColorStateList(R.color.bg_info));
            } else {

                if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT) {
                    rdo_reminder_snooze_m5.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                    rdo_reminder_snooze_m10.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                    rdo_reminder_snooze_m15.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                    rdo_reminder_snooze_m30.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                    rdo_reminder_snooze_r3.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                    rdo_reminder_snooze_r5.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));
                    rdo_reminder_snooze_rc.setButtonTintList(getResources().getColorStateList(R.color.border_color_light));

                } else {
                    rdo_reminder_snooze_m5.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                    rdo_reminder_snooze_m10.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                    rdo_reminder_snooze_m15.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                    rdo_reminder_snooze_m30.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                    rdo_reminder_snooze_r3.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                    rdo_reminder_snooze_r5.setButtonTintList(getResources().getColorStateList(R.color.border_color));
                    rdo_reminder_snooze_rc.setButtonTintList(getResources().getColorStateList(R.color.border_color));

                }
            }
        }

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

        builder.setView(view)
                .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        model.setEnable(sw_reminder_snooze_enabled.isChecked());

                        if (rdo_reminder_snooze_m5.isChecked()) {
                            model.setInterval(SnoozeModel.SnoozeIntervals.M5);
                        } else if (rdo_reminder_snooze_m10.isChecked()) {
                            model.setInterval(SnoozeModel.SnoozeIntervals.M10);
                        } else if (rdo_reminder_snooze_m15.isChecked()) {
                            model.setInterval(SnoozeModel.SnoozeIntervals.M15);
                        } else {
                            model.setInterval(SnoozeModel.SnoozeIntervals.M30);
                        }

                        if (rdo_reminder_snooze_r3.isChecked()) {
                            model.setLimit(SnoozeModel.SnoozeLimits.R3);
                        } else if (rdo_reminder_snooze_r5.isChecked()) {
                            model.setLimit(SnoozeModel.SnoozeLimits.R5);
                        } else {
                            model.setLimit(SnoozeModel.SnoozeLimits.RC);
                        }

                        listener.setSnoozeDialogModel(model);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    public interface ISnoozeInputDialogListener {

        void setSnoozeDialogModel(SnoozeModel model);

        SnoozeModel getSnoozeDialogModel();

    }
}
