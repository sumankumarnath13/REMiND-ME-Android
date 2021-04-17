package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.R;
import com.example.remindme.dataModels.DismissedReminder;
import com.example.remindme.dataModels.MissedReminder;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.ReminderModel;

import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.ParametersAreNonnullByDefault;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityReminderView extends AppCompatActivity {
    private String id;
    private String from;

    private TextView tv_reminder_time;
    private TextView tv_reminder_date;
    private TextView tv_reminder_name;
    private TextView tv_reminder_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_view);
        ActivityHelper.setTitle(this, getResources().getString(R.string.view_reminder_heading));

        tv_reminder_time = findViewById(R.id.tv_reminder_time);
        tv_reminder_date = findViewById(R.id.tv_reminder_date);
        tv_reminder_name = findViewById(R.id.tv_reminder_name);
        tv_reminder_note = findViewById(R.id.tv_reminder_note);

        Intent i = getIntent();
        id = ReminderModel.getReminderId(i);
        from = i.getStringExtra("FROM");

        final Button btnDelete = findViewById(R.id.btn_reminder_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals("ACTIVE")) {
                    ReminderModel reminderModel = new ReminderModel();
                    if (reminderModel.tryReadFrom(getIntent())) {
                        reminderModel.deleteAndCancelAlert(getApplicationContext());
                    }
                } else if (from.equals("MISSED")) {
                    Realm r = Realm.getDefaultInstance();
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        @ParametersAreNonnullByDefault
                        public void execute(Realm realm) {
                            RealmResults<MissedReminder> results = realm.where(MissedReminder.class)
                                    .equalTo("id", id).findAll();
                            results.deleteAllFromRealm();
                        }
                    });
                } else {
                    Realm r = Realm.getDefaultInstance();
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        @ParametersAreNonnullByDefault
                        public void execute(Realm realm) {
                            RealmResults<DismissedReminder> results = realm.where(DismissedReminder.class)
                                    .equalTo("id", id).findAll();
                            results.deleteAllFromRealm();
                        }
                    });
                }
                finish();
            }
        });

        final Button btnChange = findViewById(R.id.btn_reminder_edit);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent input_i = new Intent(getApplicationContext(), ActivityReminderInput.class);
                ReminderModel.setReminderId(input_i, id);
                input_i.putExtra("FROM", from);
                startActivity(input_i);
                finish();
            }
        });

        final SwitchCompat enabled = findViewById(R.id.sw_reminder_enabled);

        enabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderModel reminderModel = new ReminderModel();
                if (reminderModel.tryReadFrom(getIntent())) {
                    enabled.setChecked(reminderModel.trySetEnabled(getApplicationContext(), enabled.isChecked()));
                    if (enabled.isChecked()) {
                        ((TextView) findViewById(R.id.tv_reminder_time)).setText(StringHelper.toTimeDate(reminderModel.getOriginalTime()));
                    }
                    reminderModel.trySaveAndSetAlert(getApplicationContext(), true, true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent i = getIntent();
        id = ReminderModel.getReminderId(i);
        from = i.getStringExtra("FROM");

        final SwitchCompat sw_enabled = findViewById(R.id.sw_reminder_enabled);
        sw_enabled.setVisibility(View.GONE);

        if (from.equals("ACTIVE")) {
            ReminderModel reminderModel = new ReminderModel();
            if (reminderModel.tryReadFrom(getIntent())) {
                tv_reminder_time.setText(StringHelper.toTime(reminderModel.getOriginalTime()));
                tv_reminder_date.setText(StringHelper.toWeekdayDate(reminderModel.getOriginalTime()));

                if (!StringHelper.isNullOrEmpty(reminderModel.getName())) {
                    tv_reminder_name.setVisibility(View.VISIBLE);
                    tv_reminder_name.setText(reminderModel.getName());
                }

                if (reminderModel.getNextSnoozeOffTime() != null) {
                    final TextView next_snooze = findViewById(R.id.tv_reminder_next_snooze);
                    next_snooze.setText(StringHelper.toTime(reminderModel.getNextSnoozeOffTime()));
                }

                sw_enabled.setVisibility(View.VISIBLE);
                sw_enabled.setChecked(reminderModel.getIsEnabled());

                final TextView tv_reminder_snooze_summary = findViewById(R.id.tv_reminder_snooze_summary);
                tv_reminder_snooze_summary.setText(reminderModel.getSnoozeModel().toString());


                final TextView tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);
                tv_reminder_repeat_summary.setText(reminderModel.getRepeatSettingString());

                final TextView tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
                tv_reminder_tone_summary.setText(reminderModel.getRingToneUriSummary(this));

                final TextView tv_alarm_tone_is_enable = findViewById(R.id.tv_alarm_tone_is_enable);
                tv_alarm_tone_is_enable.setText(reminderModel.isEnableTone ? "ON" : "OFF");
                tv_alarm_tone_is_enable.setTextColor(reminderModel.isEnableTone ?
                        getResources().getColor(R.color.text_success) : getResources().getColor(R.color.text_danger));

                final TextView tv_gradually_increase_volume = findViewById(R.id.tv_gradually_increase_volume);
                tv_gradually_increase_volume.setText(reminderModel.isIncreaseVolumeGradually() ? "ON" : "OFF");
                tv_gradually_increase_volume.setTextColor(reminderModel.isIncreaseVolumeGradually() ?
                        getResources().getColor(R.color.text_success) : getResources().getColor(R.color.text_danger));

                final TextView tv_alarm_volume = findViewById(R.id.tv_alarm_volume);
                tv_alarm_volume.setText(reminderModel.getAlarmVolumePercentage() == 0 ? "Default" : reminderModel.getAlarmVolumePercentage() + "%");

                final TextView tv_reminder_vibrate = findViewById(R.id.tv_reminder_vibrate);
                tv_reminder_vibrate.setText(reminderModel.isEnableVibration ? "ON" : "OFF");
                tv_reminder_vibrate.setTextColor(reminderModel.isEnableVibration ?
                        getResources().getColor(R.color.text_success) : getResources().getColor(R.color.text_danger));

                tv_reminder_note.setText(reminderModel.getNote());

                final LinearLayout lv_missed_reminders = findViewById(R.id.lv_missed_reminders);

                if (reminderModel.getLastMissedTime() != null) {
                    lv_missed_reminders.setVisibility(View.VISIBLE);
                    final TextView tv_reminder_last_missed_time = findViewById(R.id.tv_reminder_last_missed_time);
                    tv_reminder_last_missed_time.setText(StringHelper.toTimeDate(reminderModel.getLastMissedTime()));
                    tv_reminder_last_missed_time.setVisibility(View.GONE);

                    if (reminderModel.getMissedTimes().size() > 1) {
                        final Spinner spinner_reminder_missed_times = findViewById(R.id.spinner_reminder_missed_times);
                        spinner_reminder_missed_times.setVisibility(View.VISIBLE);

                        ArrayList<String> x = new ArrayList<>(0);
                        for (int s = 0; s < reminderModel.getMissedTimes().size(); s++) {
                            x.add(StringHelper.toTimeDate(reminderModel.getMissedTimes().get(s)));
                        }

                        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, x);
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Setting the ArrayAdapter data on the Spinner
                        spinner_reminder_missed_times.setAdapter(aa);
                    } else {
                        //DUMMY
                        final Spinner spinner_reminder_missed_times = findViewById(R.id.spinner_reminder_missed_times);
                        spinner_reminder_missed_times.setVisibility(View.VISIBLE);
                        Calendar c = Calendar.getInstance();
                        ArrayList<String> x = new ArrayList<>(0);
                        for (int s = 0; s < 20; s++) {
                            c.add(Calendar.MINUTE, 5);
                            x.add(StringHelper.toTimeDate(c.getTime()));
                        }

                        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, x);
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Setting the ArrayAdapter data on the Spinner
                        spinner_reminder_missed_times.setAdapter(aa);
                    }
                }

                //getLastMissedTime


            } else {
                ToastHelper.showLong(ActivityReminderView.this, "Reminder not found!");
                finish();
            }
        } else if (from.equals("MISSED")) {
            Realm r = Realm.getDefaultInstance();
            MissedReminder reminder = r
                    .where(MissedReminder.class)
                    .equalTo("id", id)
                    .findFirst();
            if (reminder != null) {
                tv_reminder_time.setText(StringHelper.toTime(reminder.time));
                tv_reminder_date.setText(StringHelper.toWeekdayDate(reminder.time));
                tv_reminder_name.setText(reminder.name);
                tv_reminder_note.setText(reminder.name);
            } else {
                Toast.makeText(ActivityReminderView.this, "Reminder not found!", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Realm r = Realm.getDefaultInstance();
            DismissedReminder reminder = r
                    .where(DismissedReminder.class)
                    .equalTo("id", id)
                    .findFirst();
            if (reminder != null) {
                tv_reminder_time.setText(StringHelper.toTime(reminder.time));
                tv_reminder_date.setText(StringHelper.toWeekdayDate(reminder.time));
                tv_reminder_name.setText(reminder.name);
                tv_reminder_note.setText(reminder.name);
            } else {
                Toast.makeText(ActivityReminderView.this, "Reminder not found!", Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }
}