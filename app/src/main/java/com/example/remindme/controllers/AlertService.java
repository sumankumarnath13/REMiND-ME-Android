package com.example.remindme.controllers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.remindme.R;
import com.example.remindme.helpers.NotificationHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.WakeLockHelper;
import com.example.remindme.ui.activities.ActivityReminderRinging;
import com.example.remindme.viewModels.ReminderModel;

public class AlertService extends Service {
    private ReminderModel servingReminder;
    private NotificationManagerCompat notificationManager;
    private boolean isBusy = false;
    private boolean isChanged = false;
    private boolean isActivityOpenedOnce = false;
    private boolean isActivityOpen = false;
    private boolean isIdle = true;
    private boolean isInterrupted = false;
    private RingingController ringingController;

    public void setActivityOpen(boolean value) {
        isActivityOpen = value;

        if (isActivityOpen && !isActivityOpenedOnce) {
            isActivityOpenedOnce = true;
        }
    }

    private boolean isInternalBroadcastReceiverRegistered = false;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            switch (intent.getAction()) {
                case ReminderModel.ACTION_SNOOZE_ALARM:
                    snoozeByUser();
                    break;
                case ReminderModel.ACTION_DISMISS_ALARM:
                    dismiss();
                    break;
            }
        }
    };

    private final long INTERVAL = 1000L;
    private final CountDownTimer timer = new CountDownTimer(ReminderModel.RING_DURATION, INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            snoozeByApp();
        }
    };

    private final class PhoneStateChangeListener extends PhoneStateListener {

        private final AlertService service;

        public PhoneStateChangeListener(AlertService hostService) {
            service = hostService;
        }

        private final int systemSettleDelay = 531;

        @Override
        public void onCallStateChanged(int state, String ignored) {
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                isIdle = true;

                if (!isActivityOpen) {
                    if (isActivityOpenedOnce || isInterrupted) {
                        new CountDownTimer(systemSettleDelay, systemSettleDelay) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                service.startRinging();
                                openAlarmActivity();
                            }
                        }.start();
                    }
                }
            } else {
                isIdle = false;
                if (!isInterrupted) {
                    isInterrupted = true;
                }

                service.stopRinging();

                // Close the activity if it was opened
                if (isActivityOpen) {
                    broadcastCloseAlarmActivity();
                }
            }
        }
    }

    private final PhoneStateChangeListener phoneStateChangeListener = new PhoneStateChangeListener(this);

    private TelephonyManager telephonyManager;

    private Intent createNotificationActionBroadcastIntent(String actionName) {
        return new Intent(actionName);
    }

    private Intent createAlarmActivityIntent(String actionName) {
        // Setting an action is important. It help distinguish between intents with other values targeting same activity
        if (OsHelper.isOreoOrLater()) {
            return new Intent(this, ActivityReminderRinging.class)
                    .setAction(actionName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        } else {
            return new Intent(this, ActivityReminderRinging.class)
                    .setAction(actionName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);

        }
    }

    public Notification getAlarmHeadsUp(ReminderModel model) {
        String timeStamp = StringHelper.toTime(model.getOriginalTime());

        //ALERT_INTENT_DISMISS_ALERT
        PendingIntent dismissPendingIntent = PendingIntent
                .getBroadcast(this, model.getIntId(), createNotificationActionBroadcastIntent(ReminderModel.ACTION_DISMISS_ALARM), PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ReminderModel.ALARM_NOTIFICATION_CHANNEL_ID)

                .addAction(R.drawable.ic_reminder_dismiss, getString(R.string.btn_alarm_action_dismiss), dismissPendingIntent)
                .setContentTitle(model.getName())
                .setContentText(timeStamp)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(model.getName()))
                //.setSubText(note)
                .setSmallIcon(R.drawable.ic_brand)
                .setOngoing(true)
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setWhen(0)
                .setCategory(NotificationCompat.CATEGORY_ALARM);

        //ALERT_INTENT_SNOOZE_ALERT
        if (model.getSnoozeModel().isEnable) {
            final PendingIntent snoozePendingIntent = PendingIntent
                    .getBroadcast(this, model.getIntId(), createNotificationActionBroadcastIntent(ReminderModel.ACTION_SNOOZE_ALARM), PendingIntent.FLAG_CANCEL_CURRENT);
            builder.addAction(R.drawable.ic_reminder_snooze, getString(R.string.btn_snooze), snoozePendingIntent);
        }

        builder.setContentIntent(PendingIntent
                .getActivity(this, model.getIntId(), createAlarmActivityIntent(ReminderModel.ACTION_ALERT_NOTIFICATION_CONTENT), PendingIntent.FLAG_UPDATE_CURRENT));

        builder.setFullScreenIntent(PendingIntent
                        .getActivity(this, model.getIntId(), createAlarmActivityIntent(ReminderModel.ACTION_ALERT_NOTIFICATION_CONTENT_FULLSCREEN), PendingIntent.FLAG_UPDATE_CURRENT),
                true);

        builder.setLocalOnly(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }

    private void startRinging() {
        if (isBusy) {

            if (ringingController == null) {
                ringingController = new RingingController(this, servingReminder.getRingToneUri());
            }

            if (servingReminder.isEnableTone()) {
                ringingController.startTone(servingReminder.isIncreaseVolumeGradually(), servingReminder.getAlarmVolumePercentage());
            }
            if (servingReminder.isEnableVibration()) {
                ringingController.startVibrating();
            }
        }
    }

    private void stopRinging() {
        ringingController.stopRinging();
    }

    public ReminderModel getServingReminder() {
        return servingReminder;
    }

    public void snoozeByUser() {
        if (!isChanged && isBusy) {
            isChanged = true;
            servingReminder.snoozeByUser(this.getApplicationContext());
            broadcastCloseAlarmActivity();
            stopService();
        }
    }

    public void snoozeByApp() {
        if (!isChanged && isBusy) {
            isChanged = true;
            servingReminder.snoozeByApp(this.getApplicationContext());
            broadcastCloseAlarmActivity();
            stopService();
        }
    }

    public void dismiss() {
        if (!isChanged && isBusy) {
            isChanged = true;
            servingReminder.dismissByUser(this.getApplicationContext());
            broadcastCloseAlarmActivity();
            stopService();
        }
    }

    private void openAlarmActivity() {
        Intent ringingActivity = createAlarmActivityIntent(ReminderModel.ACTION_ALERT_FULLSCREEN);
        startActivity(ringingActivity);
    }

    private void broadcastCloseAlarmActivity() {
        Intent stopServiceBroadcast = new Intent(ReminderModel.ACTION_CLOSE_ALARM_ACTIVITY);
        sendBroadcast(stopServiceBroadcast);
    }

    private void stopService() {
        if (OsHelper.isOreoOrLater()) {
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

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
                newReminder.snoozeByApp(this.getApplicationContext());
                NotificationHelper.notify(this.getApplicationContext(), newReminder.getIntId(), "Missed alarm " + StringHelper.toTime(newReminder.getOriginalTime()), newReminder.getName(), newReminder.getNote());
            }
            return START_NOT_STICKY;
        }

        if (!servingReminder.tryReadFrom(intent)) {
            // Reminder not found!
            stopService();
            return START_NOT_STICKY;
        }

        isBusy = true;
        telephonyManager.getCallState();
        telephonyManager.listen(phoneStateChangeListener, PhoneStateListener.LISTEN_CALL_STATE);

        if (OsHelper.isOreoOrLater()) {

            //Oreo and onwards won't allow service to just run without notification.
            startForeground(ReminderModel.ALARM_NOTIFICATION_ID, getAlarmHeadsUp(servingReminder));

            if (isIdle) startRinging();

        } else if (OsHelper.isLollipopOrLater()) {

            notificationManager.notify(ReminderModel.ALARM_NOTIFICATION_ID, getAlarmHeadsUp(servingReminder));

            if (!OsHelper.isInteractive(this) && isIdle) { // show heads up
                startRinging();
                openAlarmActivity();
            }

        } else if (isIdle) {

            startRinging();
            openAlarmActivity();

        }

        timer.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        telephonyManager.listen(phoneStateChangeListener, PhoneStateListener.LISTEN_NONE);
        stopRinging();

        if (isInternalBroadcastReceiverRegistered) {
            unregisterReceiver(receiver);
            isInternalBroadcastReceiverRegistered = false;
        }

        if (!isChanged & isBusy) {
            // Snooze the reminder if no action was taken.
            servingReminder.snoozeByApp(this.getApplicationContext());
        }

        isBusy = false;
        super.onDestroy();
        WakeLockHelper.release();
    }
}