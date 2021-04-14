package com.example.remindme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.ui.main.IReminderRepeatListener;
import com.example.remindme.ui.main.IRepeatInputDialog;
import com.example.remindme.viewModels.ReminderRepeatModel;

public class DialogReminderRepeatInputCustom extends DialogFragment {

    private ReminderRepeatModel model;
    private boolean isCancel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            IReminderRepeatListener listener = (IReminderRepeatListener) context;
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

        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_custom, null);

        final NumberPicker value_picker = view.findViewById(R.id.value_picker);
        value_picker.setMinValue(1);

        final String[] units = new String[]{"Days", "Weeks", "Months"};
        final NumberPicker unit_picker = view.findViewById(R.id.unit_picker);
        unit_picker.setMinValue(0);
        unit_picker.setMaxValue(units.length - 1);
        unit_picker.setDisplayedValues(units);
        unit_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                value_picker.setMaxValue(ReminderRepeatModel.getMaxForTimeUnit(ReminderRepeatModel.transform(newVal)));
            }
        });

        // Value of unit must set first before setting up value of time:
        // 1
        unit_picker.setValue(ReminderRepeatModel.transform(model.getCustomTimeUnit()));
        value_picker.setMaxValue(ReminderRepeatModel.getMaxForTimeUnit(ReminderRepeatModel.transform(unit_picker.getValue())));
        //unit_picker.
        // 2
        value_picker.setValue(Math.max(model.getCustomTimeValue(), 1));

        builder.setView(view).setTitle("Customize time to Repeat").setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.setRepeatCustom(unit_picker.getValue(), value_picker.getValue());

                model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.OTHER);
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
