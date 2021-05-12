package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.RepeatModel;

import java.util.Calendar;
import java.util.Date;

public class DateCalculatorDialog extends RefreshableDialogFragmentBase {

    public static final String TAG = "DateCalculatorDialog";

    private ITimeCalculatorListener listener;
    private final Calendar calendar = Calendar.getInstance();
    private final Calendar resultCalendar = Calendar.getInstance();
    private AppCompatButton btn_reminder_date;
    private NumberPicker value_picker;
    private NumberPicker unit_picker;
    private AppCompatTextView tv_reminder_date;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ITimeCalculatorListener) context;
            if (listener.getDateCalculatorDialogModel() == null) return;
            calendar.setTime(listener.getDateCalculatorDialogModel());
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement ITimeCalculatorListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_fragment_input_date_calculator, null);

        btn_reminder_date = view.findViewById(R.id.btn_reminder_date);
        btn_reminder_date.setText(StringHelper.toWeekdayDate(this.getContext(), calendar.getTime()));

        btn_reminder_date.setOnClickListener(view12 -> {
            // This calculator suppose to help user getting their target time by adding days/months from their recalled date.
            // So, its allowed to go backward up to return value of "getMaxForTimeUnit" to eventually get result of present after adding time.
            // Its expected that remembering an event 3 years (getMaxForTimeUnit return for YEAR unit) back is more than sufficient.
            final Calendar minDateCalendar = Calendar.getInstance();
            minDateCalendar.add(Calendar.YEAR, -1 * RepeatModel.getMaxForTimeUnit(RepeatModel.TimeUnits.YEARS)); // (-1) to gho backward.

            final int mYear, mMonth, mDay;
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DatePickerStyle,
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        refresh();
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(minDateCalendar.getTimeInMillis()); // This will cause extra title on the top of the regular date picker
            datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // This line will try to solve the issue above
            datePickerDialog.setTitle(null); // This line will try to solve the issue above
            datePickerDialog.show();

            if (OsHelper.isLollipopOrLater()) {
                datePickerDialog.getDatePicker().setFirstDayOfWeek(AppSettingsHelper.getInstance().getFirstDayOfWeek());
            }
        });

        value_picker = view.findViewById(R.id.value_picker);
        value_picker.setMinValue(1);

        final String[] units = new String[]{"Days", "Weeks", "Months", "Years"};
        unit_picker = view.findViewById(R.id.unit_picker);
        unit_picker.setMinValue(0);
        unit_picker.setMaxValue(units.length - 1);
        unit_picker.setDisplayedValues(units);
        unit_picker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            value_picker.setMaxValue(RepeatModel.getMaxForTimeUnit(RepeatModel.getTimeUnitFromInteger(newVal)));
            refresh();
        });

        tv_reminder_date = view.findViewById(R.id.tv_reminder_date);

        value_picker.setOnValueChangedListener((picker, oldVal, newVal) -> refresh());


        // Value of unit must set first before setting up value of time:
        // 1
        unit_picker.setValue(0);
        value_picker.setMaxValue(RepeatModel.getMaxForTimeUnit(RepeatModel.TimeUnits.DAYS));
        //unit_picker.
        // 2
        value_picker.setValue(1);

        builder.setView(view).setPositiveButton("Use Result", (dialog, which) ->
                listener.setDateCalculatorDialogModel(resultCalendar.getTime()))
                .setNegativeButton(getString(R.string.dialog_negative),
                        (dialog, which) -> {
                            //DO nothing
                        });
        refresh();

        return builder.create();

    }

    @Override
    protected void onUIRefresh() {

        resultCalendar.setTime(calendar.getTime());

        switch (RepeatModel.getTimeUnitFromInteger(unit_picker.getValue())) {
            case DAYS:
                resultCalendar.add(Calendar.DAY_OF_YEAR, value_picker.getValue());
                break;
            case WEEKS:
                resultCalendar.add(Calendar.WEEK_OF_YEAR, value_picker.getValue());
                break;
            case MONTHS:
                resultCalendar.add(Calendar.MONTH, value_picker.getValue());
                break;
            case YEARS:
                resultCalendar.add(Calendar.YEAR, value_picker.getValue());
                break;
        }

        btn_reminder_date.setText(StringHelper.toWeekdayDate(this.getContext(), calendar.getTime()));

        tv_reminder_date.setText(StringHelper.toWeekdayDate(this.getContext(), resultCalendar.getTime()));

    }

    public interface ITimeCalculatorListener {

        void setDateCalculatorDialogModel(Date newTime);

        Date getDateCalculatorDialogModel();

    }

}
