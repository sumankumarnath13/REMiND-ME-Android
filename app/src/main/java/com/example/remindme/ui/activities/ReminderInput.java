package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.remindme.ui.fragments.dialogFragments.NoteDialog;
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
        NoteDialog.INoteInputDialogListener,
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
        return alertModel.getName();
    }

    @Override
    public void setNoteDialogModel(String note) {
        alertModel.setNote(note);
        refresh();
    }

    @Override
    public String getNoteDialogModel() {
        return alertModel.getNote();
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
        return alertModel.getRepeatModel();
    }

    @Override
    public void onSetListenerDate(int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        alertModel.getTimeModel().setTime(calendar.getTime());
        refresh();
    }

    @Override
    public Date onGetListenerDate() {
        return alertModel.getTimeModel().getTime();
    }

    @Override
    public void onSetListenerTime(int hourOfDay, int minute) {
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        alertModel.getTimeModel().setTime(calendar.getTime());
        refresh();
    }

    @Override
    public Date onGetListenerTime() {
        return alertModel.getTimeModel().getTime();
    }

    private static final int NAME_SPEECH_REQUEST_CODE = 119;
    private static final int NOTE_SPEECH_REQUEST_CODE = 113;
    private AlertModel alertModel = null;

    private AppCompatTextView tv_reminder_trigger_time;
    private AppCompatTextView tv_reminder_trigger_date;
    private AppCompatButton btn_reminder_time;
    private AppCompatTextView tv_reminder_AmPm;
    private AppCompatButton btn_reminder_date;
    private AppCompatTextView tv_reminder_name_summary;
    private AppCompatTextView tv_reminder_note_summary;
    private AppCompatCheckBox chk_reminder;
    private AppCompatCheckBox chk_alarm;
    private SwitchCompat sw_reminder_repeat;
    private AppCompatTextView tv_reminder_repeat_summary;

    private LinearLayoutCompat lvc_diff_next_reminder_trigger;

    private NameDialog nameDialog;

    private NameDialog getNameDialog() {
        if (nameDialog == null) {
            nameDialog = new NameDialog();
        }
        return nameDialog;
    }

    private RepeatDialog repeatDialog;

    private NoteDialog noteDialog;

    private NoteDialog getNoteDialog() {
        if (noteDialog == null) {
            noteDialog = new NoteDialog();
        }
        return noteDialog;
    }

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
        } else if (requestCode == NOTE_SPEECH_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            final List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            final String spokenText = results.get(0);
            alertModel.setNote(spokenText);
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
        tv_reminder_note_summary = findViewById(R.id.tv_reminder_note_summary);
        final LinearLayoutCompat mnu_reminder_note = findViewById(R.id.mnu_reminder_note);
        mnu_reminder_note.setOnClickListener(v -> getNoteDialog().show(getSupportFragmentManager(), NoteDialog.TAG));

        final AppCompatImageView img_reminder_note_voice_input = findViewById(R.id.img_reminder_note_voice_input);
        img_reminder_note_voice_input.setOnClickListener(v -> {
            final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // This starts the activity and populates the intent with the speech text.
            startActivityForResult(intent, NOTE_SPEECH_REQUEST_CODE);
        });

        tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);

        final LinearLayoutCompat advance_options_layout = findViewById(R.id.advance_options_layout);
        advance_options_layout.setOnClickListener(v -> {
            getInputAdvanceOptionsDialog().show(getSupportFragmentManager(), InputAdvanceOptionsDialog.TAG);
        });

        chk_reminder = findViewById(R.id.chk_reminder);
        chk_reminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted() && isChecked) {
                advance_options_layout.setVisibility(View.GONE);
                alertModel.setReminder(true);
                refresh();
            }
        });

        chk_alarm = findViewById(R.id.chk_alarm);
        chk_alarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted() && isChecked) {
                advance_options_layout.setVisibility(View.VISIBLE);
                alertModel.setReminder(false);
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
        tv_reminder_note_summary.setText(alertModel.getNote());

        chk_reminder.setChecked(alertModel.isReminder());
        chk_alarm.setChecked(!alertModel.isReminder());

        tv_reminder_repeat_summary.setText(alertModel.getRepeatModel().toSpannableString(
                getResources().getColor(resolveRefAttributeResourceId(R.attr.themeSuccessColor)), this));

        sw_reminder_repeat.setChecked(alertModel.getRepeatModel().isEnabled());
    }

}