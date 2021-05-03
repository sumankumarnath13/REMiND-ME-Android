package com.example.remindme.viewModels;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.remindme.helpers.StringHelper;

public class RingingModel {

    public static final int MINIMUM_INPUT_VOLUME_PERCENTAGE = 10;
    public static final int MAX_RING_DURATION = 1000 * 60 * convertToAlarmRingDuration(AlarmRingDurations.THREE_MINUTE) + 1000; // 1 sec extra if it takes some more time to close and clean everything.
    private static final long[] VIBRATE_PATTERN_BASIC = {500, 500};
    private static final long[] VIBRATE_PATTERN_HEARTBEAT = {500, 500, 117};
    private static final long[] VIBRATE_PATTERN_TICKTOCK = {500, 300, 411};
    private static final long[] VIBRATE_PATTERN_WALTZ = {500, 500, 300, 300, 300, 117};
    private static final long[] VIBRATE_PATTERN_ZIG_ZIG_ZIG = {500, 300, 300, 300};

    private static final int LONG = 0;
    private static final int HEARTBEAT = 1;
    private static final int TICKTOCK = 2;
    private static final int WALTZ = 3;
    private static final int ZIG_ZIG_ZIG = 4;


    private boolean isToneEnabled = true;

    public boolean isToneEnabled() {
        return isToneEnabled;
    }

    private Uri ringToneUri = null;

    public Uri getRingToneUri() {
        return ringToneUri;
    }

    public String getRingToneUriSummary(Context context) {
        Uri alarmToneUri = getRingToneUri();

        if (alarmToneUri == null) {
            alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmToneUri);
        return ringtone.getTitle(context);
    }

    public void setDefaultRingTone() {
        ringToneUri = null;
    }

    public void setRingToneUri(Uri value) {
        ringToneUri = value;
    }

    public void setRingToneUri(String value) {
        if (!StringHelper.isNullOrEmpty(value)) {
            setRingToneUri(Uri.parse(value));
        }
    }

    private int alarmVolumePercentage;

    public int getAlarmVolumePercentage() {
        return alarmVolumePercentage;
    }

    public void setAlarmVolumePercentage(int value) {
        alarmVolumePercentage = Math.min(Math.max(value, 0), 100);
    }

    private AlarmRingDurations ringDuration = AlarmRingDurations.ONE_MINUTE;

    public AlarmRingDurations getAlarmRingDuration() {
        return ringDuration;
    }

    public void setAlarmRingDuration(AlarmRingDurations value) {
        ringDuration = value;
    }

    public void setToneEnabled(boolean value) {
        isToneEnabled = value;
    }

    private boolean isIncreaseVolumeGradually;

    public boolean isIncreaseVolumeGradually() {
        return this.isIncreaseVolumeGradually;
    }

    public void setIncreaseVolumeGradually(boolean value) {
        isIncreaseVolumeGradually = value;
    }

    private boolean isVibrationEnabled = true;

    public boolean isVibrationEnabled() {
        return isVibrationEnabled;
    }

    public void setVibrationEnabled(boolean value) {
        isVibrationEnabled = value;
    }

    private VibratePatterns vibratePattern = VibratePatterns.LONG;

    public VibratePatterns getVibratePattern() {
        return vibratePattern;
    }

    public void setVibratePattern(VibratePatterns value) {
        vibratePattern = value;
    }

    public static AlarmRingDurations convertToAlarmRingDuration(int minute) {
        switch (minute) {
            default:
            case 0:
                return AlarmRingDurations.ONE_MINUTE;
            case 1:
                return AlarmRingDurations.TWO_MINUTE;
            case 2:
                return AlarmRingDurations.THREE_MINUTE;
        }
    }

    public static int convertToAlarmRingDuration(AlarmRingDurations duration) {
        switch (duration) {
            default:
            case ONE_MINUTE:
                return 0;
            case TWO_MINUTE:
                return 1;
            case THREE_MINUTE:
                return 2;
        }
    }

    public static VibratePatterns convertToVibratePattern(int value) {
        switch (value) {
            default:
            case LONG:
                return VibratePatterns.LONG;
            case HEARTBEAT:
                return VibratePatterns.HEARTBEAT;
            case TICKTOCK:
                return VibratePatterns.TICKTOCK;
            case WALTZ:
                return VibratePatterns.WALTZ;
            case ZIG_ZIG_ZIG:
                return VibratePatterns.ZIG_ZIG_ZIG;
        }
    }

    public static int convertToVibratePattern(VibratePatterns pattern) {
        switch (pattern) {
            default:
            case LONG:
                return LONG;
            case HEARTBEAT:
                return HEARTBEAT;
            case TICKTOCK:
                return TICKTOCK;
            case WALTZ:
                return WALTZ;
            case ZIG_ZIG_ZIG:
                return ZIG_ZIG_ZIG;
        }
    }

    public static long[] convertToVibrateFrequency(VibratePatterns pattern) {
        switch (pattern) {
            default:
            case LONG:
                return VIBRATE_PATTERN_BASIC;
            case HEARTBEAT:
                return VIBRATE_PATTERN_HEARTBEAT;
            case TICKTOCK:
                return VIBRATE_PATTERN_TICKTOCK;
            case WALTZ:
                return VIBRATE_PATTERN_WALTZ;
            case ZIG_ZIG_ZIG:
                return VIBRATE_PATTERN_ZIG_ZIG_ZIG;
        }
    }

    public enum AlarmRingDurations {
        ONE_MINUTE,
        TWO_MINUTE,
        THREE_MINUTE,
    }

    public enum VibratePatterns {
        LONG,
        HEARTBEAT,
        TICKTOCK,
        WALTZ,
        ZIG_ZIG_ZIG
    }

}
