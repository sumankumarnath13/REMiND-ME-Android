package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

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
import com.example.remindme.ui.fragments.dialogFragments.common.IDateTimePickerListener;
import com.example.remindme.ui.fragments.dialogFragments.common.RemindMeDatePickerDialog;
import com.example.remindme.ui.fragments.dialogFragments.common.RemindMeTimePickerBlackDialog;
import com.example.remindme.ui.fragments.dialogFragments.common.RemindMeTimePickerLightDialog;
import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.TimeModel;
import com.example.remindme.viewModels.factories.RepeatViewModelFactory;

import java.util.Calendar;
import java.util.Date;

public class RepeatDialog extends DialogFragmentBase
        implements
        CustomRepeatDialogBase.ICustomRepeatDialogListener,
        IDateTimePickerListener {

    public static final String TAG = "RepeatDialog";

    private IRepeatInputDialogListener listener;
    protected IRepeatInputDialogListener getListener() {
        return listener;
    }

    private RepeatModel model;
    private RadioButton rdo_reminder_repeat_off;

    private AppCompatRadioButton rdo_reminder_repeat_hourly;
    private AppCompatRadioButton rdo_reminder_repeat_daily;
    private AppCompatRadioButton rdo_reminder_repeat_daily_custom;
    private AppCompatRadioButton rdo_reminder_repeat_weekly;
    private AppCompatRadioButton rdo_reminder_repeat_weekly_custom;
    private AppCompatRadioButton rdo_reminder_repeat_monthly;
    private AppCompatRadioButton rdo_reminder_repeat_monthly_custom;
    private AppCompatRadioButton rdo_reminder_repeat_yearly;
    private AppCompatRadioButton rdo_reminder_repeat_other;
    private AppCompatTextView tv_end_date_value;
    private AppCompatTextView tv_end_time_value;
    private SwitchCompat sw_has_repeat_end;
    private LinearLayoutCompat lv_repeat_end_date;
    private LinearLayoutCompat lv_repeat_end_time;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = super.getListener(IRepeatInputDialogListener.class);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Listener incompatible!");
            dismiss();
            return;
        }

        model = new ViewModelProvider(this, new RepeatViewModelFactory(getListener().getRepeatDialogModel())).get(RepeatModel.class);
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

        rdo_reminder_repeat_off = view.findViewById(R.id.rdo_reminder_repeat_off);
        rdo_reminder_repeat_off.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.setEnabled(false);
                refresh();
            }
        });

        rdo_reminder_repeat_hourly = view.findViewById(R.id.rdo_reminder_repeat_hourly);
        rdo_reminder_repeat_hourly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.setEnabled(true);
                model.setRepeatOption(RepeatModel.ReminderRepeatOptions.HOURLY);
                refresh();
            }
        });

        rdo_reminder_repeat_daily = view.findViewById(R.id.rdo_reminder_repeat_daily);
        rdo_reminder_repeat_daily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.setEnabled(true);
                model.setRepeatOption(RepeatModel.ReminderRepeatOptions.DAILY);
                refresh();
            }
        });

        rdo_reminder_repeat_daily_custom = view.findViewById(R.id.rdo_reminder_repeat_daily_custom);
        rdo_reminder_repeat_daily_custom.setOnClickListener(v -> {
            final DailyCustomRepeatDialog ting = new DailyCustomRepeatDialog();
            ting.show(getParentFragmentManager(), DailyCustomRepeatDialog.TAG);
        });

        rdo_reminder_repeat_weekly = view.findViewById(R.id.rdo_reminder_repeat_weekly);
        rdo_reminder_repeat_weekly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.setEnabled(true);
                model.setRepeatOption(RepeatModel.ReminderRepeatOptions.WEEKLY);
                refresh();
            }
        });

        rdo_reminder_repeat_weekly_custom = view.findViewById(R.id.rdo_reminder_repeat_weekly_custom);
        rdo_reminder_repeat_weekly_custom.setOnClickListener(v -> {
            final WeeklyCustomRepeatDialog ting = new WeeklyCustomRepeatDialog();
            ting.show(getParentFragmentManager(), WeeklyCustomRepeatDialog.TAG);
        });

        rdo_reminder_repeat_monthly = view.findViewById(R.id.rdo_reminder_repeat_monthly);
        rdo_reminder_repeat_monthly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.setEnabled(true);
                model.setRepeatOption(RepeatModel.ReminderRepeatOptions.MONTHLY);
                refresh();
            }
        });

        rdo_reminder_repeat_monthly_custom = view.findViewById(R.id.rdo_reminder_repeat_monthly_custom);
        rdo_reminder_repeat_monthly_custom.setOnClickListener(v -> {
            final MonthlyCustomRepeatDialog ting = new MonthlyCustomRepeatDialog();
            ting.show(getParentFragmentManager(), MonthlyCustomRepeatDialog.TAG);
        });

        rdo_reminder_repeat_yearly = view.findViewById(R.id.rdo_reminder_repeat_yearly);
        rdo_reminder_repeat_yearly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isChecked) {
                model.setEnabled(true);
                model.setRepeatOption(RepeatModel.ReminderRepeatOptions.YEARLY);
                refresh();
            }
        });

        rdo_reminder_repeat_other = view.findViewById(R.id.rdo_reminder_repeat_other);
        rdo_reminder_repeat_other.setOnClickListener(v -> {
            final OtherRepeatDialog ting = new OtherRepeatDialog();
            ting.show(getParentFragmentManager(), OtherRepeatDialog.TAG);
        });

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
        tv_end_date_value.setOnClickListener(v -> {
            final RemindMeDatePickerDialog dialog = new RemindMeDatePickerDialog();
            dialog.show(getParentFragmentManager(), RemindMeDatePickerDialog.TAG);
        });

        tv_end_time_value = view.findViewById(R.id.tv_end_time_value);
        tv_end_time_value.setOnClickListener(v -> {

            if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.BLACK) {
                final RemindMeTimePickerBlackDialog dialog = new RemindMeTimePickerBlackDialog();
                dialog.show(getParentFragmentManager(), RemindMeTimePickerBlackDialog.TAG);
            } else {
                final RemindMeTimePickerLightDialog dialog = new RemindMeTimePickerLightDialog();
                dialog.show(getParentFragmentManager(), RemindMeTimePickerLightDialog.TAG);
            }
        });

        lv_repeat_end_date = view.findViewById(R.id.lv_repeat_end_date);
        lv_repeat_end_time = view.findViewById(R.id.lv_repeat_end_time);

        refresh();

        return builder.create();
    }

    @Override
    protected void onUIRefresh() {
        // No radio group wont work for the given layout. So resetting programmatically is required.

        rdo_reminder_repeat_off.setChecked(false);
        rdo_reminder_repeat_hourly.setChecked(false);
        rdo_reminder_repeat_daily.setChecked(false);
        rdo_reminder_repeat_daily_custom.setChecked(false);
        rdo_reminder_repeat_weekly.setChecked(false);
        rdo_reminder_repeat_weekly_custom.setChecked(false);
        rdo_reminder_repeat_monthly.setChecked(false);
        rdo_reminder_repeat_monthly_custom.setChecked(false);
        rdo_reminder_repeat_yearly.setChecked(false);
        rdo_reminder_repeat_other.setChecked(false);

        // Time list and hourly repeat cannot coexists.
        rdo_reminder_repeat_hourly.setEnabled(model.getParent().getTimeModel().getTimeListMode() == TimeModel.TimeListModes.NONE);


        if (!model.isEnabled()) {
            //model.setEnabled(true);
            rdo_reminder_repeat_off.setChecked(true);
            lv_repeat_end_date.setVisibility(View.GONE);
            lv_repeat_end_time.setVisibility(View.GONE);
            sw_has_repeat_end.setEnabled(false);

        } else {

            switch (model.getRepeatOption()) {
                default:
                case HOURLY:
                    rdo_reminder_repeat_hourly.setChecked(true);
                    break;
                case DAILY:
                    rdo_reminder_repeat_daily.setChecked(true);
                    break;
                case DAILY_CUSTOM:
                    rdo_reminder_repeat_daily_custom.setChecked(true);
                    break;
                case WEEKLY:
                    rdo_reminder_repeat_weekly.setChecked(true);
                    break;
                case WEEKLY_CUSTOM:
                    rdo_reminder_repeat_weekly_custom.setChecked(true);
                    break;
                case MONTHLY:
                    rdo_reminder_repeat_monthly.setChecked(true);
                    break;
                case MONTHLY_CUSTOM:
                    rdo_reminder_repeat_monthly_custom.setChecked(true);
                    break;
                case YEARLY:
                    rdo_reminder_repeat_yearly.setChecked(true);
                    break;
                case OTHER:
                    rdo_reminder_repeat_other.setChecked(true);
                    break;
            }

            sw_has_repeat_end.setEnabled(true);
            sw_has_repeat_end.setChecked(model.isHasRepeatEnd());

            if (model.isHasRepeatEnd()) {
                lv_repeat_end_date.setVisibility(View.VISIBLE);
                lv_repeat_end_time.setVisibility(View.VISIBLE);
                tv_end_date_value.setText(StringHelper.toWeekdayDate(this.getContext(), model.getRepeatEndDate()));
                tv_end_time_value.setText(StringHelper.toTimeAmPm(model.getRepeatEndDate()));
            } else {
                lv_repeat_end_date.setVisibility(View.GONE);
                lv_repeat_end_time.setVisibility(View.GONE);
            }
        }
    }

    public void setCustomRepeatDialogModel(RepeatModel m) {

        if (m != null && m.isValid(model.getParent().getTimeModel())) { // m will be  null to  that
            model = m;
        }
        refresh();
    }

    @Override
    public RepeatModel getCustomRepeatDialogModel() {
        return model;
    }

    @Override
    public void setDateTimePicker(String tag, Date dateTime) {
        model.setRepeatEndDate(dateTime);
        refresh();
    }

    @Override
    public Date getDateTimePicker(String tag) {
        return model.getRepeatEndDate();
    }

    @Override
    public Date getMinimumDateTime(String tag) {
        return model.getParent().getTimeModel().getTime();
    }

    public interface IRepeatInputDialogListener {

        void setRepeatDialogModel(RepeatModel model);

        RepeatModel getRepeatDialogModel();

    }
}
