package com.example.remindme.ui.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.R;
import com.example.remindme.dataModels.Reminder;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.ReminderModel;

import java.util.List;

public class ActivitySettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActivityHelper.setTitle(this, getResources().getString(R.string.activitySettingsTitle));

        final AppSettingsHelper settingsHelper = AppSettingsHelper.getInstance();

        final AudioManager audioManager = OsHelper.getAudioManager(this);
        final SeekBar seeker_alarm_stream_volume = findViewById(R.id.seeker_alarm_stream_volume);
        seeker_alarm_stream_volume.setProgress(OsHelper.getAlarmVolumeInPercentage(OsHelper.getAudioManager(this)));
        seeker_alarm_stream_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                OsHelper.setAlarmVolumeInPercentage(audioManager, seekBar.getProgress());
                ToastHelper.showShort(ActivitySettings.this, "Alarm volume is set to " + seekBar.getProgress() + "%");
            }
        });

        final SwitchCompat sw_disable_all_reminders = findViewById(R.id.sw_disable_all_reminders);
        sw_disable_all_reminders.setChecked(settingsHelper.isDisableAllReminders());
        sw_disable_all_reminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsHelper.setDisableAllReminders(isChecked);
                List<Reminder> list = ReminderModel.getActiveReminders(null);
                if (isChecked) {
                    for (int i = 0; i < list.size(); i++) {
                        ReminderModel reminderModel = ReminderModel.getInstance(list.get(i));
                        reminderModel.trySetEnabled(ActivitySettings.this, false);
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        ReminderModel reminderModel = ReminderModel.getInstance(list.get(i));
                        if (reminderModel.isEnabled()) {
                            reminderModel.trySetEnabled(ActivitySettings.this, true);
                            reminderModel.trySaveAndSetAlert(ActivitySettings.this, true, false);
                        }
                    }
                }
            }
        });

        final SwitchCompat sw_use_24_hour = findViewById(R.id.sw_use_24_hour);
        sw_use_24_hour.setChecked(settingsHelper.isUse24hourTime());
        sw_use_24_hour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsHelper.setUse24hourTime(isChecked);
            }
        });

        final Button btn_os_setup_faqs = findViewById(R.id.btn_os_setup_faqs);
        btn_os_setup_faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent faqIntent = new Intent(Intent.ACTION_VIEW);
                faqIntent.setData(Uri.parse("https://www.google.co.in"));
                startActivity(faqIntent);
            }
        });

        final TextView tv_active_reminder_count = findViewById(R.id.tv_active_reminder_count);

        int x = ReminderModel.getActiveReminders(null).size();

        tv_active_reminder_count.setText(Integer.toString(x));

        final TextView tv_expired_reminder_count = findViewById(R.id.tv_expired_reminder_count);

        int y = ReminderModel.getDismissedReminders(null).size();

        tv_expired_reminder_count.setText(Integer.toString(y));
    }
}