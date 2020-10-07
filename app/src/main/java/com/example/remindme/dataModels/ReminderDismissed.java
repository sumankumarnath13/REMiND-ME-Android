package com.example.remindme.dataModels;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReminderDismissed extends RealmObject implements RealmModel {
    @PrimaryKey
    public int id = 0;
    public String name = null;
    public String note = null;
}


