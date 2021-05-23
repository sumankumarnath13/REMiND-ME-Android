package com.example.remindme.dataModels;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Reminder extends RealmObject implements RealmModel {
    @PrimaryKey
    public String id;
    @Index
    public boolean isExpired;

    public boolean isEnabled;
    public boolean isNotification;
    public int alarmIntentId;
    public String name;
    public String note;
    public Date time;
    public int timeListMode;
    public RealmList<Date> customTimes = new RealmList<>();
    public RealmList<Integer> hourlyTimes = new RealmList<>();
    public RealmList<Date> missedTimes = new RealmList<>();
    public String selectedAlarmTone;
    public boolean isToneEnabled;
    public boolean isVibrate;
    public int vibratePattern;
    public boolean isIncreaseVolumeGradually;
    public int alarmVolume;
    public int ringDurationInMin;
    public boolean isRepeatEnabled;
    public int repeatOption;
    public RealmList<Integer> repeatDays = new RealmList<>();
    public RealmList<Integer> repeatWeeks = new RealmList<>();
    public RealmList<Integer> repeatMonths = new RealmList<>();
    public int repeatCustomTimeUnit;
    public int repeatCustomTimeValue;
    public boolean isHasRepeatEnd;
    public Date repeatEndDate;
    public boolean snoozeEnabled;
    public int snoozeInterval;
    public int snoozeLimit;
    public int snoozeCount;

}
