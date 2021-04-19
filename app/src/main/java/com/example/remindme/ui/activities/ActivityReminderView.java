package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.R;
import com.example.remindme.dataModels.DismissedReminder;
import com.example.remindme.dataModels.MissedReminder;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.ReminderModel;

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
    private static final String MISSED_ALERT_UI_STATE = "STATE";
    private boolean isMissedAlertsVisible;
    private ImageView btn_expand_missed_alerts;
    private LinearLayout lv_reminder_details;
    //private ScrollView sv_missed_alerts;
    private TextView tv_missed_alerts;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(MISSED_ALERT_UI_STATE, isMissedAlertsVisible);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_view);
        ActivityHelper.setTitle(this, getResources().getString(R.string.view_reminder_heading));

        if (savedInstanceState != null) {
            isMissedAlertsVisible = savedInstanceState.getBoolean(MISSED_ALERT_UI_STATE, false);
        }

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
                        ((TextView) findViewById(R.id.tv_reminder_time)).setText(StringHelper.toTimeWeekdayDate(reminderModel.getOriginalTime()));
                    }
                    reminderModel.trySaveAndSetAlert(getApplicationContext(), true, true);
                }
            }
        });

        lv_reminder_details = findViewById(R.id.lv_reminder_details);
        //sv_missed_alerts = findViewById(R.id.sv_missed_alerts);
        tv_missed_alerts = findViewById(R.id.tv_missed_alerts);

        btn_expand_missed_alerts = findViewById(R.id.btn_expand_missed_alerts);
        btn_expand_missed_alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMissedAlertsVisible = !isMissedAlertsVisible;
                refresh();
            }
        });

        refresh();
    }

    private void refresh() {
        if (isMissedAlertsVisible) {
            btn_expand_missed_alerts.setImageResource(R.drawable.ic_expand_up);
            btn_expand_missed_alerts.setColorFilter(getResources().getColor(R.color.bg_danger), android.graphics.PorterDuff.Mode.SRC_IN);
            lv_reminder_details.setVisibility(View.GONE);
            tv_missed_alerts.setVisibility(View.VISIBLE);
        } else {
            btn_expand_missed_alerts.setImageResource(R.drawable.ic_expand_down);
            btn_expand_missed_alerts.setColorFilter(getResources().getColor(R.color.bg_success), android.graphics.PorterDuff.Mode.SRC_IN);
            tv_missed_alerts.setVisibility(View.GONE);
            lv_reminder_details.setVisibility(View.VISIBLE);
        }
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
                final TextView tv_alarm_tone_is_off = findViewById(R.id.tv_alarm_tone_is_off);
                final LinearLayout lv_alarm_tone_is_on = findViewById(R.id.lv_alarm_tone_is_on);

                if (reminderModel.isEnableTone()) {
                    tv_alarm_tone_is_off.setVisibility(View.GONE);
                    tv_reminder_tone_summary.setVisibility(View.VISIBLE);
                    lv_alarm_tone_is_on.setVisibility(View.VISIBLE);

                    tv_reminder_tone_summary.setText(reminderModel.getRingToneUriSummary(this));

                    final TextView tv_alarm_volume = findViewById(R.id.tv_alarm_volume);
                    tv_alarm_volume.setText(reminderModel.getAlarmVolumePercentage() == 0 ?
                            "Default" : reminderModel.getAlarmVolumePercentage() + "%");

                    final TextView tv_gradually_increase_volume = findViewById(R.id.tv_gradually_increase_volume);
                    tv_gradually_increase_volume.setText(reminderModel.isIncreaseVolumeGradually() ? "ON" : "OFF");
                    tv_gradually_increase_volume.setTextColor(reminderModel.isIncreaseVolumeGradually() ?
                            getResources().getColor(R.color.text_success) : getResources().getColor(R.color.text_danger));

                } else {
                    lv_alarm_tone_is_on.setVisibility(View.GONE);
                    tv_reminder_tone_summary.setVisibility(View.GONE);
                    tv_alarm_tone_is_off.setVisibility(View.VISIBLE);
                }

                final TextView tv_reminder_vibrate = findViewById(R.id.tv_reminder_vibrate);
                tv_reminder_vibrate.setText(reminderModel.isEnableVibration() ? "ON" : "OFF");
                tv_reminder_vibrate.setTextColor(reminderModel.isEnableVibration() ?
                        getResources().getColor(R.color.text_success) : getResources().getColor(R.color.text_danger));

                tv_reminder_note.setText(reminderModel.getNote());

                final LinearLayout lv_missed_reminders = findViewById(R.id.lv_last_missed_alert);

                if (reminderModel.getLastMissedTime() != null) {

                    lv_missed_reminders.setVisibility(View.VISIBLE);
                    final TextView tv_reminder_last_missed_time = findViewById(R.id.tv_reminder_last_missed_time);
                    tv_reminder_last_missed_time.setText(StringHelper.toTimeWeekdayDate(reminderModel.getLastMissedTime()));

                    if (reminderModel.getMissedTimes().size() > 1) {
                        btn_expand_missed_alerts.setVisibility(View.VISIBLE);

                        StringBuilder builder = new StringBuilder();
                        for (int s = 0; s < reminderModel.getMissedTimes().size(); s++) { // Skip last one as that would ve visible as last missed one anyway

                            int index = s + 1;

                            if (index == reminderModel.getMissedTimes().size()) {
                                builder.append("Last  miss on  ");
                            } else {
                                builder.append(index);
                                switch (index) {
                                    default:
                                        builder.append("th  miss on  ");
                                        break;
                                    case 1:
                                        builder.append("st  miss on  ");
                                        break;
                                    case 2:
                                        builder.append("nd  miss on  ");
                                        break;
                                    case 3:
                                        builder.append("rd  miss on  ");
                                        break;
                                }
                            }

                            builder.append(StringHelper.toTimeWeekdayDate(reminderModel.getMissedTimes().get(s)));
                            builder.append("\n");
                        }

                        tv_missed_alerts.setText(builder.toString());
                    }
                }
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