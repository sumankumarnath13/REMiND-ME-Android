package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.ReminderModel;


public class ActivityReminderView extends AppCompatActivity {
    private static final String MISSED_ALERT_UI_STATE = "STATE";
    private boolean isMissedAlertsVisible;

    private TextView tv_reminder_time;
    private TextView tv_reminder_date;
    private TextView tv_reminder_name;
    private TextView tv_reminder_note;
    private ImageView btn_expand_missed_alerts;
    private LinearLayout lv_reminder_details;
    private TextView tv_missed_alerts;
    private SwitchCompat enabled;
    private ReminderModel activeReminder;

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

        Intent intent = getIntent();
        if (intent == null) {
            ToastHelper.showLong(ActivityReminderView.this, "Reminder not found");
            finish();
            return;
        }

        activeReminder = ReminderModel.getInstance(getIntent());

        if (activeReminder == null) {
            ToastHelper.showLong(ActivityReminderView.this, "Reminder not found");
            finish();
            return;
        }

        tv_reminder_time = findViewById(R.id.tv_reminder_time);
        tv_reminder_date = findViewById(R.id.tv_reminder_date);
        tv_reminder_name = findViewById(R.id.tv_reminder_name);
        tv_reminder_note = findViewById(R.id.tv_reminder_note);

        final Button btnDelete = findViewById(R.id.btn_reminder_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeReminder.deleteAndCancelAlert(getApplicationContext());
                finish();
            }
        });

        final Button btnChange = findViewById(R.id.btn_reminder_edit);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent input_i = new Intent(getApplicationContext(), ActivityReminderInput.class);
                ReminderModel.setReminderId(input_i, activeReminder.getId());
                startActivity(input_i);
                finish();
            }
        });

        enabled = findViewById(R.id.sw_reminder_enabled);
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isRefreshing) return;

                if (activeReminder != null && !activeReminder.isExpired()) {

                    if (AppSettingsHelper.getInstance().isDisableAllReminders()) { // Ignore if its blocked globally

                        buttonView.setChecked(false);
                        ToastHelper.showShort(buttonView.getContext(), "All reminders are disabled in settings");

                    } else {

                        if (activeReminder.trySetEnabled(getApplicationContext(), isChecked)) {
                            activeReminder.trySaveAndSetAlert(ActivityReminderView.this, true, true);
                        }
                    }
                }
            }
        });

        lv_reminder_details = findViewById(R.id.lv_reminder_details);
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

    private boolean isRefreshing;

    private void refresh() {

        isRefreshing = true;

        if (activeReminder != null) {

            tv_reminder_time.setText(StringHelper.toTime(activeReminder.getOriginalTime()));
            tv_reminder_date.setText(StringHelper.toWeekdayDate(activeReminder.getOriginalTime()));

            if (!StringHelper.isNullOrEmpty(activeReminder.getName())) {
                tv_reminder_name.setVisibility(View.VISIBLE);
                tv_reminder_name.setText(activeReminder.getName());
            }

            if (activeReminder.getNextSnoozeOffTime() != null) {
                final TextView next_snooze = findViewById(R.id.tv_reminder_next_snooze);
                next_snooze.setText(StringHelper.toTime(activeReminder.getNextSnoozeOffTime()));
            }

            if (activeReminder.isExpired()) {
                enabled.setVisibility(View.GONE);
            } else {
                enabled.setVisibility(View.VISIBLE);
                enabled.setChecked(activeReminder.isEnabled() && !AppSettingsHelper.getInstance().isDisableAllReminders());
            }

            final TextView tv_reminder_snooze_summary = findViewById(R.id.tv_reminder_snooze_summary);
            tv_reminder_snooze_summary.setText(activeReminder.getSnoozeModel().toString());

            final TextView tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);
            tv_reminder_repeat_summary.setText(activeReminder.getRepeatSettingString());

            final TextView tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
            final TextView tv_alarm_tone_is_off = findViewById(R.id.tv_alarm_tone_is_off);
            final LinearLayout lv_alarm_tone_is_on = findViewById(R.id.lv_alarm_tone_is_on);

            if (activeReminder.isEnableTone()) {

                tv_alarm_tone_is_off.setVisibility(View.GONE);
                tv_reminder_tone_summary.setVisibility(View.VISIBLE);
                lv_alarm_tone_is_on.setVisibility(View.VISIBLE);

                tv_reminder_tone_summary.setText(activeReminder.getRingToneUriSummary(this));

                final TextView tv_alarm_volume = findViewById(R.id.tv_alarm_volume);
                tv_alarm_volume.setText(activeReminder.getAlarmVolumePercentage() == 0 ?
                        "Default" : activeReminder.getAlarmVolumePercentage() + "%");

                final TextView tv_gradually_increase_volume = findViewById(R.id.tv_gradually_increase_volume);
                tv_gradually_increase_volume.setText(activeReminder.isIncreaseVolumeGradually() ? "ON" : "OFF");
                tv_gradually_increase_volume.setTextColor(activeReminder.isIncreaseVolumeGradually() ?
                        getResources().getColor(R.color.text_success) : getResources().getColor(R.color.text_danger));

            } else {

                lv_alarm_tone_is_on.setVisibility(View.GONE);
                tv_reminder_tone_summary.setVisibility(View.GONE);
                tv_alarm_tone_is_off.setVisibility(View.VISIBLE);

            }

            final TextView tv_reminder_vibrate = findViewById(R.id.tv_reminder_vibrate);
            tv_reminder_vibrate.setText(activeReminder.isEnableVibration() ? "ON" : "OFF");
            tv_reminder_vibrate.setTextColor(activeReminder.isEnableVibration() ?
                    getResources().getColor(R.color.text_success) : getResources().getColor(R.color.text_danger));

            tv_reminder_note.setText(activeReminder.getNote());

            final LinearLayout lv_missed_reminders = findViewById(R.id.lv_last_missed_alert);

            if (activeReminder.getLastMissedTime() != null) {

                lv_missed_reminders.setVisibility(View.VISIBLE);
                final TextView tv_reminder_last_missed_time = findViewById(R.id.tv_reminder_last_missed_time);
                tv_reminder_last_missed_time.setText(StringHelper.toTimeWeekdayDate(activeReminder.getLastMissedTime()));

                if (activeReminder.getMissedTimes().size() > 1) {
                    btn_expand_missed_alerts.setVisibility(View.VISIBLE);

                    StringBuilder builder = new StringBuilder();
                    for (int s = 0; s < activeReminder.getMissedTimes().size(); s++) { // Skip last one as that would ve visible as last missed one anyway

                        int index = s + 1;

                        if (index == activeReminder.getMissedTimes().size()) {
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

                        builder.append(StringHelper.toTimeWeekdayDate(activeReminder.getMissedTimes().get(s)));
                        builder.append("\n");
                    }

                    tv_missed_alerts.setText(builder.toString());
                }
            }

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

        isRefreshing = false;
    }
}