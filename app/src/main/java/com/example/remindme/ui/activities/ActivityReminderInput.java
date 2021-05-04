package com.example.remindme.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.controllers.RingingController;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.CustomTimeListDialog;
import com.example.remindme.ui.fragments.dialogFragments.DateCalculatorDialog;
import com.example.remindme.ui.fragments.dialogFragments.HourlyTimeListDialog;
import com.example.remindme.ui.fragments.dialogFragments.NameDialog;
import com.example.remindme.ui.fragments.dialogFragments.NoteDialog;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityReminderInput
        extends
        BaseActivity
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

    private TextView tv_reminder_trigger_time;
    private TextView tv_reminder_trigger_date;
    private Button btn_reminder_time;
    private Button btn_reminder_date;
    private Button btnTimeHours;
    private Button btnTimeList;

    private TextView tv_reminder_tone_summary;
    private TextView tv_reminder_name_summary;
    private TextView tv_reminder_note_summary;
    private SwitchCompat sw_notification;
    private SwitchCompat sw_reminder_repeat;
    private SwitchCompat sw_reminder_snooze;
    private TextView tv_reminder_repeat_summary;
    private TextView tv_reminder_snooze_summary;
    private SeekBar seeker_alarm_volume;
    private SwitchCompat sw_gradually_increase_volume;
    private LinearLayout lvc_diff_next_reminder_trigger;

    private SwitchCompat sw_reminder_tone;
    private SwitchCompat sw_reminder_vibrate;
    private LinearLayout lv_reminder_extra_inputs;
    private ImageView btn_reminder_extra_inputs;
    private ScrollView sv_container;
    private Spinner ring_duration_spinner;
    private Spinner vibrate_pattern_spinner;

    private ImageButton imgBtnPlayStop;
    private Button btnSetDefaultTone;
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
        setContentView(R.layout.activity_reminder_input);

        setUserInteracted(false);

        if (savedInstanceState != null) {
            isExtraInputsVisible = savedInstanceState.getBoolean(MORE_INPUT_UI_STATE, false);
        }

        reminderModel = new ViewModelProvider(this, new ReminderViewModelFactory(getIntent())).get(ReminderModel.class);

        if (reminderModel.isNew()) { // First time creating the activity
            ActivityHelper.setTitle(this, getResources().getString(R.string.new_reminder_heading));
        } else {
            ActivityHelper.setTitle(this, getResources().getString(R.string.edit_reminder_heading));
        }

        tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
        tv_reminder_name_summary = findViewById(R.id.tv_reminder_name_summary);
        tv_reminder_note_summary = findViewById(R.id.tv_reminder_note_summary);
        tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);
        sw_notification = findViewById(R.id.sw_notification);
        sw_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isUserInteracted()) {
                    reminderModel.setNotification(isChecked);
                    refresh();
                }
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

        sw_reminder_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isUserInteracted()) {
                    reminderModel.getRepeatModel().setEnabled(isChecked);
                    refresh();
                }
            }
        });


        sw_reminder_snooze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isUserInteracted()) {
                    reminderModel.getSnoozeModel().setEnable(sw_reminder_snooze.isChecked());
                    refresh();
                }
            }
        });

        btn_reminder_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar alertTime = Calendar.getInstance();
                //final Calendar currentTime = Calendar.getInstance();
                alertTime.setTime(reminderModel.getTimeModel().getTime());
                final int mYear, mMonth, mDay;
                mYear = alertTime.get(Calendar.YEAR);
                mMonth = alertTime.get(Calendar.MONTH);
                mDay = alertTime.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityReminderInput.this,
                        AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT ? R.style.DatePickerDialogLight : R.style.DatePickerDialogBlack,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                alertTime.set(Calendar.YEAR, year);
                                alertTime.set(Calendar.MONTH, monthOfYear);
                                alertTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                alertTime.set(Calendar.SECOND, 0);
                                alertTime.set(Calendar.MILLISECOND, 0);
                                reminderModel.getTimeModel().setTime(alertTime.getTime());
                                refresh();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()); // This will cause extra title on the top of the regular date picker
                datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // This line will try to solve the issue above
                datePickerDialog.setTitle(null); // This line will try to solve the issue above
                datePickerDialog.show();

                if (OsHelper.isLollipopOrLater()) {
                    datePickerDialog.getDatePicker().setFirstDayOfWeek(AppSettingsHelper.getInstance().getFirstDayOfWeek());
                }
            }
        });

        btn_reminder_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar alertTime = Calendar.getInstance();
                //final Calendar currentTime = Calendar.getInstance();
                alertTime.setTime(reminderModel.getTimeModel().getTime());
                final int mHour, mMinute;
                mHour = alertTime.get(Calendar.HOUR_OF_DAY);
                mMinute = alertTime.get(Calendar.MINUTE);

                final TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityReminderInput.this,
                        AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT ? R.style.TimePickerDialogLight : R.style.TimePickerDialogBlack,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                alertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                alertTime.set(Calendar.MINUTE, minute);
                                alertTime.set(Calendar.SECOND, 0);
                                alertTime.set(Calendar.MILLISECOND, 0);
                                reminderModel.getTimeModel().setTime(alertTime.getTime());
                                refresh();
                            }
                        }, mHour, mMinute, AppSettingsHelper.getInstance().isUse24hourTime());
                timePickerDialog.show();
                //timePickerDialog.;
            }
        });

        final Button btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateCalculatorDialog calculatorDialog = new DateCalculatorDialog();
                calculatorDialog.show(getSupportFragmentManager(), DateCalculatorDialog.TAG);
            }
        });

        btnTimeHours = findViewById(R.id.btnTimeHours);
        btnTimeHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reminderModel.getRepeatModel().getRepeatOption() == RepeatModel.ReminderRepeatOptions.HOURLY) {
                    ToastHelper.showShort(ActivityReminderInput.this, "Time lists aren't possible if repeat is set to hourly. Please change repeat option");
                    return;
                }

                HourlyTimeListDialog hourlyTimeListDialog = new HourlyTimeListDialog();
                hourlyTimeListDialog.show(getSupportFragmentManager(), HourlyTimeListDialog.TAG);
            }
        });

        btnTimeList = findViewById(R.id.btnTimeList);
        btnTimeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reminderModel.getRepeatModel().getRepeatOption() == RepeatModel.ReminderRepeatOptions.HOURLY) {
                    ToastHelper.showShort(ActivityReminderInput.this, "Time lists aren't possible if repeat is set to hourly. Please change repeat option");
                    return;
                }

                CustomTimeListDialog timeListInputHourlyDialog = new CustomTimeListDialog();
                timeListInputHourlyDialog.show(getSupportFragmentManager(), CustomTimeListDialog.TAG);
            }
        });

        final LinearLayout mnu_reminder_name = findViewById(R.id.mnu_reminder_name);
        mnu_reminder_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameDialog nameInput = new NameDialog();
                nameInput.show(getSupportFragmentManager(), NameDialog.TAG);
            }
        });

        final LinearLayout mnu_reminder_note = findViewById(R.id.mnu_reminder_note);
        mnu_reminder_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteDialog noteInput = new NoteDialog();
                noteInput.show(getSupportFragmentManager(), NoteDialog.TAG);
            }
        });

        final LinearLayout mnu_reminder_repeat = findViewById(R.id.mnu_reminder_repeat);
        mnu_reminder_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepeatDialog repeatInput = new RepeatDialog();
                repeatInput.show(getSupportFragmentManager(), RepeatDialog.TAG);
            }
        });

        final LinearLayout mnu_reminder_snooze = findViewById(R.id.mnu_reminder_snooze);
        mnu_reminder_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnoozeDialog snoozeInput = new SnoozeDialog();
                snoozeInput.show(getSupportFragmentManager(), SnoozeDialog.TAG);
            }
        });

        final LinearLayout mnu_reminder_tone = findViewById(R.id.mnu_reminder_tone);
        mnu_reminder_tone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone:");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, reminderModel.getRingingModel().getRingToneUri());
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, RINGTONE_DIALOG_REQ_CODE);
            }
        });

        sw_reminder_tone = findViewById(R.id.sw_reminder_tone);
        sw_reminder_tone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isUserInteracted()) return;
                reminderModel.getRingingModel().setToneEnabled(isChecked);
                refresh();
            }
        });

        sw_reminder_vibrate = findViewById(R.id.sw_reminder_vibrate);
        sw_reminder_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isUserInteracted()) return;
                reminderModel.getRingingModel().setVibrationEnabled(isChecked);
                refresh();
            }
        });

        final Button btn_reminder_save = findViewById(R.id.btn_reminder_save);
        btn_reminder_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reminderModel.getTimeModel().getAlertTime(true).after(Calendar.getInstance().getTime())) {
                    // the method "reminderModel.isHasDifferentTimeCalculated()" will ensure that time has not been changed than what was given.
                    // And changes were made on other areas.
                    // Otherwise it needs to clear snooze details.
                    reminderModel.saveAndSetAlert(ActivityReminderInput.this, true);
                    finish();
                } else {
                    ToastHelper.showShort(ActivityReminderInput.this, "Cannot save reminder in past");
                }
            }
        });

        final ImageView img_reminder_name_voice_input = findViewById(R.id.img_reminder_name_voice_input);
        img_reminder_name_voice_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                // This starts the activity and populates the intent with the speech text.
                startActivityForResult(intent, NAME_SPEECH_REQUEST_CODE);
            }
        });

        final ImageView img_reminder_note_voice_input = findViewById(R.id.img_reminder_note_voice_input);
        img_reminder_note_voice_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                // This starts the activity and populates the intent with the speech text.
                startActivityForResult(intent, NOTE_SPEECH_REQUEST_CODE);
            }
        });

        sw_gradually_increase_volume = findViewById(R.id.sw_gradually_increase_volume);
        sw_gradually_increase_volume.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isUserInteracted()) {
                    reminderModel.getRingingModel().setIncreaseVolumeGradually(sw_gradually_increase_volume.isChecked());
                    refresh();
                }
            }
        });

        imgBtnPlayStop = findViewById(R.id.imgBtnPlayStop);
        imgBtnPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayingTone) {
                    stopTone();
                } else {
                    startTone();
                }
                refresh();
            }
        });

        btnSetDefaultTone = findViewById(R.id.btnSetDefaultTone);
        btnSetDefaultTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderModel.getRingingModel().setDefaultRingTone();
                refresh();
            }
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
                ToastHelper.showShort(ActivityReminderInput.this, "Alarm will ring at " + reminderModel.getRingingModel().getAlarmVolumePercentage() + "% volume");
            }
        });

        lv_reminder_extra_inputs = findViewById(R.id.lv_reminder_extra_inputs);
        btn_reminder_extra_inputs = findViewById(R.id.btn_reminder_extra_inputs);
        btn_reminder_extra_inputs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isExtraInputsVisible = !isExtraInputsVisible;
                setUserInteracted(false); // This is very important. Because its just making a layout visible and is no real interaction.
                setExtraInputs();
                sv_container.post(new Runnable() {
                    @Override
                    public void run() {
                        sv_container.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }
        });

        sv_container = findViewById(R.id.sv_container);

        ring_duration_spinner = findViewById(R.id.ring_duration_spinner);
        ArrayAdapter<CharSequence> ring_duration_adapter = ArrayAdapter.createFromResource(this, R.array.ring_durations, R.layout.simple_spinner_dropdown_item);
        // adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        ring_duration_spinner.setAdapter(ring_duration_adapter);
        ring_duration_spinner.setOnItemSelectedListener(this);

        vibrate_pattern_spinner = findViewById(R.id.vibrate_pattern_spinner);
        ArrayAdapter<CharSequence> vibrate_pattern_adapter = ArrayAdapter.createFromResource(this, R.array.vibration_patterns, R.layout.simple_spinner_dropdown_item);
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

        btnTimeHours.setBackgroundColor(getResources().getColor(R.color.bg_warning));
        btnTimeList.setBackgroundColor(getResources().getColor(R.color.bg_warning));

        if (reminderModel.getTimeModel().getTimeListMode() == TimeModel.TimeListModes.HOURLY) {
            btnTimeHours.setBackgroundColor(getResources().getColor(R.color.bg_danger));
        } else if (reminderModel.getTimeModel().getTimeListMode() == TimeModel.TimeListModes.CUSTOM) {
            btnTimeList.setBackgroundColor(getResources().getColor(R.color.bg_danger));
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
            imgBtnPlayStop.setImageResource(R.drawable.ic_stop);
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
            btn_reminder_extra_inputs.setColorFilter(getResources().getColor(R.color.bg_danger), android.graphics.PorterDuff.Mode.SRC_IN);

            lv_reminder_extra_inputs.setVisibility(View.VISIBLE);

        } else {

            btn_reminder_extra_inputs.setImageResource(R.drawable.ic_expand_down);
            btn_reminder_extra_inputs.setColorFilter(getResources().getColor(R.color.bg_success), android.graphics.PorterDuff.Mode.SRC_IN);
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
        reminderModel.setTimeModel(model);
        refresh();
    }

    @Override
    public Date getDateCalculatorDialogModel() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getTimeModel().getAlertTime(false);
    }

    @Override
    public void setDateCalculatorDialogModel(Date newTime) {
        if (newTime == null) return;

        final Calendar currentTime = Calendar.getInstance();
        if (currentTime.getTime().compareTo(newTime) < 0) {
            reminderModel.getTimeModel().setTime(newTime);
            refresh();
        } else {
            ToastHelper.showShort(this, "Cannot save reminder in past");
        }
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

        if (model.isValid()) {
            reminderModel.setRepeatModel(model);
            reminderModel.getTimeModel().setScheduledTime(model.getValidatedNextScheduledTime());
            refresh();
        } else {
            ToastHelper.showShort(this, "Please check repeat settings");
        }

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
            ringingController = new RingingController(ActivityReminderInput.this, reminderModel.getRingingModel().getRingToneUri());
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