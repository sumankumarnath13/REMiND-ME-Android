package com.example.remindme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.util.UtilsActivity;
import com.example.remindme.util.UtilsDateTime;
import com.example.remindme.viewModels.IReminderNameListener;
import com.example.remindme.viewModels.IReminderNoteListener;
import com.example.remindme.viewModels.IReminderRepeatListener;
import com.example.remindme.viewModels.IReminderSnoozeListener;
import com.example.remindme.viewModels.ReminderModel;
import com.example.remindme.viewModels.ReminderRepeatModel;
import com.example.remindme.viewModels.ReminderSnoozeModel;

import java.util.Calendar;
import java.util.UUID;

public class ActivityReminderInput extends AppCompatActivity implements IReminderNameListener, IReminderNoteListener, IReminderRepeatListener, IReminderSnoozeListener {
    private ReminderModel reminderModel = null;
    private ReminderRepeatModel repeatModelBuffer = null;

    private TextView tv_reminder_name_summary = null;
    private TextView tv_reminder_note_summary = null;
    private SwitchCompat sw_reminder_repeat = null;
    private SwitchCompat sw_reminder_snooze = null;
    private TextView tv_reminder_repeat_summary = null;
    private TextView tv_reminder_snooze_summary = null;
    private static final int RINGTONE_DIALOG_REQ_CODE = 117;

    private boolean isUserSetDate = false;


    private DialogReminderSnoozeInput snoozeInput;
    private DialogReminderNameInput nameInput;
    private DialogReminderNoteInput noteInput;
    private DialogReminderRepeatInput repeatInput;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RINGTONE_DIALOG_REQ_CODE) {
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_input);

        final TextView tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);

        if (reminderModel == null) {

            nameInput = new DialogReminderNameInput();
            noteInput = new DialogReminderNoteInput();
            snoozeInput = new DialogReminderSnoozeInput();
            repeatInput = new DialogReminderRepeatInput();

            Intent i = getIntent();
            final String reminder_id = ReminderModel.getReminderId(i);
            if (reminder_id != null) {
                UtilsActivity.setTitle(this, "UPDATE");
                reminderModel = ReminderModel.read(ActivityReminderInput.this, reminder_id);
                final String from = i.getStringExtra("FROM");
                if (from != null && from.equals("ACTIVE")) {
                    // Do nothing
                } else {
                    //ReminderModel reminder_from = ReminderModel.read(reminder_id);
                    //ReminderModel.copy(reminder_from, reminderModel);
                    Calendar _c = Calendar.getInstance();
                    _c.add(Calendar.HOUR, 1);
                    reminderModel.time = _c.getTime();
                }
            } else {
                UtilsActivity.setTitle(this, "NEW");
                reminderModel = new ReminderModel(ActivityReminderInput.this, UUID.randomUUID().toString());
                Calendar _c = Calendar.getInstance();
                _c.add(Calendar.HOUR, 1);
                reminderModel.time = _c.getTime();
            }
        }

        tv_reminder_name_summary = findViewById(R.id.tv_reminder_name_summary);
        tv_reminder_name_summary.setText(reminderModel.name);

        tv_reminder_note_summary = findViewById(R.id.tv_reminder_note_summary);
        tv_reminder_note_summary.setText(reminderModel.note);

        tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);
        tv_reminder_repeat_summary.setText(reminderModel.repeatModel.toString());
        sw_reminder_repeat = findViewById(R.id.sw_reminder_repeat);
        if (reminderModel.repeatModel.repeatOption != ReminderRepeatModel.ReminderRepeatOptions.None) {
            sw_reminder_repeat.setChecked(true);
        }
        sw_reminder_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_reminder_repeat.isChecked()) {
                    reminderModel.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Daily; // Default
                } else {
                    reminderModel.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.None;
                }
                tv_reminder_repeat_summary.setText(reminderModel.repeatModel.toString());
            }
        });

        tv_reminder_snooze_summary = findViewById(R.id.tv_reminder_snooze_summary);
        tv_reminder_snooze_summary.setText(reminderModel.snoozeModel.toString());
        sw_reminder_snooze = findViewById(R.id.sw_reminder_snooze);
        sw_reminder_snooze.setChecked(reminderModel.snoozeModel.isEnable);
        sw_reminder_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderModel.snoozeModel.isEnable = sw_reminder_snooze.isChecked();
                tv_reminder_snooze_summary.setText(reminderModel.snoozeModel.toString());
            }
        });

        if (reminderModel.ringToneUri == null) {
            Uri alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmToneUri);
            tv_reminder_tone_summary.setText(ringtone.getTitle(this));
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(this, reminderModel.ringToneUri);
            tv_reminder_tone_summary.setText(ringtone.getTitle(this));
        }

        final Button btn_reminder_date = findViewById(R.id.btn_reminder_date);
        btn_reminder_date.setText(UtilsDateTime.toDateString(reminderModel.time));
        btn_reminder_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar alertTime = Calendar.getInstance();
                //final Calendar currentTime = Calendar.getInstance();
                alertTime.setTime(reminderModel.time);
                final int mYear, mMonth, mDay;
                mYear = alertTime.get(Calendar.YEAR);
                mMonth = alertTime.get(Calendar.MONTH);
                mDay = alertTime.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityReminderInput.this, R.style.DatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                alertTime.set(Calendar.YEAR, year);
                                alertTime.set(Calendar.MONTH, monthOfYear);
                                alertTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                                if(alertTime.before(currentTime)){
//
//                                }
                                reminderModel.time = alertTime.getTime();
                                btn_reminder_date.setText(UtilsDateTime.toDateString(ActivityReminderInput.this.reminderModel.time));
                                isUserSetDate = true;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(alertTime.getTimeInMillis()); // This will cause extra title on the top of the regular date picker
                datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // This line will try to solve the issue above
                datePickerDialog.setTitle(null); // This line will try to solve the issue above
                datePickerDialog.show();
            }
        });

        final Button btn_reminder_time = findViewById(R.id.btn_reminder_time);
        btn_reminder_time.setText(UtilsDateTime.toTimeString(reminderModel.time));
        btn_reminder_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar alertTime = Calendar.getInstance();
                final Calendar currentTime = Calendar.getInstance();
                alertTime.setTime(reminderModel.time);
                final int mHour, mMinute;
                mHour = alertTime.get(Calendar.HOUR_OF_DAY);
                mMinute = alertTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityReminderInput.this, R.style.TimePickerDialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                alertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                alertTime.set(Calendar.MINUTE, minute);
                                if (!isUserSetDate) {
                                    if (alertTime.before(currentTime)) {
                                        alertTime.add(Calendar.DAY_OF_YEAR, 1); // Set it for tomorrow if the time has already passed for today ( If user has not set the date already )
                                    } else {
                                        alertTime.set(Calendar.DAY_OF_YEAR, currentTime.get(Calendar.DAY_OF_YEAR));
                                    }
                                    btn_reminder_date.setText(UtilsDateTime.toDateString(alertTime.getTime()));
                                }
                                reminderModel.time = alertTime.getTime();
                                btn_reminder_time.setText(UtilsDateTime.toTimeString(ActivityReminderInput.this.reminderModel.time));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        final LinearLayout mnu_reminder_name = findViewById(R.id.mnu_reminder_name);
        mnu_reminder_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameInput.show(getSupportFragmentManager(), "Reminder_Input_Name");
            }
        });

        final LinearLayout mnu_reminder_note = findViewById(R.id.mnu_reminder_note);
        mnu_reminder_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteInput.show(getSupportFragmentManager(), "Reminder_Input_Note");
            }
        });

        final LinearLayout mnu_reminder_repeat = findViewById(R.id.mnu_reminder_repeat);
        mnu_reminder_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a buffer and send it to dialog:
                repeatModelBuffer = new ReminderRepeatModel();
                // Copy from real object:
                repeatModelBuffer.repeatOption = reminderModel.repeatModel.repeatOption;
                repeatModelBuffer.dailyModel = reminderModel.repeatModel.dailyModel;
                repeatModelBuffer.monthlyModel = reminderModel.repeatModel.monthlyModel;

                repeatInput.show(getSupportFragmentManager(), "Reminder_Repeat");

            }
        });

        final LinearLayout mnu_reminder_snooze = findViewById(R.id.mnu_reminder_snooze);
        mnu_reminder_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (reminderModel.trySetEnabled(!sw_reminder_disable.isChecked())) {
                    btn_reminder_time.setText(UtilsDateTime.toTimeString(ActivityReminderInput.this.reminderModel.time));
                    btn_reminder_date.setText(UtilsDateTime.toDateString(ActivityReminderInput.this.reminderModel.time));

//                    if (reminderModel.canEnable()) {
//                        reminderModel.trySetEnabled(sw_reminder_disable.isChecked());
//                    } else {
//                        Toast.makeText(ActivityReminderInput.this, "Cannot enable in past time.", Toast.LENGTH_SHORT).show();
//
//                    }
                } else {
                    sw_reminder_disable.setChecked(true);
                }
            }
        });

        final Button btn_reminder_save = findViewById(R.id.btn_reminder_save);
        btn_reminder_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminderModel.isEnableVibration = sw_reminder_vibrate.isChecked();
                if (reminderModel.trySaveAndSetAlert(true, true)) {
                    finish();
                } else {
                    Toast.makeText(ActivityReminderInput.this, "Time cannot be set in past!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isChangingConfigurations()) {
            if (nameInput.isVisible()) {
                nameInput.dismiss();
            }
            if (noteInput.isVisible()) {
                noteInput.dismiss();
            }
            if (snoozeInput.isVisible()) {
                snoozeInput.dismiss();
            }
            if (repeatInput.isVisible()) {
                repeatInput.dismiss();
            }


        } else {
            reminderModel = null;
        }

//        if (!isReady) { // If not ready then only make it read. Else, just keep the state.
//            isReady = !isChangingConfigurations(); //isChangingConfigurations returns true if switching from "landscape <> portrait"
//        }
    }

    @Override
    public void set(ReminderRepeatModel model, boolean isEOF) {

        switch (model.repeatOption) {
            case None:
            case Hourly:
            case Weekly:
            case Yearly:
            default:
                if (isEOF) {
                    reminderModel.repeatModel = model;
                    sw_reminder_repeat.setChecked(reminderModel.repeatModel.repeatOption != ReminderRepeatModel.ReminderRepeatOptions.None);
                    tv_reminder_repeat_summary.setText(reminderModel.repeatModel.toString());
                }
                break;
            case Daily:
                if (isEOF) {
                    if (model.dailyModel.isSun ||
                            model.dailyModel.isMon ||
                            model.dailyModel.isTue ||
                            model.dailyModel.isWed ||
                            model.dailyModel.isThu ||
                            model.dailyModel.isFri ||
                            model.dailyModel.isSat) {
                        reminderModel.repeatModel = model;
                        sw_reminder_repeat.setChecked(reminderModel.repeatModel.repeatOption != ReminderRepeatModel.ReminderRepeatOptions.None);
                        tv_reminder_repeat_summary.setText(reminderModel.repeatModel.toString());
                    }
                } else {
                    DialogReminderRepeatInputDaily inputDaily = new DialogReminderRepeatInputDaily();
                    inputDaily.show(getSupportFragmentManager(), "Reminder_Repeat_Daily");
                }
                break;
            case Monthly:
                if (isEOF) {
                    if (model.monthlyModel.isJan ||
                            model.monthlyModel.isFeb ||
                            model.monthlyModel.isMar ||
                            model.monthlyModel.isApr ||
                            model.monthlyModel.isMay ||
                            model.monthlyModel.isJun ||
                            model.monthlyModel.isJul ||
                            model.monthlyModel.isAug ||
                            model.monthlyModel.isSep ||
                            model.monthlyModel.isOct ||
                            model.monthlyModel.isNov ||
                            model.monthlyModel.isDec) {
                        reminderModel.repeatModel = model;
                        sw_reminder_repeat.setChecked(reminderModel.repeatModel.repeatOption != ReminderRepeatModel.ReminderRepeatOptions.None);
                        tv_reminder_repeat_summary.setText(reminderModel.repeatModel.toString());
                    }
                } else {
                    DialogReminderRepeatInputMonthly inputMonthly = new DialogReminderRepeatInputMonthly();
                    inputMonthly.show(getSupportFragmentManager(), "Reminder_Repeat_Monthly");
                }
                break;
        }
    }

    @Override
    public void set(ReminderSnoozeModel model, boolean isEOF) {
        if (isEOF) {
            sw_reminder_snooze.setChecked(reminderModel.snoozeModel.isEnable);
            tv_reminder_snooze_summary.setText(reminderModel.snoozeModel.toString());
        }
    }

    @Override
    public void setName(String name, boolean isEOF) {
        if (isEOF) {
            reminderModel.name = name;
            tv_reminder_name_summary.setText(reminderModel.name);
        }
    }

    @Override
    public void setNote(String note, boolean isEOF) {
        if (isEOF) {
            reminderModel.note = note;
            tv_reminder_note_summary.setText(reminderModel.note);
        }
    }

    @Override
    public ReminderSnoozeModel getSnoozeModel() {
        return reminderModel.snoozeModel;
    }

    @Override
    public ReminderRepeatModel getRepeatModel() {
        return repeatModelBuffer;
    }

    @Override
    public String getReminderName() {
        return reminderModel.name;
    }

    @Override
    public String getReminderNote() {
        return reminderModel.note;
    }
}