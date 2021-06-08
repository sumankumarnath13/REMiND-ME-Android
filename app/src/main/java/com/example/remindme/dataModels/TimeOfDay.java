package com.example.remindme.dataModels;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class TimeOfDay extends RealmObject implements RealmModel {
    public int hourOfDay;
    public int minute;
}
