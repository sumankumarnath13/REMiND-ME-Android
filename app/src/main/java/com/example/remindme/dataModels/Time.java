package com.example.remindme.dataModels;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class Time extends RealmObject implements RealmModel {
    public int hour;
    public int minute;

}
