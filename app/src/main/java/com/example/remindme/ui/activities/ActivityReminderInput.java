package com.example.remindme.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.DialogReminderNameInput;
import com.example.remindme.ui.fragments.dialogFragments.DialogReminderNoteInput;
import com.example.remindme.ui.fragments.dialogFragments.DialogReminderRepeatInput;
import com.example.remindme.ui.fragments.dialogFragments.DialogReminderSnoozeInput;
import com.example.remindme.viewModels.ReminderModel;
import com.example.remindme.viewModels.ReminderRepeatModel;
import com.example.remindme.viewModels.ReminderSnoozeModel;

import java.util.Calendar;
import java.util.List;

public class ActivityReminderInput
        extends
        AppCompatActivity
        implements
        DialogReminderNameInput.INameInputDialogListener,
        DialogReminderNoteInput.INoteInputDialogListener,
        DialogReminderRepeatInput.IRepeatInputDialogListener,
        DialogReminderSnoozeInput.ISnoozeInputDialogListener {

    private static final String INTENT_FROM = "FROM";
    private static final String FLAG_REMINDER_TYPE_ACTIVE = "ACTIVE";
    private static final int NAME_SPEECH_REQUEST_CODE = 119;
    private static final int NOTE_SPEECH_REQUEST_CODE = 113;
    private static final int RINGTONE_DIALOG_REQ_CODE = 117;

    private ReminderModel reminderModel = null;

    private TextView tv_reminder_trigger_datetime;
    private Button btn_reminder_time;
    private Button btn_reminder_date;
    private TextView tv_reminder_tone_summary;
    private TextView tv_reminder_name_summary;
    private TextView tv_reminder_note_summary;
    private SwitchCompat sw_reminder_repeat;
    private SwitchCompat sw_reminder_snooze;
    private TextView tv_reminder_repeat_summary;
    private TextView tv_reminder_snooze_summary;
    private SeekBar seeker_alarm_volume;
    private SwitchCompat sw_gradually_increase_volume;
    private LinearLayout lvc_diff_next_reminder_trigger;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RINGTONE_DIALOG_REQ_CODE && data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            TextView tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
            if (uri != null) {
                reminderModel.ringToneUri = uri;
                Ringtone ringtone = RingtoneManager.getRingtone(this, reminderModel.ringToneUri);
                tv_reminder_tone_summary.setText(ringtone.getTitle(this));
            } else {
                Uri alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                Ringtone alarmTone = RingtoneManager.getRingtone(getApplicationContext(), alarmToneUri);
                tv_reminder_tone_summary.setText(alarmTone.getTitle(this));
            }
        } else if (requestCode == NAME_SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            tv_reminder_name_summary.setText(spokenText);
            reminderModel.name = spokenText;
        } else if (requestCode == NOTE_SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            tv_reminder_note_summary.setText(spokenText);
            reminderModel.note = spokenText;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_input);

        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);

        if (reminderModel.isNew()) { // First time creating the activity
            //Check intents
            final String reminderType = getIntent().getStringExtra(INTENT_FROM);
            // Open if intent is update
            if (FLAG_REMINDER_TYPE_ACTIVE.equals(reminderType)) {
                if (reminderModel.tryReadFrom(getIntent())) {
                    // Everything looks good so far. Set the title as Update
                    ActivityHelper.setTitle(this, getResources().getString(R.string.edit_reminder_heading));
                } else {
                    // Close the activity if intent was update bu no existing reminder found!
                    ToastHelper.error(this, "Reminder not found!");
                    finish();
                }
            } else { // Advance the time 1 hour if intent is new
                ActivityHelper.setTitle(this, getResources().getString(R.string.new_reminder_heading));
            }
        }

        tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
        tv_reminder_name_summary = findViewById(R.id.tv_reminder_name_summary);
        tv_reminder_note_summary = findViewById(R.id.tv_reminder_note_summary);
        tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);
        sw_reminder_repeat = findViewById(R.id.sw_reminder_repeat);
        tv_reminder_snooze_summary = findViewById(R.id.tv_reminder_snooze_summary);
        sw_reminder_snooze = findViewById(R.id.sw_reminder_snooze);
        tv_reminder_trigger_datetime = findViewById(R.id.tv_reminder_trigger_datetime);
        btn_reminder_date = findViewById(R.id.btn_reminder_date);
        btn_reminder_time = findViewById(R.id.btn_reminder_time);
        lvc_diff_next_reminder_trigger = findViewById(R.id.lvc_diff_next_reminder_trigger);

        sw_reminder_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderRepeatModel repeatModel = reminderModel.getRepeatSettings();
                if (sw_reminder_repeat.isChecked()) {
                    repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.DAILY); // Default
                } else {
                    repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.NONE);
                }

                if (reminderModel.trySetRepeatSettingChanges()) {
                    refreshForm();
                }
            }
        });

        sw_reminder_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderModel.getSnoozeModel().isEnable = sw_reminder_snooze.isChecked();
                refreshForm();
            }
        });

        btn_reminder_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar alertTime = Calendar.getInstance();
                //final Calendar currentTime = Calendar.getInstance();
                alertTime.setTime(reminderModel.getOriginalTime());
                final int mYear, mMonth, mDay;
                mYear = alertTime.get(Calendar.YEAR);
                mMonth = alertTime.get(Calendar.MONTH);
                mDay = alertTime.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityReminderInput.this, R.style.DatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                alertTime.set(Calendar.YEAR, year);
                                alertTime.set(Calendar.MONTH, monthOfYear);
                                alertTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                reminderModel.setOriginalTime(alertTime.getTime());
                                refreshForm();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()); // This will cause extra title on the top of the regular date picker
                datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // This line will try to solve the issue above
                datePickerDialog.setTitle(null); // This line will try to solve the issue above
                datePickerDialog.show();
            }
        });

        btn_reminder_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar alertTime = Calendar.getInstance();
                //final Calendar currentTime = Calendar.getInstance();
                alertTime.setTime(reminderModel.getOriginalTime());
                final int mHour, mMinute;
                mHour = alertTime.get(Calendar.HOUR_OF_DAY);
                mMinute = alertTime.get(Calendar.MINUTE);
                final TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityReminderInput.this, R.style.TimePickerDialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                alertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                alertTime.set(Calendar.MINUTE, minute);
                                alertTime.set(Calendar.SECOND, 0); // Setting second to 0 is important.
                                reminderModel.setOriginalTime(alertTime.getTime());
                                refreshForm();
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        final LinearLayout mnu_reminder_name = findViewById(R.id.mnu_reminder_name);
        mnu_reminder_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogReminderNameInput nameInput = new DialogReminderNameInput();
                nameInput.show(getSupportFragmentManager(), "Reminder_Input_Name");
            }
        });

        final LinearLayout mnu_reminder_note = findViewById(R.id.mnu_reminder_note);
        mnu_reminder_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogReminderNoteInput noteInput = new DialogReminderNoteInput();
                noteInput.show(getSupportFragmentManager(), "Reminder_Input_Note");
            }
        });

        final LinearLayout mnu_reminder_repeat = findViewById(R.id.mnu_reminder_repeat);
        mnu_reminder_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogReminderRepeatInput repeatInput = new DialogReminderRepeatInput();
                repeatInput.show(getSupportFragmentManager(), "repeatInput");
            }
        });

        final LinearLayout mnu_reminder_snooze = findViewById(R.id.mnu_reminder_snooze);
        mnu_reminder_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogReminderSnoozeInput snoozeInput = new DialogReminderSnoozeInput();
                snoozeInput.show(getSupportFragmentManager(), "Reminder_Snooze");
            }
        });

        final LinearLayout mnu_reminder_tone = findViewById(R.id.mnu_reminder_tone);
        mnu_reminder_tone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderModel.ringToneUri == null) {
                    reminderModel.ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                }
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone:");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, reminderModel.ringToneUri);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, RINGTONE_DIALOG_REQ_CODE);
            }
        });

        final SwitchCompat sw_reminder_tone = findViewById(R.id.sw_reminder_tone);
        sw_reminder_tone.setChecked(reminderModel.isEnableTone);
        sw_reminder_tone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderModel.isEnableTone = sw_reminder_tone.isChecked();
            }
        });

        final SwitchCompat sw_reminder_vibrate = findViewById(R.id.sw_reminder_vibrate);
        sw_reminder_vibrate.setChecked(reminderModel.isEnableVibration);
        sw_reminder_vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderModel.isEnableVibration = sw_reminder_vibrate.isChecked();
            }
        });

        final SwitchCompat sw_reminder_disable = findViewById(R.id.sw_reminder_disable);
        sw_reminder_disable.setChecked(!reminderModel.getIsEnabled());
        sw_reminder_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderModel.trySetEnabled(getApplicationContext(), !sw_reminder_disable.isChecked());
                sw_reminder_disable.setChecked(!reminderModel.getIsEnabled());
            }
        });

        final Button btn_reminder_save = findViewById(R.id.btn_reminder_save);
        btn_reminder_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reminderModel.trySaveAndSetAlert(getApplicationContext(), reminderModel.getIsHasDifferentTimeCalculated(), true)) {
                    finish();
                } else {
                    ToastHelper.toast(ActivityReminderInput.this, "Time cannot be set in past!");
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
        sw_gradually_increase_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderModel.setIncreaseVolumeGradually(sw_gradually_increase_volume.isChecked());
            }
        });

        final AudioManager audioManager = OsHelper.getAudioManager(ActivityReminderInput.this);
        seeker_alarm_volume = findViewById(R.id.seeker_alarm_volume);
        seeker_alarm_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress < ReminderModel.MINIMUM_INPUT_VOLUME_PERCENTAGE)
                        seekBar.setProgress(ReminderModel.MINIMUM_INPUT_VOLUME_PERCENTAGE);
                    OsHelper.setAlarmVolume(audioManager, seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                reminderModel.setAlarmVolumePercentage(seekBar.getProgress());
            }
        });

        refreshForm();
    }

    private void createInputFragments() {
        //lv_reminder_inputs_container
    }

    private void refreshForm() {
        if (reminderModel.getOriginalTime() == null) {
            // User has not entered any value yet! Set a default time to start.
            Calendar _c = Calendar.getInstance();
            _c.add(Calendar.HOUR_OF_DAY, 1);
            reminderModel.setOriginalTime(_c.getTime());
        }

        btn_reminder_time.setText(StringHelper.toTime(reminderModel.getOriginalTime()));
        btn_reminder_date.setText(StringHelper.toWeekdayDate(reminderModel.getOriginalTime()));

        if (reminderModel.getIsHasDifferentTimeCalculated()) {
            lvc_diff_next_reminder_trigger.setVisibility(View.VISIBLE);
            tv_reminder_trigger_datetime.setText(StringHelper.toTimeDate(reminderModel.getCalculatedTime()));
        } else {
            lvc_diff_next_reminder_trigger.setVisibility(View.GONE);
            tv_reminder_trigger_datetime.setText("");
        }

        tv_reminder_name_summary.setText(reminderModel.name);
        tv_reminder_note_summary.setText(reminderModel.note);
        tv_reminder_repeat_summary.setText(reminderModel.getRepeatSettingString());
        tv_reminder_snooze_summary.setText(reminderModel.getSnoozeModel().toString());

        sw_reminder_snooze.setChecked(reminderModel.getSnoozeModel().isEnable);
        sw_reminder_repeat.setChecked(reminderModel.getRepeatOption() != ReminderRepeatModel.ReminderRepeatOptions.NONE);
        sw_gradually_increase_volume.setChecked(reminderModel.isIncreaseVolumeGradually());

        if (reminderModel.getAlarmVolumePercentage() == 0) {
            int x = OsHelper.getAlarmVolumeInPercentage(OsHelper.getAudioManager(this));
            seeker_alarm_volume.setProgress(x);
        } else {
            seeker_alarm_volume.setProgress(reminderModel.getAlarmVolumePercentage());
        }

        if (reminderModel.ringToneUri == null) {
            Uri alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmToneUri);
            tv_reminder_tone_summary.setText(ringtone.getTitle(this));
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(this, reminderModel.ringToneUri);
            tv_reminder_tone_summary.setText(ringtone.getTitle(this));
        }

    }

    @Override
    public ReminderRepeatModel getRepeatModel() {
        if (reminderModel == null) {
            reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        }
        return reminderModel.getRepeatSettings();
    }

    @Override
    public void discardChanges() {
//        if (reminderModel == null) {
//            reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
//        }
        reminderModel.discardRepeatSettingChanges();
    }

    @Override
    public void commitChanges(ReminderRepeatModel repeatModel) {
//        if (reminderModel == null) {
//            reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
//        }

        if (reminderModel.trySetRepeatSettingChanges()) {
            refreshForm();
        }
    }

    @Override
    public ReminderSnoozeModel getSnoozeModel() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getSnoozeModel();
    }

    @Override
    public void commitChanges(ReminderSnoozeModel model) {
        refreshForm();
    }

    @Override
    public String getReminderName() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.name;
    }

    @Override
    public void setName(String name, boolean isEOF) {
        if (isEOF) {
            reminderModel.name = name;
            refreshForm();
        }
    }

    @Override
    public String getReminderNote() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.note;
    }

    @Override
    public void setNote(String note, boolean isEOF) {
        if (isEOF) {
            reminderModel.note = note;
            refreshForm();
        }
    }

}