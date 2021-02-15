package com.example.remindme;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.example.remindme.util.UtilsAlarm;

import java.text.ParseException;

import io.realm.Realm;


public class App extends Application {

    public static final String NOTIFICATION_CHANNEL_ID = "DHINKACHIKA";

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "App created", Toast.LENGTH_SHORT).show();
        Realm.init(this);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "My Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("My Desc");
            //notificationChannel.enableLights(true);
            //notificationChannel.setLightColor(Color.RED);
            //notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            //notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

/*        try {
            UtilsAlarm.boot(this);
        } catch (ParseException e) {
            Toast.makeText(this, "Parsing error - App start rescheduling : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }*/
    }
}
