package com.example.remindme.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.ReminderModel;

public class AlertBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_QUICK_BOOT = "android.intent.action.QUICKBOOT_POWERON";
    private static final String ACTION_HTC_BOOT = "com.htc.intent.action.QUICKBOOT_POWERON";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (StringHelper.isEmpty(action)) {
            return;
        }

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case ACTION_QUICK_BOOT:
            case ACTION_HTC_BOOT:
                ReminderModel.reScheduleAllActive(true);
                break;
            case ReminderModel.ACTION_RECEIVE_ALARM:
                Intent startService = new Intent(context, AlertService.class).putExtra(ReminderModel.REMINDER_ID_INTENT, ReminderModel.getReminderId(intent));
                if (OsHelper.isOreoOrLater()) {
                    context.startForegroundService(startService);
                } else {
                    context.startService(startService);
                }
                break;
        }
    }
}
