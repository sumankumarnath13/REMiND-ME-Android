package com.example.remindme;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;

import com.example.remindme.viewModels.ReminderModel;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;


public class App extends Application {

    public static final String ALARM_NOTIFICATION_CHANNEL_ID = "_DING_DONG";
    public static final String ALARM_NOTIFICATION_CHANNEL_NAME = "Alarm notifications";
    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "_TING TONG";
    public static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "Other notifications";

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel alarmChannel = new NotificationChannel(
                    ALARM_NOTIFICATION_CHANNEL_ID,
                    ALARM_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationChannel defaultChannel = new NotificationChannel(
                    DEFAULT_NOTIFICATION_CHANNEL_ID,
                    DEFAULT_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel1.
            //channel1.setDescription("Remind_me");
            //channel1.enableLights(true);
            //channel1.setLightColor(Color.RED);
            //channel1.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            //channel1.enableVibration(true);

            //alarmChannel.setSound(null, null);
            notificationManager.createNotificationChannel(alarmChannel);
            notificationManager.createNotificationChannel(defaultChannel);
        }

        Realm.init(getApplicationContext());

        try {
            Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException r) {
            RealmConfiguration config = Realm.getDefaultConfiguration();
            Realm.deleteRealm(config);
        }

        ReminderModel.reScheduleAllActive(getApplicationContext());
    }
}
