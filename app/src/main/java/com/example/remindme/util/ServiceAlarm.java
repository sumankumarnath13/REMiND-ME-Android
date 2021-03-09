package com.example.remindme.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.remindme.ActivityReminderRinging;
import com.example.remindme.App;
import com.example.remindme.R;
import com.example.remindme.viewModels.ReminderModel;

public class ServiceAlarm extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ServiceAlarm() {
        super("TEST_TEST");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            Notification notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_1_ID)
                    .setContentTitle("Error!")
                    .setContentText("Null intent!")
                    .setSmallIcon(R.drawable.ic_reminder_time)
                    .setAutoCancel(true)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(117, notification);

            startForeground(117, notification);
        } else {
            String id = intent.getStringExtra(ReminderModel.INTENT_ATTR_ID);
            Intent activityIntent = new Intent(this, ActivityReminderRinging.class);
            activityIntent.putExtra(ReminderModel.INTENT_ATTR_ID, id);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 117, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            ReminderModel reminderModel = ReminderModel.read(id);

            Notification notification;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (reminderModel == null) {
                notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_1_ID)
                        .setContentTitle("Error!")
                        .setContentText("Reminder not found!")
                        .setSmallIcon(R.drawable.ic_reminder_time)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(117, notification);

            } else {
                notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_1_ID)
                        .setContentTitle(reminderModel.name)
                        .setContentText(reminderModel.note)
                        .setSmallIcon(R.drawable.ic_reminder_time)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(117, notification);

                startActivity(activityIntent);
            }
            startForeground(117, notification);
        }
    }
}