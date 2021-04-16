package com.example.remindme.ui.activities;

import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.ToastHelper;

public class ActivitySettings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActivityHelper.setTitle(this, getResources().getString(R.string.settings_heading));

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
    }
}