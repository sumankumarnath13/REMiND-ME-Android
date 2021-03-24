package com.example.remindme.viewModels;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModel;

import com.example.remindme.R;
import com.example.remindme.dataModels.ActiveReminder;
import com.example.remindme.dataModels.DismissedReminder;
import com.example.remindme.dataModels.MissedReminder;
import com.example.remindme.util.UtilsDateTime;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class ReminderModel extends ViewModel {

    //region Private constants
    //private static final String REMINDER_EDIT_MODE_NEW = "NEW";
    //private static final String REMINDER_EDIT_MODE_UPDATE = "NEW";
    public static final String INTENT_ATTR_FROM = "FROM";

    private static final String ALARM_NOTIFICATION_CHANNEL_ID = "z_0EdcKpGP";
    private static final String ALARM_NOTIFICATION_CHANNEL_NAME = "Alarm notifications";
    private static final int ALARM_NOTIFICATION_ID = 117;

    private static final String DEFAULT_NOTIFICATION_GROUP_KEY = "ÆjËèúÒ+·_²";
    private static final String DEFAULT_NOTIFICATION_GROUP_NAME = "Default notification group";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "RxLwKNdHEL";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "Other notifications";
    private static final int DEFAULT_NOTIFICATION_ID = 25;
    private static final int DEFAULT_NOTIFICATION_GROUP_ID = 13;

    private static final String ALERT_INTENT_ACTION = "È)wß³ç{TÃ£";
    private static final String ALERT_INTENT_RAISE_ALERT = "Å'*»àLÇ)»í";
    private static final String ALERT_INTENT_SNOOZE_ALERT = "n)QmeW¸æ#±";
    private static final String ALERT_INTENT_DISMISS_ALERT = "ÿÃ(Y£ÝZïJ<";
    private static final String ALERT_INTENT_IS_USER = "öÈýl®4óþ¿?";
    private static final String REMINDER_ID_INTENT = "uNX¯3Á×MòP";
    private static final String ALERT_NOTIFICATION_CONTENT_INTENT_ACTION = "£fcEB]¬B9æ";
    private static final String ALERT_NOTIFICATION_FULLSCREEN_INTENT_ACTION = ")F#¦¬ÔVI*N";
    //endregion

    //region Private static Members
    private static Class<? extends BroadcastReceiver> alertBroadcastReceiverClass;
    private static Class<? extends Activity> lockScreenAlertActivityClass;
    private static Class<? extends Service> alertServiceClass;
    private static Application application;
    private static Vibrator vibrator;
    private static AlarmManager alarmManager;
    private static boolean isRinging;
    private static Ringtone playingRingtone;
    private static String ringingReminderId;
    //endregion

    //region Private static Functions
    private static void reScheduleAllActive(boolean isDeviceRebooted) {
        final Calendar calendar = Calendar.getInstance();
        List<ActiveReminder> reminders = getActiveReminders(null);
        boolean isNewAlertFound = false;
        for (int i = 0; i < reminders.size(); i++) {
            final ActiveReminder r = reminders.get(i);
            final ReminderModel reminderModel = new ReminderModel();
            transformToModel(r, reminderModel);
            if (reminderModel.isEnable) {
                Date _time;
                if (reminderModel.nextSnoozeOffTime == null) {
                    _time = reminderModel.actual_time;
                } else {
                    _time = reminderModel.nextSnoozeOffTime;
                }
                if (calendar.getTime().after(_time) && isDeviceRebooted) {
                    // App getting killed after a while. But Broadcast receiver recreating app which leads to rescheduling.
                    // And for the logic below it getting dismissed before it could be snoozed from alerts.
                    // isDeviceRebooted will prevent this from happening.
                    reminderModel.dismissByApp(calendar);
                } else if (!reminderModel.isAlertExists()) {
                    if (reminderModel.name != null && !reminderModel.name.isEmpty()) {
                        notify(reminderModel.intId, "New reminder warning!", "name : " + reminderModel.name, reminderModel.note);
                    } else {
                        notify(reminderModel.intId, "New reminder warning!", "id : " + reminderModel.intId, reminderModel.note);
                    }
                    isNewAlertFound = true;
                    reminderModel.setAlarm(_time, false);
                }
            }
        }
        if (isNewAlertFound) {
            notifySummary("Resetting alarms", null, null);
        }
    }

    private static void stopAlarm() {
        if (playingRingtone != null) {
            playingRingtone.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }

        isRinging = false;
    }

    public static void transformToData(ReminderModel from, ActiveReminder to) {
        to.id = from.id;
        to.alarmIntentId = from.intId;
        to.name = from.name;
        to.note = from.note;
        to.time = from.actual_time;
        if (from.ringToneUri != null) {
            to.selectedAlarmTone = from.ringToneUri.toString();
        }
        to.isEnableTone = from.isEnableTone;
        to.isEnable = from.isEnable;
        to.isVibrate = from.isEnableVibration;

        to.repeatHours.clear();
        to.repeatDays.clear();
        to.repeatWeeks.clear();
        to.repeatMonths.clear();
        switch (from.repeatModel.repeatOption) {
            default:
            case NONE:
                to.repeatOption = 0;
                break;
            case HOURLY:
                to.repeatOption = 1;
                break;
            case HOURLY_CUSTOM:
                to.repeatOption = 11;
                to.repeatHours.addAll(from.repeatModel.customHours);
                break;
            case DAILY:
                to.repeatOption = 2;
                break;
            case DAILY_CUSTOM:
                to.repeatOption = 21;
                to.repeatDays.addAll(from.repeatModel.customDays);
                break;
            case WEEKLY:
                to.repeatOption = 3;
                break;
            case WEEKLY_CUSTOM:
                to.repeatOption = 31;
                to.repeatWeeks.addAll(from.repeatModel.customWeeks);
                break;
            case MONTHLY:
                to.repeatOption = 4;
                break;
            case MONTHLY_CUSTOM:
                to.repeatOption = 41;
                to.repeatMonths.addAll(from.repeatModel.customMonths);
                break;
            case YEARLY:
                to.repeatOption = 5;
                break;
        }

        to.isSnoozeEnable = from.snoozeModel.isEnable;
        to.nextSnoozeTime = from.nextSnoozeOffTime;
        to.snoozeCount = from.snoozeModel.count;

        if (from.snoozeModel.isEnable) {

            switch (from.snoozeModel.countOptions) {
                default:
                case R3:
                    to.snoozeLength = 3;
                    break;
                case R5:
                    to.snoozeLength = 5;
                    break;
                case RC:
                    to.snoozeLength = -1;
                    break;
            }

            switch (from.snoozeModel.intervalOption) {
                default:
                case M5:
                    to.snoozeInterval = 5;
                    break;
                case M10:
                    to.snoozeInterval = 10;
                    break;
                case M15:
                    to.snoozeInterval = 15;
                    break;
                case M30:
                    to.snoozeInterval = 30;
                    break;
            }
        } else {
            to.snoozeLength = 0;
            to.snoozeInterval = 0;
        }
    }

    public static void transformToModel(ActiveReminder from, ReminderModel to) {
        to.id = from.id;
        to.intId = from.alarmIntentId;
        to.name = from.name;
        to.note = from.note;
        to.actual_time = from.time;
        if (from.selectedAlarmTone != null) {
            to.ringToneUri = Uri.parse(from.selectedAlarmTone);
        }
        to.isEnableTone = from.isEnableTone;
        to.isEnable = from.isEnable;
        to.isEnableVibration = from.isVibrate;

        to.repeatModel.customHours.clear();
        to.repeatModel.customDays.clear();
        to.repeatModel.customWeeks.clear();
        to.repeatModel.customMonths.clear();
        switch (from.repeatOption) {
            default:
            case 0:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.NONE;
                break;
            case 1:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.HOURLY;
                break;
            case 11:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.HOURLY_CUSTOM;
                to.repeatModel.customHours.addAll(from.repeatHours);
                break;
            case 2:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.DAILY;
                break;
            case 21:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.DAILY_CUSTOM;
                to.repeatModel.customDays.addAll(from.repeatDays);
                break;
            case 3:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.WEEKLY;
                break;
            case 31:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM;
                to.repeatModel.customWeeks.addAll(from.repeatWeeks);
                break;
            case 4:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.MONTHLY;
                break;
            case 41:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM;
                to.repeatModel.customMonths.addAll(from.repeatMonths);
                break;
            case 5:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.YEARLY;
                break;
        }

        to.snoozeModel.isEnable = from.isSnoozeEnable;
        to.nextSnoozeOffTime = from.nextSnoozeTime;
        to.snoozeModel.count = from.snoozeCount;

        if (from.isSnoozeEnable) {

            switch (from.snoozeLength) {
                default:
                case 3:
                    to.snoozeModel.countOptions = ReminderSnoozeModel.SnoozeCountOptions.R3;
                    break;
                case 5:
                    to.snoozeModel.countOptions = ReminderSnoozeModel.SnoozeCountOptions.R5;
                    break;
                case -1:
                    to.snoozeModel.countOptions = ReminderSnoozeModel.SnoozeCountOptions.RC;
                    break;
            }

            switch (from.snoozeInterval) {
                default:
                case 5:
                    to.snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M5;
                    break;
                case 10:
                    to.snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M10;
                    break;
                case 15:
                    to.snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M15;
                    break;
                case 30:
                    to.snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M30;
                    break;
            }
        }
    }
    //endregion

    //region Private instance Members
    private String id;
    private int intId;
    private boolean isEnable = true;
    private final NotificationManagerCompat notificationManager;
    private ReminderRepeatModel repeatModel;
    private Date nextSnoozeOffTime = null;

    private ReminderRepeatModel repeatValueChangeBuffer;

    public ReminderModel() {
        id = null;
        repeatModel = new ReminderRepeatModel();
        snoozeModel = new ReminderSnoozeModel();
        notificationManager = NotificationManagerCompat.from(application.getApplicationContext());
    }
    //endregion

    //region Private instance Functions
    private void dismissByUser() {
        Date nextTime = getNextScheduleTime(Calendar.getInstance());
        if (nextTime == null) { // EOF situation
            archiveToFinished();
            deleteAndCancelAlert();
        } else {
            actual_time = nextTime; // Set next trigger time.
            //String net = UtilsDateTime.toTimeDateString(time);
            trySaveAndSetAlert(true, false); // Save changes. // Set alarm for next trigger time.
        }
    }

    private void dismissByApp(final Calendar currentTime) {
        Date nextTime = getNextScheduleTime(currentTime);
        archiveToMissed();
        Toast.makeText(application.getApplicationContext(), "Dismissing to missed! " + intId, Toast.LENGTH_LONG).show();
        if (nextTime == null) { // EOF situation
            deleteAndCancelAlert();
        } else {
            actual_time = nextTime; // Set next trigger time.
            trySaveAndSetAlert(true, false); // Save changes. // Set alarm for next trigger time.
        }
    }

    private boolean isAlertExists() {
        if (intId != 0) {
            if (alarmManager == null) {
                return false;
            } else {
                PendingIntent pendingIntent = getAlarmManagerAlarmPendingIntent(false);
                return pendingIntent != null;
            }
        } else {
            return false;
        }
    }

    private void cancelAlarm() {
        if (intId != 0) {
            if (alarmManager == null) {
                error("Warning! No alarm manager found for the device.");
            } else {
                PendingIntent pendingIntent = getAlarmManagerAlarmPendingIntent(false);
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent);
                }
            }
        }
    }

    private void setAlarm(Date atTime, boolean isShowElapseTimeToast) {
        if (!isEnable) {
            return;
        }

        Calendar calendar = Calendar.getInstance();

        long different = atTime.getTime() - calendar.getTime().getTime();

        if (different <= 0) // Meaningless to set time in past. BUG ALERT: negative value means something is very wrong somewhere
        {
            if (isShowElapseTimeToast) {
                error("Warning! discarding minus time value for alarm");
                return;
            }
        }

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        calendar.setTime(atTime);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getAlarmManagerAlarmPendingIntent(true));

        if (isShowElapseTimeToast) {
            StringBuilder stringBuilder = new StringBuilder("Alarm set after");

            if (elapsedDays > 0) {
                stringBuilder.append(" ");
                stringBuilder.append(elapsedDays);
                stringBuilder.append(" days,");
            }

            if (elapsedHours > 0) {
                stringBuilder.append(" ");
                stringBuilder.append(elapsedHours);
                stringBuilder.append(" hours,");
            }

            if (elapsedMinutes > 0) {
                stringBuilder.append(" ");
                stringBuilder.append(elapsedMinutes);
                stringBuilder.append(" minutes,");
            }

            if (elapsedSeconds > 0) {
                stringBuilder.append(" ");
                stringBuilder.append(elapsedSeconds);
                stringBuilder.append(" seconds,");
            }

            stringBuilder.append(" from now");
            showToast(stringBuilder.toString());
        }
    }

    private void raiseAlarm() {
        if ((isEnableVibration || isEnableTone) && !isRinging) {

            if (ringToneUri == null) {
                ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }

            if (vibrator != null) {
                long[] pattern = {500, 500};
                vibrator.vibrate(pattern, 0);
            }

            if (isEnableTone) {
                playingRingtone = RingtoneManager.getRingtone(application.getApplicationContext(), ringToneUri);
                playingRingtone.play();
            }

            isRinging = true;
        }
    }

    private void archiveToMissed() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                MissedReminder to = new MissedReminder();
                to.id = id;
                to.time = actual_time;
                to.name = name;
                to.note = note;
                realm.insertOrUpdate(to);
            }
        });
    }

    private void archiveToFinished() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                DismissedReminder to = new DismissedReminder();
                to.id = id;
                to.time = actual_time;
                to.name = name;
                to.note = note;
                realm.insertOrUpdate(to);
            }
        });
    }

    private void snooze(boolean isByUser) {
        Date _time;
        if (nextSnoozeOffTime == null) {
            _time = actual_time;
        } else {
            _time = nextSnoozeOffTime;
        }

        if (snoozeModel.isEnable) {
            Calendar currentTime = Calendar.getInstance();
            if (currentTime.getTime().after(_time)) { // Set snooze only if current time is past alarm time or previous snooze time.
                nextSnoozeOffTime = null; // RESET
                Calendar nextSnoozeOff = Calendar.getInstance();
                nextSnoozeOff.setTime(_time);
                switch (snoozeModel.intervalOption) {
                    default:
                    case M5:
                        nextSnoozeOff.add(Calendar.MINUTE, 5);
                        break;
                    case M10:
                        nextSnoozeOff.add(Calendar.MINUTE, 10);
                        break;
                    case M15:
                        nextSnoozeOff.add(Calendar.MINUTE, 15);
                        break;
                    case M30:
                        nextSnoozeOff.add(Calendar.MINUTE, 30);
                        break;
                }
                switch (snoozeModel.countOptions) {
                    default:
                    case R3:
                        if (snoozeModel.count < 3) {
                            snoozeModel.count++;
                            nextSnoozeOffTime = nextSnoozeOff.getTime();
                        }
                        break;
                    case R5:
                        if (snoozeModel.count < 5) {
                            snoozeModel.count++;
                            nextSnoozeOffTime = nextSnoozeOff.getTime();
                        }
                        break;
                    case RC:
                        snoozeModel.count++;
                        nextSnoozeOffTime = nextSnoozeOff.getTime();
                        break;
                }

                if (nextSnoozeOffTime == null) { // Next snooze time null means there is no more alarms and it has reached its EOF:
                    showToast("Dismissing from snooze! " + intId);
                    if (isByUser) {
                        dismissByUser();
                    } else {
                        dismissByApp(Calendar.getInstance());
                    }
                } else if (currentTime.getTime().after(nextSnoozeOffTime)) { // Snooze makes no sense if its in past!
                    showToast("Dismissing from snooze! " + intId);
                    if (isByUser) {
                        dismissByUser();
                    } else {
                        dismissByApp(Calendar.getInstance());
                    }
                } else {
                    showToast("Snoozing! " + intId);
                    trySaveAndSetAlert(false, false);
                }
            }
        }
    }

    private void raiseAlert() {
        PowerManager powerManager = (PowerManager) application.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= 21) { // Show heads up notification if screen is on
            boolean isScreenOn = powerManager.isInteractive();
            if (isScreenOn) {
                String timeStamp;
                if (nextSnoozeOffTime == null) {
                    timeStamp = UtilsDateTime.toTimeString(actual_time) + " " + intId;
                } else {
                    timeStamp = UtilsDateTime.toTimeString(actual_time) + " & was snoozed for " + snoozeModel.count + " times" + " " + intId;
                }
                //ALERT_INTENT_SNOOZE_ALERT
                PendingIntent snoozePendingIntent = PendingIntent
                        .getBroadcast(application.getApplicationContext(), intId, createNotificationActionBroadcastIntent(true, ALERT_INTENT_SNOOZE_ALERT), PendingIntent.FLAG_CANCEL_CURRENT);

                //ALERT_INTENT_DISMISS_ALERT
                PendingIntent dismissPendingIntent = PendingIntent
                        .getBroadcast(application.getApplicationContext(), intId, createNotificationActionBroadcastIntent(true, ALERT_INTENT_DISMISS_ALERT), PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(application.getApplicationContext(), ALARM_NOTIFICATION_CHANNEL_ID)
                        .addAction(R.drawable.ic_reminder_snooze, application.getApplicationContext().getString(R.string.btn_snooze), snoozePendingIntent)
                        .addAction(R.drawable.ic_reminder_dismiss, application.getApplicationContext().getString(R.string.btn_alarm_action_dismiss), dismissPendingIntent)
                        .setContentTitle(name)
                        .setContentText(timeStamp)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(note))
                        //.setSubText(note)
                        .setSmallIcon(R.drawable.ic_reminder_time)
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                        .setWhen(0)
                        .setCategory(NotificationCompat.CATEGORY_ALARM);

                builder.setContentIntent(PendingIntent
                        .getActivity(application.getApplicationContext(), intId, createLockScreenAlertIntent(ALERT_NOTIFICATION_CONTENT_INTENT_ACTION), PendingIntent.FLAG_UPDATE_CURRENT));

                builder.setFullScreenIntent(PendingIntent
                                .getActivity(application.getApplicationContext(), intId, createLockScreenAlertIntent(ALERT_NOTIFICATION_FULLSCREEN_INTENT_ACTION), PendingIntent.FLAG_UPDATE_CURRENT),
                        true);

                builder.setLocalOnly(true);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build());
            } else {
                raiseFullScreenAlert();
            }
        } else { // Show full screen anyway
            raiseFullScreenAlert();
        }

        raiseAlarm();
    }

    private void raiseFullScreenAlert() {
        application.getApplicationContext().startActivity(createLockScreenAlertIntent(ALERT_NOTIFICATION_FULLSCREEN_INTENT_ACTION));
    }

    private boolean trySaveAndSetAlert(boolean isResetSnooze, boolean isShowElapseTimeToast) {

        if (isResetSnooze) {
            nextSnoozeOffTime = null;
            snoozeModel.count = 0;
        }

        Date _time;
        if (nextSnoozeOffTime == null) {
            _time = actual_time;
        } else {
            _time = nextSnoozeOffTime;
        }

        Calendar calendar = Calendar.getInstance();

        if (_time.after(calendar.getTime())) {

            if (id == null) { // First save
                UUID uuid = UUID.randomUUID();
                id = uuid.toString();
                intId = (int) uuid.getMostSignificantBits();
            } else { // Update
                cancelAlarm();
            }

            final ActiveReminder reminder = new ActiveReminder();
            ReminderModel.transformToData(this, reminder);
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @ParametersAreNonnullByDefault
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(reminder);
                }
            });

            if (isEnable) {
                setAlarm(_time, isShowElapseTimeToast);
            }

            return true;

        } else {
            showToast("Cannot save reminder. The time set is in past!");
            return false;
        }
    }
    //endregion

    //region Private instance Functions : Intent Creators/Managers
    private Intent createNotificationActionBroadcastIntent(boolean isByUser, String actionName) {
        return new Intent(application.getApplicationContext(), alertBroadcastReceiverClass)
                .setAction(actionName)
                .putExtra(REMINDER_ID_INTENT, id)
                .putExtra(ALERT_INTENT_ACTION, actionName)
                .putExtra(ALERT_INTENT_IS_USER, isByUser);
    }

    private Intent createLockScreenAlertIntent(String actionName) {
        // Setting an action is important. It help distinguish between intents with other values targeting same activity
        return new Intent(application.getApplicationContext(), lockScreenAlertActivityClass)
                .setAction(actionName)
                .putExtra(ReminderModel.REMINDER_ID_INTENT, id)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
    }

    private Intent createAlarmManagerAlarmIntent() {
        return new Intent(application.getApplicationContext(), alertBroadcastReceiverClass)
                .putExtra(REMINDER_ID_INTENT, id)
                .putExtra(ALERT_INTENT_ACTION, ALERT_INTENT_RAISE_ALERT);
    }

    private PendingIntent getAlarmManagerAlarmPendingIntent(boolean isCreateNew) {
        Intent intent = createAlarmManagerAlarmIntent();
        PendingIntent pendingIntent;
        if (isCreateNew) {
            pendingIntent = PendingIntent.getBroadcast(application.getApplicationContext(), intId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(application.getApplicationContext(), intId, intent, PendingIntent.FLAG_NO_CREATE);
        }
        return pendingIntent;
    }
    //endregion

    //region Public static Functions
    public static void onAppCreate(Class<? extends BroadcastReceiver> alertReceiver, Class<? extends Service> alertService, Class<? extends Activity> ringingActivity, Application app) {
        alertBroadcastReceiverClass = alertReceiver;
        alertServiceClass = alertService;
        lockScreenAlertActivityClass = ringingActivity;
        application = app;

        // Initialize notification channels
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(application.getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannelGroup(
                    new NotificationChannelGroup(
                            DEFAULT_NOTIFICATION_GROUP_KEY,
                            DEFAULT_NOTIFICATION_GROUP_NAME));

            NotificationChannel alarmChannel = new NotificationChannel(
                    ALARM_NOTIFICATION_CHANNEL_ID,
                    ALARM_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            alarmChannel.setSound(null, null);
            alarmChannel.enableVibration(false);

            NotificationChannel defaultChannel = new NotificationChannel(
                    DEFAULT_NOTIFICATION_CHANNEL_ID,
                    DEFAULT_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            defaultChannel.setGroup(DEFAULT_NOTIFICATION_GROUP_KEY);

            notificationManager.createNotificationChannel(alarmChannel);
            notificationManager.createNotificationChannel(defaultChannel);
        }

        vibrator = (Vibrator) application.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        alarmManager = (AlarmManager) application.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        // Initialize Realm database
        Realm.init(application.getApplicationContext());
        // Force drop the database and create new in case of schema mismatch
        try {
            Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException r) {
            RealmConfiguration config = Realm.getDefaultConfiguration();
            Realm.deleteRealm(config);
        }
        reScheduleAllActive(false);
    }

    public static void onBootCompleted() {
        reScheduleAllActive(true);
    }

    public static String getReminderId(Intent intent) {
        return intent.getStringExtra(ReminderModel.REMINDER_ID_INTENT);
    }

    public static void setReminderId(Intent intent, String reminderId) {
        intent.putExtra(ReminderModel.REMINDER_ID_INTENT, reminderId);
    }

    public static void error(String message) {
        stopAlarm();
        Toast.makeText(application.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public static void showToast(String message) {
        Toast.makeText(application.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public static void notify(int Id, String title, String text, String bigText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(application.getApplicationContext(), DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder_notification)
                .setLocalOnly(true)
                .setGroup(DEFAULT_NOTIFICATION_GROUP_KEY)
                .setContentTitle(title)
                .setContentText(text);

        if (bigText != null && !bigText.isEmpty()) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(application.getApplicationContext());
        if (notificationManager != null) {
            notificationManager.notify(Id, builder.build());
        }
    }

    public static void notifySummary(String title, String text, String bigText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(application.getApplicationContext(), DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder_notification)
                .setLocalOnly(true)
                .setGroup(DEFAULT_NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .setContentTitle(title)
                .setContentText(text);

        if (bigText != null && !bigText.isEmpty()) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(application.getApplicationContext());
        if (notificationManager != null) {
            notificationManager.notify(DEFAULT_NOTIFICATION_GROUP_ID, builder.build());
        }
    }

    public static List<ActiveReminder> getActiveReminders(String name) {
        Realm realm = Realm.getDefaultInstance();
        if (name != null && !name.isEmpty()) {
            return realm.where(ActiveReminder.class).beginsWith("name", name).findAll();
        } else {
            return realm.where(ActiveReminder.class).findAll();
        }
    }

    public static List<MissedReminder> getMissedReminders(String name) {
        Realm realm = Realm.getDefaultInstance();
        if (name != null && !name.isEmpty()) {
            return realm.where(MissedReminder.class).beginsWith("name", name).findAll();
        } else {
            return realm.where(MissedReminder.class).findAll();
        }
    }

    public static List<DismissedReminder> getDismissedReminders(String name) {
        Realm realm = Realm.getDefaultInstance();
        if (name != null && !name.isEmpty()) {
            return realm.where(DismissedReminder.class).beginsWith("name", name).findAll();
        } else {
            return realm.where(DismissedReminder.class).findAll();
        }
    }

    public static void onBroadcastReceive(Context context, Intent intent) {
        if (intent != null) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (notificationManager != null) {

                String receivedAction = intent.getStringExtra(ALERT_INTENT_ACTION);
                ReminderModel reminderModel = new ReminderModel();

                if (!reminderModel.tryReadFrom(intent)) {
                    stopAlarm(); // STOP RINGING

                    //notificationManager.get
                    notificationManager.cancel(ALARM_NOTIFICATION_ID);
                    notify(DEFAULT_NOTIFICATION_ID, "Error!", "Reminder not found!", null);
                } else {

                    boolean isUser = intent.getBooleanExtra(ALERT_INTENT_IS_USER, false);
                    stopAlarm(); // STOP RINGING
                    notificationManager.cancel(ALARM_NOTIFICATION_ID); // Need to do something with previous alarm (Overlapping)

                    switch (receivedAction) {
                        case ReminderModel.ALERT_INTENT_RAISE_ALERT:
                            // START RINGING
                            reminderModel.raiseAlert();
                            break;

                        default:
                        case ReminderModel.ALERT_INTENT_SNOOZE_ALERT:
                            reminderModel.snooze(isUser);
                            break;

                        case ReminderModel.ALERT_INTENT_DISMISS_ALERT:
                            if (isUser) {
                                reminderModel.dismissByUser();
                            } else {
                                reminderModel.dismissByApp(Calendar.getInstance());
                            }
                            break;
                    }
                }
            }
        }
    }

    //endregion

    //region Public instance Members
    public String name;
    public String note;
    private Date actual_time;
    private Date given_time;

    public Uri ringToneUri = null;
    public boolean isEnableTone = true;
    public boolean isEnableVibration = true;
    public ReminderSnoozeModel snoozeModel;
    //endregion

    //region Public instance functions
    public void setTime(Date reminderTime) {
        Calendar givenTimeCal = Calendar.getInstance();
        givenTimeCal.setTime(reminderTime);
        givenTimeCal.set(Calendar.SECOND, 0);
        given_time = givenTimeCal.getTime(); // Second value should be 0 here.

        Calendar currentTimeCal = Calendar.getInstance();
        actual_time = getNextScheduleTime(currentTimeCal); // Given_time will be used if its not null.

        currentTimeCal.setTime(actual_time);
        repeatModel.customMinute = currentTimeCal.get(Calendar.MINUTE); // To preserve the minute value for various repeat options provided to user.
    }

    public Date getScheduledTime() {
        return actual_time;
    }

    public Date getGivenTime() {
        return given_time;
    }

    public Date getNextSnoozeOffTime() {
        return nextSnoozeOffTime;
    }

    public boolean isEmpty() {
        return id == null;
    }

    public boolean tryReadFrom(Intent intent) {
        if (intent == null) {
            return false;
        }

        String reminderId = intent.getStringExtra(ReminderModel.REMINDER_ID_INTENT);

        if (reminderId == null || reminderId.isEmpty()) {
            return false;
        } else {
            return tryReadFrom(reminderId);
        }

    }

    public boolean tryReadFrom(String reminderId) {
        Realm realm = Realm.getDefaultInstance();
        ActiveReminder reminderData = realm.where(ActiveReminder.class).equalTo("id", reminderId).findFirst();
        if (reminderData == null) {
            return false;
        } else {
            transformToModel(reminderData, this);
            return true;
        }
    }

    public boolean getIsEnabled() {
        return isEnable;
    }

    public boolean trySetEnabled(boolean value) {
        //isEnable = value;
        if (value) {
            //Date buffer = time;
            Calendar currentTime = Calendar.getInstance();

            if (currentTime.getTime().after(actual_time)) { //If the time is in past then find if next schedule exists
                // SET NEW TRIGGER TIME
                Date nextTime = getNextScheduleTime(currentTime);
                if (nextTime == null) { // EOF situation. No next schedule possible
                    //archiveToFinished();
                    //deleteAndCancelAlert();
                    showToast("Alarm cannot be scheduled further. Please set time into future to enable.");
                } else { // Found next trigger point.
                    actual_time = nextTime; // Set next trigger time.
                    isEnable = true;
                    //insertOrUpdateAndSetAlert(true, isShowElapseTimeToast);
                }
            } else {
                //insertOrUpdateAndSetAlert(true, isShowElapseTimeToast);
                isEnable = true;
            }
        } else {
            //insertOrUpdateAndSetAlert(false, true, isShowElapseTimeToast);
            isEnable = false;
        }

        return isEnable;
    }

    public ReminderRepeatModel.ReminderRepeatOptions getRepeatOption() {
        return repeatModel.repeatOption;
    }

    public ReminderRepeatModel beginRepeatModelChange() {
        if (repeatValueChangeBuffer == null) {
            repeatValueChangeBuffer = new ReminderRepeatModel();
            // Copy from real object:
            Calendar c = Calendar.getInstance();
            c.setTime(actual_time);
            repeatValueChangeBuffer.repeatOption = repeatModel.repeatOption;
            repeatValueChangeBuffer.customMinute = c.get(Calendar.MINUTE);
            repeatValueChangeBuffer.customHours.addAll(repeatModel.customHours);
            repeatValueChangeBuffer.customDays.addAll(repeatModel.customDays);
            repeatValueChangeBuffer.customWeeks.addAll(repeatModel.customWeeks);
            repeatValueChangeBuffer.customMonths.addAll(repeatModel.customMonths);
        }
        return repeatValueChangeBuffer;
    }

    public boolean tryEndRepeatModelChange() {
        if (repeatValueChangeBuffer == null) return false;
        switch (repeatValueChangeBuffer.repeatOption) {
            default: //NONE: HOURLY: DAILY: WEEKLY: MONTHLY: YEARLY:
                repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                repeatModel.customHours.clear();
                repeatModel.customDays.clear();
                repeatModel.customWeeks.clear();
                repeatModel.customMonths.clear();
                repeatModel.customMinute = 0;
                repeatValueChangeBuffer = null;
                actual_time = getNextScheduleTime(Calendar.getInstance());
                return true;
            case HOURLY_CUSTOM:
                if (repeatValueChangeBuffer.customHours.size() > 0) {
                    repeatModel.customHours.clear();
                    repeatModel.customDays.clear();
                    repeatModel.customWeeks.clear();
                    repeatModel.customMonths.clear();
                    repeatModel.customMinute = repeatValueChangeBuffer.customMinute;
                    repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                    repeatModel.customHours.addAll(repeatValueChangeBuffer.customHours);
                    repeatValueChangeBuffer = null;
                    actual_time = getNextScheduleTime(Calendar.getInstance());
                    return true;
                } else {
                    repeatValueChangeBuffer = null;
                    return false;
                }
            case DAILY_CUSTOM:
                if (repeatValueChangeBuffer.customDays.size() > 0) {
                    repeatModel.customHours.clear();
                    repeatModel.customDays.clear();
                    repeatModel.customWeeks.clear();
                    repeatModel.customMonths.clear();
                    repeatModel.customMinute = 0;
                    repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                    repeatModel.customDays.addAll(repeatValueChangeBuffer.customDays);
                    repeatValueChangeBuffer = null;
                    actual_time = getNextScheduleTime(Calendar.getInstance());
                    return true;
                } else {
                    repeatValueChangeBuffer = null;
                    return false;
                }
            case WEEKLY_CUSTOM:
                if (repeatValueChangeBuffer.customWeeks.size() > 0) {
                    repeatModel.customHours.clear();
                    repeatModel.customDays.clear();
                    repeatModel.customWeeks.clear();
                    repeatModel.customMonths.clear();
                    repeatModel.customMinute = 0;
                    repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                    repeatModel.customWeeks.addAll(repeatValueChangeBuffer.customWeeks);
                    repeatValueChangeBuffer = null;
                    actual_time = getNextScheduleTime(Calendar.getInstance());
                    return true;
                } else {
                    repeatValueChangeBuffer = null;
                    return false;
                }
            case MONTHLY_CUSTOM:
                if (repeatValueChangeBuffer.customMonths.size() > 0) {
                    repeatModel.customHours.clear();
                    repeatModel.customDays.clear();
                    repeatModel.customWeeks.clear();
                    repeatModel.customMonths.clear();
                    repeatModel.customMinute = 0;
                    repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                    repeatModel.customMonths.addAll(repeatValueChangeBuffer.customMonths);
                    repeatValueChangeBuffer = null;
                    actual_time = getNextScheduleTime(Calendar.getInstance());
                    return true;
                } else {
                    repeatValueChangeBuffer = null;
                    return false;
                }
        }
    }

    public String getRepeatModelString() {
        return repeatModel.toString();
    }

    public void deleteAndCancelAlert() {
        cancelAlarm();
        Realm realm = Realm.getDefaultInstance();
        final ActiveReminder reminder = realm.where(ActiveReminder.class).equalTo("id", id).findFirst();
        if (reminder != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @ParametersAreNonnullByDefault
                @Override
                public void execute(Realm realm) {
                    reminder.deleteFromRealm();
                }
            });
        }
    }

    public boolean trySaveAndSetAlert(boolean isShowElapseTimeToast) {
        return trySaveAndSetAlert(true, isShowElapseTimeToast);
    }

    public Date getNextScheduleTime(final Calendar currentTime) {
        if (given_time == null) {
            return getNextScheduleTime(currentTime, actual_time);
        } else {
            return getNextScheduleTime(currentTime, given_time);
        }
    }

    public Date getNextScheduleTime(final Calendar currentTime, final Date reminderTime) {
        /*
         * This method will look for next closest date and time to repeat from reminder set time.
         * If the time is in past then it will bring the DAY of YEAR to present and then will look for next possible schedule based on repeat settings.
         * */

        Date nextTime = null;
        Calendar nextScheduleCal = Calendar.getInstance();
        //Calculation to find next schedule will begin from current time
        nextScheduleCal.setTime(reminderTime);

        final int MINUTE = nextScheduleCal.get(Calendar.MINUTE);
        final int HOUR_OF_DAY = nextScheduleCal.get(Calendar.HOUR_OF_DAY);
        final int DAY_OF_YEAR = nextScheduleCal.get(Calendar.DAY_OF_YEAR);
        final int WEEK_OF_YEAR = nextScheduleCal.get(Calendar.WEEK_OF_YEAR);
        final int WEEK_OF_MONTH = nextScheduleCal.get(Calendar.WEEK_OF_MONTH);
        final int MONTH = nextScheduleCal.get(Calendar.MONTH);
        final int YEAR = nextScheduleCal.get(Calendar.YEAR);


        // If the time from which it needs to calculate is in past then use current time as start point
        Calendar minCal = Calendar.getInstance();
        if (reminderTime.before(currentTime.getTime())) {
            //Reminder was in past: Start calculation from present.
            nextScheduleCal.setTime(currentTime.getTime());
            minCal.setTime(currentTime.getTime());
        } else {
            //Reminder is in future. Start from there then.
            minCal.setTime(reminderTime);
        }

        //Calculation will not take seconds into consideration
        minCal.set(Calendar.SECOND, 0);
        nextScheduleCal.set(Calendar.SECOND, 0);

        if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.HOURLY) {
            // Set alarm values to current time onwards
            nextScheduleCal.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (nextScheduleCal.before(minCal)) {
                nextScheduleCal.add(Calendar.HOUR_OF_DAY, 1);
            }
            nextTime = nextScheduleCal.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.DAILY) {
            // Set alarm values to current time onwards
            nextScheduleCal.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            nextScheduleCal.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (nextScheduleCal.before(minCal)) {
                nextScheduleCal.add(Calendar.DAY_OF_YEAR, 1);
            }
            nextTime = nextScheduleCal.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.WEEKLY) {
            // Set alarm values to current time onwards
            nextScheduleCal.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            nextScheduleCal.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            nextScheduleCal.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (nextScheduleCal.before(minCal)) {
                nextScheduleCal.add(Calendar.WEEK_OF_YEAR, 1);
            }
            nextTime = nextScheduleCal.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.MONTHLY) {
            // Set alarm values to current time onwards
            nextScheduleCal.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            nextScheduleCal.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            nextScheduleCal.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (nextScheduleCal.before(minCal)) {
                nextScheduleCal.add(Calendar.MONTH, 1);
            }
            nextTime = nextScheduleCal.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.YEARLY) {
            nextScheduleCal.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            nextScheduleCal.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            nextScheduleCal.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (nextScheduleCal.before(minCal)) {
                nextScheduleCal.add(Calendar.YEAR, 1);
            }
            nextTime = nextScheduleCal.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.HOURLY_CUSTOM) {
            // Set alarm values to current time onwards
            nextScheduleCal.set(Calendar.MINUTE, MINUTE);
            // Check when is the next closest time from onwards
            Collections.sort(repeatModel.customHours);
            //Find next schedule today
            nextScheduleCal.set(Calendar.HOUR_OF_DAY, 0);
            for (int i = 0; i < repeatModel.customHours.size(); i++) {
                nextScheduleCal.set(Calendar.HOUR_OF_DAY, repeatModel.customHours.get(i));
                if (nextScheduleCal.after(minCal)) {
                    nextTime = nextScheduleCal.getTime();
                    break;
                }
            }
            if (nextTime == null) {
                //Reset and Find next schedule tomorrow :
                nextScheduleCal.set(Calendar.HOUR_OF_DAY, 0);
                nextScheduleCal.add(Calendar.DATE, 1);
                for (int i = 0; i < repeatModel.customHours.size(); i++) {
                    nextScheduleCal.set(Calendar.HOUR_OF_DAY, repeatModel.customHours.get(i));
                    if (nextScheduleCal.after(minCal)) {
                        nextTime = nextScheduleCal.getTime();
                        break;
                    }
                }
            }
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.DAILY_CUSTOM) {
            // Set alarm values to current time onwards
            nextScheduleCal.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            nextScheduleCal.set(Calendar.MINUTE, MINUTE);

            // Check when is the next closest time from onwards
            Collections.sort(repeatModel.customDays);
            //Find next schedule today
            nextScheduleCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            for (int i = 0; i < repeatModel.customDays.size(); i++) {
                nextScheduleCal.set(Calendar.DAY_OF_WEEK, repeatModel.customDays.get(i));
                if (nextScheduleCal.after(minCal)) {
                    nextTime = nextScheduleCal.getTime();
                    break;
                }
            }
            if (nextTime == null) {
                //Find next schedule next week :
                nextScheduleCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                nextScheduleCal.add(Calendar.WEEK_OF_YEAR, 1);
                for (int i = 0; i < repeatModel.customDays.size(); i++) {
                    nextScheduleCal.set(Calendar.DAY_OF_WEEK, repeatModel.customDays.get(i));
                    if (nextScheduleCal.after(minCal)) {
                        nextTime = nextScheduleCal.getTime();
                        break;
                    }
                }
            }
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM) {
            // Set alarm values to current time onwards
            nextScheduleCal.set(Calendar.DAY_OF_WEEK, Calendar.DAY_OF_WEEK);
            nextScheduleCal.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            nextScheduleCal.set(Calendar.MINUTE, MINUTE);

            // Check when is the next closest time from onwards
            Collections.sort(repeatModel.customWeeks);
            nextScheduleCal.set(Calendar.DAY_OF_MONTH, 1);
            //Find next schedule today
            for (int i = 0; i < repeatModel.customWeeks.size(); i++) {
                nextScheduleCal.set(Calendar.WEEK_OF_MONTH, repeatModel.customWeeks.get(i) + 1);
                if (nextScheduleCal.after(minCal)) {
                    nextTime = nextScheduleCal.getTime();
                    break;
                }
            }
            if (nextTime == null) {
                //Find next schedule next week :
                nextScheduleCal.set(Calendar.DAY_OF_MONTH, 1);
                nextScheduleCal.add(Calendar.MONTH, 1);
                for (int i = 0; i < repeatModel.customWeeks.size(); i++) {
                    nextScheduleCal.set(Calendar.WEEK_OF_MONTH, repeatModel.customWeeks.get(i) + 1);
                    if (nextScheduleCal.after(minCal)) {
                        nextTime = nextScheduleCal.getTime();
                        break;
                    }
                }
            }
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM) {
            // Set alarm values to current time onwards
            nextScheduleCal.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            nextScheduleCal.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            nextScheduleCal.set(Calendar.MINUTE, MINUTE);

            // Check when is the next closest time from onwards
            Collections.sort(repeatModel.customMonths);
            //Find next schedule today
            nextScheduleCal.set(Calendar.MONTH, Calendar.JANUARY);
            for (int i = 0; i < repeatModel.customMonths.size(); i++) {
                nextScheduleCal.set(Calendar.MONTH, repeatModel.customMonths.get(i));
                if (nextScheduleCal.after(minCal)) {
                    nextTime = nextScheduleCal.getTime();
                    break;
                }
            }
            if (nextTime == null) {
                //Find next schedule next year :
                nextScheduleCal.set(Calendar.MONTH, Calendar.JANUARY);
                nextScheduleCal.add(Calendar.YEAR, 1);
                for (int i = 0; i < repeatModel.customMonths.size(); i++) {
                    nextScheduleCal.set(Calendar.MONTH, repeatModel.customMonths.get(i));
                    if (nextScheduleCal.after(minCal)) {
                        nextTime = nextScheduleCal.getTime();
                        break;
                    }
                }
            }
        }
        return nextTime;
    }

    public void broadcastSnooze(boolean isByUser) {
        application.getApplicationContext().sendBroadcast(createNotificationActionBroadcastIntent(isByUser, ALERT_INTENT_SNOOZE_ALERT));
    }

    public void broadcastDismiss(boolean isByUser) {
        application.getApplicationContext().sendBroadcast(createNotificationActionBroadcastIntent(isByUser, ALERT_INTENT_DISMISS_ALERT));
    }
    //endregion


}
