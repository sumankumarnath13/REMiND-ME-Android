package com.example.remindme.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.remindme.ActivityReminderRinging;
import com.example.remindme.App;
import com.example.remindme.R;
import com.example.remindme.viewModels.ReminderModel;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private static final String ALERT_CONTENT_INTENT_ACTION = "content_activity";
    private static final String ALERT_FULLSCREEN_INTENT_ACTION = "fullscreen_activity";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getStringExtra(ReminderModel.INTENT_ATTR_ACTION);
            String reminderId = intent.getStringExtra(ReminderModel.INTENT_ATTR_ID);
            ReminderModel reminderModel = ReminderModel.read(reminderId);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (notificationManager != null) {
                if (reminderModel == null) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.DEFAULT_NOTIFICATION_CHANNEL_ID)
                            .setContentTitle("Error!")
                            .setContentText("Reminder not found!")
                            .setSmallIcon(R.drawable.ic_reminder_time)
                            .setAutoCancel(true);
                    notificationManager.notify(reminderModel.alarmIntentId, builder.build());
                } else {
                    if (action != null) {
                        if (action.equals(ReminderModel.INTENT_VALUE_ACTION_RAISE_ALARM)) {
                            raiseAlarm(reminderModel, notificationManager, context);
                        } else if (action.equals(ReminderModel.INTENT_VALUE_ACTION_SNOOZE_ALARM)) {
                            int notificationId = intent.getIntExtra(ReminderModel.INTENT_ATTR_NOTIFICATION_ID, 0);
                            if (notificationId != 0) {
                                notificationManager.cancel(notificationId);
                                reminderModel.snooze(true, context);
                            }
                        } else if (action.equals(ReminderModel.INTENT_VALUE_ACTION_DISMISS_ALARM)) {
                            int notificationId = intent.getIntExtra(ReminderModel.INTENT_ATTR_NOTIFICATION_ID, 0);
                            if (notificationId != 0) {
                                notificationManager.cancel(notificationId);
                                reminderModel.dismiss(context);
                            }
                        }
                    }
                }
            }
        }
    }

    private void raiseAlarm(ReminderModel reminderModel, NotificationManagerCompat notificationManager, Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= 21) { // Show heads up notification if screen is on
            boolean isScreenOn = powerManager.isInteractive();
            if (isScreenOn) {
                String title;
                if (reminderModel.nextSnoozeOffTime == null) {
                    title = "R:" + UtilsDateTime.toTimeDateString(reminderModel.time) + "-" + reminderModel.name;
                } else {
                    title = "R:" + UtilsDateTime.toTimeDateString(reminderModel.time) + ",S:" + UtilsDateTime.toTimeString(reminderModel.nextSnoozeOffTime) + "-" + reminderModel.name;
                }

                Intent snoozeIntent = new Intent(context, AlarmBroadcastReceiver.class);
                snoozeIntent.setAction(ReminderModel.INTENT_VALUE_ACTION_SNOOZE_ALARM);
                snoozeIntent.putExtra(ReminderModel.INTENT_ATTR_ID, reminderModel.id);
                snoozeIntent.putExtra(ReminderModel.INTENT_ATTR_NOTIFICATION_ID, reminderModel.alarmIntentId);
                snoozeIntent.putExtra(ReminderModel.INTENT_ATTR_ACTION, ReminderModel.INTENT_VALUE_ACTION_SNOOZE_ALARM);
                PendingIntent snoozePendingIntent =
                        PendingIntent.getBroadcast(context,
                                reminderModel.alarmIntentId, snoozeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                Intent dismissIntent = new Intent(context, AlarmBroadcastReceiver.class);
                dismissIntent.setAction(ReminderModel.INTENT_VALUE_ACTION_DISMISS_ALARM);
                dismissIntent.putExtra(ReminderModel.INTENT_ATTR_ID, reminderModel.id);
                dismissIntent.putExtra(ReminderModel.INTENT_ATTR_NOTIFICATION_ID, reminderModel.alarmIntentId);
                dismissIntent.putExtra(ReminderModel.INTENT_ATTR_ACTION, ReminderModel.INTENT_VALUE_ACTION_DISMISS_ALARM);
                PendingIntent dismissPendingIntent =
                        PendingIntent.getBroadcast(context,
                                reminderModel.alarmIntentId, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.ALARM_NOTIFICATION_CHANNEL_ID)
                        .addAction(R.drawable.ic_reminder_snooze, context.getString(R.string.btn_snooze), snoozePendingIntent)
                        .addAction(R.drawable.ic_reminder_dismiss, context.getString(R.string.btn_alarm_action_dismiss), dismissPendingIntent)
                        .setContentTitle(title)
                        .setContentText(reminderModel.note)
                        .setSmallIcon(R.drawable.ic_reminder_time)
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                        .setWhen(0)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                Intent contentIntent = new Intent(context, ActivityReminderRinging.class);
                contentIntent.setAction(ALERT_CONTENT_INTENT_ACTION);
                contentIntent.putExtra(ReminderModel.INTENT_ATTR_ID, reminderModel.id);
                contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                builder.setContentIntent(PendingIntent.getActivity(context,
                        reminderModel.alarmIntentId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                Intent fullScreenIntent = new Intent(context, ActivityReminderRinging.class);
                // set action, so we can be different then content pending intent
                fullScreenIntent.setAction(ALERT_FULLSCREEN_INTENT_ACTION);
                fullScreenIntent.putExtra(ReminderModel.INTENT_ATTR_ID, reminderModel.id);
                fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                builder.setFullScreenIntent(PendingIntent.getActivity(context,
                        reminderModel.alarmIntentId, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT),
                        true);

                builder.setLocalOnly(true);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                notificationManager.notify(reminderModel.alarmIntentId, builder.build());
            } else {
                raiseFullScreenAlert(reminderModel, context);
            }
        } else { // Show full screen anyway
            raiseFullScreenAlert(reminderModel, context);
        }
    }

    private void raiseFullScreenAlert(ReminderModel reminderModel, Context context) {
        Intent activityIntent = new Intent(context, ActivityReminderRinging.class);
        activityIntent.putExtra(ReminderModel.INTENT_ATTR_ID, reminderModel.id);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        context.startActivity(activityIntent);
    }
}
