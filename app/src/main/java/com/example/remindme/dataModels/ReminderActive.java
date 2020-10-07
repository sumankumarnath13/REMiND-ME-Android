package com.example.remindme.dataModels;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReminderActive extends RealmObject implements RealmModel {
    @PrimaryKey
    public int id;
    public String name;
    public String note;
    public int next_snooze_id;
    public boolean enabled;
}
