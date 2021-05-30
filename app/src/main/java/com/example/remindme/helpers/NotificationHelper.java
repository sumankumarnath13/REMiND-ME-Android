package com.example.remindme.helpers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.remindme.R;
import com.example.remindme.controllers.AlertBroadcastReceiver;
import com.example.remindme.viewModels.AlertModel;

import java.util.Locale;

public class NotificationHelper {

    public static void notifyMissed(Context context, AlertModel model) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context.getApplicationContext(),
                AlertModel.DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_brand)
                .setLocalOnly(true)
                .setGroup(AlertModel.DEFAULT_NOTIFICATION_GROUP_KEY)
                .setContentTitle(model.getSignatureName())
                .setContentText(String.format(Locale.getDefault(), "Missed %s on %s",
                        model.isReminder() ? "Reminder" : "Alarm",
                        StringHelper.toTimeWeekdayDate(context, model.getTimeModel().getTime())));

        if (!StringHelper.isNullOrEmpty(model.getNote())) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(model.getNote()));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManager.notify(model.getIntId(), builder.build());
    }

    public static void notifyReminder(Context context, AlertModel model) {

        final Intent closeReminder = new Intent(context.getApplicationContext(), AlertBroadcastReceiver.class)
                .setAction(AlertModel.BROADCAST_FILTER_REMINDER_DISMISS)
                .putExtra(AlertModel.REMINDER_ID_INTENT, model.getId())
                .putExtra(AlertModel.REMINDER_INT_ID_INTENT, model.getIntId());

        final PendingIntent closeReminderPendingIntent = PendingIntent
                .getBroadcast(context, model.getIntId(),
                        closeReminder,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context.getApplicationContext(),
                AlertModel.ALARM_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_brand)
                .setOngoing(true)
                .setAutoCancel(false)
                .setLocalOnly(true)
                .setWhen(0)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .addAction(R.drawable.ic_set, context.getString(R.string.action_label_dismiss), closeReminderPendingIntent)
                .setContentTitle(model.getSignatureName());

        if (!StringHelper.isNullOrEmpty(model.getNote())) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(model.getNote()));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManager.notify(model.getIntId(), builder.build());
    }

//    public static void notifySummary(Context context, String title, String text, String bigText) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ReminderModel.DEFAULT_NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_brand)
//                .setLocalOnly(true)
//                .setGroup(ReminderModel.DEFAULT_NOTIFICATION_GROUP_KEY)
//                .setGroupSummary(true)
//                .setContentTitle(title)
//                .setContentText(text);
//
//        if (!StringHelper.isNullOrEmpty(bigText)) {
//            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText));
//        }
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(ReminderModel.DEFAULT_NOTIFICATION_GROUP_ID, builder.build());
//    }

}
