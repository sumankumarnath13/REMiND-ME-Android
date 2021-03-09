package com.example.remindme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
    private TextView tv_reminder_repeat_summary = null;
    private TextView tv_reminder_snooze_summary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reminderModel = new ReminderModel(UUID.randomUUID().toString());

        setContentView(R.layout.activity_reminder_input);

        UtilsActivity.setTitle(this);

        tv_reminder_name_summary = findViewById(R.id.tv_reminder_name_summary);
        tv_reminder_note_summary = findViewById(R.id.tv_reminder_note_summary);
        tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);
        tv_reminder_snooze_summary = findViewById(R.id.tv_reminder_snooze_summary);

        final TextView tv_title = findViewById(R.id.tv_title);
        final Button btn_reminder_date = findViewById(R.id.btn_reminder_date);
        final Button btn_reminder_time = findViewById(R.id.btn_reminder_time);
        final Button btn_reminder_save = findViewById(R.id.btn_reminder_save);
        final LinearLayout mnu_reminder_name = findViewById(R.id.mnu_reminder_name);
        final LinearLayout mnu_reminder_note = findViewById(R.id.mnu_reminder_note);
        final LinearLayout mnu_reminder_repeat = findViewById(R.id.mnu_reminder_repeat);
        final SwitchCompat sw_reminder_vibrate = findViewById(R.id.sw_reminder_vibrate);
        final SwitchCompat sw_reminder_disable = findViewById(R.id.sw_reminder_disable);
        final LinearLayout mnu_reminder_snooze = findViewById(R.id.mnu_reminder_snooze);

        //final Button btn_sound = findViewById(R.id.btn_reminder_sound);

        Intent i = getIntent();
        final String reminder_id = i.getStringExtra(ReminderModel.INTENT_ATTR_ID);
        if (reminder_id != null) {
            tv_title.setText(getResources().getString(R.string.edit_reminder_heading));

            final String from = i.getStringExtra("FROM");

            if (from != null && from.equals("ACTIVE")) {
                reminderModel = ReminderModel.read(reminder_id);
            } else {
                //ReminderModel reminder_from = ReminderModel.read(reminder_id);
                //ReminderModel.copy(reminder_from, reminderModel);
                Calendar _c = Calendar.getInstance();
                _c.add(Calendar.HOUR, 1);
                reminderModel.time = _c.getTime();
            }
        } else {
            Calendar _c = Calendar.getInstance();
            _c.add(Calendar.HOUR, 1);
            reminderModel.time = _c.getTime();
        }

        btn_reminder_date.setText(UtilsDateTime.toDateString(reminderModel.time));
        btn_reminder_time.setText(UtilsDateTime.toTimeString(reminderModel.time));
        tv_reminder_name_summary.setText(reminderModel.name);
        tv_reminder_note_summary.setText(reminderModel.note);
        tv_reminder_repeat_summary.setText(reminderModel.repeatModel.toString());
        tv_reminder_snooze_summary.setText(reminderModel.snoozeModel.toString());
        sw_reminder_vibrate.setChecked(reminderModel.isVibrate);
        sw_reminder_disable.setChecked(!reminderModel.getIsEnabled());

        btn_reminder_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                c.setTime(reminderModel.time);
                int mHour, mMinute;
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityReminderInput.this, R.style.TimePickerDialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                reminderModel.time = c.getTime();
                                btn_reminder_time.setText(UtilsDateTime.toTimeString(ActivityReminderInput.this.reminderModel.time));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        btn_reminder_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                c.setTime(reminderModel.time);
                int mYear, mMonth, mDay;
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityReminderInput.this, R.style.DatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                reminderModel.time = c.getTime();
                                btn_reminder_date.setText(UtilsDateTime.toDateString(ActivityReminderInput.this.reminderModel.time));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btn_reminder_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reminderModel.canUpdate()) {
                    reminderModel.isVibrate = sw_reminder_vibrate.isChecked();
                    reminderModel.setIsEnabled(!sw_reminder_disable.isChecked(), getApplicationContext());
                    finish();
                } else {
                    Toast.makeText(ActivityReminderInput.this, "Time cannot be set in past!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mnu_reminder_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogReminderNameInput input = new DialogReminderNameInput();
                input.show(getSupportFragmentManager(), "Reminder_Input_Name");
            }
        });

        mnu_reminder_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogReminderNoteInput input = new DialogReminderNoteInput();
                input.show(getSupportFragmentManager(), "Reminder_Input_Note");
            }
        });

        mnu_reminder_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a buffer and send it to dialog:
                repeatModelBuffer = new ReminderRepeatModel();
                // Copy from real object:
                repeatModelBuffer.repeatOption = reminderModel.repeatModel.repeatOption;
                repeatModelBuffer.dailyModel = reminderModel.repeatModel.dailyModel;
                repeatModelBuffer.monthlyModel = reminderModel.repeatModel.monthlyModel;

                DialogReminderRepeatInput input = new DialogReminderRepeatInput();
                input.show(getSupportFragmentManager(), "Reminder_Repeat");

            }
        });

        mnu_reminder_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogReminderSnoozeInput input = new DialogReminderSnoozeInput();
                input.show(getSupportFragmentManager(), "Reminder_Snooze");
            }
        });

        sw_reminder_vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sw_reminder_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_reminder_disable.isChecked()) {
                    if (reminderModel.canEnable()) {
                        reminderModel.setIsEnabled(sw_reminder_disable.isChecked(), ActivityReminderInput.this.getApplicationContext());
                        btn_reminder_time.setText(UtilsDateTime.toTimeString(ActivityReminderInput.this.reminderModel.time));
                        btn_reminder_date.setText(UtilsDateTime.toDateString(ActivityReminderInput.this.reminderModel.time));
                    } else {
                        Toast.makeText(ActivityReminderInput.this, "Cannot enable in past time.", Toast.LENGTH_SHORT).show();
                        sw_reminder_disable.setChecked(false);
                    }
                } else {
                    reminderModel.setIsEnabled(false, ActivityReminderInput.this.getApplicationContext());
                }
            }
        });
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