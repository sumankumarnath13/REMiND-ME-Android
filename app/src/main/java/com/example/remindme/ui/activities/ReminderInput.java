package com.example.remindme.ui.activities;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.controllers.RingingController;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.NameDialog;
import com.example.remindme.ui.fragments.dialogFragments.NoteDialog;
import com.example.remindme.ui.fragments.dialogFragments.RepeatDialog;
import com.example.remindme.ui.fragments.dialogFragments.SnoozeDialog;
import com.example.remindme.ui.fragments.dialogFragments.TimeListAnyTimeDialog;
import com.example.remindme.ui.fragments.dialogFragments.TimeListHourlyDialog;
import com.example.remindme.ui.fragments.dialogFragments.common.RemindMeDatePickerDialog;
import com.example.remindme.ui.fragments.dialogFragments.common.TimeListDialogBase;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogBase;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogBlack;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogLight;
import com.example.remindme.viewModels.AlertModel;
import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.RingingModel;
import com.example.remindme.viewModels.SnoozeModel;
import com.example.remindme.viewModels.TimeModel;
import com.example.remindme.viewModels.factories.AlertViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderInput
        extends
        ActivityBase
        implements
        AdapterView.OnItemSelectedListener,
        TimePickerDialogBase.ITimePickerListener,
        RemindMeDatePickerDialog.IDatePickerListener,
        SnoozeDialog.ISnoozeInputDialogListener,
        RepeatDialog.IRepeatInputDialogListener,
        NameDialog.INameInputDialogListener,
        NoteDialog.INoteInputDialogListener {

    @Override
    public void setNoteDialogModel(String note) {
        alertModel.setNote(note);
        refresh();
    }

    @Override
    public String getNoteDialogModel() {
        alertModel = new ViewModelProvider(ReminderInput.this).get(AlertModel.class);
        return alertModel.getNote();
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
        if (model.isValid(alertModel.getTimeModel())) {
            alertModel.setRepeatModel(model);
            alertModel.getTimeModel().setScheduledTime(model.getValidatedScheduledTime());
        } else {
            ToastHelper.showShort(ReminderInput.this, "Please check repeat settings");
        }
    }

    @Override
    public RepeatModel getRepeatDialogModel() {
        alertModel = new ViewModelProvider(ReminderInput.this).get(AlertModel.class);
        return alertModel.getRepeatModel();
    }

    @Override
    public void onSetListenerSnoozeModel(SnoozeModel model) {
        alertModel.setSnoozeModel(model);
        refresh();
    }

    @Override
    public SnoozeModel onGetListenerSnoozeModel() {
        alertModel = new ViewModelProvider(ReminderInput.this).get(AlertModel.class);
        return alertModel.getSnoozeModel();
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
    private static final int NOTE_SPEECH_REQUEST_CODE = 113;
    private static final int RINGTONE_DIALOG_REQ_CODE = 117;

    private static final String MORE_INPUT_UI_STATE = "MORE_INPUT";

    private boolean isExtraInputsVisible;
    private AlertModel alertModel = null;
    private RingingController ringingController;

    private AppCompatTextView tv_reminder_trigger_time;
    private AppCompatTextView tv_reminder_trigger_date;
    private AppCompatButton btn_reminder_time;
    private AppCompatTextView tv_reminder_AmPm;
    private AppCompatButton btn_reminder_date;
    private AppCompatCheckBox chk_time_list_hours;
    private AppCompatCheckBox chk_time_list_anytime;
    private AppCompatTextView tv_reminder_time_list_summary;

    private AppCompatTextView tv_reminder_tone_summary;
    private AppCompatTextView tv_reminder_name_summary;
    private AppCompatTextView tv_reminder_note_summary;
    private AppCompatCheckBox chk_reminder;
    private AppCompatCheckBox chk_alarm;
    private SwitchCompat sw_reminder_repeat;
    private SwitchCompat sw_reminder_snooze;
    private AppCompatTextView tv_reminder_repeat_summary;
    private AppCompatTextView tv_reminder_snooze_summary;
    private AppCompatSeekBar seeker_alarm_volume;
    private SwitchCompat sw_gradually_increase_volume;

    private SwitchCompat sw_reminder_tone;
    private SwitchCompat sw_reminder_vibrate;
    private LinearLayoutCompat advance_options_layout;
    private AppCompatTextView tv_advance_options_status;
    private AppCompatImageView advance_options_image_view;
    private LinearLayoutCompat lv_reminder_extra_inputs;
    private NestedScrollView sv_container;
    private AppCompatSpinner ring_duration_spinner;
    private AppCompatSpinner vibrate_pattern_spinner;

    private AppCompatImageButton imgBtnPlayStop;
    private AppCompatButton btnSetDefaultTone;
    private boolean isPlayingTone;
    private int deviceAlarmVolume;

    private LinearLayoutCompat lvc_diff_next_reminder_trigger;
    private LinearLayoutCompat alarm_only_layout;

    private NameDialog nameDialog;

    private NameDialog getNameDialog() {
        if (nameDialog == null) {
            nameDialog = new NameDialog();
        }
        return nameDialog;
    }

    private NoteDialog noteDialog;

    private NoteDialog getNoteDialog() {
        if (noteDialog == null) {
            noteDialog = new NoteDialog();
        }
        return noteDialog;
    }

    private RepeatDialog repeatDialog;

    private RepeatDialog getRepeatDialog() {
        if (repeatDialog == null) {
            repeatDialog = new RepeatDialog();
        }
        return repeatDialog;
    }

    private SnoozeDialog snoozeDialog;

    private SnoozeDialog getSnoozeDialog() {
        if (snoozeDialog == null) {
            snoozeDialog = new SnoozeDialog();
        }
        return snoozeDialog;
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

    private TimeListDialogBase.ITimeListListener timeListListener;

    private TimeListDialogBase.ITimeListListener getTimeListListener() {
        if (timeListListener == null) {
            timeListListener = new TimeListDialogBase.ITimeListListener() {
                @Override
                public TimeModel getTimeListDialogModel() {
                    alertModel = new ViewModelProvider(ReminderInput.this).get(AlertModel.class);
                    return alertModel.getTimeModel();
                }

                @Override
                public void setTimeListDialogModel(TimeModel model) {
                    if (alertModel.getRepeatModel().isValid(model)) {
                        alertModel.setTimeModel(model);
                        alertModel.getTimeModel().setScheduledTime(alertModel.getRepeatModel().getValidatedScheduledTime());
                    } else {
                        ToastHelper.showShort(ReminderInput.this, "Please check repeat settings");
                    }
                }
            };
        }
        return timeListListener;
    }

    private TimeListHourlyDialog timeListHourlyDialog;

    private TimeListHourlyDialog getTimeListHourlyDialog() {
        if (timeListHourlyDialog == null) {
            timeListHourlyDialog = new TimeListHourlyDialog();
            timeListHourlyDialog.setListener(getTimeListListener());
        }
        return timeListHourlyDialog;
    }

    private TimeListAnyTimeDialog timeListAnyTimeDialog;

    private TimeListAnyTimeDialog getTimeListAnyTimeDialog() {
        if (timeListAnyTimeDialog == null) {
            timeListAnyTimeDialog = new TimeListAnyTimeDialog();
            timeListAnyTimeDialog.setListener(getTimeListListener());
        }
        return timeListAnyTimeDialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RINGTONE_DIALOG_REQ_CODE && data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                alertModel.getRingingModel().setRingToneUri(uri);
            }
            refresh();
        } else if (requestCode == NAME_SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            tv_reminder_name_summary.setText(spokenText);
            alertModel.setName(spokenText);
        } else if (requestCode == NOTE_SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            tv_reminder_note_summary.setText(spokenText);
            alertModel.setNote(spokenText);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(MORE_INPUT_UI_STATE, isExtraInputsVisible);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_input);

        setUserInteracted(false);

        if (savedInstanceState != null) {
            isExtraInputsVisible = savedInstanceState.getBoolean(MORE_INPUT_UI_STATE, false);
        }

        alertModel = new ViewModelProvider(this, new AlertViewModelFactory(getIntent())).get(AlertModel.class);

        if (alertModel.isNew()) { // First time creating the activity
            setActivityTitle(getResources().getString(R.string.heading_label_new_reminder));
        } else {
            setActivityTitle(getResources().getString(R.string.heading_label_edit_reminder));
        }

        alarm_only_layout = findViewById(R.id.alarm_only_layout);
        tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
        tv_reminder_name_summary = findViewById(R.id.tv_reminder_name_summary);
        tv_reminder_note_summary = findViewById(R.id.tv_reminder_note_summary);
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

        sw_reminder_repeat = findViewById(R.id.sw_reminder_repeat);
        tv_reminder_snooze_summary = findViewById(R.id.tv_reminder_snooze_summary);
        sw_reminder_snooze = findViewById(R.id.sw_reminder_snooze);
        tv_reminder_trigger_time = findViewById(R.id.tv_reminder_trigger_time);
        tv_reminder_trigger_date = findViewById(R.id.tv_reminder_trigger_date);
        btn_reminder_date = findViewById(R.id.btn_reminder_date);
        btn_reminder_time = findViewById(R.id.btn_reminder_time);
        tv_reminder_AmPm = findViewById(R.id.tv_reminder_AmPm);
        lvc_diff_next_reminder_trigger = findViewById(R.id.lvc_diff_next_reminder_trigger);

        sw_reminder_repeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                alertModel.getRepeatModel().setEnable(isChecked);
                refresh();
            }
        });

        sw_reminder_snooze.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                alertModel.getSnoozeModel().setEnable(sw_reminder_snooze.isChecked());
                refresh();
            }
        });

        btn_reminder_date.setOnClickListener(view -> getDatePickerDialog().show(getSupportFragmentManager(), RemindMeDatePickerDialog.TAG));

        btn_reminder_time.setOnClickListener(view -> getTimePickerDialog().show(getSupportFragmentManager(), TimePickerDialogBase.TAG));

        tv_reminder_time_list_summary = findViewById(R.id.tv_reminder_time_list_summary);

        chk_time_list_hours = findViewById(R.id.chk_time_list_hours);
        chk_time_list_hours.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isUserInteracted()) {

                buttonView.setChecked(!buttonView.isChecked()); // Keep the check status as it is here. Real changes will occur once user return from dialog
                setUserInteracted(false); // This is very important. Because its just making a dialog visible and is no real interaction.

                if (alertModel.getRepeatModel().getRepeatOption() == RepeatModel.ReminderRepeatOptions.HOURLY) {
                    ToastHelper.showShort(ReminderInput.this, "Time lists aren't possible if repeat is set to hourly. Please change repeat option");
                    return;
                }

                getTimeListHourlyDialog().show(getSupportFragmentManager(), TimeListHourlyDialog.TAG);
            }
        });

        chk_time_list_anytime = findViewById(R.id.chk_time_list_anytime);
        chk_time_list_anytime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRefreshing() && isUserInteracted()) {

                buttonView.setChecked(!buttonView.isChecked()); // Keep the check status as it is here. Real changes will occur once user return from dialog
                setUserInteracted(false); // This is very important. Because its just making a dialog visible and is no real interaction.

                if (alertModel.getRepeatModel().getRepeatOption() == RepeatModel.ReminderRepeatOptions.HOURLY) {
                    ToastHelper.showShort(ReminderInput.this, "Time lists aren't possible if repeat is set to hourly. Please change repeat option");
                    return;
                }

                //final TimeListAnyTimeDialog timeListInputHourlyDialog = new TimeListAnyTimeDialog();
                getTimeListAnyTimeDialog().show(getSupportFragmentManager(), TimeListAnyTimeDialog.TAG);
            }
        });

        final LinearLayoutCompat mnu_reminder_name = findViewById(R.id.mnu_reminder_name);
        mnu_reminder_name.setOnClickListener(v -> getNameDialog().show(getSupportFragmentManager(), NameDialog.TAG));

        final LinearLayoutCompat mnu_reminder_note = findViewById(R.id.mnu_reminder_note);
        mnu_reminder_note.setOnClickListener(v -> getNoteDialog().show(getSupportFragmentManager(), NoteDialog.TAG));

        final LinearLayoutCompat mnu_reminder_repeat = findViewById(R.id.mnu_reminder_repeat);
        mnu_reminder_repeat.setOnClickListener(v -> getRepeatDialog().show(getSupportFragmentManager(), RepeatDialog.TAG));

        final LinearLayoutCompat mnu_reminder_snooze = findViewById(R.id.snooze_input_layout);
        mnu_reminder_snooze.setOnClickListener(v -> getSnoozeDialog().show(getSupportFragmentManager(), SnoozeDialog.TAG));

        final LinearLayoutCompat mnu_reminder_tone = findViewById(R.id.tone_input_layout);
        mnu_reminder_tone.setOnClickListener(v -> {
            final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone:");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, alertModel.getRingingModel().getRingToneUri());
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent, RINGTONE_DIALOG_REQ_CODE);
        });

        sw_reminder_tone = findViewById(R.id.sw_reminder_tone);
        sw_reminder_tone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUserInteracted())
                return;
            alertModel.getRingingModel().setToneEnabled(isChecked);
            refresh();
        });

        sw_reminder_vibrate = findViewById(R.id.sw_reminder_vibrate);
        sw_reminder_vibrate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUserInteracted())
                return;
            alertModel.getRingingModel().setVibrationEnabled(isChecked);
            refresh();
        });

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

        final AppCompatImageView img_reminder_note_voice_input = findViewById(R.id.img_reminder_note_voice_input);
        img_reminder_note_voice_input.setOnClickListener(v -> {
            final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // This starts the activity and populates the intent with the speech text.
            startActivityForResult(intent, NOTE_SPEECH_REQUEST_CODE);
        });

        sw_gradually_increase_volume = findViewById(R.id.sw_gradually_increase_volume);
        sw_gradually_increase_volume.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                alertModel.getRingingModel().setIncreaseVolumeGradually(sw_gradually_increase_volume.isChecked());
                refresh();
            }
        });

        imgBtnPlayStop = findViewById(R.id.imgBtnPlayStop);
        imgBtnPlayStop.setOnClickListener(v -> {
            if (isPlayingTone) {
                stopTone();
            } else {
                startTone();
            }
            refresh();
        });

        btnSetDefaultTone = findViewById(R.id.btnSetDefaultTone);
        btnSetDefaultTone.setOnClickListener(v -> {
            alertModel.getRingingModel().setDefaultRingTone();
            refresh();
        });

        deviceAlarmVolume = OsHelper.getAlarmVolumeInPercentage(OsHelper.getAudioManager(this));
        seeker_alarm_volume = findViewById(R.id.seeker_alarm_volume);
        seeker_alarm_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress < RingingModel.MINIMUM_INPUT_VOLUME_PERCENTAGE)
                        seekBar.setProgress(RingingModel.MINIMUM_INPUT_VOLUME_PERCENTAGE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                alertModel.getRingingModel().setAlarmVolumePercentage(seekBar.getProgress());
                ToastHelper.showShort(ReminderInput.this, "Alarm will ring at " + alertModel.getRingingModel().getAlarmVolumePercentage() + "% volume");
            }
        });

        lv_reminder_extra_inputs = findViewById(R.id.lv_reminder_extra_inputs);
        tv_advance_options_status = findViewById(R.id.tv_advance_options_status);
        advance_options_image_view = findViewById(R.id.advance_options_image_view);
        advance_options_layout = findViewById(R.id.advance_options_layout);
        advance_options_layout.setOnClickListener(v -> {
            isExtraInputsVisible = !isExtraInputsVisible;
            setUserInteracted(false); // This is very important. Because its just making a layout visible and is no real interaction.
            setExtraInputs();
            sv_container.post(() -> sv_container.fullScroll(ScrollView.FOCUS_DOWN));
        });

        sv_container = findViewById(R.id.sv_container);

        ring_duration_spinner = findViewById(R.id.ring_duration_spinner);
        ArrayAdapter<CharSequence> ring_duration_adapter = ArrayAdapter.createFromResource(this, R.array.values_ring_duration, R.layout.item_dropdown_fragment_simple_spinner);
        // adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        ring_duration_spinner.setAdapter(ring_duration_adapter);
        ring_duration_spinner.setOnItemSelectedListener(this);

        vibrate_pattern_spinner = findViewById(R.id.vibrate_pattern_spinner);
        ArrayAdapter<CharSequence> vibrate_pattern_adapter = ArrayAdapter.createFromResource(this, R.array.values_vibration_pattern, R.layout.item_dropdown_fragment_simple_spinner);
        // adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        vibrate_pattern_spinner.setAdapter(vibrate_pattern_adapter);
        vibrate_pattern_spinner.setOnItemSelectedListener(this);

        refresh();

    }

    @Override
    protected void onUIRefresh() {
        super.onUIRefresh();

        btn_reminder_time.setText(StringHelper.toTime(alertModel.getTimeModel().getTime()));
        tv_reminder_AmPm.setText(StringHelper.toAmPm(alertModel.getTimeModel().getTime()));
        btn_reminder_date.setText(StringHelper.toWeekdayDate(this, alertModel.getTimeModel().getTime()));

        chk_time_list_hours.setChecked(false);
        chk_time_list_anytime.setChecked(false);
        tv_reminder_time_list_summary.setText(alertModel.getTimeModel().toSpannableString(
                getResources().getColor(resolveRefAttributeResourceId(R.attr.themeSuccessColor))));

        if (alertModel.getTimeModel().getTimeListMode() == TimeModel.TimeListModes.HOURLY) {
            chk_time_list_hours.setChecked(true);
        } else if (alertModel.getTimeModel().getTimeListMode() == TimeModel.TimeListModes.ANYTIME) {
            chk_time_list_anytime.setChecked(true);
        }

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

        if (alertModel.isReminder()) {
            alarm_only_layout.setVisibility(View.GONE);
        } else {
            alarm_only_layout.setVisibility(View.VISIBLE);
        }

        tv_reminder_repeat_summary.setText(alertModel.getRepeatModel().toString(this));
        tv_reminder_snooze_summary.setText(alertModel.getSnoozeModel().toString());

        sw_reminder_snooze.setChecked(alertModel.getSnoozeModel().isEnable());
        sw_reminder_repeat.setChecked(alertModel.getRepeatModel().isEnabled());
        sw_reminder_tone.setChecked(alertModel.getRingingModel().isToneEnabled());

        if (alertModel.getRingingModel().getRingToneUri() == null) {
            final Uri alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            final Ringtone alarmTone = RingtoneManager.getRingtone(this, alarmToneUri);
            tv_reminder_tone_summary.setText(alarmTone.getTitle(this));
            btnSetDefaultTone.setVisibility(View.INVISIBLE);
        } else {
            final Ringtone ringtone = RingtoneManager.getRingtone(this, alertModel.getRingingModel().getRingToneUri());
            tv_reminder_tone_summary.setText(ringtone.getTitle(this));
            btnSetDefaultTone.setVisibility(View.VISIBLE);
        }

        if (alertModel.getRingingModel().isToneEnabled()) {
            sw_gradually_increase_volume.setEnabled(true);
            sw_gradually_increase_volume.setChecked(alertModel.getRingingModel().isIncreaseVolumeGradually());

            imgBtnPlayStop.setEnabled(true);
            btnSetDefaultTone.setEnabled(true);

            seeker_alarm_volume.setEnabled(true);
            tv_reminder_tone_summary.setEnabled(true);

            ring_duration_spinner.setEnabled(true);
            ring_duration_spinner.setSelection(RingingModel.convertToAlarmRingDuration(alertModel.getRingingModel().getAlarmRingDuration()));

        } else {
            sw_gradually_increase_volume.setEnabled(false);
            sw_gradually_increase_volume.setChecked(false);

            imgBtnPlayStop.setEnabled(false);
            btnSetDefaultTone.setEnabled(false);

            seeker_alarm_volume.setEnabled(false);
            tv_reminder_tone_summary.setEnabled(false);
            ring_duration_spinner.setEnabled(false);
        }

        if (alertModel.getRingingModel().getAlarmVolumePercentage() == 0) {
            seeker_alarm_volume.setProgress(deviceAlarmVolume);
        } else {
            seeker_alarm_volume.setProgress(alertModel.getRingingModel().getAlarmVolumePercentage());
        }

        if (isPlayingTone) {
            imgBtnPlayStop.setImageResource(R.drawable.ic_play_stop);
        } else {
            imgBtnPlayStop.setImageResource(R.drawable.ic_play);
        }

        tv_reminder_tone_summary.setText(alertModel.getRingingModel().getRingToneUriSummary(this));
        sw_reminder_vibrate.setChecked(alertModel.getRingingModel().isVibrationEnabled());
        vibrate_pattern_spinner.setSelection(RingingModel.convertToVibratePattern(alertModel.getRingingModel().getVibratePattern()));
        vibrate_pattern_spinner.setEnabled(alertModel.getRingingModel().isVibrationEnabled());

        setExtraInputs();
    }

    private void setExtraInputs() {

        if (isExtraInputsVisible) {

            advance_options_image_view.setImageResource(R.drawable.ic_expand_up);
            advance_options_image_view.setColorFilter(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDangerColor)),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            tv_advance_options_status.setText(R.string.hide_advance_options_label);
            lv_reminder_extra_inputs.setVisibility(View.VISIBLE);

        } else {

            advance_options_image_view.setImageResource(R.drawable.ic_expand_down);
            advance_options_image_view.setColorFilter(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeSuccessColor)),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            tv_advance_options_status.setText(R.string.show_advance_options_label);
            lv_reminder_extra_inputs.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onDestroy() {
        stopTone();
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!isUserInteracted()) {
            return;
        }

        if (parent.getId() == R.id.ring_duration_spinner) {
            alertModel.getRingingModel().setAlarmRingDuration(RingingModel.convertToAlarmRingDuration(position));
        } else if (parent.getId() == R.id.vibrate_pattern_spinner) {
            alertModel.getRingingModel().setVibratePattern(RingingModel.convertToVibratePattern(position));
            startVibrating();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private RingingController getRingingController() {
        if (ringingController == null || !isPlayingTone) { // "!isPlayingTone" this condition will ensure new instance of RingingController with updated ReminderModel tone URI.
            ringingController = new RingingController(ReminderInput.this, alertModel.getRingingModel().getRingToneUri());
        }
        return ringingController;
    }

    private void startVibrating() {
        getRingingController().vibrateOnce(RingingModel.convertToVibrateFrequency(alertModel.getRingingModel().getVibratePattern()));
    }

    private void startTone() {
        getRingingController().startTone(alertModel.getRingingModel().isIncreaseVolumeGradually(), alertModel.getRingingModel().getAlarmVolumePercentage());
        isPlayingTone = true;
    }

    private void stopTone() {
        if (ringingController != null) {
            ringingController.stopRinging(); // Stop ring stops both tone and vibration if is playing.
        }
        isPlayingTone = false;
    }

}