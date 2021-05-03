package com.example.remindme.dataModels;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AppSetting extends RealmObject implements RealmModel {
    @PrimaryKey
    public String id;
    public boolean disableAllReminders;
    public boolean use24hourTime;
    public int firstDayOfWeek;
    public String dateFormat;
    public int theme;
}
