package com.example.remindme.dataModels;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class AlarmDetails extends RealmObject implements RealmModel {
    public String selectedAlarmTone;
    public boolean isToneEnabled;
    public boolean isVibrate;
    public int vibratePattern;
    public boolean isIncreaseVolumeGradually;
    public int alarmVolume;
    public int ringDurationInMin;
    public ReminderSnooze snooze;
}
