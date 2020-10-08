package com.example.remindme.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.remindme.App;
import com.example.remindme.R;
import com.example.remindme.ActivityReminderRinging;

public class ServiceAlarm extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int alarmId = intent.getIntExtra("ID", 0);
        int time = intent.getIntExtra("TIME", 0);
        String name = intent.getStringExtra("NAME");
        String note = intent.getStringExtra("NOTE");

        Intent notificationIntent = new Intent(this, ActivityReminderRinging.class);

        notificationIntent.putExtra("ID", alarmId);
        notificationIntent.putExtra("TIME", time);
        notificationIntent.putExtra("NAME", name);
        notificationIntent.putExtra("NOTE", note);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, alarmId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(name)
                .setContentText(note)
                .setSmallIcon(R.drawable.ic_reminder_time)
                .setContentIntent(pendingIntent)
                .build();

        //mediaPlayer.start();
        //long[] pattern = { 0, 100, 1000 };
        //vibrator.vibrate(pattern, 0);

        startForeground(alarmId, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //mediaPlayer.stop();
        //vibrator.cancel();
    }
}
