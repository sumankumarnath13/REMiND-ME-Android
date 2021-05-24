package com.example.remindme;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.remindme.controllers.AlertBroadcastReceiver;
import com.example.remindme.viewModels.AlertModel;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        if (!AlertModel.tryAppCreate(AlertBroadcastReceiver.class, this)) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

}
