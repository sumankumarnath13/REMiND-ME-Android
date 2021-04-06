package com.example.remindme.helpers;

import android.app.AlarmManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;

public class OsHelper {

    public static boolean isLollipopOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isMarshmallowOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isOreoMr1OrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1;
    }

    public static boolean isOreoOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean isPieOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    public static boolean isInteractive(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (isLollipopOrLater()) {
            return powerManager.isInteractive();
        } else {
            return false;
        }
    }

    public static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static Vibrator getVibrator(Context context) {
        return ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
    }

    public static int getMaxAlarmVolume(Context context) {
        final AudioManager audioManager = getAudioManager(context);
        if (audioManager == null) {
            return 0;
        } else {
            return getAudioManager(context).getStreamMaxVolume(AudioManager.STREAM_ALARM);
        }
    }

    public static int getMinAlarmVolume(Context context) {
        final AudioManager audioManager = getAudioManager(context);
        if (audioManager == null) {
            return 0;
        } else {
            if (isPieOrLater()) {
                return getAudioManager(context).getStreamMinVolume(AudioManager.STREAM_ALARM);
            } else {
                return 0;
            }
        }
    }

    public static int getAlarmVolume(Context context) {
        final AudioManager audioManager = getAudioManager(context);
        if (audioManager == null) {
            return 0;
        } else {
            return getAudioManager(context).getStreamVolume(AudioManager.STREAM_ALARM);
        }
    }
}
