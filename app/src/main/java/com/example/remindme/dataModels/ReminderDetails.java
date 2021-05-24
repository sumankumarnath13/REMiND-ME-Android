package com.example.remindme.dataModels;

import java.util.Date;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class ReminderDetails extends RealmObject implements RealmModel {
    public Date time;
    public boolean isCompleted;
}
