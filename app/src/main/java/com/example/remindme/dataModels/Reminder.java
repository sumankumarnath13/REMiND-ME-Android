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
    public boolean expired;
    public int alarmIntentId;
    public String name;
    public String note;
    public Date time;
    public Date lastMissedTime;
    public RealmList<Date> missedTimes;
    public String selectedAlarmTone;
    public Date nextSnoozeTime;
    public boolean isEnableTone;
    public boolean enabled;
    public boolean vibrate;
    public int vibrationPattern;
    public boolean increaseVolumeGradually;
    public int alarmVolume;
    public int ringDurationInMin;

    public int repeatOption;
    public RealmList<Integer> repeatHours = new RealmList<>();
    public RealmList<Integer> repeatDays = new RealmList<>();
    public RealmList<Integer> repeatWeeks = new RealmList<>();
    public RealmList<Integer> repeatMonths = new RealmList<>();
    public int customTimeUnit;
    public int customTimeValue;
    public boolean hasRepeatEnd;
    public Date repeatEndDate;

    public boolean snoozeEnabled;
    public int snoozeInterval;
    public int snoozeLength;
    public int snoozeCount;


}
