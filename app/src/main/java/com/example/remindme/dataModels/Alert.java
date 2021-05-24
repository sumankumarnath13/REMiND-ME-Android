package com.example.remindme.dataModels;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Alert extends RealmObject implements RealmModel {
    @PrimaryKey
    public String id;
    @Index
    public boolean isExpired;
    public boolean isEnabled;
    public int alarmIntentId;
    public String name;
    public String note;
    public Date time;
    public int timeListMode;
    public MultipleTimeDetails multipleTimeDetails;
    //    public RealmList<Date> customTimes = new RealmList<>();
//    public RealmList<Integer> hourlyTimes = new RealmList<>();
    public ReminderRepeat repeat;
    public RealmList<Date> missedTimes = new RealmList<>();

    public AlarmDetails alarmDetails;
    public ReminderDetails reminderDetails;
}
