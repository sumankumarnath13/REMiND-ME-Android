package com.example.remindme.controllers;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;

import com.example.remindme.helpers.OsHelper;
import com.example.remindme.viewModels.ReminderModel;

public class RingingController {
    private static final int volumeIncreaseInterval = 3000;
    private boolean isRinging = false;
    private Vibrator vibrator;
    private boolean isVibrating = false;
    private Ringtone playingRingtone;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private int audioFocusGrantStatus = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    private boolean isAudioFocusRequested = false;
    private int deviceVolumeIndex = 0;
    private boolean isOriginalVolumeAltered;
    private int alarmVolumeIndex = 0;
    private int ringingVolumeIndex = 0;
    private boolean isIncreaseVolume;

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            }
        }
    };

    private final CountDownTimer volumeUpTimer = new CountDownTimer(Integer.MAX_VALUE, volumeIncreaseInterval) {
        @Override
        public void onTick(long millisUntilFinished) {
            ringingVolumeIndex++;
            if (ringingVolumeIndex > alarmVolumeIndex) {
                volumeUpTimer.cancel();
                return;
            }

            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, ringingVolumeIndex, 0);
        }

        @Override
        public void onFinish() {

        }
    };

    public RingingController(Context context, Uri ringToneUri) {
        audioManager = OsHelper.getAudioManager(context);
        vibrator = OsHelper.getVibrator(context);
        if (ringToneUri == null) {
            ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        playingRingtone = RingtoneManager.getRingtone(context, ringToneUri);
    }

    private int getVolumeFromPercentage(int percentValue) {
        return (int) ((OsHelper.getMaxAlarmVolume(audioManager) / 100F) * percentValue);
    }

    public void startTone(boolean isGraduallyIncreaseVolume, int alarmVolumePercentage) {
        isIncreaseVolume = isGraduallyIncreaseVolume;

        if (audioManager != null && playingRingtone != null) {
            playingRingtone.setStreamType(AudioManager.STREAM_ALARM);
            if (OsHelper.isOreoOrLater()) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                        .setAudioAttributes(audioAttributes)
                        .setOnAudioFocusChangeListener(audioFocusChangeListener).build();

                if (audioFocusRequest != null) {
                    audioFocusGrantStatus = audioManager.requestAudioFocus(audioFocusRequest);
                    isAudioFocusRequested = true;
                }
            } else {
                audioFocusGrantStatus = audioManager.requestAudioFocus(audioFocusChangeListener,
                        AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                isAudioFocusRequested = true;
            }

            if (isAudioFocusRequested && audioFocusGrantStatus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (alarmVolumePercentage > 0) {
                    deviceVolumeIndex = OsHelper.getAlarmVolume(audioManager);
                    final int possibleMaxVolume = OsHelper.getMaxAlarmVolume(audioManager);
                    final int possibleMinVolume = OsHelper.getMinAlarmVolume(audioManager);

                    alarmVolumeIndex = Math.max(Math.min(getVolumeFromPercentage(alarmVolumePercentage), possibleMaxVolume), possibleMinVolume);
                    isOriginalVolumeAltered = true;

                    if (isIncreaseVolume) {

                        ringingVolumeIndex = Math.max(possibleMinVolume, Math.min(1, possibleMaxVolume));

                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, ringingVolumeIndex, 0);
                        playingRingtone.play();
                        isRinging = true;

                        volumeUpTimer.start();

                    } else {

                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolumeIndex, 0);
                        playingRingtone.play();
                        isRinging = true;

                    }
                } else {

                    playingRingtone.play();
                    isRinging = true;

                }
            }
        }

        startVibrating();

    }

    public void startVibrating() {
        if (vibrator != null) {
            if (OsHelper.isLollipopOrLater()) {
                vibrator.vibrate(ReminderModel.VIBRATE_PATTERN, 0, new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
            } else {
                vibrator.vibrate(ReminderModel.VIBRATE_PATTERN, 0);
            }
            isVibrating = true;
        }
    }

    public void stopRinging() {
        if (isRinging) {

            playingRingtone.stop();

            isRinging = false;

            if (isIncreaseVolume) {
                volumeUpTimer.cancel();
            }

            if (isOriginalVolumeAltered) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, deviceVolumeIndex, 0);
                isOriginalVolumeAltered = false;
            }

            if (OsHelper.isOreoOrLater()) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            } else {
                audioManager.abandonAudioFocus(audioFocusChangeListener);
            }

            isAudioFocusRequested = false;
        }

        if (vibrator != null && isVibrating) {
            vibrator.cancel();
        }
    }

    public void setAlarmVolume(int percentage) {
        //int value = getVolumeFromPercentage(percentage);
        //Values must not exceed maximum or go below minimum:
        final int possibleMaxVolume = OsHelper.getMaxAlarmVolume(audioManager);
        final int possibleMinVolume = OsHelper.getMinAlarmVolume(audioManager);

        int value = Math.max(Math.min(getVolumeFromPercentage(percentage), possibleMaxVolume), possibleMinVolume);

        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, value, 0);
    }

}
