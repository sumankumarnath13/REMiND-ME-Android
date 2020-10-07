package com.example.remindme.dataModels;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReminderMissed extends RealmObject {
    @PrimaryKey
    public int id;
    public String name;
    public String note;
}
