package com.example.remindme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.util.IReminderRepeatListener;
import com.example.remindme.util.ReminderRepeatDailyModel;
import com.example.remindme.util.ReminderRepeatModel;
import com.example.remindme.util.ReminderRepeatMonthlyModel;
import com.example.remindme.util.UtilsActivity;
import com.example.remindme.util.UtilsAlarm;
import com.example.remindme.util.UtilsDateTime;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.ParametersAreNonnullByDefault;
import io.realm.Realm;

public class ActivityReminderInput extends AppCompatActivity implements IReminderRepeatListener {
    private ReminderActive reminder = null;
    private ReminderRepeatModel repeatModel = null;
    private Date date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repeatModel = new ReminderRepeatModel();
        repeatModel.dailyModel = new ReminderRepeatDailyModel();
        repeatModel.monthlyModel = new ReminderRepeatMonthlyModel();

        setContentView(R.layout.activity_reminder_input);

        UtilsActivity.setTitle(this);

        final TextView tv_title = findViewById(R.id.tv_title);
        final TextView tv_name = findViewById(R.id.tv_reminder_name);
        final TextView tv_note = findViewById(R.id.tv_reminder_note);
        final Button btn_date = findViewById(R.id.btn_reminder_date);
        final Button btn_time = findViewById(R.id.btn_reminder_time);
        final Button btn_save = findViewById(R.id.btn_reminder_save);

        final Button btn_repeat = findViewById(R.id.btn_reminder_repeat);
        final Button btn_sound = findViewById(R.id.btn_reminder_sound);
        final Button btn_snooze = findViewById(R.id.btn_reminder_snooze);
        final SwitchCompat sw_vibrate = findViewById(R.id.sw_reminder_vibrate);
        final SwitchCompat sw_disable = findViewById(R.id.sw_reminder_disable);

        Intent i = getIntent();
        final int reminder_id = i.getIntExtra("ID", 0);
        if(reminder_id > 0) {
            Realm realm = Realm.getDefaultInstance();
            final String from = i.getStringExtra("FROM");
            if(from != null && from.equals("ACTIVE")){
                tv_title.setText(getResources().getString(R.string.title_edit_reminder));
                reminder = realm.where(ReminderActive.class).equalTo("id", reminder_id).findFirst();
                if(reminder != null) {
                    try {
                        date = UtilsDateTime.toDate(reminder.id);
                    } catch (ParseException e) {
                        Toast.makeText(this, "PARSE ERROR : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            else{
                Calendar _c = Calendar.getInstance();
                _c.add(Calendar.MINUTE, 5);
                date = _c.getTime();
            }
        }
        else {
            Calendar _c = Calendar.getInstance();
            _c.add(Calendar.MINUTE, 5);
            date = _c.getTime();
        }

        //tv_name.setText(i.getStringExtra("NAME"));
        //tv_note.setText(i.getStringExtra("NOTE"));

        btn_date.setText(UtilsDateTime.toDateString(date));
        btn_time.setText(UtilsDateTime.toTimeString(date));

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            int mHour, mMinute;
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityReminderInput.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);
                    date = c.getTime();
                    btn_time.setText(UtilsDateTime.toTimeString(ActivityReminderInput.this.date));
                    }
                }, mHour, mMinute, false);
            timePickerDialog.show();
            }
        });

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            int mYear, mMonth, mDay;
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityReminderInput.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                date = c.getTime();
                btn_date.setText(UtilsDateTime.toDateString(ActivityReminderInput.this.date));
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          if(Calendar.getInstance().getTime().after(date)){
              Toast.makeText(ActivityReminderInput.this, "Time cannot be set in past!", Toast.LENGTH_SHORT).show();
          }
          else {
              Realm realm = Realm.getDefaultInstance();
              realm.executeTransaction(new Realm.Transaction() {
                  @ParametersAreNonnullByDefault
                  @Override
                  public void execute(Realm realm) {
                  final int new_reminder_id = UtilsDateTime.toInt(date);
                  if(reminder == null){ // ADD NEW MODE
                      reminder = realm.where(ReminderActive.class).equalTo("id", new_reminder_id) .findFirst();
                      if(reminder == null){ // No reminder found at target time. Green.
                          reminder = new ReminderActive();
                          reminder.id = new_reminder_id;
                          reminder.enabled = true;
                      }
                      else{ // Reminder on same time exists! Red.
                          Toast.makeText(ActivityReminderInput.this, "Reminder already exists!", Toast.LENGTH_LONG).show();
                          //End of function: return
                      }
                  }
                  else{ //EDIT MODE
                      if(reminder.id != new_reminder_id ){ // Time has been changed. Find if a reminder already exists in target time.
                          ReminderActive new_reminder = realm.where(ReminderActive.class).equalTo("id", new_reminder_id) .findFirst();

                          if(new_reminder == null){

                              new_reminder = new ReminderActive();
                              new_reminder.id = new_reminder_id;
                              new_reminder.enabled = reminder.enabled;
                              new_reminder.name = reminder.name;
                              new_reminder.note = reminder.note;

                              if(reminder.next_snooze_id > 0){
                                  UtilsAlarm.unSet(getApplicationContext(), reminder.next_snooze_id);
                              }
                              else{
                                  UtilsAlarm.unSet(getApplicationContext(), reminder.id);
                              }

                              reminder.deleteFromRealm();

                              reminder = new_reminder;
                          }
                          else{
                              Toast.makeText(ActivityReminderInput.this, "Reminder already exists!", Toast.LENGTH_LONG).show();
                              //End of function: return
                          }
                      }
                  }
                  }
              });

              if(reminder != null){
                  realm.executeTransaction(new Realm.Transaction() {
                      @ParametersAreNonnullByDefault
                      @Override
                      public void execute(Realm realm) {
                      reminder.name = tv_name.getText().toString();
                      reminder.note = tv_note.getText().toString();
                      realm.insertOrUpdate(reminder);
                      try {
                          UtilsAlarm.set(getApplicationContext(), reminder);
                      } catch (ParseException e) {
                          Toast.makeText(ActivityReminderInput.this, "PARSE ERROR : " + e.getMessage(), Toast.LENGTH_LONG).show();
                      }
                      }
                  });
                  finish();
              }
          }
          }
      });

        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogReminderRepeatInput repeatInput = new DialogReminderRepeatInput();
                repeatInput.show(getSupportFragmentManager(), "Reminder_Repeat");

            }
        });

        btn_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sw_vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sw_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void set(ReminderRepeatModel repeatModel) {
        if(repeatModel.isContinue){
            repeatModel.isContinue = false;
        }
        else {
            switch (repeatModel.repeatOption){
                case None:
                default:
                    //reminder.repeatTypes = 0;
                    Toast.makeText(ActivityReminderInput.this, "None", Toast.LENGTH_LONG).show();
                    break;
                case Hourly:
                    //reminder.repeatTypes = 1;
                    Toast.makeText(ActivityReminderInput.this, "Hourly", Toast.LENGTH_LONG).show();
                    break;
                case Daily:
                    //reminder.repeatTypes = 1;
                    repeatModel.isContinue = true;
                    DialogReminderRepeatInputDaily inputDaily = new DialogReminderRepeatInputDaily();
                    inputDaily.show(getSupportFragmentManager(), "Reminder_Repeat_Daily");
                    //Toast.makeText(ActivityReminderInput.this, "Daily", Toast.LENGTH_LONG).show();
                    break;
                case Monthly:
                    repeatModel.isContinue = true;
                    //reminder.repeatTypes = 1;
                    DialogReminderRepeatInputMonthly inputMonthly = new DialogReminderRepeatInputMonthly();
                    inputMonthly.show(getSupportFragmentManager(), "Reminder_Repeat_Monthly");
                    //Toast.makeText(ActivityReminderInput.this, "Monthly", Toast.LENGTH_LONG).show();
                    break;
                case Yearly:
                    //reminder.repeatTypes = 1;
                    Toast.makeText(ActivityReminderInput.this, "Yearly", Toast.LENGTH_LONG).show();
                    break;

            }
        }
    }

    @Override
    public ReminderRepeatModel get() {
        return repeatModel;
    }
}