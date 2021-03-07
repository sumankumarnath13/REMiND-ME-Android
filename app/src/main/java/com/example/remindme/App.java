package com.example.remindme;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;


public class App extends Application {

    public static final String NOTIFICATION_CHANNEL_1_ID = "_DING_DONG";
    public static final String NOTIFICATION_CHANNEL_1_NAME = "Primary channel";
    public static final String NOTIFICATION_CHANNEL_2_NAME = "Secondary channel";

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        Realm.init(getApplicationContext());

        try {
            Realm.getDefaultInstance();
        }
        catch (RealmMigrationNeededException r) {
            RealmConfiguration config = Realm.getDefaultConfiguration();
            Realm.deleteRealm(config);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    NOTIFICATION_CHANNEL_1_ID,
                    NOTIFICATION_CHANNEL_1_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel1.
            //channel1.setDescription("Remind_me");
            //channel1.enableLights(true);
            //channel1.setLightColor(Color.RED);
            //channel1.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            //channel1.enableVibration(true);
            notificationManager.createNotificationChannel(channel1);
        }
    }
}
