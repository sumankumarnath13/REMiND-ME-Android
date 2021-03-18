package com.example.remindme.dataModels;

import java.util.Date;

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
    public String selectedAlarmTone = null;
    public Date nextSnoozeTime = null;
    public boolean isEnableTone = true;
    public boolean isEnable = true;
    public boolean isVibrate;

    public int repeatOption;
    public boolean isRepeatOn_Sun;
    public boolean isRepeatOn_Mon;
    public boolean isRepeatOn_Tue;
    public boolean isRepeatOn_Wed;
    public boolean isRepeatOn_Thu;
    public boolean isRepeatOn_Fri;
    public boolean isRepeatOn_Sat;

    public boolean isRepeatOn_Jan;
    public boolean isRepeatOn_Feb;
    public boolean isRepeatOn_Mar;
    public boolean isRepeatOn_Apr;
    public boolean isRepeatOn_May;
    public boolean isRepeatOn_Jun;
    public boolean isRepeatOn_Jul;
    public boolean isRepeatOn_Aug;
    public boolean isRepeatOn_Sep;
    public boolean isRepeatOn_Oct;
    public boolean isRepeatOn_Nov;
    public boolean isRepeatOn_Dec;

    public boolean isSnoozeEnable;
    public int snoozeInterval;
    public int snoozeLength;
    public int snoozeCount;

}
