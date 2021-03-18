package com.example.remindme.dataModels;

import java.util.Date;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DismissedReminder extends RealmObject implements RealmModel {
    @PrimaryKey
    public String id;
    public Date time;
    public String name;
    public String note;
}


