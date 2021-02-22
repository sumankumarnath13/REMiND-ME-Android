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
import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.viewModels.IReminderNameListener;
import com.example.remindme.viewModels.IReminderNoteListener;
import com.example.remindme.viewModels.IReminderRepeatListener;
import com.example.remindme.viewModels.IReminderSnoozeListener;
import com.example.remindme.viewModels.ReminderModel;
import com.example.remindme.viewModels.ReminderRepeatModel;
import com.example.remindme.viewModels.ReminderSnoozeModel;
import com.example.remindme.util.UtilsActivity;
import com.example.remindme.util.UtilsDateTime;
import java.text.ParseException;
import java.util.Calendar;
import javax.annotation.ParametersAreNonnullByDefault;
import io.realm.Realm;

public class ActivityReminderInput extends AppCompatActivity implements IReminderNameListener, IReminderNoteListener, IReminderRepeatListener, IReminderSnoozeListener {
    private ReminderActive reminder = null;
    private ReminderModel reminderModel = null;
    private ReminderRepeatModel repeatModelBuffer = null;
    private ReminderSnoozeModel snoozeModelBuffer = null;

    private TextView tv_reminder_name_summary = null;
    private TextView tv_reminder_note_summary = null;
    private TextView tv_reminder_repeat_summary = null;
    private TextView tv_reminder_snooze_summary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reminderModel = new ReminderModel();

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
        final int reminder_id = i.getIntExtra("ID", 0);
        if (reminder_id > 0) {
            Realm realm = Realm.getDefaultInstance();
            final String from = i.getStringExtra("FROM");
            if (from != null && from.equals("ACTIVE")) {
                tv_title.setText(getResources().getString(R.string.edit_reminder_heading));
                reminder = realm.where(ReminderActive.class).equalTo("id", reminder_id).findFirst();
                if (reminder != null) {
                    reminderModel = ReminderModel.transform(reminder);

                    try {
                        reminderModel.time = UtilsDateTime.toDate(reminder.id);
                    } catch (ParseException e) {
                        Toast.makeText(this, "PARSE ERROR : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Calendar _c = Calendar.getInstance();
                _c.add(Calendar.MINUTE, 5);
                reminderModel.time = _c.getTime();
            }
        } else {
            Calendar _c = Calendar.getInstance();
            _c.add(Calendar.MINUTE, 5);
            reminderModel.time = _c.getTime();
        }

        btn_reminder_date.setText(UtilsDateTime.toDateString(reminderModel.time));
        btn_reminder_time.setText(UtilsDateTime.toTimeString(reminderModel.time));
        tv_reminder_name_summary.setText(reminderModel.name);
        tv_reminder_note_summary.setText(reminderModel.note);
        tv_reminder_repeat_summary.setText(reminderModel.repeatModel.toString());
        tv_reminder_snooze_summary.setText(reminderModel.snoozeModel.toString());
        sw_reminder_vibrate.setChecked(reminderModel.isVibrate);
        sw_reminder_disable.setChecked(!reminderModel.isEnable);

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityReminderInput.this,  R.style.DatePickerDialog,
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
                if (Calendar.getInstance().getTime().after(reminderModel.time)) {
                    Toast.makeText(ActivityReminderInput.this, "Time cannot be set in past!", Toast.LENGTH_SHORT).show();
                } else {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @ParametersAreNonnullByDefault
                        @Override
                        public void execute(Realm realm) {

                            final int new_reminder_id = UtilsDateTime.toInt(reminderModel.time);

                            if (reminder == null) { // ADD NEW MODE
                                reminder = realm.where(ReminderActive.class).equalTo("id", new_reminder_id).findFirst();
                                if (reminder == null) {
                                    // No reminder found at target time. Green.
                                    reminderModel.id = new_reminder_id;
                                    reminderModel.isValid = true;
//                          reminder = new ReminderActive();
//                          reminder.id = new_reminder_id;
//                          reminder.is_enable = true;
                                } else { // Reminder on same time exists! Red.
                                    Toast.makeText(ActivityReminderInput.this, "Reminder already exists!", Toast.LENGTH_LONG).show();
                                    //End of function: return
                                }
                            } else { //EDIT MODE
                                if (reminderModel.id != new_reminder_id) { // Time has been changed. Find if a reminder already exists in target time.
                                    ReminderActive new_reminder = realm.where(ReminderActive.class).equalTo("id", new_reminder_id).findFirst();

                                    if (new_reminder == null) {
                                        // No reminder found at target time. Green.
                                        reminderModel.id = new_reminder_id;
                                        reminderModel.isValid = true;
//                            new_reminder = new ReminderActive();
//                            new_reminder.id = new_reminder_id;
//                            new_reminder.is_enable = reminder.is_enable;
//                            new_reminder.name = reminder.name;
//                            new_reminder.note = reminder.note;
//
//                            if(reminder.next_snooze_id > 0){
//                              UtilsAlarm.unSet(getApplicationContext(), reminder.next_snooze_id);
//                            }
//                            else{
//                              UtilsAlarm.unSet(getApplicationContext(), reminder.id);
//                            }

                                        reminder.deleteFromRealm();

                                        //reminder = new_reminder;
                                    } else {
                                        Toast.makeText(ActivityReminderInput.this, "Reminder already exists!", Toast.LENGTH_LONG).show();
                                        //End of function: return
                                    }
                                } else {
                                    reminderModel.isValid = true;
                                }
                            }
                        }
                    });

                    if (reminderModel.isValid) {
                        reminderModel.isVibrate = sw_reminder_vibrate.isChecked();
                        reminderModel.isEnable = !sw_reminder_disable.isChecked();

                        reminder = ReminderModel.transform(reminderModel);

                        realm.executeTransaction(new Realm.Transaction() {
                            @ParametersAreNonnullByDefault
                            @Override
                            public void execute(Realm realm) {
                                realm.insertOrUpdate(reminder);
//                      try {
//                          UtilsAlarm.set(getApplicationContext(), reminder);
//                      } catch (ParseException e) {
//                          Toast.makeText(ActivityReminderInput.this, "PARSE ERROR : " + e.getMessage(), Toast.LENGTH_LONG).show();
//                      }
                            }
                        });
                        finish();
                    }
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
        if(isEOF){
            reminderModel.name = name;
            tv_reminder_name_summary.setText(reminderModel.name);
        }
    }

    @Override
    public void setNote(String note, boolean isEOF) {
        if(isEOF){
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