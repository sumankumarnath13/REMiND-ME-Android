package com.example.remindme.controllers;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.example.remindme.helpers.OsHelper;
import com.example.remindme.viewModels.ReminderModel;

public class RingingController {
    private boolean isRinging = false;
    private Vibrator vibrator;
    private boolean isVibrating = false;
    private Ringtone playingRingtone;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private int audioFocusGrantStatus = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    private boolean isAudioFocusRequested = false;

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            }
        }
    };

    public void startTone(Context context, Uri ringToneUri, boolean isGraduallyIncreaseVolume, int maxVolume) {
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        if (audioManager != null) {
            if (ringToneUri == null) {
                ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }

            playingRingtone = RingtoneManager.getRingtone(context, ringToneUri);

            if (playingRingtone != null) {
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
                    playingRingtone.play();
                    isRinging = true;
                }
            }
        }

        startVibrating(context);
    }

    public void startVibrating(Context context) {
        if (vibrator == null) {
            vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        }

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

}
