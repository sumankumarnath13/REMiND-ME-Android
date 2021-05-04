package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.ReminderModel;
import com.example.remindme.viewModels.RingingModel;

import java.util.Locale;


public class ActivityReminderView extends BaseActivity {
    private static final String MISSED_ALERT_UI_STATE = "STATE";
    private boolean isMissedAlertsVisible;
    private TextView tv_reminder_time;
    private TextView tv_reminder_date;
    private TextView tv_reminder_name;
    private LinearLayout ly_note_summary_header;
    private TextView tv_reminder_note;
    private ImageView btn_expand_missed_alerts;
    private LinearLayout lv_reminder_details;
    private TextView tv_missed_alerts;
    private SwitchCompat enabled;
    private ReminderModel activeReminder;
    private TextView tv_expired;
    private TextView next_snooze;
    private ImageButton imgBtnShareNote;
    private static final String STATUS_OFF = "OFF";

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
        ly_note_summary_header = findViewById(R.id.ly_note_summary_header);
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
                ReminderModel.setReminderIdInIntent(input_i, activeReminder.getId());
                startActivity(input_i);
                finish();
            }
        });

        enabled = findViewById(R.id.sw_reminder_enabled);
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isUserInteracted()) return;

                if (activeReminder != null && !activeReminder.isExpired()) {

                    if (AppSettingsHelper.getInstance().isDisableAllReminders()) { // Ignore if its blocked globally

                        buttonView.setChecked(false);
                        ToastHelper.showShort(buttonView.getContext(), "All reminders are disabled in settings");

                    } else {

                        if (activeReminder.trySetEnabled(getApplicationContext(), isChecked)) {
                            activeReminder.saveAndSetAlert(ActivityReminderView.this, true);
                        } else {
                            buttonView.setChecked(false);
                        }
                    }
                }
            }
        });

        next_snooze = findViewById(R.id.tv_reminder_next_snooze);
        btn_reminder_dismiss = findViewById(R.id.btn_reminder_dismiss);
        btn_reminder_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeReminder.dismissByUser(ActivityReminderView.this);
                refresh();
            }
        });

        lv_reminder_details = findViewById(R.id.lv_reminder_details);
        tv_missed_alerts = findViewById(R.id.tv_missed_alerts);
        btn_expand_missed_alerts = findViewById(R.id.btn_expand_missed_alerts);
        btn_expand_missed_alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMissedAlertsVisible = !isMissedAlertsVisible;
                setUserInteracted(false); // This is very important. Because its just making a layout visible and is no real interaction.
                refresh();
            }
        });

        tv_expired = findViewById(R.id.tv_expired);

        imgBtnShareNote = findViewById(R.id.imgBtnShareNote);
        imgBtnShareNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.shareText(ActivityReminderView.this, activeReminder.getNote());
            }
        });

        refresh();
    }

    private Button btn_reminder_dismiss;

    @Override
    protected void onUIRefresh() {
        super.onUIRefresh();

        if (activeReminder != null) {

            // If snooze do not exists then show original time|calculated time. Because next schedule may set as calculated time until its being fetched from database
            // Else show just original time
//            if (!activeReminder.getSnoozeModel().isSnoozed()) {
//                tv_reminder_time.setText(StringHelper.toTime(activeReminder.getAlertTime()));
//                tv_reminder_date.setText(StringHelper.toWeekdayDate(activeReminder.getAlertTime()));
//            } else {
//            }

            tv_reminder_time.setText(StringHelper.toTime(activeReminder.getTimeModel().getTime()));
            tv_reminder_date.setText(StringHelper.toWeekdayDate(this, activeReminder.getTimeModel().getTime()));


            if (!StringHelper.isNullOrEmpty(activeReminder.getName())) {
                tv_reminder_name.setVisibility(View.VISIBLE);
                tv_reminder_name.setText(activeReminder.getName());
            }

            if (activeReminder.getSnoozeModel().isSnoozed()) {
                next_snooze.setVisibility(View.VISIBLE);
                next_snooze.setText(StringHelper.toTime(
                        activeReminder.getSnoozeModel().getSnoozedTime(
                                activeReminder.getTimeModel().getTime())));

                btn_reminder_dismiss.setVisibility(View.VISIBLE);
            } else {
                next_snooze.setVisibility(View.GONE);
                btn_reminder_dismiss.setVisibility(View.GONE);
            }

            if (activeReminder.isExpired()) {
                enabled.setVisibility(View.GONE);
                tv_expired.setVisibility(View.VISIBLE);
            } else {
                tv_expired.setVisibility(View.GONE);
                enabled.setVisibility(View.VISIBLE);
                enabled.setChecked(activeReminder.isEnabled() && !AppSettingsHelper.getInstance().isDisableAllReminders());
            }

            final TextView tv_reminder_snooze_summary = findViewById(R.id.tv_reminder_snooze_summary);
            tv_reminder_snooze_summary.setText(activeReminder.getSnoozeModel().toString());

            final TextView tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);
            tv_reminder_repeat_summary.setText(activeReminder.getRepeatModel().toString(this));

            final TextView tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
            final LinearLayout lv_alarm_tone_is_on = findViewById(R.id.lv_alarm_tone_is_on);

            if (activeReminder.getRingingModel().isToneEnabled()) {

                lv_alarm_tone_is_on.setVisibility(View.VISIBLE);
                tv_reminder_tone_summary.setText(activeReminder.getRingingModel().getRingToneUriSummary(this));

                final TextView tv_alarm_volume = findViewById(R.id.tv_alarm_volume);

                if (activeReminder.getRingingModel().isIncreaseVolumeGradually()) {

                    tv_alarm_volume.setText(String.format(Locale.ENGLISH, "%d%% to %s",
                            RingingModel.MINIMUM_INPUT_VOLUME_PERCENTAGE,
                            activeReminder.getRingingModel().getAlarmVolumePercentage() == 0 ?
                                    "default" : activeReminder.getRingingModel().getAlarmVolumePercentage() + "%"
                    ));

                } else {

                    tv_alarm_volume.setText(String.format(Locale.ENGLISH, "%s",
                            activeReminder.getRingingModel().getAlarmVolumePercentage() == 0 ?
                                    "default" : activeReminder.getRingingModel().getAlarmVolumePercentage() + "%")
                    );
                }

            } else {
                lv_alarm_tone_is_on.setVisibility(View.GONE);
                tv_reminder_tone_summary.setText(STATUS_OFF);
            }

            tv_reminder_tone_summary.setTextColor(activeReminder.getRingingModel().isToneEnabled() ?
                    getResources().getColor(R.color.text_dim) : getResources().getColor(R.color.text_danger));

            final TextView tv_reminder_vibrate = findViewById(R.id.tv_reminder_vibrate);

            if (activeReminder.getRingingModel().isVibrationEnabled()) {
                tv_reminder_vibrate.setText(getResources().getStringArray(R.array.vibration_patterns)[RingingModel.convertToVibratePattern(activeReminder.getRingingModel().getVibratePattern())]);
            } else {
                tv_reminder_vibrate.setText(STATUS_OFF);
            }

            tv_reminder_vibrate.setTextColor(activeReminder.getRingingModel().isVibrationEnabled() ?
                    getResources().getColor(R.color.text_dim) : getResources().getColor(R.color.text_danger));

            if (!StringHelper.isNullOrEmpty(activeReminder.getNote())) {
                ly_note_summary_header.setVisibility(View.VISIBLE);
                tv_reminder_note.setVisibility(View.VISIBLE);
                tv_reminder_note.setText(activeReminder.getNote());
            } else {
                ly_note_summary_header.setVisibility(View.GONE);
                tv_reminder_note.setVisibility(View.GONE);
            }

            final LinearLayout lv_last_missed_alert = findViewById(R.id.lv_last_missed_alert);


            if (activeReminder.getLastMissedTime() != null) {
                lv_last_missed_alert.setVisibility(View.VISIBLE);
                final TextView tv_reminder_last_missed_time = findViewById(R.id.tv_reminder_last_missed_time);
                tv_reminder_last_missed_time.setText(StringHelper.toTimeWeekdayDate(this, activeReminder.getLastMissedTime()));

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

                        builder.append(StringHelper.toTimeWeekdayDate(this, activeReminder.getMissedTimes().get(s)));
                        builder.append("\n");
                    }

                    tv_missed_alerts.setText(builder.toString());
                }

            } else {
                lv_last_missed_alert.setVisibility(View.GONE);
                isMissedAlertsVisible = false; // This will let "tv_missed_alerts" hide as well using the "else" part below
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
    }
}