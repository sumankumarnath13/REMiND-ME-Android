package com.example.remindme.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.remindme.viewModels.ReminderModel;

public class AlertService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return ReminderModel.onServiceBind(this, intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return ReminderModel.onServiceStart(this, intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ReminderModel.onServiceCreate(this);
    }

    @Override
    public void onDestroy() {
        ReminderModel.onServiceDestroy(this);
        super.onDestroy();
    }
}