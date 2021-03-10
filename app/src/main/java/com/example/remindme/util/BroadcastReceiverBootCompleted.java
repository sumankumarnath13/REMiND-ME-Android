package com.example.remindme.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class BroadcastReceiverBootCompleted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//            Notification notification = new NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_1_ID)
//                    .setContentTitle("Reminder(reboot)")
//                    .setContentText("Reminder alerts are ready after device reboot.")
//                    .setSmallIcon(R.drawable.ic_reminder_time)
//                    .setAutoCancel(true)
//                    .build();
//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(117, notification);
        }
    }
}
