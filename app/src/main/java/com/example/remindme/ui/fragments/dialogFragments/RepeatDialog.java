package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.common.CustomRepeatDialogBase;
import com.example.remindme.ui.fragments.dialogFragments.common.DialogFragmentBase;
import com.example.remindme.ui.fragments.dialogFragments.common.RemindMeDatePickerDialog;
import com.example.remindme.ui.fragments.dialogFragments.common.TimeListDialogBase;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogBase;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogBlack;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogLight;
import com.example.remindme.viewModels.MultipleTimeRepeatModel;
import com.example.remindme.viewModels.PeriodicRepeatModel;
import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.factories.RepeatViewModelFactory;

import java.util.Calendar;
import java.util.Date;

public class RepeatDialog extends DialogFragmentBase
        implements
        RemindMeDatePickerDialog.IDatePickerListener,
        TimePickerDialogBase.ITimePickerListener,
        CustomRepeatDialogBase.ICustomRepeatDialogListener,
        TimeListDialogBase.ITimeListListener {

    public static final String TAG = "RepeatDialog";

    @Override
    public void onSetListenerDate(Date dateTime) {
        this.model.setRepeatEndDate(dateTime);
        refresh();
    }

    @Override
    public Date onGetListenerDate() {
        return model.getRepeatEndDate();
    }

    @Override
    public void onSetListenerTime(Date dateTime) {
        this.model.setRepeatEndDate(dateTime);
        refresh();
    }

    @Override
    public Date onGetListenerTime() {
        return model.getRepeatEndDate();
    }

    @Override
    public void setCustomRepeatDialogModel(RepeatModel model) {
        if (model != null) {
            if (model.isValid(model.getParent().getTimeModel(), model)) { // m will be  null to that
                this.model.setPeriodicRepeatModel(model.getPeriodicRepeatModel());
            }
        }
        isRefreshPeriodicSection = true;
        refresh();
    }

    @Override
    public RepeatModel getCustomRepeatDialogModel() {
        return model;
    }

    @Override
    public RepeatModel getTimeListDialogModel() {
        return model;
    }

    @Override
    public void setTimeListDialogModel(RepeatModel model) {
        if (model != null) {
            if (model.isValid(model.getParent().getTimeModel(), model)) {
                this.model.setMultipleTimeRepeatModel(model.getMultipleTimeRepeatModel());
                this.model.getParent().getTimeModel().setScheduledTime(model.getValidatedScheduledTime());
            } else {
                ToastHelper.showShort(getContext(), "Please check repeat settings");
            }
        }
        isRefreshMultipleTimeSection = true;
        refresh();
    }

    public interface IRepeatInputDialogListener {

        void setRepeatDialogModel(RepeatModel model);

        RepeatModel getRepeatDialogModel();

    }

    private IRepeatInputDialogListener listener;

    protected IRepeatInputDialogListener getListener() {
        if (listener == null) {
            listener = super.getListener(IRepeatInputDialogListener.class);
        }
        return listener;
    }

    private RepeatModel model;

    private AppCompatRadioButton rdo_time_repeat_off;
    private AppCompatRadioButton rdo_time_repeat_hourly;
    private AppCompatRadioButton rdo_time_repeat_any_time;
    private AppCompatRadioButton rdo_time_repeat_selected_hours;

    private AppCompatRadioButton rdo_periodic_repeat_off;
    private AppCompatRadioButton rdo_periodic_repeat_daily;
    private AppCompatRadioButton rdo_periodic_repeat_days_of_week;
    private AppCompatRadioButton rdo_periodic_repeat_weekly;
    private AppCompatRadioButton rdo_periodic_repeat_weeks_of_month;
    private AppCompatRadioButton rdo_periodic_repeat_monthly;
    private AppCompatRadioButton rdo_periodic_repeat_months_of_year;
    private AppCompatRadioButton rdo_periodic_repeat_yearly;
    private AppCompatRadioButton rdo_periodic_repeat_other;
    private AppCompatTextView tv_end_date_value;
    private AppCompatTextView tv_end_time_value;
    private SwitchCompat sw_has_repeat_end;
    private LinearLayoutCompat lv_repeat_end_date;
    private LinearLayoutCompat lv_repeat_end_time;

    private boolean isRefreshPeriodicSection;
    private boolean isRefreshMultipleTimeSection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Dialog listener is not set!");
            dismiss();
            return;
        }

        model = new ViewModelProvider(this,
                new RepeatViewModelFactory(getListener()
                        .getRepeatDialogModel())).get(RepeatModel.class);
    }

    private CustomRepeatDialogBase.ICustomRepeatDialogListener customRepeatDialogListener;

    private DailyCustomRepeatDialog dailyCustomRepeatDialog;

    private DailyCustomRepeatDialog getDailyCustomRepeatDialog() {
        if (this.dailyCustomRepeatDialog == null) {
            this.dailyCustomRepeatDialog = new DailyCustomRepeatDialog();
        }
        return this.dailyCustomRepeatDialog;
    }

    private WeeklyCustomRepeatDialog weeklyCustomRepeatDialog;

    private WeeklyCustomRepeatDialog getWeeklyCustomRepeatDialog() {
        if (this.weeklyCustomRepeatDialog == null) {
            this.weeklyCustomRepeatDialog = new WeeklyCustomRepeatDialog();
        }
        return this.weeklyCustomRepeatDialog;
    }

    private MonthlyCustomRepeatDialog monthlyCustomRepeatDialog;

    private MonthlyCustomRepeatDialog getMonthlyCustomRepeatDialog() {
        if (this.monthlyCustomRepeatDialog == null) {
            this.monthlyCustomRepeatDialog = new MonthlyCustomRepeatDialog();
        }
        return this.monthlyCustomRepeatDialog;
    }

    private OtherRepeatDialog otherRepeatDialog;

    private OtherRepeatDialog getOtherRepeatDialog() {
        if (this.otherRepeatDialog == null) {
            this.otherRepeatDialog = new OtherRepeatDialog();
        }
        return this.otherRepeatDialog;
    }

    private RemindMeDatePickerDialog datePickerDialog;

    private RemindMeDatePickerDialog getDatePickerDialog() {
        if (datePickerDialog == null) {
            datePickerDialog = new RemindMeDatePickerDialog();
        }
        return datePickerDialog;
    }

    private TimePickerDialogBase timePickerDialog;

    private TimePickerDialogBase getTimePickerDialog() {
        if (timePickerDialog == null) {
            if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.BLACK) {
                timePickerDialog = new TimePickerDialogBlack();
            } else {
                timePickerDialog = new TimePickerDialogLight();
            }
        }
        return timePickerDialog;
    }

    private TimeListHourlyDialog timeListHourlyDialog;

    private TimeListHourlyDialog getTimeListHourlyDialog() {
        if (timeListHourlyDialog == null) {
            timeListHourlyDialog = new TimeListHourlyDialog();
        }
        return timeListHourlyDialog;
    }

    private TimeListAnyTimeDialog timeListAnyTimeDialog;

    private TimeListAnyTimeDialog getTimeListAnyTimeDialog() {
        if (timeListAnyTimeDialog == null) {
            timeListAnyTimeDialog = new TimeListAnyTimeDialog();
        }
        return timeListAnyTimeDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_fragment_input_repeat, null);
        builder.setView(view).setTitle("Select " + getString(R.string.heading_repeat_settings))
                .setPositiveButton(R.string.acton_dialog_positive, (dialog, which) -> getListener().setRepeatDialogModel(model))
                .setNegativeButton(R.string.acton_dialog_negative, (dialog, which) -> {

                });

        rdo_time_repeat_off = view.findViewById(R.id.rdo_time_repeat_off);
        rdo_time_repeat_off.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.getMultipleTimeRepeatModel().setTimeListMode(MultipleTimeRepeatModel.TimeListModes.OFF);
                isRefreshMultipleTimeSection = true;
                refresh();
            }
        });

        rdo_time_repeat_hourly = view.findViewById(R.id.rdo_time_repeat_hourly);
        rdo_time_repeat_hourly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.getMultipleTimeRepeatModel().setTimeListMode(MultipleTimeRepeatModel.TimeListModes.HOURLY);
                isRefreshMultipleTimeSection = true;
                refresh();
            }
        });

        rdo_time_repeat_any_time = view.findViewById(R.id.rdo_time_repeat_any_time);
        rdo_time_repeat_any_time.setOnClickListener(v -> getTimeListAnyTimeDialog().show(getParentFragmentManager(), TimeListAnyTimeDialog.TAG));

        rdo_time_repeat_selected_hours = view.findViewById(R.id.rdo_time_repeat_selected_hours);
        rdo_time_repeat_selected_hours.setOnClickListener(v -> getTimeListHourlyDialog().show(getParentFragmentManager(), TimeListHourlyDialog.TAG));

        rdo_periodic_repeat_off = view.findViewById(R.id.rdo_periodic_repeat_off);
        rdo_periodic_repeat_off.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.getPeriodicRepeatModel().setRepeatOption(PeriodicRepeatModel.PeriodicRepeatOptions.OFF);
                isRefreshPeriodicSection = true;
                refresh();
            }
        });

        rdo_periodic_repeat_daily = view.findViewById(R.id.rdo_periodic_repeat_daily);
        rdo_periodic_repeat_daily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.getPeriodicRepeatModel().setRepeatOption(PeriodicRepeatModel.PeriodicRepeatOptions.DAILY);
                isRefreshPeriodicSection = true;
                refresh();
            }
        });

        rdo_periodic_repeat_days_of_week = view.findViewById(R.id.rdo_periodic_repeat_days_of_week);
        rdo_periodic_repeat_days_of_week.setOnClickListener(v -> getDailyCustomRepeatDialog().show(getParentFragmentManager(), DailyCustomRepeatDialog.TAG));

        rdo_periodic_repeat_weekly = view.findViewById(R.id.rdo_periodic_repeat_weekly);
        rdo_periodic_repeat_weekly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.getPeriodicRepeatModel().setRepeatOption(PeriodicRepeatModel.PeriodicRepeatOptions.WEEKLY);
                isRefreshPeriodicSection = true;
                refresh();
            }
        });

        rdo_periodic_repeat_weeks_of_month = view.findViewById(R.id.rdo_periodic_repeat_weeks_of_month);
        rdo_periodic_repeat_weeks_of_month.setOnClickListener(v -> getWeeklyCustomRepeatDialog().show(getParentFragmentManager(), WeeklyCustomRepeatDialog.TAG));

        rdo_periodic_repeat_monthly = view.findViewById(R.id.rdo_periodic_repeat_monthly);
        rdo_periodic_repeat_monthly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.getPeriodicRepeatModel().setRepeatOption(PeriodicRepeatModel.PeriodicRepeatOptions.MONTHLY);
                isRefreshPeriodicSection = true;
                refresh();
            }
        });

        rdo_periodic_repeat_months_of_year = view.findViewById(R.id.rdo_periodic_repeat_months_of_year);
        rdo_periodic_repeat_months_of_year.setOnClickListener(v -> getMonthlyCustomRepeatDialog().show(getParentFragmentManager(), MonthlyCustomRepeatDialog.TAG));

        rdo_periodic_repeat_yearly = view.findViewById(R.id.rdo_periodic_repeat_yearly);
        rdo_periodic_repeat_yearly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.getPeriodicRepeatModel().setRepeatOption(PeriodicRepeatModel.PeriodicRepeatOptions.YEARLY);
                isRefreshPeriodicSection = true;
                refresh();
            }
        });

        rdo_periodic_repeat_other = view.findViewById(R.id.rdo_periodic_repeat_other);
        rdo_periodic_repeat_other.setOnClickListener(v -> getOtherRepeatDialog().show(getParentFragmentManager(), OtherRepeatDialog.TAG));

        sw_has_repeat_end = view.findViewById(R.id.sw_has_repeat_end);
        sw_has_repeat_end.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isRefreshing())
                return;

            if (isChecked) { // Use a default repeat end to next month
                if (!model.isEnabled()) {
                    buttonView.setChecked(false);
                    ToastHelper.showShort(view.getContext(), "Cannot enable repeat ending if its set to None.");
                    return;
                }
                if (!model.isHasRepeatEnd()) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(model.getParent().getTimeModel().getAlertTime(false));
                    c.add(Calendar.MONTH, 6);
                    model.setRepeatEndDate(c.getTime());
                }
            }

            model.setHasRepeatEnd(isChecked);
            refresh();
        });

        tv_end_date_value = view.findViewById(R.id.tv_end_date_value);
        tv_end_date_value.setOnClickListener(v -> getDatePickerDialog().show(getParentFragmentManager(), RemindMeDatePickerDialog.TAG));

        tv_end_time_value = view.findViewById(R.id.tv_end_time_value);
        tv_end_time_value.setOnClickListener(v -> getTimePickerDialog().show(getParentFragmentManager(), TimePickerDialogBlack.TAG));

        lv_repeat_end_date = view.findViewById(R.id.lv_repeat_end_date);
        lv_repeat_end_time = view.findViewById(R.id.lv_repeat_end_time);

        isRefreshMultipleTimeSection = true;
        isRefreshPeriodicSection = true;

        refresh();

        return builder.create();
    }

    @Override
    protected void onUIRefresh() {
        // No radio group wont work for the given layout. So resetting programmatically is required.

        if (isRefreshPeriodicSection) {

            rdo_periodic_repeat_off.setChecked(false);
            rdo_periodic_repeat_daily.setChecked(false);
            rdo_periodic_repeat_days_of_week.setChecked(false);
            rdo_periodic_repeat_weekly.setChecked(false);
            rdo_periodic_repeat_weeks_of_month.setChecked(false);
            rdo_periodic_repeat_monthly.setChecked(false);
            rdo_periodic_repeat_months_of_year.setChecked(false);
            rdo_periodic_repeat_yearly.setChecked(false);
            rdo_periodic_repeat_other.setChecked(false);

            switch (model.getPeriodicRepeatModel().getRepeatOption()) {
                default:
                case OFF:
                    rdo_periodic_repeat_off.setChecked(true);
                    break;
                case DAILY:
                    rdo_periodic_repeat_daily.setChecked(true);
                    break;
                case DAILY_CUSTOM:
                    rdo_periodic_repeat_days_of_week.setChecked(true);
                    break;
                case WEEKLY:
                    rdo_periodic_repeat_weekly.setChecked(true);
                    break;
                case WEEKLY_CUSTOM:
                    rdo_periodic_repeat_weeks_of_month.setChecked(true);
                    break;
                case MONTHLY:
                    rdo_periodic_repeat_monthly.setChecked(true);
                    break;
                case MONTHLY_CUSTOM:
                    rdo_periodic_repeat_months_of_year.setChecked(true);
                    break;
                case YEARLY:
                    rdo_periodic_repeat_yearly.setChecked(true);
                    break;
                case OTHER:
                    rdo_periodic_repeat_other.setChecked(true);
                    break;
            }

            isRefreshPeriodicSection = false;

        }

        if (isRefreshMultipleTimeSection) {

            rdo_time_repeat_off.setChecked(false);
            rdo_time_repeat_hourly.setChecked(false);
            rdo_time_repeat_any_time.setChecked(false);
            rdo_time_repeat_selected_hours.setChecked(false);

            switch (model.getMultipleTimeRepeatModel().getTimeListMode()) {
                default:
                case OFF:
                    rdo_time_repeat_off.setChecked(true);
                    break;
                case HOURLY:
                    rdo_time_repeat_hourly.setChecked(true);
                    break;
                case ANYTIME:
                    rdo_time_repeat_any_time.setChecked(true);
                    break;
                case SELECTED_HOURS:
                    rdo_time_repeat_selected_hours.setChecked(true);
                    break;

            }

            isRefreshMultipleTimeSection = false;
        }

        sw_has_repeat_end.setEnabled(model.isEnabled());
        sw_has_repeat_end.setChecked(model.isHasRepeatEnd());

        if (model.isHasRepeatEnd()) {
            lv_repeat_end_date.setVisibility(View.VISIBLE);
            lv_repeat_end_time.setVisibility(View.VISIBLE);
            tv_end_date_value.setText(StringHelper.toWeekdayDate(getContext(), model.getRepeatEndDate()));
            tv_end_time_value.setText(StringHelper.toTimeAmPm(model.getRepeatEndDate()));
        } else {
            lv_repeat_end_date.setVisibility(View.GONE);
            lv_repeat_end_time.setVisibility(View.GONE);
        }
    }

    boolean isCanceled;

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (getListener() != null) {
            getListener().setRepeatDialogModel(null);
        }
        isCanceled = true;
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (!isCanceled) {
            if (getListener() != null) {
                getListener().setRepeatDialogModel(null);
            }
        }
        super.onDismiss(dialog);
    }
}
