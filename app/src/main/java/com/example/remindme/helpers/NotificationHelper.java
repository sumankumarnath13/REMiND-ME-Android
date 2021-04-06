package com.example.remindme.helpers;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.remindme.R;
import com.example.remindme.viewModels.ReminderModel;

public class NotificationHelper {

    public static void notify(Context context, int Id, String title, String text, String bigText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ReminderModel.DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder_notification)
                .setLocalOnly(true)
                .setGroup(ReminderModel.DEFAULT_NOTIFICATION_GROUP_KEY)
                .setContentTitle(title)
                .setContentText(text);

        if (!StringHelper.isNullOrEmpty(bigText)) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Id, builder.build());
    }

    public static void notifySummary(Context context, String title, String text, String bigText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ReminderModel.DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder_notification)
                .setLocalOnly(true)
                .setGroup(ReminderModel.DEFAULT_NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .setContentTitle(title)
                .setContentText(text);

        if (!StringHelper.isNullOrEmpty(bigText)) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(ReminderModel.DEFAULT_NOTIFICATION_GROUP_ID, builder.build());
    }

}
