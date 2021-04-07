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

import com.example.remindme.viewModels.IReminderRepeatListener;
import com.example.remindme.viewModels.ReminderRepeatModel;

public class DialogReminderRepeatInputCustom extends DialogFragment {

    private IReminderRepeatListener listener;
    private ReminderRepeatModel model;
    private boolean isCancel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (IReminderRepeatListener) context;
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
            listener.set(null, true);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_custom, null);

        final NumberPicker value_picker = view.findViewById(R.id.value_picker);

        final String[] units = new String[]{"Days", "Weeks", "Months", "Years"};
        final NumberPicker unit_picker = view.findViewById(R.id.unit_picker);
        unit_picker.setMinValue(0);
        unit_picker.setMaxValue(units.length - 1);
        unit_picker.setDisplayedValues(units);
        unit_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                if (newVal == 0) { // Min
//                    value_picker.setMinValue(5);
//                    value_picker.setMaxValue(59);
//                } else if (newVal == 1) { // Hour
//                    value_picker.setMinValue(1);
//                    value_picker.setMaxValue(23);
//                } else { // Day / Week / Month and Year
//                    value_picker.setMinValue(1);
//                    value_picker.setMaxValue(1000);
//                }
            }
        });

        switch (model.customTimeUnit) {
            case DAYS:
                unit_picker.setValue(0);
                break;
            case WEEKS:
                unit_picker.setValue(1);
                break;
            case MONTHS:
                unit_picker.setValue(2);
                break;
            case YEARS:
                unit_picker.setValue(3);
                break;
        }

        value_picker.setMinValue(1);
        value_picker.setMaxValue(1000);
        value_picker.setValue(Math.max(model.customTimeValue, 1));

        builder.setView(view).setTitle("Customize time to Repeat").setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (unit_picker.getValue()) {
                    case 0:
                        model.customTimeUnit = ReminderRepeatModel.TimeUnits.DAYS;
                        break;
                    case 1:
                        model.customTimeUnit = ReminderRepeatModel.TimeUnits.WEEKS;
                        break;
                    case 2:
                        model.customTimeUnit = ReminderRepeatModel.TimeUnits.MONTHS;
                        break;
                    case 3:
                        model.customTimeUnit = ReminderRepeatModel.TimeUnits.YEARS;
                        break;
                }

                model.customTimeValue = value_picker.getValue();
                listener.set(model, true);
            }
        }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.set(null, true);
            }
        });

        return builder.create();
    }

}
