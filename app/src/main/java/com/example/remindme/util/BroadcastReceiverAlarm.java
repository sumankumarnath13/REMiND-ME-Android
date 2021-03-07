package com.example.remindme.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.remindme.viewModels.ReminderModel;

public class BroadcastReceiverAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra(ReminderModel.INTENT_ATTR_ID);
        Intent alarmServiceIntent = new Intent(context, ServiceAlarm.class);
        alarmServiceIntent.putExtra(ReminderModel.INTENT_ATTR_ID, id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(alarmServiceIntent);
        } else {
            context.startService(alarmServiceIntent);
        }
    }
}
