package com.example.remindme.dataModels;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class ReminderSnooze extends RealmObject implements RealmModel {
    public int snoozeInterval;
    public int snoozeLimit;
    public int snoozeCount;
}
