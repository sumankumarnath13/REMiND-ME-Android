package com.example.remindme.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
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
import com.example.remindme.ui.fragments.dialogFragments.DateCalculatorDialog;
import com.example.remindme.ui.fragments.dialogFragments.HourlyTimeListDialog;
import com.example.remindme.ui.fragments.dialogFragments.NameDialog;
import com.example.remindme.ui.fragments.dialogFragments.NoteDialog;
import com.example.remindme.ui.fragments.dialogFragments.RandomTimeListDialog;
import com.example.remindme.ui.fragments.dialogFragments.RepeatDialog;
import com.example.remindme.ui.fragments.dialogFragments.SnoozeDialog;
import com.example.remindme.ui.fragments.dialogFragments.TimeListDialogBase;
import com.example.remindme.viewModels.ReminderModel;
import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.RingingModel;
import com.example.remindme.viewModels.SnoozeModel;
import com.example.remindme.viewModels.TimeModel;
import com.example.remindme.viewModels.factories.ReminderViewModelFactory;
import com.example.remindme.viewModels.factories.TimeViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderInput
        extends
        ActivityBase
        implements
        AdapterView.OnItemSelectedListener,
        TimeListDialogBase.ITimeListListener,
        NameDialog.INameInputDialogListener,
        NoteDialog.INoteInputDialogListener,
        RepeatDialog.IRepeatInputDialogListener,
        SnoozeDialog.ISnoozeInputDialogListener,
        DateCalculatorDialog.ITimeCalculatorListener {

    private static final int NAME_SPEECH_REQUEST_CODE = 119;
    private static final int NOTE_SPEECH_REQUEST_CODE = 113;
    private static final int RINGTONE_DIALOG_REQ_CODE = 117;
    private static final String MORE_INPUT_UI_STATE = "MORE_INPUT";

    private boolean isExtraInputsVisible;
    private ReminderModel reminderModel = null;
    private RingingController ringingController;

    private AppCompatTextView tv_reminder_trigger_time;
    private AppCompatTextView tv_reminder_trigger_date;
    private AppCompatButton btn_reminder_time;
    private AppCompatButton btn_reminder_date;
    private AppCompatButton btnTimeHours;
    private AppCompatButton btnTimeList;

    private AppCompatTextView tv_reminder_tone_summary;
    private AppCompatTextView tv_reminder_name_summary;
    private AppCompatTextView tv_reminder_note_summary;
    private SwitchCompat sw_notification;
    private SwitchCompat sw_reminder_repeat;
    private SwitchCompat sw_reminder_snooze;
    private AppCompatTextView tv_reminder_repeat_summary;
    private AppCompatTextView tv_reminder_snooze_summary;
    private AppCompatSeekBar seeker_alarm_volume;
    private SwitchCompat sw_gradually_increase_volume;
    private LinearLayoutCompat lvc_diff_next_reminder_trigger;

    private SwitchCompat sw_reminder_tone;
    private SwitchCompat sw_reminder_vibrate;
    private LinearLayoutCompat lv_reminder_extra_inputs;
    private AppCompatImageButton btn_reminder_extra_inputs;
    private NestedScrollView sv_container;
    private AppCompatSpinner ring_duration_spinner;
    private AppCompatSpinner vibrate_pattern_spinner;

    private AppCompatImageButton imgBtnPlayStop;
    private AppCompatButton btnSetDefaultTone;
    private boolean isPlayingTone;
    private int deviceAlarmVolume;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RINGTONE_DIALOG_REQ_CODE && data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                reminderModel.getRingingModel().setRingToneUri(uri);
            }
            refresh();
        } else if (requestCode == NAME_SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            tv_reminder_name_summary.setText(spokenText);
            reminderModel.setName(spokenText);
        } else if (requestCode == NOTE_SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            tv_reminder_note_summary.setText(spokenText);
            reminderModel.setNote(spokenText);
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

        reminderModel = new ViewModelProvider(this, new ReminderViewModelFactory(getIntent())).get(ReminderModel.class);

        if (reminderModel.isNew()) { // First time creating the activity
            setActivityTitle(getResources().getString(R.string.new_reminder_heading));
        } else {
            setActivityTitle(getResources().getString(R.string.edit_reminder_heading));
        }

        tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
        tv_reminder_name_summary = findViewById(R.id.tv_reminder_name_summary);
        tv_reminder_note_summary = findViewById(R.id.tv_reminder_note_summary);
        tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);
        sw_notification = findViewById(R.id.sw_notification);
        sw_notification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                reminderModel.setNotification(isChecked);
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
        lvc_diff_next_reminder_trigger = findViewById(R.id.lvc_diff_next_reminder_trigger);

        sw_reminder_repeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                reminderModel.getRepeatModel().setEnabled(isChecked);
                refresh();
            }
        });

        sw_reminder_snooze.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                reminderModel.getSnoozeModel().setEnable(sw_reminder_snooze.isChecked());
                refresh();
            }
        });

        btn_reminder_date.setOnClickListener(view -> {
            final Calendar alertTime = Calendar.getInstance();
            //final Calendar currentTime = Calendar.getInstance();
            alertTime.setTime(reminderModel.getTimeModel().getTime());
            final int mYear, mMonth, mDay;
            mYear = alertTime.get(Calendar.YEAR);
            mMonth = alertTime.get(Calendar.MONTH);
            mDay = alertTime.get(Calendar.DAY_OF_MONTH);
            final DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderInput.this, R.style.DatePickerStyle,
                    (view12, year, monthOfYear, dayOfMonth) -> {
                        alertTime.set(Calendar.YEAR, year);
                        alertTime.set(Calendar.MONTH, monthOfYear);
                        alertTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        alertTime.set(Calendar.SECOND, 0);
                        alertTime.set(Calendar.MILLISECOND, 0);
                        reminderModel.getTimeModel().setTime(alertTime.getTime());
                        refresh();
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()); // This will cause extra title on the top of the regular date picker
            datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // This line will try to solve the issue above
            datePickerDialog.setTitle(null); // This line will try to solve the issue above
            datePickerDialog.show();

            if (OsHelper.isLollipopOrLater()) {
                datePickerDialog.getDatePicker().setFirstDayOfWeek(AppSettingsHelper.getInstance().getFirstDayOfWeek());
            }
        });

        btn_reminder_time.setOnClickListener(view -> {
            final Calendar alertTime = Calendar.getInstance();
            //final Calendar currentTime = Calendar.getInstance();
            alertTime.setTime(reminderModel.getTimeModel().getTime());
            final int mHour, mMinute;
            mHour = alertTime.get(Calendar.HOUR_OF_DAY);
            mMinute = alertTime.get(Calendar.MINUTE);

            final TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderInput.this, R.style.TimePickerStyle,
                    (view1, hourOfDay, minute) -> {
                        alertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        alertTime.set(Calendar.MINUTE, minute);
                        alertTime.set(Calendar.SECOND, 0);
                        alertTime.set(Calendar.MILLISECOND, 0);
                        reminderModel.getTimeModel().setTime(alertTime.getTime());
                        refresh();
                    }, mHour, mMinute, AppSettingsHelper.getInstance().isUse24hourTime());
            timePickerDialog.show();
            //timePickerDialog.;
        });

        final AppCompatButton btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(v -> {
            DateCalculatorDialog calculatorDialog = new DateCalculatorDialog();
            calculatorDialog.show(getSupportFragmentManager(), DateCalculatorDialog.TAG);
        });

        btnTimeHours = findViewById(R.id.btnTimeHours);
        btnTimeHours.setOnClickListener(v -> {

            if (reminderModel.getRepeatModel().getRepeatOption() == RepeatModel.ReminderRepeatOptions.HOURLY) {
                ToastHelper.showShort(ReminderInput.this, "Time lists aren't possible if repeat is set to hourly. Please change repeat option");
                return;
            }

            HourlyTimeListDialog hourlyTimeListDialog = new HourlyTimeListDialog();
            hourlyTimeListDialog.show(getSupportFragmentManager(), HourlyTimeListDialog.TAG);
        });

        btnTimeList = findViewById(R.id.btnTimeList);
        btnTimeList.setOnClickListener(v -> {

            if (reminderModel.getRepeatModel().getRepeatOption() == RepeatModel.ReminderRepeatOptions.HOURLY) {
                ToastHelper.showShort(ReminderInput.this, "Time lists aren't possible if repeat is set to hourly. Please change repeat option");
                return;
            }

            RandomTimeListDialog timeListInputHourlyDialog = new RandomTimeListDialog();
            timeListInputHourlyDialog.show(getSupportFragmentManager(), RandomTimeListDialog.TAG);
        });

        final LinearLayoutCompat mnu_reminder_name = findViewById(R.id.mnu_reminder_name);
        mnu_reminder_name.setOnClickListener(v -> {
            NameDialog nameInput = new NameDialog();
            nameInput.show(getSupportFragmentManager(), NameDialog.TAG);
        });

        final LinearLayoutCompat mnu_reminder_note = findViewById(R.id.mnu_reminder_note);
        mnu_reminder_note.setOnClickListener(v -> {
            NoteDialog noteInput = new NoteDialog();
            noteInput.show(getSupportFragmentManager(), NoteDialog.TAG);
        });

        final LinearLayoutCompat mnu_reminder_repeat = findViewById(R.id.mnu_reminder_repeat);
        mnu_reminder_repeat.setOnClickListener(v -> {
            RepeatDialog repeatInput = new RepeatDialog();
            repeatInput.show(getSupportFragmentManager(), RepeatDialog.TAG);
        });

        final LinearLayoutCompat mnu_reminder_snooze = findViewById(R.id.mnu_reminder_snooze);
        mnu_reminder_snooze.setOnClickListener(v -> {
            SnoozeDialog snoozeInput = new SnoozeDialog();
            snoozeInput.show(getSupportFragmentManager(), SnoozeDialog.TAG);
        });

        final LinearLayoutCompat mnu_reminder_tone = findViewById(R.id.mnu_reminder_tone);
        mnu_reminder_tone.setOnClickListener(v -> {

            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone:");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, reminderModel.getRingingModel().getRingToneUri());
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent, RINGTONE_DIALOG_REQ_CODE);
        });

        sw_reminder_tone = findViewById(R.id.sw_reminder_tone);
        sw_reminder_tone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUserInteracted())
                return;
            reminderModel.getRingingModel().setToneEnabled(isChecked);
            refresh();
        });

        sw_reminder_vibrate = findViewById(R.id.sw_reminder_vibrate);
        sw_reminder_vibrate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUserInteracted())
                return;
            reminderModel.getRingingModel().setVibrationEnabled(isChecked);
            refresh();
        });

//        final AppCompatButton btn_reminder_save = findViewById(R.id.btn_reminder_save);
//        btn_reminder_save.setOnClickListener(view -> {
//            if (reminderModel.getTimeModel().getAlertTime(true).after(Calendar.getInstance().getTime())) {
//                // the method "reminderModel.isHasDifferentTimeCalculated()" will ensure that time has not been changed than what was given.
//                // And changes were made on other areas.
//                // Otherwise it needs to clear snooze details.
//                reminderModel.saveAndSetAlert(ReminderInput.this, true);
//                finish();
//            } else {
//                ToastHelper.showShort(ReminderInput.this, "Cannot save reminder in past");
//            }
//        });

        final FloatingActionButton imgBtnSetReminder = findViewById(R.id.imgBtnSetReminder);
        imgBtnSetReminder.setOnClickListener(view -> {
            if (reminderModel.getTimeModel().getAlertTime(true).after(Calendar.getInstance().getTime())) {
                // the method "reminderModel.isHasDifferentTimeCalculated()" will ensure that time has not been changed than what was given.
                // And changes were made on other areas.
                // Otherwise it needs to clear snooze details.
                reminderModel.saveAndSetAlert(ReminderInput.this, true);
                finish();
            } else {
                ToastHelper.showShort(ReminderInput.this, "Cannot save reminder in past");
            }
        });


        final AppCompatImageView img_reminder_name_voice_input = findViewById(R.id.img_reminder_name_voice_input);
        img_reminder_name_voice_input.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // This starts the activity and populates the intent with the speech text.
            startActivityForResult(intent, NAME_SPEECH_REQUEST_CODE);
        });

        final AppCompatImageView img_reminder_note_voice_input = findViewById(R.id.img_reminder_note_voice_input);
        img_reminder_note_voice_input.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // This starts the activity and populates the intent with the speech text.
            startActivityForResult(intent, NOTE_SPEECH_REQUEST_CODE);
        });

        sw_gradually_increase_volume = findViewById(R.id.sw_gradually_increase_volume);
        sw_gradually_increase_volume.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                reminderModel.getRingingModel().setIncreaseVolumeGradually(sw_gradually_increase_volume.isChecked());
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
            reminderModel.getRingingModel().setDefaultRingTone();
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
                reminderModel.getRingingModel().setAlarmVolumePercentage(seekBar.getProgress());
                ToastHelper.showShort(ReminderInput.this, "Alarm will ring at " + reminderModel.getRingingModel().getAlarmVolumePercentage() + "% volume");
            }
        });


        lv_reminder_extra_inputs = findViewById(R.id.lv_reminder_extra_inputs);
        btn_reminder_extra_inputs = findViewById(R.id.btn_reminder_extra_inputs);
        btn_reminder_extra_inputs.setOnClickListener(v -> {

            isExtraInputsVisible = !isExtraInputsVisible;
            setUserInteracted(false); // This is very important. Because its just making a layout visible and is no real interaction.
            setExtraInputs();
            sv_container.post(() -> sv_container.fullScroll(ScrollView.FOCUS_DOWN));

        });

        sv_container = findViewById(R.id.sv_container);

        ring_duration_spinner = findViewById(R.id.ring_duration_spinner);
        ArrayAdapter<CharSequence> ring_duration_adapter = ArrayAdapter.createFromResource(this, R.array.ring_durations, R.layout.item_dropdown_fragment_simple_spinner);
        // adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        ring_duration_spinner.setAdapter(ring_duration_adapter);
        ring_duration_spinner.setOnItemSelectedListener(this);

        vibrate_pattern_spinner = findViewById(R.id.vibrate_pattern_spinner);
        ArrayAdapter<CharSequence> vibrate_pattern_adapter = ArrayAdapter.createFromResource(this, R.array.vibration_patterns, R.layout.item_dropdown_fragment_simple_spinner);
        // adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        vibrate_pattern_spinner.setAdapter(vibrate_pattern_adapter);
        vibrate_pattern_spinner.setOnItemSelectedListener(this);

        refresh();

    }

    @Override
    protected void onUIRefresh() {
        super.onUIRefresh();

        btn_reminder_time.setText(StringHelper.toTime(reminderModel.getTimeModel().getTime()));
        btn_reminder_date.setText(StringHelper.toWeekdayDate(this, reminderModel.getTimeModel().getTime()));

        final Resources.Theme theme = getTheme();
        final TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.themeSoothingText, typedValue, true);
        final int soothingTextColor = typedValue.resourceId;

        btnTimeHours.setTextColor(getResources().getColor(soothingTextColor));
        btnTimeList.setTextColor(getResources().getColor(soothingTextColor));

        if (reminderModel.getTimeModel().getTimeListMode() == TimeModel.TimeListModes.HOURLY) {
            btnTimeHours.setTextColor(getResources().getColor(R.color.colorSuccess));
        } else if (reminderModel.getTimeModel().getTimeListMode() == TimeModel.TimeListModes.CUSTOM) {
            btnTimeList.setTextColor(getResources().getColor(R.color.colorSuccess));
        }

        if (reminderModel.getTimeModel().isHasScheduledTime()) {
            lvc_diff_next_reminder_trigger.setVisibility(View.VISIBLE);
            tv_reminder_trigger_time.setText(StringHelper.toTime(reminderModel.getTimeModel().getScheduledTime()));
            tv_reminder_trigger_date.setText(StringHelper.toWeekdayDate(this, reminderModel.getTimeModel().getScheduledTime()));
        } else {
            lvc_diff_next_reminder_trigger.setVisibility(View.GONE);
        }

        tv_reminder_name_summary.setText(reminderModel.getName());
        tv_reminder_note_summary.setText(reminderModel.getNote());

        sw_notification.setChecked(reminderModel.isNotification());

        tv_reminder_repeat_summary.setText(reminderModel.getRepeatModel().toString(this));
        tv_reminder_snooze_summary.setText(reminderModel.getSnoozeModel().toString());

        sw_reminder_snooze.setChecked(reminderModel.getSnoozeModel().isEnable());
        sw_reminder_repeat.setChecked(reminderModel.getRepeatModel().isEnabled());
        sw_reminder_tone.setChecked(reminderModel.getRingingModel().isToneEnabled());

        if (reminderModel.getRingingModel().getRingToneUri() == null) {
            final Uri alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            final Ringtone alarmTone = RingtoneManager.getRingtone(this, alarmToneUri);
            tv_reminder_tone_summary.setText(alarmTone.getTitle(this));
            btnSetDefaultTone.setVisibility(View.INVISIBLE);
        } else {
            final Ringtone ringtone = RingtoneManager.getRingtone(this, reminderModel.getRingingModel().getRingToneUri());
            tv_reminder_tone_summary.setText(ringtone.getTitle(this));
            btnSetDefaultTone.setVisibility(View.VISIBLE);
        }

        if (reminderModel.getRingingModel().isToneEnabled()) {
            sw_gradually_increase_volume.setEnabled(true);
            sw_gradually_increase_volume.setChecked(reminderModel.getRingingModel().isIncreaseVolumeGradually());

            imgBtnPlayStop.setEnabled(true);
            btnSetDefaultTone.setEnabled(true);

            seeker_alarm_volume.setEnabled(true);
            tv_reminder_tone_summary.setEnabled(true);

            ring_duration_spinner.setEnabled(true);
            ring_duration_spinner.setSelection(RingingModel.convertToAlarmRingDuration(reminderModel.getRingingModel().getAlarmRingDuration()));

        } else {
            sw_gradually_increase_volume.setEnabled(false);
            sw_gradually_increase_volume.setChecked(false);

            imgBtnPlayStop.setEnabled(false);
            btnSetDefaultTone.setEnabled(false);

            seeker_alarm_volume.setEnabled(false);
            tv_reminder_tone_summary.setEnabled(false);
            ring_duration_spinner.setEnabled(false);
        }

        if (reminderModel.getRingingModel().getAlarmVolumePercentage() == 0) {
            seeker_alarm_volume.setProgress(deviceAlarmVolume);
        } else {
            seeker_alarm_volume.setProgress(reminderModel.getRingingModel().getAlarmVolumePercentage());
        }

        if (isPlayingTone) {
            imgBtnPlayStop.setImageResource(R.drawable.ic_play_stop);
        } else {
            imgBtnPlayStop.setImageResource(R.drawable.ic_play);
        }

        tv_reminder_tone_summary.setText(reminderModel.getRingingModel().getRingToneUriSummary(this));
        sw_reminder_vibrate.setChecked(reminderModel.getRingingModel().isVibrationEnabled());
        vibrate_pattern_spinner.setSelection(RingingModel.convertToVibratePattern(reminderModel.getRingingModel().getVibratePattern()));
        vibrate_pattern_spinner.setEnabled(reminderModel.getRingingModel().isVibrationEnabled());

        setExtraInputs();
    }

    private void setExtraInputs() {

        if (isExtraInputsVisible) {

            btn_reminder_extra_inputs.setImageResource(R.drawable.ic_expand_up);
            btn_reminder_extra_inputs.setColorFilter(getResources().getColor(R.color.colorDangerLight), android.graphics.PorterDuff.Mode.SRC_IN);

            lv_reminder_extra_inputs.setVisibility(View.VISIBLE);

        } else {

            btn_reminder_extra_inputs.setImageResource(R.drawable.ic_expand_down);
            btn_reminder_extra_inputs.setColorFilter(getResources().getColor(R.color.colorSuccessLight), android.graphics.PorterDuff.Mode.SRC_IN);
            lv_reminder_extra_inputs.setVisibility(View.GONE);

        }
    }

    @Override
    public TimeModel getTimeListDialogModel() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return new ViewModelProvider(this, new TimeViewModelFactory(reminderModel)).get(TimeModel.class);
    }

    @Override
    public void setTimeListDialogModel(TimeModel model) {

        if (reminderModel.getRepeatModel().isValid(model)) {
            reminderModel.setTimeModel(model);
            reminderModel.getTimeModel().setScheduledTime(reminderModel.getRepeatModel().getValidatedScheduledTime());
        } else {
            ToastHelper.showShort(this, "Please check repeat settings");
        }

        refresh();
    }

    @Override
    public Date getDateCalculatorDialogModel() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getTimeModel().getTime();
    }

    @Override
    public void setDateCalculatorDialogModel(Date newTime) {
        if (newTime == null) return;
        reminderModel.getTimeModel().setTime(newTime);
        refresh();
    }

    @Override
    public String getNameInputDialogModel() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getName();
    }

    @Override
    public void setNameInputDialogModel(String name) {
        reminderModel.setName(name);
        refresh();
    }

    @Override
    public String getNoteDialogModel() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getNote();
    }

    @Override
    public void setNoteDialogModel(String note) {
        reminderModel.setNote(note);
        refresh();
    }

    @Override
    public RepeatModel getRepeatDialogModel() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getRepeatModel();
    }

    @Override
    public void setRepeatDialogModel(RepeatModel model) {

        if (model.isValid(reminderModel.getTimeModel())) {
            reminderModel.setRepeatModel(model);
            reminderModel.getTimeModel().setScheduledTime(model.getValidatedScheduledTime());
        } else {
            ToastHelper.showShort(this, "Please check repeat settings");
        }

        refresh();
    }

    @Override
    public SnoozeModel getSnoozeDialogModel() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getSnoozeModel();
    }

    @Override
    public void setSnoozeDialogModel(SnoozeModel model) {
        reminderModel.setSnoozeModel(model);
        refresh();
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
            reminderModel.getRingingModel().setAlarmRingDuration(RingingModel.convertToAlarmRingDuration(position));
        } else if (parent.getId() == R.id.vibrate_pattern_spinner) {
            reminderModel.getRingingModel().setVibratePattern(RingingModel.convertToVibratePattern(position));
            startVibrating();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private RingingController getRingingController() {
        if (ringingController == null || !isPlayingTone) { // "!isPlayingTone" this condition will ensure new instance of RingingController with updated ReminderModel tone URI.
            ringingController = new RingingController(ReminderInput.this, reminderModel.getRingingModel().getRingToneUri());
        }
        return ringingController;
    }

    private void startVibrating() {
        getRingingController().vibrateOnce(RingingModel.convertToVibrateFrequency(reminderModel.getRingingModel().getVibratePattern()));
    }

    private void startTone() {
        getRingingController().startTone(reminderModel.getRingingModel().isIncreaseVolumeGradually(), reminderModel.getRingingModel().getAlarmVolumePercentage());
        isPlayingTone = true;
    }

    private void stopTone() {
        if (ringingController != null) {
            ringingController.stopRinging(); // Stop ring stops both tone and vibration if is playing.
        }
        isPlayingTone = false;
    }

}