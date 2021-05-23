package com.example.remindme.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.example.remindme.helpers.NotificationHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.WakeLockHelper;
import com.example.remindme.viewModels.ReminderModel;

public class AlertBroadcastReceiver extends BroadcastReceiver {

    private static final String BROADCAST_FILTER_QUICK_BOOT = "android.intent.action.QUICKBOOT_POWERON";
    private static final String BROADCAST_FILTER_HTC_BOOT = "com.htc.intent.action.QUICKBOOT_POWERON";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        if (StringHelper.isNullOrEmpty(action)) {
            return;
        }

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case BROADCAST_FILTER_QUICK_BOOT:
            case BROADCAST_FILTER_HTC_BOOT:
                ReminderModel.reScheduleAllActive(context.getApplicationContext(), true);
                break;
            case ReminderModel.BROADCAST_FILTER_ALARM:
                WakeLockHelper.acquire(context.getApplicationContext());
                final Intent startService = new Intent(context, AlertService.class)
                        .putExtra(ReminderModel.REMINDER_ID_INTENT, ReminderModel.getReminderIdFromIntent(intent));
                if (OsHelper.isOreoOrLater()) {
                    startService.putExtra(ReminderModel.SERVICE_TYPE, 1);
                    context.startForegroundService(startService);
                } else {
                    startService.putExtra(ReminderModel.SERVICE_TYPE, 0);
                    context.startService(startService);
                }
                break;

            case ReminderModel.BROADCAST_FILTER_REMINDER:
                final ReminderModel reminder = ReminderModel.getInstance(intent);
                if (reminder == null)
                    return;
                NotificationHelper.notifyReminder(context, reminder);
                reminder.dismissByUser(context); // Notification dose not requires any user interaction thus it will always assume action taken by user.
                break;

            case ReminderModel.BROADCAST_FILTER_REMINDER_DISMISS:
                final ReminderModel actionReminder = ReminderModel.getInstance(intent);
                NotificationManagerCompat notifyMgr = NotificationManagerCompat.from(context);
                notifyMgr.cancel(actionReminder.getIntId());

//                final ReminderModel reminder = ReminderModel.getInstance(intent);
//                if (reminder == null)
//                    return;
//                NotificationHelper.notify(context, reminder.getIntId(), reminder.getName(), reminder.getNote(), null);
//                reminder.dismissByUser(context); // Notification dose not requires any user interaction thus it will always assume action taken by user.
                break;

        }
    }
}
