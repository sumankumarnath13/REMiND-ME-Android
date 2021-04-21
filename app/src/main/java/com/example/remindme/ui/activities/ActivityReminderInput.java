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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.AppSettingsHelper;
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
    private static final String MORE_INPUT_UI_STATE = "MORE_INPUT";

    private ReminderModel reminderModel = null;

    private TextView tv_reminder_trigger_time;
    private TextView tv_reminder_trigger_date;
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

    private SwitchCompat sw_reminder_tone;
    private SwitchCompat sw_reminder_vibrate;
    private LinearLayout lv_reminder_extra_inputs;
    private ImageView btn_reminder_extra_inputs;
    private ScrollView sv_container;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RINGTONE_DIALOG_REQ_CODE && data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            TextView tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
            if (uri != null) {
                reminderModel.setRingToneUri(uri);
                Ringtone ringtone = RingtoneManager.getRingtone(this, reminderModel.getRingToneUri());
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

        if (savedInstanceState != null) {
            isExtraInputsVisible = savedInstanceState.getBoolean(MORE_INPUT_UI_STATE, false);
        }

        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);

        if (reminderModel.isNew()) { // First time creating the activity
            //Check intents
            final String reminderType = getIntent().getStringExtra(INTENT_FROM);
            // Open if intent is update
            if (FLAG_REMINDER_TYPE_ACTIVE.equals(reminderType)) {
                if (reminderModel.setInstance(getIntent())) {
                    // Everything looks good so far. Set the title as Update
                    ActivityHelper.setTitle(this, getResources().getString(R.string.edit_reminder_heading));
                } else {
                    // Close the activity if intent was update bu no existing reminder found!
                    ToastHelper.showError(this, "Reminder not found!");
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
        tv_reminder_trigger_time = findViewById(R.id.tv_reminder_trigger_time);
        tv_reminder_trigger_date = findViewById(R.id.tv_reminder_trigger_date);
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
                    repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.OFF);
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
                        }, mHour, mMinute, AppSettingsHelper.getInstance().isUse24hourTime());
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
                if (reminderModel.getRingToneUri() == null) {
                    reminderModel.setRingToneUri(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                }
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone:");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, reminderModel.getRingToneUri());
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, RINGTONE_DIALOG_REQ_CODE);
            }
        });

        sw_reminder_tone = findViewById(R.id.sw_reminder_tone);
        sw_reminder_tone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isRefreshing) return;

                reminderModel.setEnableTone(isChecked);

                refreshForm();

            }
        });

        sw_reminder_vibrate = findViewById(R.id.sw_reminder_vibrate);
        sw_reminder_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isRefreshing) return;

                reminderModel.setEnableVibration(isChecked);

                refreshForm();

            }
        });

        final Button btn_reminder_save = findViewById(R.id.btn_reminder_save);
        btn_reminder_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reminderModel.trySaveAndSetAlert(getApplicationContext(), reminderModel.isHasDifferentTimeCalculated(), true)) {
                    finish();
                } else {
                    ToastHelper.showLong(ActivityReminderInput.this, "Time cannot be set in past!");
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

        //final AudioManager audioManager = OsHelper.getAudioManager(ActivityReminderInput.this);
        seeker_alarm_volume = findViewById(R.id.seeker_alarm_volume);
        seeker_alarm_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress < ReminderModel.MINIMUM_INPUT_VOLUME_PERCENTAGE)
                        seekBar.setProgress(ReminderModel.MINIMUM_INPUT_VOLUME_PERCENTAGE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                reminderModel.setAlarmVolumePercentage(seekBar.getProgress());
                ToastHelper.showShort(ActivityReminderInput.this, "Alarm will ring at " + reminderModel.getAlarmVolumePercentage() + "% volume");
            }
        });

        lv_reminder_extra_inputs = findViewById(R.id.lv_reminder_extra_inputs);
        btn_reminder_extra_inputs = findViewById(R.id.btn_reminder_extra_inputs);
        btn_reminder_extra_inputs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExtraInputsVisible = !isExtraInputsVisible;
                refreshForm();
            }
        });

        sv_container = findViewById(R.id.sv_container);

        refreshForm();
    }

    private boolean isExtraInputsVisible;

    private boolean isRefreshing;

    private void refreshForm() {

        isRefreshing = true;

        if (reminderModel.getOriginalTime() == null) {
            // User has not entered any value yet! Set a default time to start.
            Calendar _c = Calendar.getInstance();
            _c.add(Calendar.HOUR_OF_DAY, 1);
            reminderModel.setOriginalTime(_c.getTime());
        }

        btn_reminder_time.setText(StringHelper.toTime(reminderModel.getOriginalTime()));
        btn_reminder_date.setText(StringHelper.toWeekdayDate(reminderModel.getOriginalTime()));

        if (reminderModel.isHasDifferentTimeCalculated()) {
            lvc_diff_next_reminder_trigger.setVisibility(View.VISIBLE);
            tv_reminder_trigger_time.setText(StringHelper.toTime(reminderModel.getCalculatedTime()));
            tv_reminder_trigger_date.setText(StringHelper.toWeekdayDate(reminderModel.getCalculatedTime()));
        } else {
            lvc_diff_next_reminder_trigger.setVisibility(View.GONE);
        }

        tv_reminder_name_summary.setText(reminderModel.getName());
        tv_reminder_note_summary.setText(reminderModel.getNote());
        tv_reminder_repeat_summary.setText(reminderModel.getRepeatSettingString());
        tv_reminder_snooze_summary.setText(reminderModel.getSnoozeModel().toString());

        sw_reminder_snooze.setChecked(reminderModel.getSnoozeModel().isEnable);
        sw_reminder_repeat.setChecked(reminderModel.getRepeatOption() != ReminderRepeatModel.ReminderRepeatOptions.OFF);

        sw_reminder_tone.setChecked(reminderModel.isEnableTone());
        sw_reminder_vibrate.setChecked(reminderModel.isEnableVibration());

        if (reminderModel.isEnableTone()) {
            sw_gradually_increase_volume.setEnabled(true);
            sw_gradually_increase_volume.setChecked(reminderModel.isIncreaseVolumeGradually());

            seeker_alarm_volume.setEnabled(true);
            tv_reminder_tone_summary.setEnabled(true);

            if (reminderModel.getAlarmVolumePercentage() == 0) {
                seeker_alarm_volume.setProgress(OsHelper.getAlarmVolumeInPercentage(OsHelper.getAudioManager(this)));
            } else {
                seeker_alarm_volume.setProgress(reminderModel.getAlarmVolumePercentage());
            }

        } else {
            sw_gradually_increase_volume.setEnabled(false);
            sw_gradually_increase_volume.setChecked(false);
            seeker_alarm_volume.setEnabled(false);
            tv_reminder_tone_summary.setEnabled(false);
        }

        tv_reminder_tone_summary.setText(reminderModel.getRingToneUriSummary(this));

        if (isExtraInputsVisible) {
            btn_reminder_extra_inputs.setImageResource(R.drawable.ic_expand_up);
            btn_reminder_extra_inputs.setColorFilter(getResources().getColor(R.color.bg_danger), android.graphics.PorterDuff.Mode.SRC_IN);

            lv_reminder_extra_inputs.setVisibility(View.VISIBLE);
            sv_container.post(new Runnable() {
                @Override
                public void run() {
                    sv_container.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        } else {
            btn_reminder_extra_inputs.setImageResource(R.drawable.ic_expand_down);
            btn_reminder_extra_inputs.setColorFilter(getResources().getColor(R.color.bg_success), android.graphics.PorterDuff.Mode.SRC_IN);

            lv_reminder_extra_inputs.setVisibility(View.GONE);
        }

        isRefreshing = false;
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
    public void commitChanges() {
        if (reminderModel.trySetRepeatSettingChanges()) {
            refreshForm();
        }
    }

    @Override
    public void commitSnoozeChanges() {
        refreshForm();
    }

    @Override
    public ReminderSnoozeModel getSnoozeModel() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getSnoozeModel();
    }

    @Override
    public String getReminderName() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getName();
    }

    @Override
    public void setName(String name, boolean isEOF) {
        if (isEOF) {
            reminderModel.setName(name);
            refreshForm();
        }
    }

    @Override
    public String getReminderNote() {
        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        return reminderModel.getNote();
    }

    @Override
    public void setNote(String note, boolean isEOF) {
        if (isEOF) {
            reminderModel.setNote(note);
            refreshForm();
        }
    }

}