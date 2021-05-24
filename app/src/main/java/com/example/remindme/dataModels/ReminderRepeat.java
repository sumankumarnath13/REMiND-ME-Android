package com.example.remindme.dataModels;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class ReminderRepeat extends RealmObject implements RealmModel {
    public boolean isRepeatEnabled;
    public int repeatOption;
    public RealmList<Integer> repeatDays = new RealmList<>();
    public RealmList<Integer> repeatWeeks = new RealmList<>();
    public RealmList<Integer> repeatMonths = new RealmList<>();
    public int repeatCustomTimeUnit;
    public int repeatCustomTimeValue;
    public boolean isHasRepeatEnd;
    public Date repeatEndDate;

}
