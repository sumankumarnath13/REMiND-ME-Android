package com.example.remindme.viewModels;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModel;

import com.example.remindme.R;
import com.example.remindme.dataModels.ActiveReminder;
import com.example.remindme.dataModels.DismissedReminder;
import com.example.remindme.dataModels.MissedReminder;
import com.example.remindme.helpers.StringHelper;

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

    //region Constants

    //region Private Constants
    private static final String DEFAULT_NOTIFICATION_GROUP_KEY = "ÆjËèúÒ+·_²";
    private static final String DEFAULT_NOTIFICATION_GROUP_NAME = "Default notification group";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "RxLwKNdHEL";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "Other notifications";
    //endregion

    //region Public Constants
    public static final int RING_DURATION = 1000 * 60;
    public static long[] VIBRATE_PATTERN = {500, 500};
    public static final int ALARM_NOTIFICATION_ID = 117;
    public static final String ACTION_SNOOZE_ALARM = "com.example.remindme.SNOOZE.ALARM";
    public static final String ACTION_DISMISS_ALARM = "com.example.remindme.DISMISS.ALARM";
    public static final String ACTION_ALERT_NOTIFICATION_CONTENT_FULLSCREEN = "com.example.remindme.DISMISS.ALARM";
    public static final String ACTION_ALERT_NOTIFICATION_CONTENT = "com.example.remindme.£fcEB]¬B9æ";
    public static final String ACTION_ALERT_FULLSCREEN = "com.example.remindme.ALERT.FULLSCREEN";
    public static final String ACTION_CLOSE_ALARM_ACTIVITY = "com.example.remindme.CLOSE.ALARM.ACTIVITY";

    public static final String INTENT_ATTR_FROM = "FROM";
    public static final String ALARM_NOTIFICATION_CHANNEL_ID = "z_0EdcKpGP";
    public static final String ALARM_NOTIFICATION_CHANNEL_NAME = "Alarm notifications";

    public static final int DEFAULT_NOTIFICATION_GROUP_ID = 13;
    public static final String ACTION_RECEIVE_ALARM = "com.example.remindme.2eXXCW2ZrH.RECEIVE.ALARM";
    public static final String REMINDER_ID_INTENT = "uNX¯3Á×MòP";
    //endregion

    //endregion

    //region Static Members

    //region Private Static Members

    //region Private Static Variables
    private static Class<? extends BroadcastReceiver> externalBroadcastReceiverClass;
    private static Application application;
    private static AlarmManager alarmManager;
    //endregion

    //region Private Static Functions
    private static void transformToData(ReminderModel from, ActiveReminder to) {
        to.id = from.id;
        to.alarmIntentId = from.getIntId();
        to.name = from.name;
        to.note = from.note;
        to.time = from.calculatedTime == null ? from.originalTime : from.calculatedTime;
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
    //endregion

    //endregion

    //region Public Static Functions
    public static void reScheduleAllActive(boolean isDeviceRebooted) {
        final Calendar calendar = Calendar.getInstance();
        List<ActiveReminder> reminders = getActiveReminders(null);
        boolean isNewAlertFound = false;
        for (int i = 0; i < reminders.size(); i++) {
            final ActiveReminder r = reminders.get(i);
            final ReminderModel reminderModel = new ReminderModel();
            transformToModel(r, reminderModel);
            if (reminderModel.getIsEnabled()) {
                if (calendar.getTime().after(reminderModel.getAlarmTime()) && isDeviceRebooted) {
                    // App getting killed after a while. But Broadcast receiver recreating app which leads to rescheduling.
                    // And for the logic below it getting dismissed before it could be snoozed from alerts.
                    // isDeviceRebooted will prevent this from happening.
                    reminderModel.dismissByApp(calendar);
                } else if (!reminderModel.isAlertExists()) {
                    isNewAlertFound = true;

                    if (calendar.getTime().after(reminderModel.getAlarmTime())) {
                        notify(reminderModel.getIntId(), "Dismissing reminder!", reminderModel.getSignatureName(), reminderModel.note);
                        reminderModel.dismissByApp(calendar);
                    } else {
                        notify(reminderModel.getIntId(), "New reminder!", reminderModel.getSignatureName(), reminderModel.note);
                        reminderModel.setAlarm(reminderModel.getAlarmTime(), false);
                    }
                }
            }
        }
        if (isNewAlertFound) {
            notifySummary("Rescheduling reminders", null, null);
        }
    }

    public static void transformToModel(ActiveReminder from, ReminderModel to) {
        to.id = from.id;
        to.intId = from.alarmIntentId;
        to.name = from.name;
        to.note = from.note;
        to.originalTime = from.time;

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

    public static boolean tryAppCreate(Class<? extends BroadcastReceiver> broadcastReceiverClass, Application app) {
        externalBroadcastReceiverClass = broadcastReceiverClass;
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

        alarmManager = (AlarmManager) application.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        // Initialize Realm database
        Realm.init(application.getApplicationContext());
        // Force drop the database and create new in case of schema mismatch
        try {
            Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException r) {
            RealmConfiguration config = Realm.getDefaultConfiguration();
            if (config == null) {
                error("Couldn't load required configurations for Remind Me! ");
                return false;
            }
            try {
                Realm.deleteRealm(config);
            } catch (IllegalStateException e) {
                error("Error in initializing the Remind Me! " + e.getMessage());
                return false;
            }
        }

        reScheduleAllActive(false);

        return true;
    }

    public static String getReminderId(Intent intent) {
        return intent.getStringExtra(ReminderModel.REMINDER_ID_INTENT);
    }

    public static void setReminderId(Intent intent, String reminderId) {
        intent.putExtra(ReminderModel.REMINDER_ID_INTENT, reminderId);
    }

    public static void error(String message) {
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
        notificationManager.notify(Id, builder.build());
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
        notificationManager.notify(DEFAULT_NOTIFICATION_GROUP_ID, builder.build());
    }

    public static List<ActiveReminder> getActiveReminders(String name) {
        Realm realm = Realm.getDefaultInstance();
        if (name != null && !name.isEmpty()) {
            return realm.where(ActiveReminder.class).beginsWith("name", name).sort("time").findAll();
        } else {
            return realm.where(ActiveReminder.class).sort("time").findAll();
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
    //endregion

    //endregion

    //region Instanced Members

    //region Private

    //region Private Instanced Variables
    private String id;
    private int intId;
    private boolean isEnable = true;
    private final ReminderRepeatModel repeatModel;
    private final ReminderSnoozeModel snoozeModel;
    private Date nextSnoozeOffTime = null;
    private ReminderRepeatModel repeatValueChangeBuffer;
    private Date originalTime;
    private Date calculatedTime;
    //endregion

    //region Private Instanced Functions
    private boolean isAlertExists() {
        if (getIntId() != 0) {
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
        if (getIntId() != 0) {
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
            showToast(StringHelper.trimEnd(stringBuilder.toString(), ","));
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
                to.time = originalTime;
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
                to.time = originalTime;
                to.name = name;
                to.note = note;
                realm.insertOrUpdate(to);
            }
        });
    }

    private Date getAlarmTime() {
        Date _time;
        if (nextSnoozeOffTime == null) {
            _time = calculatedTime == null ? originalTime : calculatedTime;
        } else {
            _time = nextSnoozeOffTime;
        }
        return _time;
    }

    private boolean trySaveAndSetAlert(boolean isResetSnooze, boolean isShowElapseTimeToast) {

        if (isResetSnooze) {
            nextSnoozeOffTime = null;
            snoozeModel.count = 0;
        }

        Date _time = getAlarmTime();
        if (_time.after(Calendar.getInstance().getTime())) {

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

    private Date getNextScheduleTime(final Calendar currentTime, final Date reminderBaseTime) {
        /*
         * This method will look for next closest date and time to repeat from reminder set time.
         * If the time is in past then it will bring the DAY of YEAR to present and then will look for next possible schedule based on repeat settings.
         * This method will return a non null value only if there is a dat can reached in future.
         * */

        Date nextTime = null;
        final Calendar reminderCal = Calendar.getInstance();
        //Calculation to find next schedule will begin from current time
        reminderCal.setTime(reminderBaseTime);

        final int MINUTE = reminderCal.get(Calendar.MINUTE);
        final int HOUR_OF_DAY = reminderCal.get(Calendar.HOUR_OF_DAY);
        final int DAY_OF_YEAR = reminderCal.get(Calendar.DAY_OF_YEAR);
//        final int WEEK_OF_YEAR = baseTimeCal.get(Calendar.WEEK_OF_YEAR);
//        final int WEEK_OF_MONTH = baseTimeCal.get(Calendar.WEEK_OF_MONTH);
//        final int MONTH = baseTimeCal.get(Calendar.MONTH);
//        final int YEAR = baseTimeCal.get(Calendar.YEAR);


        // If the time from which it needs to calculate is in past then use current time as start point
        final Calendar baseCl = Calendar.getInstance();
        if (reminderCal.before(currentTime)) {
            //Reminder was in past: Start calculation from present.
            baseCl.setTime(currentTime.getTime());
        } else {
            //Reminder is in future. Start from there then.
            baseCl.setTime(reminderBaseTime);
        }

        final Calendar newScheduleCl = Calendar.getInstance();
        // Any reminder time be it base or calculated will not take seconds/mil.s. into consideration. However, current time must contain it to compare if its in past or future precisely.
        newScheduleCl.set(Calendar.SECOND, 0);
        newScheduleCl.set(Calendar.MILLISECOND, 0);

        if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.HOURLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.HOUR_OF_DAY, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.DAILY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.DAY_OF_YEAR, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.WEEKLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.WEEK_OF_YEAR, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.MONTHLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.MONTH, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.YEARLY) {
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.YEAR, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.HOURLY_CUSTOM) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Check when is the next closest time from onwards
            Collections.sort(repeatModel.customHours);
            //Find next schedule today
            //nextScheduleCal.set(Calendar.HOUR_OF_DAY, 0);
            for (int i = 0; i < repeatModel.customHours.size(); i++) {
                newScheduleCl.set(Calendar.HOUR_OF_DAY, repeatModel.customHours.get(i));
                if (newScheduleCl.compareTo(baseCl) >= 0) {
                    nextTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextTime == null) {
                //Reset and Find next schedule tomorrow :
                newScheduleCl.set(Calendar.HOUR_OF_DAY, 0);
                newScheduleCl.add(Calendar.DATE, 1);
                for (int i = 0; i < repeatModel.customHours.size(); i++) {
                    newScheduleCl.set(Calendar.HOUR_OF_DAY, repeatModel.customHours.get(i));
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextTime = newScheduleCl.getTime();
                        break;
                    }
                }
            }
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.DAILY_CUSTOM) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);

            // Check when is the next closest time from onwards
            Collections.sort(repeatModel.customDays);
            //Find next schedule today
            //nextScheduleCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            for (int i = 0; i < repeatModel.customDays.size(); i++) {
                newScheduleCl.set(Calendar.DAY_OF_WEEK, repeatModel.customDays.get(i));
                if (newScheduleCl.compareTo(baseCl) >= 0) {
                    nextTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextTime == null) {
                //Find next schedule next week :
                newScheduleCl.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                newScheduleCl.add(Calendar.WEEK_OF_YEAR, 1);
                for (int i = 0; i < repeatModel.customDays.size(); i++) {
                    newScheduleCl.set(Calendar.DAY_OF_WEEK, repeatModel.customDays.get(i));
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextTime = newScheduleCl.getTime();
                        break;
                    }
                }
            }
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_WEEK, Calendar.DAY_OF_WEEK);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);

            // Check when is the next closest time from onwards
            Collections.sort(repeatModel.customWeeks);
            //nextScheduleCal.set(Calendar.DAY_OF_MONTH, 1);
            //Find next schedule today
            for (int i = 0; i < repeatModel.customWeeks.size(); i++) {
                newScheduleCl.set(Calendar.WEEK_OF_MONTH, repeatModel.customWeeks.get(i) + 1);
                if (newScheduleCl.compareTo(baseCl) >= 0) {
                    nextTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextTime == null) {
                //Find next schedule next week :
                newScheduleCl.set(Calendar.DAY_OF_MONTH, 1);
                newScheduleCl.add(Calendar.MONTH, 1);
                for (int i = 0; i < repeatModel.customWeeks.size(); i++) {
                    newScheduleCl.set(Calendar.WEEK_OF_MONTH, repeatModel.customWeeks.get(i) + 1);
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextTime = newScheduleCl.getTime();
                        break;
                    }
                }
            }
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);

            // Check when is the next closest time from onwards
            Collections.sort(repeatModel.customMonths);
            //Find next schedule today
            //nextScheduleCal.set(Calendar.MONTH, Calendar.JANUARY);
            for (int i = 0; i < repeatModel.customMonths.size(); i++) {
                newScheduleCl.set(Calendar.MONTH, repeatModel.customMonths.get(i));
                if (newScheduleCl.compareTo(baseCl) >= 0) {
                    nextTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextTime == null) {
                //Find next schedule next year :
                newScheduleCl.set(Calendar.MONTH, Calendar.JANUARY);
                newScheduleCl.add(Calendar.YEAR, 1);
                for (int i = 0; i < repeatModel.customMonths.size(); i++) {
                    newScheduleCl.set(Calendar.MONTH, repeatModel.customMonths.get(i));
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextTime = newScheduleCl.getTime();
                        break;
                    }
                }
            }
        }
        return nextTime;
    }

    private Intent createAlarmManagerAlarmIntent() {
        return new Intent(application.getApplicationContext(), externalBroadcastReceiverClass)
                .setAction(ACTION_RECEIVE_ALARM)
                .putExtra(REMINDER_ID_INTENT, id);
    }

    private PendingIntent getAlarmManagerAlarmPendingIntent(boolean isCreateNew) {
        Intent intent = createAlarmManagerAlarmIntent();
        PendingIntent pendingIntent;
        if (isCreateNew) {
            pendingIntent = PendingIntent.getBroadcast(application.getApplicationContext(), getIntId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(application.getApplicationContext(), getIntId(), intent, PendingIntent.FLAG_NO_CREATE);
        }
        return pendingIntent;
    }
    //endregion

    //endregion

    //region Public

    //region Public Instanced Functions/Constructor
    public ReminderModel() {
        id = null;
        repeatModel = new ReminderRepeatModel();
        snoozeModel = new ReminderSnoozeModel();
    }

    public ReminderSnoozeModel getSnoozeModel() {
        return snoozeModel;
    }

    public int getIntId() {
        return intId;
    }

    public String getSignatureName() {
        if (StringHelper.isEmpty(name)) {
            return String.valueOf(getIntId());
        } else {
            return getIntId() +
                    " - " +
                    name;
        }
    }

    public void dismissByUser() {
        Date nextTime = getNextScheduleTime(Calendar.getInstance(), originalTime);
        if (nextTime == null) { // EOF situation
            archiveToFinished();
            deleteAndCancelAlert();
        } else {
            calculatedTime = nextTime; // Set next trigger time.
            //String net = UtilsDateTime.toTimeDateString(time);
            trySaveAndSetAlert(true, false); // Save changes. // Set alarm for next trigger time.
        }
    }

    public void dismissByApp(final Calendar currentTime) {
        Date nextTime = getNextScheduleTime(currentTime, originalTime);
        archiveToMissed();
        Toast.makeText(application.getApplicationContext(), "Dismissing to missed! " + getIntId(), Toast.LENGTH_LONG).show();
        if (nextTime == null) { // EOF situation
            deleteAndCancelAlert();
        } else {
            calculatedTime = nextTime; // Set next trigger time.
            trySaveAndSetAlert(true, false); // Save changes. // Set alarm for next trigger time.
        }
    }

    public void snooze(boolean isByUser) {
        Date _time = getAlarmTime();

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
                    showToast("Dismissing from snooze! " + getIntId());
                    if (isByUser) {
                        dismissByUser();
                    } else {
                        dismissByApp(Calendar.getInstance());
                    }
                } else if (currentTime.getTime().after(nextSnoozeOffTime)) { // Snooze makes no sense if its in past!
                    showToast("Dismissing from snooze! " + getIntId());
                    if (isByUser) {
                        dismissByUser();
                    } else {
                        dismissByApp(Calendar.getInstance());
                    }
                } else {
                    showToast("Snoozing! " + getIntId());
                    trySaveAndSetAlert(false, false);
                }
            }
        }
    }
    //endregion

    //region Public Instanced Members
    public String name;
    public String note;
    public Uri ringToneUri = null;
    public boolean isEnableTone = true;
    public boolean isEnableVibration = true;
    //endregion

    //region Public instance functions
    public void setOriginalTime(Date userTime) {
        Calendar userTimeCl = Calendar.getInstance();
        userTimeCl.setTime(userTime);
        userTimeCl.set(Calendar.SECOND, 0);
        userTimeCl.set(Calendar.MILLISECOND, 0);
        originalTime = userTimeCl.getTime();

        switch (repeatModel.repeatOption) {
            default:
                if (Calendar.getInstance().compareTo(userTimeCl) >= 0) {
                    //If the user value "effectively" is in past then calculate next schedule.
                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime); // Given_time will be used if its not null.
                } else {
                    // If not then no need of calculated time. The given time will be used.
                    calculatedTime = null;
                }
                break;
            case HOURLY_CUSTOM:
            case DAILY_CUSTOM:
            case WEEKLY_CUSTOM:
            case MONTHLY_CUSTOM:
                calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime); // Given_time will be used if its not null.
                repeatModel.customMinute = userTimeCl.get(Calendar.MINUTE); // To preserve the minute value for various repeat options provided to user.
                break;
        }
    }

    public Date getOriginalTime() {
        return originalTime;
    }

    public Date getCalculatedTime() {
        return calculatedTime;
    }

    public Date getNextSnoozeOffTime() {
        return nextSnoozeOffTime;
    }

    public boolean getIsEmpty() {
        return id == null;
    }

    public boolean getIsHasDifferentTimeCalculated() {
        return calculatedTime != null && !calculatedTime.equals(originalTime);
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

            if (currentTime.getTime().after(originalTime)) { //If the time is in past then find if next schedule exists
                // SET NEW TRIGGER TIME
                Date nextTime = getNextScheduleTime(currentTime, originalTime);
                if (nextTime == null) { // EOF situation. No next schedule possible
                    //archiveToFinished();
                    //deleteAndCancelAlert();
                    showToast("Alarm cannot be scheduled further. Please set time into future to enable.");
                } else { // Found next trigger point.
                    calculatedTime = nextTime; // Set next trigger time.
                    isEnable = true;
                }
            } else {
                isEnable = true;
            }
        } else {
            isEnable = false;
        }

        return isEnable;
    }

    public ReminderRepeatModel.ReminderRepeatOptions getRepeatOption() {
        return repeatModel.repeatOption;
    }

    public ReminderRepeatModel getRepeatSettings() {
        if (repeatValueChangeBuffer == null) {
            //Make a new instance copied from original. This way original repeat settings wont get affected until applied by method "trySetReminderRepeatModel"
            repeatValueChangeBuffer = new ReminderRepeatModel();
            // Copy from real object:
            Calendar c = Calendar.getInstance();
            c.setTime(originalTime);
            repeatValueChangeBuffer.repeatOption = repeatModel.repeatOption;
            repeatValueChangeBuffer.customMinute = c.get(Calendar.MINUTE);
            repeatValueChangeBuffer.customHours.addAll(repeatModel.customHours);
            repeatValueChangeBuffer.customDays.addAll(repeatModel.customDays);
            repeatValueChangeBuffer.customWeeks.addAll(repeatModel.customWeeks);
            repeatValueChangeBuffer.customMonths.addAll(repeatModel.customMonths);
        }
        return repeatValueChangeBuffer;
    }

    public boolean trySetRepeatSettingChanges() {
        if (repeatValueChangeBuffer == null) return false;
        switch (repeatValueChangeBuffer.repeatOption) {
            default: //NONE: HOURLY: DAILY: WEEKLY: MONTHLY: YEARLY:
                this.repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                this.repeatModel.customHours.clear();
                this.repeatModel.customDays.clear();
                this.repeatModel.customWeeks.clear();
                this.repeatModel.customMonths.clear();
                this.repeatModel.customMinute = 0;
                calculatedTime = null;
                discardRepeatSettingChanges();
                return true;
            case HOURLY_CUSTOM:
                if (repeatValueChangeBuffer.customHours.size() > 0) {
                    this.repeatModel.customHours.clear();
                    this.repeatModel.customDays.clear();
                    this.repeatModel.customWeeks.clear();
                    this.repeatModel.customMonths.clear();
                    this.repeatModel.customMinute = repeatValueChangeBuffer.customMinute;
                    this.repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                    this.repeatModel.customHours.addAll(repeatValueChangeBuffer.customHours);
                    //Reminder time will be different than given time only if if Custom option are selected.
                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime);
                    discardRepeatSettingChanges();
                    return true;
                } else {
                    discardRepeatSettingChanges();
                    return false;
                }
            case DAILY_CUSTOM:
                if (repeatValueChangeBuffer.customDays.size() > 0) {
                    this.repeatModel.customHours.clear();
                    this.repeatModel.customDays.clear();
                    this.repeatModel.customWeeks.clear();
                    this.repeatModel.customMonths.clear();
                    this.repeatModel.customMinute = 0;
                    this.repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                    this.repeatModel.customDays.addAll(repeatValueChangeBuffer.customDays);
                    //Reminder time will be different than given time only if if Custom option are selected.
                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime);
                    discardRepeatSettingChanges();
                    return true;
                } else {
                    discardRepeatSettingChanges();
                    return false;
                }
            case WEEKLY_CUSTOM:
                if (repeatValueChangeBuffer.customWeeks.size() > 0) {
                    this.repeatModel.customHours.clear();
                    this.repeatModel.customDays.clear();
                    this.repeatModel.customWeeks.clear();
                    this.repeatModel.customMonths.clear();
                    this.repeatModel.customMinute = 0;
                    this.repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                    this.repeatModel.customWeeks.addAll(repeatValueChangeBuffer.customWeeks);
                    //Reminder time will be different than given time only if if Custom option are selected.
                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime);
                    discardRepeatSettingChanges();
                    return true;
                } else {
                    discardRepeatSettingChanges();
                    return false;
                }
            case MONTHLY_CUSTOM:
                if (repeatValueChangeBuffer.customMonths.size() > 0) {
                    this.repeatModel.customHours.clear();
                    this.repeatModel.customDays.clear();
                    this.repeatModel.customWeeks.clear();
                    this.repeatModel.customMonths.clear();
                    this.repeatModel.customMinute = 0;
                    this.repeatModel.repeatOption = repeatValueChangeBuffer.repeatOption;
                    this.repeatModel.customMonths.addAll(repeatValueChangeBuffer.customMonths);
                    //Reminder time will be different than given time only if if Custom option are selected.
                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime);
                    discardRepeatSettingChanges();
                    return true;
                } else {
                    discardRepeatSettingChanges();
                    return false;
                }
        }
    }

    public void discardRepeatSettingChanges() {
        repeatValueChangeBuffer = null;
    }

    public String getRepeatSettingString() {
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
    //endregion

    //endregion

    //endregion

}
