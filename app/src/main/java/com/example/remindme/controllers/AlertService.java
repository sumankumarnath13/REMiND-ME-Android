package com.example.remindme.controllers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.remindme.ActivityReminderRinging;
import com.example.remindme.R;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.ReminderModel;

public class AlertService extends Service {
    private ReminderModel servingReminder;
    private NotificationManagerCompat notificationManager;
    private boolean isBusy = false;
    private boolean isChanged = false;
    private boolean isRinging = false;
    private Ringtone playingRingtone;
    private boolean isInternalBroadcastReceiverRegistered = false;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            switch (intent.getAction()) {
                case ReminderModel.ACTION_SNOOZE_ALARM:
                    snooze();
                    break;
                case ReminderModel.ACTION_DISMISS_ALARM:
                    dismiss();
                    break;
            }
        }
    };

    private final long INTERVAL = 1000L;
    private final long DURATION = 60 * INTERVAL;
    private final CountDownTimer timer = new CountDownTimer(DURATION, INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            snooze();
        }
    };

    private Intent createNotificationActionBroadcastIntent(String actionName) {
        return new Intent(actionName);
    }

    private Intent createAlarmActivityIntent(String actionName) {
        // Setting an action is important. It help distinguish between intents with other values targeting same activity
        return new Intent(this, ActivityReminderRinging.class)
                .setAction(actionName)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
    }

    public Notification getAlarmHeadsUp(ReminderModel model) {
        String timeStamp = StringHelper.toTime(model.getOriginalTime());

        //ALERT_INTENT_SNOOZE_ALERT
        PendingIntent snoozePendingIntent = PendingIntent
                .getBroadcast(this, model.getIntId(), createNotificationActionBroadcastIntent(ReminderModel.ACTION_SNOOZE_ALARM), PendingIntent.FLAG_CANCEL_CURRENT);

        //ALERT_INTENT_DISMISS_ALERT
        PendingIntent dismissPendingIntent = PendingIntent
                .getBroadcast(this, model.getIntId(), createNotificationActionBroadcastIntent(ReminderModel.ACTION_DISMISS_ALARM), PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ReminderModel.ALARM_NOTIFICATION_CHANNEL_ID)
                .addAction(R.drawable.ic_reminder_snooze, getString(R.string.btn_snooze), snoozePendingIntent)
                .addAction(R.drawable.ic_reminder_dismiss, getString(R.string.btn_alarm_action_dismiss), dismissPendingIntent)
                .setContentTitle(model.name)
                .setContentText(timeStamp)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(model.note))
                //.setSubText(note)
                .setSmallIcon(R.drawable.ic_reminder_time)
                .setOngoing(true)
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setWhen(0)
                .setCategory(NotificationCompat.CATEGORY_ALARM);

        builder.setContentIntent(PendingIntent
                .getActivity(this, model.getIntId(), createAlarmActivityIntent(ReminderModel.ACTION_ALERT_NOTIFICATION_CONTENT), PendingIntent.FLAG_UPDATE_CURRENT));

        builder.setFullScreenIntent(PendingIntent
                        .getActivity(this, model.getIntId(), createAlarmActivityIntent(ReminderModel.ACTION_ALERT_NOTIFICATION_CONTENT_FULLSCREEN), PendingIntent.FLAG_UPDATE_CURRENT),
                true);

        builder.setLocalOnly(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }

    private Vibrator getVibrator(Context context) {
        return ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
    }

    public void startVibrating(Context context) {
        if (servingReminder != null && servingReminder.isEnableVibration) {
            final Vibrator vibrator = getVibrator(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                vibrator.vibrate(ReminderModel.VIBRATE_PATTERN, 0, new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
            } else {
                vibrator.vibrate(ReminderModel.VIBRATE_PATTERN, 0);
            }
        }
    }

    private void startRinging(Context context) {
        if (servingReminder == null) return;

        if (servingReminder.isEnableTone && !isRinging) {
            if (servingReminder.ringToneUri == null) {
                servingReminder.ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
            playingRingtone = RingtoneManager.getRingtone(context, servingReminder.ringToneUri);
            playingRingtone.play();
            isRinging = true;
        }

        startVibrating(context);
    }

    private void stopRinging(Context context) {
        if (playingRingtone != null && isRinging) {
            playingRingtone.stop();
            isRinging = false;
        }

        getVibrator(context).cancel();
    }

    public ReminderModel getServingReminder() {
        return servingReminder;
    }

    public void snooze() {
        if (!isChanged && isBusy) {
            isChanged = true;
            servingReminder.snooze(true);
            broadcastServiceStop();
            stopService();
        }
    }

    public void dismiss() {
        if (!isChanged && isBusy) {
            isChanged = true;
            servingReminder.dismissByUser();
            broadcastServiceStop();
            stopService();
        }
    }

    private void broadcastServiceStop() {
        Intent stopServiceBroadcast = new Intent(ReminderModel.ACTION_STOP_SERVICE);
        sendBroadcast(stopServiceBroadcast);
    }

    private void stopService() {
        if (OsHelper.isForegroundServiceRequired()) {
            stopForeground(true);
        }
        stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        servingReminder = new ReminderModel();
        notificationManager = NotificationManagerCompat.from(this);

        if (!isInternalBroadcastReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ReminderModel.ACTION_SNOOZE_ALARM);
            filter.addAction(ReminderModel.ACTION_DISMISS_ALARM);
            registerReceiver(receiver, filter);
            isInternalBroadcastReceiverRegistered = true;
        }

        //ReminderModel.onServiceCreate(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AlertServiceBinder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isBusy) {
            // snooze concurrent calls if already serving
            final ReminderModel newReminder = new ReminderModel();
            if (newReminder.tryReadFrom(intent)) {
                newReminder.snooze(false);
            }
            return START_NOT_STICKY;
        }

        if (!servingReminder.tryReadFrom(intent)) {
            // Reminder not found!
            stopService();
            return START_NOT_STICKY;
        }

        isBusy = true;

        if (OsHelper.isForegroundServiceRequired()) {
            //Oreo and onwards won't allow service to just run without notification.
            startForeground(ReminderModel.ALARM_NOTIFICATION_ID, getAlarmHeadsUp(servingReminder));
        } else {

            if (OsHelper.isHeadsUpSupported()) { // Show heads up notification if screen is on
                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = powerManager.isInteractive();
                if (isScreenOn) { // show heads up
                    notificationManager.notify(ReminderModel.ALARM_NOTIFICATION_ID, getAlarmHeadsUp(servingReminder));
                } else { // show full screen ringing activity
                    Intent ringingActivity = createAlarmActivityIntent(ReminderModel.ACTION_ALERT_FULLSCREEN);
                    startActivity(ringingActivity);
                }
            } else { // heads ups not supported. Show full screen ringing activity is only option available.
                Intent ringingActivity = createAlarmActivityIntent(ReminderModel.ACTION_ALERT_FULLSCREEN);
                startActivity(ringingActivity);
            }
        }

        startRinging(this);
        timer.start();

        return START_NOT_STICKY;
        // return ReminderModel.onServiceStart(this, intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        stopRinging(this);

        if (isInternalBroadcastReceiverRegistered) {
            unregisterReceiver(receiver);
            isInternalBroadcastReceiverRegistered = false;
        }

        if (!isChanged & isBusy) {
            // Snooze the reminder if no action was taken.
            servingReminder.snooze(false);
        }

        isBusy = false;

        super.onDestroy();
    }
}