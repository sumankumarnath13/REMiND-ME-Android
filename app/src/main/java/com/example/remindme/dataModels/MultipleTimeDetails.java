package com.example.remindme.dataModels;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class MultipleTimeDetails extends RealmObject implements RealmModel {
    public RealmList<Date> customTimes = new RealmList<>();
    public RealmList<Integer> hourlyTimes = new RealmList<>();
    public int hourInterval;
    public int minuteInterVal;
}
