package com.example.remindme.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.remindme.helpers.NotificationHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.WakeLockHelper;
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
        if (StringHelper.isNullOrEmpty(action)) {
            return;
        }

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case ACTION_QUICK_BOOT:
            case ACTION_HTC_BOOT:
                ReminderModel.reScheduleAllActive(context.getApplicationContext(), true);
                break;
            case ReminderModel.ACTION_RECEIVE_ALARM:
                WakeLockHelper.acquire(context.getApplicationContext());
                Intent startService = new Intent(context, AlertService.class).putExtra(ReminderModel.REMINDER_ID_INTENT, ReminderModel.getReminderIdFromIntent(intent));
                if (OsHelper.isOreoOrLater()) {
                    context.startForegroundService(startService);
                } else {
                    context.startService(startService);
                }
                break;

            case ReminderModel.ACTION_RECEIVE_NOTIFICATION:
                final ReminderModel reminder = ReminderModel.getInstance(intent);
                if (reminder == null)
                    return;
                NotificationHelper.notify(context, reminder.getIntId(), reminder.getName(), reminder.getNote(), null);
                reminder.dismissByUser(context); // Notification dose not requires any user interaction thus it will always assume action taken by user.
                break;
        }
    }
}
