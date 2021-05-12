package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.viewModels.RepeatModel;

public class OtherRepeatDialog extends CustomRepeatDialogBase {

    public static final String TAG = "OtherRepeatDialog";


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_fragment_input_repeat_other, null);

        final NumberPicker value_picker = view.findViewById(R.id.value_picker);
        value_picker.setMinValue(1);

        final String[] units = new String[]{"Days", "Weeks", "Months", "Years"};
        final NumberPicker unit_picker = view.findViewById(R.id.unit_picker);
        unit_picker.setMinValue(0);
        unit_picker.setMaxValue(units.length - 1);
        unit_picker.setDisplayedValues(units);
        unit_picker.setOnValueChangedListener((picker, oldVal, newVal) -> value_picker.setMaxValue(RepeatModel.getMaxForTimeUnit(RepeatModel.getTimeUnitFromInteger(newVal))));

        // Value of unit must set first before setting up value of time:
        // 1
        unit_picker.setValue(RepeatModel.getIntegerFromTimeUnit(getModel().getCustomTimeUnit()));
        value_picker.setMaxValue(RepeatModel.getMaxForTimeUnit(RepeatModel.getTimeUnitFromInteger(unit_picker.getValue())));
        //unit_picker.
        // 2
        value_picker.setValue(Math.max(getModel().getCustomTimeValue(), 1));

        builder.setView(view).setTitle("Customize time to Repeat").setPositiveButton(getString(R.string.dialog_positive), (dialog, which) -> {
            getModel().setRepeatCustom(unit_picker.getValue(), value_picker.getValue());

            getModel().setRepeatOption(RepeatModel.ReminderRepeatOptions.OTHER);
            getListener().setCustomRepeatDialogModel(getModel());

        }).setNegativeButton(getString(R.string.dialog_negative), (dialog, which) -> {

        });

        return builder.create();
    }

}
