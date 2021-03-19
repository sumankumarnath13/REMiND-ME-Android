package com.example.remindme.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.remindme.viewModels.ReminderModel;

public class AlertBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderModel.onBroadcastReceive(context, intent);

//        if (intent != null) {
//            String receivedAction = intent.getStringExtra(ReminderModel.ALERT_RECEIVER_ACTION_INTENT_KEY);
//            String reminderId = intent.getStringExtra(ReminderModel.REMINDER_ID_INTENT_KEY);
//            ReminderModel reminderModel = ReminderModel.read(reminderId);
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//            if (notificationManager != null) {
//                if (reminderModel == null) {
//                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.DEFAULT_NOTIFICATION_CHANNEL_ID)
//                            .setContentTitle("Error!")
//                            .setContentText("Reminder not found!")
//                            .setSmallIcon(R.drawable.ic_reminder_time)
//                            .setAutoCancel(true);
//                    notificationManager.notify(reminderModel.alarmIntentId, builder.build());
//                } else {
//                    if (receivedAction != null) {
//                        if (receivedAction.equals(ReminderModel.ALERT_RECEIVER_ACTION_INTENT_RAISE_ALARM)) {
//                            raiseAlarm(reminderModel, notificationManager, context);
//                        } else if (receivedAction.equals(ReminderModel.ALERT_RECEIVER_ACTION_INTENT_SNOOZE_ALARM)) {
//                            int notificationId = intent.getIntExtra(ReminderModel.REMINDER_INT_ID_INTENT_KEY, 0);
//                            if (notificationId != 0) {
//                                notificationManager.cancel(notificationId);
//                                reminderModel.snooze(true, context);
//                            }
//                        } else if (receivedAction.equals(ReminderModel.ALERT_RECEIVER_ACTION_INTENT_DISMISS_ALARM)) {
//                            int notificationId = intent.getIntExtra(ReminderModel.REMINDER_INT_ID_INTENT_KEY, 0);
//                            if (notificationId != 0) {
//                                notificationManager.cancel(notificationId);
//                                reminderModel.dismiss(context);
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

//    private void raiseAlarm(ReminderModel reminderModel, NotificationManagerCompat notificationManager, Context context) {
//        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        if (Build.VERSION.SDK_INT >= 21) { // Show heads up notification if screen is on
//            boolean isScreenOn = powerManager.isInteractive();
//            if (isScreenOn) {
//                String title;
//                if (reminderModel.nextSnoozeOffTime == null) {
//                    title = "R:" + UtilsDateTime.toTimeDateString(reminderModel.time) + "-" + reminderModel.name;
//                } else {
//                    title = "R:" + UtilsDateTime.toTimeDateString(reminderModel.time) + ",S:" + UtilsDateTime.toTimeString(reminderModel.nextSnoozeOffTime) + "-" + reminderModel.name;
//                }
//
//                Intent snoozeIntent = new Intent(context, AlarmBroadcastReceiver.class);
//                snoozeIntent.setAction(ReminderModel.ALERT_RECEIVER_ACTION_INTENT_SNOOZE_ALARM);
//                snoozeIntent.putExtra(ReminderModel.REMINDER_ID_INTENT_KEY, reminderModel.id);
//                snoozeIntent.putExtra(ReminderModel.REMINDER_INT_ID_INTENT_KEY, reminderModel.alarmIntentId);
//                snoozeIntent.putExtra(ReminderModel.ALERT_RECEIVER_ACTION_INTENT_KEY, ReminderModel.ALERT_RECEIVER_ACTION_INTENT_SNOOZE_ALARM);
//                PendingIntent snoozePendingIntent =
//                        PendingIntent.getBroadcast(context,
//                                reminderModel.alarmIntentId, snoozeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//                Intent dismissIntent = new Intent(context, AlarmBroadcastReceiver.class);
//                dismissIntent.setAction(ReminderModel.ALERT_RECEIVER_ACTION_INTENT_DISMISS_ALARM);
//                dismissIntent.putExtra(ReminderModel.REMINDER_ID_INTENT_KEY, reminderModel.id);
//                dismissIntent.putExtra(ReminderModel.REMINDER_INT_ID_INTENT_KEY, reminderModel.alarmIntentId);
//                dismissIntent.putExtra(ReminderModel.ALERT_RECEIVER_ACTION_INTENT_KEY, ReminderModel.ALERT_RECEIVER_ACTION_INTENT_DISMISS_ALARM);
//                PendingIntent dismissPendingIntent =
//                        PendingIntent.getBroadcast(context,
//                                reminderModel.alarmIntentId, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.ALARM_NOTIFICATION_CHANNEL_ID)
//                        .addAction(R.drawable.ic_reminder_snooze, context.getString(R.string.btn_snooze), snoozePendingIntent)
//                        .addAction(R.drawable.ic_reminder_dismiss, context.getString(R.string.btn_alarm_action_dismiss), dismissPendingIntent)
//                        .setContentTitle(title)
//                        .setContentText(reminderModel.note)
//                        .setSmallIcon(R.drawable.ic_reminder_time)
//                        .setOngoing(true)
//                        .setAutoCancel(false)
//                        .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
//                        .setWhen(0)
//                        .setCategory(NotificationCompat.CATEGORY_ALARM)
//                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//                Intent contentIntent = new Intent(context, ActivityReminderRinging.class);
//                contentIntent.setAction(ALERT_CONTENT_INTENT_ACTION);
//                contentIntent.putExtra(ReminderModel.REMINDER_ID_INTENT_KEY, reminderModel.id);
//                contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//                builder.setContentIntent(PendingIntent.getActivity(context,
//                        reminderModel.alarmIntentId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT));
//
//                Intent fullScreenIntent = new Intent(context, ActivityReminderRinging.class);
//                // set action, so we can be different then content pending intent
//                fullScreenIntent.setAction(ALERT_FULLSCREEN_INTENT_ACTION);
//                fullScreenIntent.putExtra(ReminderModel.REMINDER_ID_INTENT_KEY, reminderModel.id);
//                fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//                builder.setFullScreenIntent(PendingIntent.getActivity(context,
//                        reminderModel.alarmIntentId, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT),
//                        true);
//
//                builder.setLocalOnly(true);
//                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
//                notificationManager.notify(reminderModel.alarmIntentId, builder.build());
//            } else {
//                raiseFullScreenAlert(reminderModel, context);
//            }
//        } else { // Show full screen anyway
//            raiseFullScreenAlert(reminderModel, context);
//        }
//    }
//
//    private void raiseFullScreenAlert(ReminderModel reminderModel, Context context) {
//        Intent activityIntent = new Intent(context, ActivityReminderRinging.class);
//        activityIntent.putExtra(ReminderModel.REMINDER_ID_INTENT_KEY, reminderModel.id);
//        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//        context.startActivity(activityIntent);
//    }
}
