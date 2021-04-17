package com.example.remindme.viewModels;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModel;

import com.example.remindme.dataModels.ActiveReminder;
import com.example.remindme.dataModels.DismissedReminder;
import com.example.remindme.dataModels.MissedReminder;
import com.example.remindme.helpers.NotificationHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;

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
    public static final String DEFAULT_NOTIFICATION_GROUP_KEY = "ÆjËèúÒ+·_²";
    private static final String DEFAULT_NOTIFICATION_GROUP_NAME = "Default notification group";
    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "RxLwKNdHEL";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "Other notifications";
    //endregion

    //region Public Constants
    public static final int MINIMUM_INPUT_VOLUME_PERCENTAGE = 10;
    public static final int RING_DURATION = 1000 * 60;
    public static final long[] VIBRATE_PATTERN = {500, 500};
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

    private static Class<? extends BroadcastReceiver> externalBroadcastReceiverClass;

    private int alarmVolumePercentage;

    public int getAlarmVolumePercentage() {
        return alarmVolumePercentage;
    }

    public void setAlarmVolumePercentage(int value) {
        alarmVolumePercentage = Math.min(Math.max(value, 0), 100);
    }

    private String id;

    public static String getReminderId(Intent intent) {
        return intent.getStringExtra(ReminderModel.REMINDER_ID_INTENT);
    }

    public static void setReminderId(Intent intent, String reminderId) {
        intent.putExtra(ReminderModel.REMINDER_ID_INTENT, reminderId);
    }

    public boolean isNew() {
        return id == null;
    }

    private int intId;

    public int getIntId() {
        return intId;
    }

    private boolean isEnabled = true;

    public boolean getIsEnabled() {
        return isEnabled;
    }

    private final ReminderRepeatModel repeatModel;

    private final ReminderSnoozeModel snoozeModel;

    public ReminderSnoozeModel getSnoozeModel() {
        return snoozeModel;
    }

    private Date nextSnoozeOffTime = null;
    private ReminderRepeatModel repeatValueChangeBuffer;

    private Date originalTime;

    public void setOriginalTime(Date userTime) {
        Calendar userTimeCl = Calendar.getInstance();
        userTimeCl.setTime(userTime);
        userTimeCl.set(Calendar.SECOND, 0);
        userTimeCl.set(Calendar.MILLISECOND, 0);
        originalTime = userTimeCl.getTime();
        //isOriginalTimeChanged = true;
        repeatModel.setReminderTime(userTimeCl); //Repeat model has complex calculations for which it needs a reference to the original time.

        switch (repeatModel.getRepeatOption()) {
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
            case OTHER:
                calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime); // Given_time will be used if its not null.
                break;
        }
    }

    public Date getOriginalTime() {
        return originalTime;
    }

    private Date calculatedTime;

    public Date getCalculatedTime() {
        return calculatedTime;
    }

    public Date getNextSnoozeOffTime() {
        return nextSnoozeOffTime;
    }

    public boolean getIsHasDifferentTimeCalculated() {
        return calculatedTime != null && !calculatedTime.equals(originalTime);
    }

    private boolean increaseVolumeGradually;

    public boolean isIncreaseVolumeGradually() {
        return this.increaseVolumeGradually;
    }

    public void setIncreaseVolumeGradually(boolean value) {
        increaseVolumeGradually = value;
    }

    public String name;
    public String note;
    private Uri ringToneUri = null;

    public Uri getRingToneUri() {
        return ringToneUri;
    }

    public CharSequence getRingToneUriSummary(Context context) {
        Uri alarmToneUri = getRingToneUri();

        if (alarmToneUri == null) {
            alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmToneUri);
        return ringtone.getTitle(context);
    }

    public void setRingToneUri(Uri value) {
        ringToneUri = value;
    }

    public boolean isEnableTone = true;
    public boolean isEnableVibration = true;


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
        to.isEnable = from.isEnabled;
        to.isVibrate = from.isEnableVibration;

        to.increaseVolumeGradually = from.increaseVolumeGradually;
        to.alarmVolume = from.alarmVolumePercentage;

        to.repeatHours.clear();
        to.repeatDays.clear();
        to.repeatWeeks.clear();
        to.repeatMonths.clear();

        switch (from.repeatModel.getRepeatOption()) {
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
            case OTHER:
                to.repeatOption = 6;
                to.customTimeUnit = ReminderRepeatModel.transform(from.repeatModel.getCustomTimeUnit());
                to.customTimeValue = from.repeatModel.getCustomTimeValue();
                break;
        }

        to.isHasRepeatEnd = from.repeatModel.isHasRepeatEnd();
        to.repeatEndDate = from.repeatModel.getRepeatEndDate();

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
    public static void reScheduleAllActive(Context context, boolean isDeviceRebooted) {
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
                    reminderModel.dismissByApp(context, calendar);
                } else if (!reminderModel.isAlertExists(context)) {
                    isNewAlertFound = true;

                    if (calendar.getTime().after(reminderModel.getAlarmTime())) {
                        NotificationHelper.notify(context, reminderModel.getIntId(), "Dismissing reminder!", reminderModel.getSignatureName(), reminderModel.note);
                        reminderModel.dismissByApp(context, calendar);
                    } else {
                        NotificationHelper.notify(context, reminderModel.getIntId(), "New reminder!", reminderModel.getSignatureName(), reminderModel.note);
                        reminderModel.setAlarm(context, reminderModel.getAlarmTime(), false);
                    }
                }
            }
        }
        if (isNewAlertFound) {
            NotificationHelper.notifySummary(context, "Rescheduling reminders", null, null);
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
        to.isEnabled = from.isEnable;
        to.isEnableVibration = from.isVibrate;

        to.increaseVolumeGradually = from.increaseVolumeGradually;
        to.alarmVolumePercentage = from.alarmVolume;

        to.repeatModel.setReminderTime(from.time);
        to.repeatModel.customHours.clear();
        to.repeatModel.customDays.clear();
        to.repeatModel.customWeeks.clear();
        to.repeatModel.customMonths.clear();

        switch (from.repeatOption) {
            default:
            case 0:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.NONE);
                break;
            case 1:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.HOURLY);
                break;
            case 11:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.HOURLY_CUSTOM);
                to.repeatModel.customHours.addAll(from.repeatHours);
                break;
            case 2:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.DAILY);
                break;
            case 21:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.DAILY_CUSTOM);
                to.repeatModel.customDays.addAll(from.repeatDays);
                break;
            case 3:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.WEEKLY);
                break;
            case 31:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM);
                to.repeatModel.customWeeks.addAll(from.repeatWeeks);
                break;
            case 4:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.MONTHLY);
                break;
            case 41:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM);
                to.repeatModel.customMonths.addAll(from.repeatMonths);
                break;
            case 5:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.YEARLY);
                break;
            case 6:
                to.repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.OTHER);
                to.repeatModel.setRepeatCustom(from.customTimeUnit, from.customTimeValue);
                break;
        }
        to.repeatModel.setRepeatEndDate(from.repeatEndDate);
        to.repeatModel.setHasRepeatEnd(from.isHasRepeatEnd);

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

    public static boolean tryAppCreate(Class<? extends BroadcastReceiver> broadcastReceiverClass, Context context) {
        externalBroadcastReceiverClass = broadcastReceiverClass;

        // Initialize notification channels
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
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

        // Initialize Realm database
        Realm.init(context);
        // Force drop the database and create new in case of schema mismatch
        try {
            Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException r) {
            RealmConfiguration config = Realm.getDefaultConfiguration();
            if (config == null) {
                ToastHelper.showError(context, "Couldn't load required configurations for Remind Me! ");
                return false;
            }
            try {
                Realm.deleteRealm(config);
            } catch (IllegalStateException e) {
                ToastHelper.showError(context, "Error in initializing the Remind Me! " + e.getMessage());
                return false;
            }
        }

        reScheduleAllActive(context, false);

        return true;
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

    private boolean isAlertExists(Context context) {
        if (getIntId() == 0) {
            return false;
        } else {
            PendingIntent pendingIntent = getAlarmManagerAlarmPendingIntent(context, false);
            return pendingIntent != null;
        }
    }

    private void cancelAlarm(Context context) {
        if (getIntId() != 0) {
            PendingIntent pendingIntent = getAlarmManagerAlarmPendingIntent(context, false);
            if (pendingIntent != null) {
                OsHelper.getAlarmManager(context).cancel(pendingIntent);
            }
        }
    }

    private void setAlarm(Context context, Date atTime, boolean isShowElapseTimeToast) {
        if (!isEnabled) {
            return;
        }

        Calendar calendar = Calendar.getInstance();

        long different = atTime.getTime() - calendar.getTime().getTime();

        if (different <= 0) // Meaningless to set time in past. BUG ALERT: negative value means something is very wrong somewhere
        {
            if (isShowElapseTimeToast) {
                ToastHelper.showError(context, "Warning! discarding minus time value for alarm");
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

        if (OsHelper.isMarshmallowOrLater()) {
            OsHelper.getAlarmManager(context).setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getAlarmManagerAlarmPendingIntent(context, true));
        } else {
            OsHelper.getAlarmManager(context).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getAlarmManagerAlarmPendingIntent(context, true));
        }

        if (isShowElapseTimeToast) {
            if (elapsedDays > 0 || elapsedHours > 0 || elapsedMinutes > 0 || elapsedSeconds > 0) {

                StringBuilder stringBuilder = new StringBuilder("Alarm is set after ");

                if (elapsedDays > 0) {
                    stringBuilder.append(elapsedDays);
                    stringBuilder.append(" days,");
                }

                if (elapsedHours > 0) {
                    stringBuilder.append(elapsedHours);
                    stringBuilder.append(" hours,");
                }

                if (elapsedMinutes > 0) {
                    stringBuilder.append(elapsedMinutes);
                    stringBuilder.append(" minutes,");
                }

                if (elapsedSeconds > 0) {
                    stringBuilder.append(elapsedSeconds);
                    stringBuilder.append(" seconds,");
                }

                stringBuilder.append(" from now");
                ToastHelper.showLong(context, StringHelper.trimEnd(stringBuilder.toString(), ","));
            } else {
                ToastHelper.showLong(context, "Alarm is set");
            }
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
        final Date _time;

        if (getIsHasDifferentTimeCalculated()) {
            _time = calculatedTime;
        } else if (nextSnoozeOffTime == null) {
            _time = originalTime;
        } else {
            _time = nextSnoozeOffTime;
        }

        return _time;
    }

    private void saveToDb() {
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
    }

    public boolean trySaveAndSetAlert(Context context, boolean isResetSnooze, boolean isShowElapseTimeToast) {

        if (getAlarmTime().after(Calendar.getInstance().getTime())) {

            if (isNew()) { // New reminder. First save

                UUID uuid = UUID.randomUUID();
                id = uuid.toString();
                intId = (int) uuid.getMostSignificantBits();

            } else { // Edit reminder

                cancelAlarm(context);

                if (isResetSnooze) {
                    nextSnoozeOffTime = null;
                    snoozeModel.count = 0;
                }

            }

            saveToDb();

            if (isEnabled) {
                setAlarm(context, getAlarmTime(), isShowElapseTimeToast);
            }

            return true;


        } else {

            ToastHelper.showLong(context, "Cannot save reminder. The time set is in past!");
            return false;

        }


//        if (!isOriginalTimeChanged) {
//            Date _time = getAlarmTime();
//            if (_time.after(Calendar.getInstance().getTime())) {
//
//                if (id == null) { // First save
//                    UUID uuid = UUID.randomUUID();
//                    id = uuid.toString();
//                    intId = (int) uuid.getMostSignificantBits();
//                } else { // Update
//                    cancelAlarm(context);
//                }
//
//                final ActiveReminder reminder = new ActiveReminder();
//                ReminderModel.transformToData(this, reminder);
//                Realm realm = Realm.getDefaultInstance();
//                realm.executeTransaction(new Realm.Transaction() {
//                    @ParametersAreNonnullByDefault
//                    @Override
//                    public void execute(Realm realm) {
//                        realm.insertOrUpdate(reminder);
//                    }
//                });
//
//                if (isEnable) {
//                    setAlarm(context, _time, isShowElapseTimeToast);
//                }
//
//                return true;
//
//            } else {
//                ToastHelper.toast(context, "Cannot save reminder. The time set is in past!");
//                return false;
//            }
//        } else {
//            if (id == null) { // First save
//                UUID uuid = UUID.randomUUID();
//                id = uuid.toString();
//                intId = (int) uuid.getMostSignificantBits();
//            } else { // Update
//                cancelAlarm(context);
//            }
//
//            final ActiveReminder reminder = new ActiveReminder();
//            ReminderModel.transformToData(this, reminder);
//            Realm realm = Realm.getDefaultInstance();
//            realm.executeTransaction(new Realm.Transaction() {
//                @ParametersAreNonnullByDefault
//                @Override
//                public void execute(Realm realm) {
//                    realm.insertOrUpdate(reminder);
//                }
//            });
//
//            if (isEnable) {
//                setAlarm(context, _time, isShowElapseTimeToast);
//            }
//
//            return true;
//        }


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

        if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.HOURLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.HOUR_OF_DAY, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.DAILY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.DAY_OF_YEAR, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.WEEKLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.WEEK_OF_YEAR, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.MONTHLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.MONTH, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.YEARLY) {
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.YEAR, 1);
            }
            nextTime = newScheduleCl.getTime();
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.HOURLY_CUSTOM) {
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
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.DAILY_CUSTOM) {
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
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM) {
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
                //Find next schedule next month :
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
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM) {
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
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.OTHER) {
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                switch (repeatModel.getCustomTimeUnit()) {
                    case DAYS:
                        newScheduleCl.add(Calendar.DAY_OF_YEAR, repeatModel.getCustomTimeValue());
                        break;
                    case WEEKS:
                        newScheduleCl.add(Calendar.WEEK_OF_YEAR, repeatModel.getCustomTimeValue());
                        break;
                    case MONTHS:
                        newScheduleCl.add(Calendar.MONTH, repeatModel.getCustomTimeValue());
                        break;
//                    case YEARS:
//                        newScheduleCl.add(Calendar.YEAR, repeatModel.customTimeValue);
//                        break;
                }
            }
            nextTime = newScheduleCl.getTime();
        }

        if (nextTime != null && repeatModel.getRepeatEndDate() != null) {
            return nextTime.after(repeatModel.getRepeatEndDate()) ? null : nextTime;
        }

        return nextTime;
    }

    private Intent createAlarmManagerAlarmIntent(Context context) {
        return new Intent(context, externalBroadcastReceiverClass)
                .setAction(ACTION_RECEIVE_ALARM)
                .putExtra(REMINDER_ID_INTENT, id);
    }

    private PendingIntent getAlarmManagerAlarmPendingIntent(Context context, boolean isCreateNew) {
        Intent intent = createAlarmManagerAlarmIntent(context);
        PendingIntent pendingIntent;
        if (isCreateNew) {
            pendingIntent = PendingIntent.getBroadcast(context, getIntId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, getIntId(), intent, PendingIntent.FLAG_NO_CREATE);
        }
        return pendingIntent;
    }

    public ReminderModel() {
        id = null;
        repeatModel = new ReminderRepeatModel();
        snoozeModel = new ReminderSnoozeModel();
    }

    public String getSignatureName() {
        if (StringHelper.isNullOrEmpty(name)) {
            return String.valueOf(getIntId());
        } else {
            return getIntId() +
                    " - " +
                    name;
        }
    }

    public void dismissByUser(Context context) {
        Date nextTime = getNextScheduleTime(Calendar.getInstance(), originalTime);
        if (nextTime == null) { // EOF situation
            archiveToFinished();
            deleteAndCancelAlert(context);
        } else {
            calculatedTime = nextTime; // Set next trigger time.
            //String net = UtilsDateTime.toTimeDateString(time);
            trySaveAndSetAlert(context, true, false); // Save changes. // Set alarm for next trigger time.
        }
    }

    public void dismissByApp(Context context, final Calendar currentTime) {
        Date nextTime = getNextScheduleTime(currentTime, originalTime);
        archiveToMissed();
        ToastHelper.showLong(context, "Dismissing to missed! " + getIntId());
        if (nextTime == null) { // EOF situation
            archiveToFinished();
            deleteAndCancelAlert(context);
        } else {
            calculatedTime = nextTime; // Set next trigger time.
            trySaveAndSetAlert(context, true, false); // Save changes. // Set alarm for next trigger time.
        }
    }

    public void snooze(Context context, boolean isByUser) {
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
                    ToastHelper.showLong(context, "Dismissing from snooze! " + getIntId());
                    if (isByUser) {
                        dismissByUser(context);
                    } else {
                        dismissByApp(context, Calendar.getInstance());
                    }
                } else if (currentTime.getTime().after(nextSnoozeOffTime)) { // Snooze makes no sense if its in past!
                    ToastHelper.showLong(context, "Dismissing from snooze! " + getIntId());
                    if (isByUser) {
                        dismissByUser(context);
                    } else {
                        dismissByApp(context, Calendar.getInstance());
                    }
                } else {
                    ToastHelper.showLong(context, "Snoozing! " + getIntId());
                    trySaveAndSetAlert(context, false, false);
                }
            }
        }
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

    public boolean trySetEnabled(Context context, boolean value) {
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
                    ToastHelper.showLong(context, "Alarm cannot be scheduled further. Please set time into future to enable.");
                } else { // Found next trigger point.
                    calculatedTime = nextTime; // Set next trigger time.
                    isEnabled = true;
                }
            } else {
                isEnabled = true;
            }
        } else {
            isEnabled = false;
        }

        return isEnabled;
    }

    public ReminderRepeatModel.ReminderRepeatOptions getRepeatOption() {
        return repeatModel.getRepeatOption();
    }

    public ReminderRepeatModel getRepeatSettings() {
        if (repeatValueChangeBuffer == null) {
            //Make a new instance copied from original. This way original repeat settings wont get affected until applied by method "trySetReminderRepeatModel"
            repeatValueChangeBuffer = new ReminderRepeatModel();
            repeatValueChangeBuffer.setReminderTime(originalTime);
            repeatValueChangeBuffer.setRepeatEndDate(repeatModel.getRepeatEndDate());
            repeatValueChangeBuffer.setHasRepeatEnd(repeatModel.isHasRepeatEnd());
            repeatValueChangeBuffer.setRepeatOption(repeatModel.getRepeatOption());
            repeatValueChangeBuffer.setRepeatCustom(repeatModel.getCustomTimeUnit(), repeatModel.getCustomTimeValue());

            repeatValueChangeBuffer.customHours.addAll(repeatModel.customHours);
            repeatValueChangeBuffer.customDays.addAll(repeatModel.customDays);
            repeatValueChangeBuffer.customWeeks.addAll(repeatModel.customWeeks);
            repeatValueChangeBuffer.customMonths.addAll(repeatModel.customMonths);
        }
        return repeatValueChangeBuffer;
    }

    public boolean trySetRepeatSettingChanges() {
        if (repeatValueChangeBuffer == null) return false;

        switch (repeatValueChangeBuffer.getRepeatOption()) {
            default: //NONE: HOURLY: DAILY: WEEKLY: MONTHLY: YEARLY:
                resetRepeatOptions();
                this.repeatModel.setRepeatOption(repeatValueChangeBuffer.getRepeatOption());
                repeatModel.setRepeatEndDate(repeatValueChangeBuffer.getRepeatEndDate());
                repeatModel.setHasRepeatEnd(repeatValueChangeBuffer.isHasRepeatEnd());

                calculatedTime = null;
                discardRepeatSettingChanges();
                return true;
            case HOURLY_CUSTOM:
                if (repeatValueChangeBuffer.customHours.size() > 0) {
                    resetRepeatOptions();
                    this.repeatModel.setRepeatOption(repeatValueChangeBuffer.getRepeatOption());
                    this.repeatModel.customHours.addAll(repeatValueChangeBuffer.customHours);
                    repeatModel.setRepeatEndDate(repeatValueChangeBuffer.getRepeatEndDate());
                    repeatModel.setHasRepeatEnd(repeatValueChangeBuffer.isHasRepeatEnd());
                    //Reminder time will be different than given time only if if Custom option are selected.
                    //setOriginalTime(originalTime);
                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime);


                    discardRepeatSettingChanges();
                    return true;
                } else {
                    discardRepeatSettingChanges();
                    return false;
                }
            case DAILY_CUSTOM:
                if (repeatValueChangeBuffer.customDays.size() > 0) {
                    resetRepeatOptions();
                    this.repeatModel.setRepeatOption(repeatValueChangeBuffer.getRepeatOption());
                    this.repeatModel.customDays.addAll(repeatValueChangeBuffer.customDays);
                    repeatModel.setRepeatEndDate(repeatValueChangeBuffer.getRepeatEndDate());
                    repeatModel.setHasRepeatEnd(repeatValueChangeBuffer.isHasRepeatEnd());

                    //Reminder time will be different than given time only if if Custom option are selected.
                    //setOriginalTime(originalTime);
                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime);
                    discardRepeatSettingChanges();
                    return true;
                } else {
                    discardRepeatSettingChanges();
                    return false;
                }
            case WEEKLY_CUSTOM:
                if (repeatValueChangeBuffer.customWeeks.size() > 0) {
                    resetRepeatOptions();
                    this.repeatModel.setRepeatOption(repeatValueChangeBuffer.getRepeatOption());
                    this.repeatModel.customWeeks.addAll(repeatValueChangeBuffer.customWeeks);
                    repeatModel.setRepeatEndDate(repeatValueChangeBuffer.getRepeatEndDate());
                    repeatModel.setHasRepeatEnd(repeatValueChangeBuffer.isHasRepeatEnd());

                    //Reminder time will be different than given time only if if Custom option are selected.
                    //setOriginalTime(originalTime);
                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime);
                    discardRepeatSettingChanges();
                    return true;
                } else {
                    discardRepeatSettingChanges();
                    return false;
                }
            case MONTHLY_CUSTOM:
                if (repeatValueChangeBuffer.customMonths.size() > 0) {
                    resetRepeatOptions();
                    this.repeatModel.setRepeatOption(repeatValueChangeBuffer.getRepeatOption());
                    this.repeatModel.customMonths.addAll(repeatValueChangeBuffer.customMonths);
                    repeatModel.setRepeatEndDate(repeatValueChangeBuffer.getRepeatEndDate());
                    repeatModel.setHasRepeatEnd(repeatValueChangeBuffer.isHasRepeatEnd());

                    //Reminder time will be different than given time only if if Custom option are selected.
                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime);
                    discardRepeatSettingChanges();
                    return true;
                } else {
                    discardRepeatSettingChanges();
                    return false;
                }
            case OTHER:
                if (repeatValueChangeBuffer.getCustomTimeValue() > 0 &&
                        repeatValueChangeBuffer.getCustomTimeValue() <= ReminderRepeatModel.getMaxForTimeUnit(repeatValueChangeBuffer.getCustomTimeUnit())) {
                    resetRepeatOptions();
                    this.repeatModel.setRepeatOption(repeatValueChangeBuffer.getRepeatOption());
                    this.repeatModel.setRepeatCustom(repeatValueChangeBuffer.getCustomTimeUnit(), repeatValueChangeBuffer.getCustomTimeValue());
                    repeatModel.setRepeatEndDate(repeatValueChangeBuffer.getRepeatEndDate());
                    repeatModel.setHasRepeatEnd(repeatValueChangeBuffer.isHasRepeatEnd());

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

    private void resetRepeatOptions() {
        this.repeatModel.customHours.clear();
        this.repeatModel.customDays.clear();
        this.repeatModel.customWeeks.clear();
        this.repeatModel.customMonths.clear();
        //this.repeatModel.reminderTime = null;
    }

    public void discardRepeatSettingChanges() {
        repeatValueChangeBuffer = null;
    }

    public String getRepeatSettingString() {
        return repeatModel.toString();
    }

    public void deleteAndCancelAlert(Context context) {
        cancelAlarm(context);
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

}
