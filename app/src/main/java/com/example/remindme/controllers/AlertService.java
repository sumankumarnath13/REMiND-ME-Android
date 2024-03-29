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
import com.example.remindme.helpers.WakeLockHelper;
import com.example.remindme.ui.activities.AlarmBell;
import com.example.remindme.viewModels.AlertModel;
import com.example.remindme.viewModels.RingingModel;

public class AlertService extends Service {
    private AlertModel servingReminder;
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
                case AlertModel.ACTION_ALARM_SNOOZE:
                    snoozeByUser();
                    break;
                case AlertModel.ACTION_ALARM_DISMISS:
                    dismiss();
                    break;
            }
        }
    };

    private CountDownTimer timer;

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
            return new Intent(this, AlarmBell.class)
                    .setAction(actionName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        } else {
            return new Intent(this, AlarmBell.class)
                    .setAction(actionName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);

        }
    }

    public Notification getAlarmHeadsUp(AlertModel model) {
        //ALERT_INTENT_DISMISS_ALERT
        final PendingIntent dismissPendingIntent = PendingIntent
                .getBroadcast(this, model.getIntId(), createNotificationActionBroadcastIntent(AlertModel.ACTION_ALARM_DISMISS), PendingIntent.FLAG_CANCEL_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this.getApplicationContext(),
                AlertModel.ALARM_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_brand)
                .setOngoing(true)
                .setAutoCancel(false)
                .setLocalOnly(true)
                .setWhen(0)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(model.getName()))
                .addAction(R.drawable.ic_reminder_dismiss, getString(R.string.action_label_dismiss), dismissPendingIntent)
                .setContentTitle(model.getSignatureName());
        //.setSubText(note)

        //ALERT_INTENT_SNOOZE_ALERT
        if (model.canSnooze()) {
            final PendingIntent snoozePendingIntent = PendingIntent
                    .getBroadcast(this, model.getIntId(),
                            createNotificationActionBroadcastIntent(AlertModel.ACTION_ALARM_SNOOZE),
                            PendingIntent.FLAG_CANCEL_CURRENT);
            builder.addAction(R.drawable.ic_reminder_snooze,
                    getString(R.string.action_label_snooze), snoozePendingIntent);
        }

        builder.setContentIntent(PendingIntent
                .getActivity(this, model.getIntId(),
                        createAlarmActivityIntent(AlertModel.ACTION_ALERT_NOTIFICATION_CONTENT),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        builder.setFullScreenIntent(PendingIntent
                .getActivity(this, model.getIntId(),
                        createAlarmActivityIntent(AlertModel.ACTION_ALERT_NOTIFICATION_CONTENT_FULLSCREEN),
                        PendingIntent.FLAG_UPDATE_CURRENT), true);

        builder.setLocalOnly(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }

    private void startRinging() {
        if (isBusy) {

            if (ringingController == null) {
                ringingController = new RingingController(this, servingReminder.getRingingModel().getRingToneUri());
            }

            if (servingReminder.getRingingModel().isToneEnabled()) {
                ringingController.startTone(servingReminder.getRingingModel().isIncreaseVolumeGradually(), servingReminder.getRingingModel().getAlarmVolumePercentage());
            }

            if (servingReminder.getRingingModel().isVibrationEnabled()) {
                ringingController.startVibrating(RingingModel.convertToVibrateFrequency(servingReminder.getRingingModel().getVibratePattern()));
            }

        }
    }

    private void stopRinging() {
        if (ringingController != null) {
            ringingController.stopRinging();
        }
    }

    public AlertModel getServingReminder() {
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
        Intent ringingActivity = createAlarmActivityIntent(AlertModel.ACTION_ALERT_FULLSCREEN);
        startActivity(ringingActivity);
    }

    private void broadcastCloseAlarmActivity() {
        Intent stopServiceBroadcast = new Intent(AlertModel.ACTION_CLOSE_ALARM_ACTIVITY);
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
        notificationManager = NotificationManagerCompat.from(this);

        if (!isInternalBroadcastReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(AlertModel.ACTION_ALARM_SNOOZE);
            filter.addAction(AlertModel.ACTION_ALARM_DISMISS);
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
            final AlertModel newReminder = AlertModel.getInstance(intent);
            if (newReminder != null) {
                newReminder.snoozeByApp(this);
                NotificationHelper.notifyMissed(this, newReminder);
            }
            return START_NOT_STICKY;
        }

        servingReminder = AlertModel.getInstance(intent);
        if (servingReminder == null) {
            // Reminder not found!
            stopService();
            return START_NOT_STICKY;
        }

        isBusy = true;
        telephonyManager.getCallState();
        telephonyManager.listen(phoneStateChangeListener, PhoneStateListener.LISTEN_CALL_STATE);

        if (intent.getIntExtra(AlertModel.SERVICE_TYPE, 0) == 1) {
            //Oreo and onwards won't allow service to just run without notification.
            startForeground(AlertModel.ALARM_NOTIFICATION_ID, getAlarmHeadsUp(servingReminder));
            if (isIdle)
                startRinging();
        } else if (intent.getIntExtra(AlertModel.SERVICE_TYPE, 0) == 0) {
            notificationManager.notify(AlertModel.ALARM_NOTIFICATION_ID, getAlarmHeadsUp(servingReminder));
            if (!OsHelper.isInteractive(this) && isIdle) { // show heads up
                startRinging();
                openAlarmActivity();
            }
        } else if (isIdle) {

            startRinging();
            openAlarmActivity();

        }

        int duration;
        switch (servingReminder.getRingingModel().getAlarmRingDuration()) {
            default:
            case ONE_MINUTE:
                duration = 1000 * 60;
                break;

            case TWO_MINUTE:
                duration = 1000 * 60 * 2;
                break;

            case THREE_MINUTE:
                duration = 1000 * 60 * 3;
                break;
        }

        final long INTERVAL = 1000L;
        timer = new CountDownTimer(duration, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                snoozeByApp();
            }
        }.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        if (timer != null) {
            timer.cancel();
        }

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