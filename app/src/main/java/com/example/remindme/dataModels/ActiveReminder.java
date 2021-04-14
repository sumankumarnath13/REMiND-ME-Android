package com.example.remindme.dataModels;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ActiveReminder extends RealmObject implements RealmModel {
    @PrimaryKey
    public String id;
    public int alarmIntentId;
    public String name;
    public String note;
    public Date time;
    public String selectedAlarmTone;
    public Date nextSnoozeTime;
    public boolean isEnableTone;
    public boolean isEnable;
    public boolean isVibrate;
    public boolean increaseVolumeGradually;
    public int alarmVolume;

    public int repeatOption;
    public RealmList<Integer> repeatHours = new RealmList<>();
    public RealmList<Integer> repeatDays = new RealmList<>();
    public RealmList<Integer> repeatWeeks = new RealmList<>();
    public RealmList<Integer> repeatMonths = new RealmList<>();
    public int customTimeUnit;
    public int customTimeValue;
    public boolean isHasRepeatEnd;
    public Date repeatEndDate;

    public boolean isSnoozeEnable;
    public int snoozeInterval;
    public int snoozeLength;
    public int snoozeCount;

}
