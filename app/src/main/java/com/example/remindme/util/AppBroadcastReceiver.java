package com.example.remindme.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.remindme.viewModels.ReminderModel;

public class AppBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderModel.onBroadcastReceive(context, intent);
    }
}
