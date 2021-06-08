package com.example.remindme.dataModels;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class TimelyRepeat extends RealmObject implements RealmModel {
    public RealmList<TimeOfDay> customTimes = new RealmList<>();
    public int hourInterval;
    public int minuteInterVal;
}
