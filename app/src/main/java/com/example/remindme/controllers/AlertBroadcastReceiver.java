package com.example.remindme.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.example.remindme.helpers.NotificationHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.WakeLockHelper;
import com.example.remindme.viewModels.AlertModel;

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
                AlertModel.reScheduleAllActive(context.getApplicationContext(), true);
                break;
            case AlertModel.BROADCAST_FILTER_ALARM:
                WakeLockHelper.acquire(context.getApplicationContext());
                final Intent startService = new Intent(context, AlertService.class)
                        .putExtra(AlertModel.REMINDER_ID_INTENT, AlertModel.getReminderIdFromIntent(intent));
                if (OsHelper.isOreoOrLater()) {
                    startService.putExtra(AlertModel.SERVICE_TYPE, 1);
                    context.startForegroundService(startService);
                } else {
                    startService.putExtra(AlertModel.SERVICE_TYPE, 0);
                    context.startService(startService);
                }
                break;

            case AlertModel.BROADCAST_FILTER_REMINDER:
                final AlertModel reminder = AlertModel.getInstance(intent);
                if (reminder == null)
                    return;
                NotificationHelper.notifyReminder(context, reminder);
                reminder.dismissByApp(context); // dismiss
                break;

            case AlertModel.BROADCAST_FILTER_REMINDER_DISMISS:
                NotificationManagerCompat notifyMgr = NotificationManagerCompat.from(context);
                final AlertModel actionReminder = AlertModel.getInstance(intent);
                if (actionReminder == null) {
                    final int intId = intent.getIntExtra(AlertModel.REMINDER_INT_ID_INTENT, 0);
                    notifyMgr.cancel(intId);
                } else {
                    actionReminder.dismissByUser(context);
                    notifyMgr.cancel(actionReminder.getIntId());
                }
                break;

        }
    }
}
