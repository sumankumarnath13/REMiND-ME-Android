package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.InputAdvanceOptionsDialog;
import com.example.remindme.ui.fragments.dialogFragments.NameDialog;
import com.example.remindme.ui.fragments.dialogFragments.RepeatDialog;
import com.example.remindme.ui.fragments.dialogFragments.common.RemindMeDatePickerDialog;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogBase;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogBlack;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogLight;
import com.example.remindme.viewModels.AlertModel;
import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.factories.AlertViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderInput
        extends
        ActivityBase
        implements
        TimePickerDialogBase.ITimePickerListener,
        RemindMeDatePickerDialog.IDatePickerListener,
        RepeatDialog.IRepeatInputDialogListener,
        NameDialog.INameInputDialogListener,
        InputAdvanceOptionsDialog.IInputAdvanceOptions {

    @Override
    public void setInputAdvanceOptionsModel(AlertModel model) {
        alertModel.setNote(model.getNote());
        alertModel.setSnoozeModel(model.getSnoozeModel());
        alertModel.getRingingModel().setToneEnabled(model.getRingingModel().isToneEnabled());
        alertModel.getRingingModel().setRingToneUri(model.getRingingModel().getRingToneUri());
        alertModel.getRingingModel().setAlarmRingDuration(model.getRingingModel().getAlarmRingDuration());
        alertModel.getRingingModel().setAlarmVolumePercentage(model.getRingingModel().getAlarmVolumePercentage());
        alertModel.getRingingModel().setIncreaseVolumeGradually(model.getRingingModel().isIncreaseVolumeGradually());
        alertModel.getRingingModel().setVibrationEnabled(model.getRingingModel().isVibrationEnabled());
        alertModel.getRingingModel().setVibratePattern(model.getRingingModel().getVibratePattern());
    }

    @Override
    public AlertModel getInputAdvanceOptionsModel() {
        return alertModel;
    }

    @Override
    public void setNameInputDialogModel(String name) {
        alertModel.setName(name);
        refresh();
    }

    @Override
    public String getNameInputDialogModel() {
        alertModel = new ViewModelProvider(ReminderInput.this).get(AlertModel.class);
        return alertModel.getName();
    }

    @Override
    public void setRepeatDialogModel(RepeatModel model) {
        if (model != null) {
            if (model.isValid(alertModel.getTimeModel(), model)) {
                alertModel.setRepeatModel(model);
                alertModel.getTimeModel().setScheduledTime(model.getValidatedScheduledTime());
            } else {
                ToastHelper.showShort(ReminderInput.this, "Please check repeat settings");
            }
        }
        refresh();
    }

    @Override
    public RepeatModel getRepeatDialogModel() {
        alertModel = new ViewModelProvider(ReminderInput.this).get(AlertModel.class);
        return alertModel.getRepeatModel();
    }

    @Override
    public void onSetListenerDate(Date dateTime) {
        alertModel.getTimeModel().setTime(dateTime);
        refresh();
    }

    @Override
    public Date onGetListenerDate() {
        alertModel = new ViewModelProvider(ReminderInput.this).get(AlertModel.class);
        return alertModel.getTimeModel().getTime();
    }

    @Override
    public void onSetListenerTime(Date dateTime) {
        alertModel.getTimeModel().setTime(dateTime);
        refresh();
    }

    @Override
    public Date onGetListenerTime() {
        alertModel = new ViewModelProvider(ReminderInput.this).get(AlertModel.class);
        return alertModel.getTimeModel().getTime();
    }

    private static final int NAME_SPEECH_REQUEST_CODE = 119;
    //    private static final String MORE_INPUT_UI_STATE = "MORE_INPUT";
//    private boolean isExtraInputsVisible;
    private AlertModel alertModel = null;


    private AppCompatTextView tv_reminder_trigger_time;
    private AppCompatTextView tv_reminder_trigger_date;
    private AppCompatButton btn_reminder_time;
    private AppCompatTextView tv_reminder_AmPm;
    private AppCompatButton btn_reminder_date;
    private AppCompatTextView tv_reminder_name_summary;
    private AppCompatCheckBox chk_reminder;
    private AppCompatCheckBox chk_alarm;

    private SwitchCompat sw_reminder_repeat;
    private AppCompatTextView tv_reminder_repeat_summary;

    //    private LinearLayoutCompat advance_options_layout;
//    private AppCompatTextView tv_advance_options_status;
//    private AppCompatImageView advance_options_image_view;
//    private NestedScrollView sv_container;
    private LinearLayoutCompat lvc_diff_next_reminder_trigger;


    private NameDialog nameDialog;

    private NameDialog getNameDialog() {
        if (nameDialog == null) {
            nameDialog = new NameDialog();
        }
        return nameDialog;
    }

    private RepeatDialog repeatDialog;

    private RepeatDialog getRepeatDialog() {
        if (repeatDialog == null) {
            repeatDialog = new RepeatDialog();
        }
        return repeatDialog;
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

    private InputAdvanceOptionsDialog inputAdvanceOptionsDialog;

    public InputAdvanceOptionsDialog getInputAdvanceOptionsDialog() {
        if (inputAdvanceOptionsDialog == null) {
            inputAdvanceOptionsDialog = new InputAdvanceOptionsDialog();
        }
        return inputAdvanceOptionsDialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NAME_SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            final String spokenText = results.get(0);
            alertModel.setName(spokenText);
            refresh();
        }

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_input);

        setUserInteracted(false);

        alertModel = new ViewModelProvider(this, new AlertViewModelFactory(getIntent())).get(AlertModel.class);

        if (alertModel.isNew()) { // First time creating the activity
            setActivityTitle(getResources().getString(R.string.heading_label_new_reminder));
        } else {
            setActivityTitle(getResources().getString(R.string.heading_label_edit_reminder));
        }

        tv_reminder_name_summary = findViewById(R.id.tv_reminder_name_summary);
        tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);

        chk_reminder = findViewById(R.id.chk_reminder);
        chk_reminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                alertModel.setReminder(isChecked);
                refresh();
            }
        });

        chk_alarm = findViewById(R.id.chk_alarm);
        chk_alarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                alertModel.setReminder(!isChecked);
                refresh();
            }
        });

        tv_reminder_trigger_time = findViewById(R.id.tv_reminder_trigger_time);
        tv_reminder_trigger_date = findViewById(R.id.tv_reminder_trigger_date);
        btn_reminder_date = findViewById(R.id.btn_reminder_date);
        btn_reminder_time = findViewById(R.id.btn_reminder_time);
        tv_reminder_AmPm = findViewById(R.id.tv_reminder_AmPm);
        lvc_diff_next_reminder_trigger = findViewById(R.id.lvc_diff_next_reminder_trigger);
        sw_reminder_repeat = findViewById(R.id.sw_reminder_repeat);

        sw_reminder_repeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                alertModel.getRepeatModel().setEnable(isChecked);
                refresh();
            }
        });

        btn_reminder_date.setOnClickListener(view -> getDatePickerDialog().show(getSupportFragmentManager(), RemindMeDatePickerDialog.TAG));
        btn_reminder_time.setOnClickListener(view -> getTimePickerDialog().show(getSupportFragmentManager(), TimePickerDialogBase.TAG));

        final LinearLayoutCompat mnu_reminder_name = findViewById(R.id.mnu_reminder_name);
        mnu_reminder_name.setOnClickListener(v -> getNameDialog().show(getSupportFragmentManager(), NameDialog.TAG));

        final LinearLayoutCompat mnu_reminder_repeat = findViewById(R.id.mnu_reminder_repeat);
        mnu_reminder_repeat.setOnClickListener(v -> getRepeatDialog().show(getSupportFragmentManager(), RepeatDialog.TAG));

        final FloatingActionButton imgBtnSetReminder = findViewById(R.id.imgBtnSetReminder);
        imgBtnSetReminder.setOnClickListener(view -> {
            if (alertModel.getTimeModel().getAlertTime(true).after(Calendar.getInstance().getTime())) {
                // the method "reminderModel.isHasDifferentTimeCalculated()" will ensure that time has not been changed than what was given.
                // And changes were made on other areas.
                // Otherwise it needs to clear snooze details.
                alertModel.saveAndSetAlert(ReminderInput.this, true);
                finish();
            } else {
                ToastHelper.showShort(ReminderInput.this, "Cannot save reminder in past");
            }
        });

        final AppCompatImageView img_reminder_name_voice_input = findViewById(R.id.img_reminder_name_voice_input);
        img_reminder_name_voice_input.setOnClickListener(v -> {
            final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // This starts the activity and populates the intent with the speech text.
            startActivityForResult(intent, NAME_SPEECH_REQUEST_CODE);
        });

        //lv_reminder_extra_inputs = findViewById(R.id.lv_reminder_extra_inputs);
//        tv_advance_options_status = findViewById(R.id.tv_advance_options_status);
//        advance_options_image_view = findViewById(R.id.advance_options_image_view);
        LinearLayoutCompat advance_options_layout = findViewById(R.id.advance_options_layout);
        advance_options_layout.setOnClickListener(v -> {
            getInputAdvanceOptionsDialog().show(getSupportFragmentManager(), InputAdvanceOptionsDialog.TAG);
        });

//        sv_container = findViewById(R.id.sv_container);

        refresh();

    }

    @Override
    protected void onUIRefresh() {
        super.onUIRefresh();

        btn_reminder_time.setText(StringHelper.toTime(alertModel.getTimeModel().getTime()));
        tv_reminder_AmPm.setText(StringHelper.toAmPm(alertModel.getTimeModel().getTime()));
        btn_reminder_date.setText(StringHelper.toWeekdayDate(this, alertModel.getTimeModel().getTime()));

        if (alertModel.getTimeModel().isHasScheduledTime()) {
            lvc_diff_next_reminder_trigger.setVisibility(View.VISIBLE);
            tv_reminder_trigger_time.setText(StringHelper.toTimeAmPm(alertModel.getTimeModel().getScheduledTime()));
            tv_reminder_trigger_date.setText(StringHelper.toWeekdayDate(this, alertModel.getTimeModel().getScheduledTime()));
        } else {
            lvc_diff_next_reminder_trigger.setVisibility(View.GONE);
        }

        tv_reminder_name_summary.setText(alertModel.getName());


        chk_reminder.setChecked(alertModel.isReminder());
        chk_alarm.setChecked(!alertModel.isReminder());

        tv_reminder_repeat_summary.setText(alertModel.getRepeatModel().toString(this));
        sw_reminder_repeat.setChecked(alertModel.getRepeatModel().isEnabled());

        //setExtraInputs();
    }


//    private void setExtraInputs() {
//
//        if (isExtraInputsVisible) {
//
//            advance_options_image_view.setImageResource(R.drawable.ic_expand_up);
//            advance_options_image_view.setColorFilter(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDangerColor)),
//                    android.graphics.PorterDuff.Mode.SRC_IN);
//            tv_advance_options_status.setText(R.string.hide_advance_options_label);
//            lv_reminder_extra_inputs.setVisibility(View.VISIBLE);
//
//        } else {
//
//            advance_options_image_view.setImageResource(R.drawable.ic_expand_down);
//            advance_options_image_view.setColorFilter(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeSuccessColor)),
//                    android.graphics.PorterDuff.Mode.SRC_IN);
//            tv_advance_options_status.setText(R.string.show_advance_options_label);
//            lv_reminder_extra_inputs.setVisibility(View.GONE);
//
//        }
//    }

}