package com.example.remindme;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.remindme.util.AlertService;
import com.example.remindme.util.AppBroadcastReceiver;
import com.example.remindme.viewModels.ReminderModel;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        ReminderModel.onAppCreate(AppBroadcastReceiver.class, AlertService.class, ActivityReminderRinging.class, App.this);
    }
}
