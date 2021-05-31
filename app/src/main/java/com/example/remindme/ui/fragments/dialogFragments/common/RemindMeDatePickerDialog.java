package com.example.remindme.ui.fragments.dialogFragments.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.DateCalculatorDialog;
import com.example.remindme.viewModels.PeriodicRepeatModel;

import java.util.Calendar;
import java.util.Date;

public final class RemindMeDatePickerDialog extends DialogFragmentBase implements DateCalculatorDialog.ITimeCalculatorListener {

    public static final String TAG = "DatePickerDialog";

    @Override
    public void setDateCalculatorDialogModel(Date newTime) {
        final Calendar calendar = Calendar.getInstance();
        if (getCurrentDate().compareTo(newTime) <= 0) {
            calendar.setTime(newTime);
            getOnDateChangeListener().onDateChanged(datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), getOnDateChangeListener());
        } else {
            ToastHelper.showError(getContext(), "Selected date is in past!");
        }
    }

    @Override
    public Date getDateCalculatorDialogModel() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        return calendar.getTime();
    }

    public interface IDatePickerListener {
        void onSetListenerDate(Date dateTime);

        Date onGetListenerDate();
    }

    private IDatePickerListener listener;

    private IDatePickerListener getListener() {
        if (listener == null) {
            listener = super.getListener(IDatePickerListener.class);
        }
        return listener;
    }

    private DatePicker datePicker;

    private Calendar calendar;

    private Calendar getCalendar() {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        return calendar;
    }

    private void setCalendar(Date date) {
        getCalendar().setTime(date);
    }

    private Date currentDate;

    private Date getCurrentDate() {
        if (currentDate == null) {
            currentDate = Calendar.getInstance().getTime();
        }
        return currentDate;
    }

    private int getDayOfMonth() {
        return getCalendar().get(Calendar.DAY_OF_MONTH);
    }

    private int getMonthOfYear() {
        return getCalendar().get(Calendar.MONTH);
    }

    private int getYear() {
        return getCalendar().get(Calendar.YEAR);
    }

    private DatePicker.OnDateChangedListener onDateChangeListener;

    private DatePicker.OnDateChangedListener getOnDateChangeListener() {
        if (onDateChangeListener == null) {
            onDateChangeListener = (view, year, monthOfYear, dayOfMonth) -> {
                if (getDialog() != null) {
                    final Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
                    final Calendar calendar = Calendar.getInstance();
                    final Date currentDate = calendar.getTime();

                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    if (calendar.getTime().compareTo(currentDate) >= 0) {
                        positiveButton.setText(getResources().getString(R.string.acton_dialog_positive));
                        positiveButton.setEnabled(true);
                        positiveButton.setTextColor(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeSoothingText)));
                    } else {
                        positiveButton.setText("Expired");
                        positiveButton.setEnabled(false);
                        positiveButton.setTextColor(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDisabledControlColor)));
                    }
                }
            };
        }
        return onDateChangeListener;
    }

    private DateCalculatorDialog dateCalculatorDialog;

    private DateCalculatorDialog getDateCalculatorDialog() {
        if (dateCalculatorDialog == null) {
            dateCalculatorDialog = new DateCalculatorDialog();
        }
        return dateCalculatorDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Dialog listener is not set!");
            dismiss();
            return;
        }

        setCalendar(getListener().onGetListenerDate());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.common_dialog_date_picker, null);

        datePicker = view.findViewById(R.id.date_picker);
        if (OsHelper.isLollipopOrLater()) {
            datePicker.setFirstDayOfWeek(AppSettingsHelper.getInstance().getFirstDayOfWeek());
        }

        AppCompatButton xxx = view.findViewById(R.id.xxx);
        xxx.setOnClickListener(v -> getDateCalculatorDialog().show(getParentFragmentManager(), DateCalculatorDialog.TAG));

        datePicker.init(getYear(), getMonthOfYear(), getDayOfMonth(), getOnDateChangeListener());
        // This calculator suppose to help user getting their target time by adding days/months from their recalled date.
        // So, its allowed to go backward up to return value of "getMaxForTimeUnit" to eventually get result of present after adding time.
        // Its expected that remembering an event 3 years (getMaxForTimeUnit return for YEAR unit) back is more than sufficient.
        final Calendar minDateCalendar = Calendar.getInstance();
        minDateCalendar.add(Calendar.YEAR, -1 * PeriodicRepeatModel.getMaxForTimeUnit(PeriodicRepeatModel.TimeUnits.YEARS)); // (-1) to go backward.
        datePicker.setMinDate(minDateCalendar.getTimeInMillis());

        builder.setView(view)
                .setPositiveButton(getString(R.string.acton_dialog_positive), (dialog, which) -> {

                    final Calendar setterCalendar = Calendar.getInstance();
                    setterCalendar.set(Calendar.YEAR, datePicker.getYear());
                    setterCalendar.set(Calendar.MONTH, datePicker.getMonth());
                    setterCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                    if (getCurrentDate().compareTo(setterCalendar.getTime()) <= 0) {
                        getListener().onSetListenerDate(setterCalendar.getTime());
                    } else {
                        ToastHelper.showError(this.getContext(), "Selected date is in past!");
                    }

                }).setNegativeButton(getString(R.string.acton_dialog_negative), (dialog, which) -> {
        });

        return builder.create();
    }

    @Override
    protected void onUIRefresh() {

    }
}