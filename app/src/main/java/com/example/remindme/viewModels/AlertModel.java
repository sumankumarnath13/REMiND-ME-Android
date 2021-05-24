package com.example.remindme.viewModels;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModel;

import com.example.remindme.dataModels.AlarmDetails;
import com.example.remindme.dataModels.Alert;
import com.example.remindme.dataModels.MultipleTimeDetails;
import com.example.remindme.dataModels.ReminderDetails;
import com.example.remindme.dataModels.ReminderRepeat;
import com.example.remindme.dataModels.ReminderSnooze;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.exceptions.RealmMigrationNeededException;

public class AlertModel extends ViewModel {

    public static final String SERVICE_TYPE = "SERVICE_TYPE";
    public static final int DEFAULT_NOTIFICATION_GROUP_ID = 13;
    public static final String DEFAULT_NOTIFICATION_GROUP_KEY = "ÆjËèúÒ+·_²";
    private static final String DEFAULT_NOTIFICATION_GROUP_NAME = "Default notification group";
    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "RxLwKNdHEL";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "Other notifications";


    public static final int ALARM_NOTIFICATION_ID = 117;
    public static final String ACTION_ALARM_SNOOZE = "com.example.remindme.SNOOZE.ALARM";
    public static final String ACTION_ALARM_DISMISS = "com.example.remindme.DISMISS.ALARM";
    public static final String ACTION_ALERT_NOTIFICATION_CONTENT_FULLSCREEN = "com.example.remindme.DISMISS.ALARM";
    public static final String ACTION_ALERT_NOTIFICATION_CONTENT = "com.example.remindme.£fcEB]¬B9æ";
    public static final String ACTION_ALERT_FULLSCREEN = "com.example.remindme.ALERT.FULLSCREEN";
    public static final String ACTION_CLOSE_ALARM_ACTIVITY = "com.example.remindme.CLOSE.ALARM.ACTIVITY";
    public static final String ALARM_NOTIFICATION_CHANNEL_ID = "z_0EdcKpGP";
    public static final String ALARM_NOTIFICATION_CHANNEL_NAME = "Alarm notifications";


    public static final String BROADCAST_FILTER_ALARM = "com.example.remindme.2eXXCW2ZrH.RECEIVE.ALARM";
    public static final String BROADCAST_FILTER_REMINDER = "com.example.remindme.2eXXCW2ZrH.RECEIVE.REMINDER";
    public static final String BROADCAST_FILTER_REMINDER_DISMISS = "com.example.remindme.2eXXCW2ZrH.RECEIVE.REMINDER.DISMISS";
    public static final String REMINDER_ID_INTENT = "uNX¯3Á×MòP";

    private static Class<? extends BroadcastReceiver> externalBroadcastReceiverClass;

    private String id;

    public String getId() {
        return id;
    }

    private boolean isExpired;

    public boolean isExpired() {
        return isExpired;
    }

    private int intId;

    public int getIntId() {
        return intId;
    }

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isEnabled = true;

    public boolean isEnabled() {
        return isEnabled;
    }

    private TimeModel timeModel;

    public TimeModel getTimeModel() {
        return timeModel;
    }

    public void setTimeModel(final TimeModel model) {
        timeModel = model;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String value) {
        name = value;
    }

    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(final String value) {
        note = value;
    }

    private RepeatModel repeatModel;

    public RepeatModel getRepeatModel() {
        return repeatModel;
    }

    public void setRepeatModel(final RepeatModel repeatModel) {
        if (repeatModel == null)
            return;
        this.repeatModel = repeatModel;
    }

    private SnoozeModel snoozeModel;

    public SnoozeModel getSnoozeModel() {
        return snoozeModel;
    }

    public void setSnoozeModel(SnoozeModel value) {
        snoozeModel = value;
    }

    private final RingingModel ringingModel;

    public RingingModel getRingingModel() {
        return ringingModel;
    }

    public Date getLastMissedTime() {
        if (missedTimes.size() > 0) {
            return missedTimes.last();
        } else {
            return null;
        }
    }

    private final RealmList<Date> missedTimes = new RealmList<>();

    public List<Date> getMissedTimes() {
        return missedTimes;
    }

    private boolean isReminder;

    public boolean isReminder() {
        return isReminder;
    }

    public void setReminder(final boolean value) {
        isReminder = value;
    }

    private final ReminderModel reminderModel;

    public ReminderModel getReminderModel() {
        return reminderModel;
    }

    public static String getReminderIdFromIntent(final Intent intent) {
        return intent.getStringExtra(AlertModel.REMINDER_ID_INTENT);
    }

    public static void setReminderIdInIntent(final Intent intent, final String reminderId) {
        intent.putExtra(AlertModel.REMINDER_ID_INTENT, reminderId);
    }

    public static void reScheduleAllActive(final Context context, boolean isDeviceRebooted) {

        if (AppSettingsHelper.getInstance().isDisableAllReminders())
            return; // Ignore if all reminders are disabled from settings

        final Calendar calendar = Calendar.getInstance();
        final List<AlertModel> reminders = getActiveReminders(null);
        // boolean isNewAlertFound = false;
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).isEnabled()) {
                if (calendar.getTime().after(reminders.get(i).getTimeModel().getTime()) && isDeviceRebooted) {
                    // App getting killed after a while. But Broadcast receiver recreating app which leads to rescheduling.
                    // And for the logic below it getting dismissed before it could be snoozed from alerts.
                    // isDeviceRebooted will prevent this from happening.
                    reminders.get(i).dismissByApp(context);
                } else if (!reminders.get(i).isPendingIntentExists(context)) {
                    // isNewAlertFound = true;
                    if (calendar.getTime().after(reminders.get(i).getTimeModel().getTime())) {
                        // NotificationHelper.notify(context, reminders.get(i).getIntId(), "Dismissing reminder!", reminders.get(i).getSignatureName(), reminders.get(i).note);
                        reminders.get(i).dismissByApp(context);
                    } else {
                        // NotificationHelper.notify(context, reminders.get(i).getIntId(), "New reminder!", reminders.get(i).getSignatureName(), reminders.get(i).note);
                        reminders.get(i).setPendingIntent(context, reminders.get(i).getTimeModel().getTime(), false);
                    }
                }
            }
        }
        //    if (isNewAlertFound) {
        //        NotificationHelper.notifySummary(context, "Rescheduling reminders", null, null);
        //    }
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

    public String getSignatureName() {
        if (StringHelper.isNullOrEmpty(name)) {
            return StringHelper.toTimeAmPm(getTimeModel().getTime());
        } else {
            return getName();
        }
    }

    public boolean trySetEnabled(final Context context, boolean value) {

        if (value && !AppSettingsHelper.getInstance().isDisableAllReminders()) {

            final Date nextTime = getRepeatModel().schedule(getTimeModel());

            if (nextTime == null) { // EOF situation. No next schedule possible
                isEnabled = false;
                ToastHelper.showLong(context, "Reminder time expired. Please edit with a future time to enable.");
                return false;
            } else { // Found next trigger point.
                timeModel.setScheduledTime(nextTime);
                //calculatedTime = nextTime; // Set next trigger time.
                isEnabled = true;
                return true;
            }

        } else {
            cancelPendingIntent(context);
            isEnabled = false;
            return true;
        }
    }

    public String getRepeatSettingShortString() {
        return repeatModel.toShortString();
    }

    private boolean isPendingIntentExists(final Context context) {
        if (getIntId() == 0) {
            return false;
        } else {
            final PendingIntent pendingIntent = getReminderPendingIntent(context, false);
            return pendingIntent != null;
        }
    }

    private void cancelPendingIntent(final Context context) {
        if (getIntId() != 0) {
            final PendingIntent pendingIntent = getReminderPendingIntent(context, false);
            if (pendingIntent != null) {
                OsHelper.getAlarmManager(context).cancel(pendingIntent);
            }
        }
    }

    private void setPendingIntent(final Context context, Date atTime, boolean isShowElapseTimeToast) {
        if (!isEnabled) {
            return;
        }

        final Calendar calendar = Calendar.getInstance();

        long different = atTime.getTime() - calendar.getTime().getTime();

        if (different <= 0) { // Meaningless to set time in past. BUG ALERT: negative value means something is very wrong somewhere
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
            OsHelper.getAlarmManager(context).setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getReminderPendingIntent(context, true));
        } else {
            OsHelper.getAlarmManager(context).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getReminderPendingIntent(context, true));
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

    private Intent createReminderIntent(final Context context) {
        return new Intent(context.getApplicationContext(), externalBroadcastReceiverClass)
                .setAction(isReminder() ? BROADCAST_FILTER_REMINDER : BROADCAST_FILTER_ALARM)
                .putExtra(REMINDER_ID_INTENT, id);
    }

    private PendingIntent getReminderPendingIntent(final Context context, boolean isCreateNew) {
        final Intent intent = createReminderIntent(context);
        PendingIntent pendingIntent;
        if (isCreateNew) {
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), getIntId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), getIntId(), intent, PendingIntent.FLAG_NO_CREATE);
        }
        return pendingIntent;
    }

    public void dismissByUser(final Context context) {
        if (isReminder()) {
            getReminderModel().setCompleted(true);
            save();
        } else {
            final Date nextTime = getRepeatModel().schedule(getTimeModel());
            if (nextTime == null) { // EOF situation
                saveAsExpired();
            } else {
                // User's dismiss will erase missed alert history:
                missedTimes.clear();
                getTimeModel().setScheduledTime(nextTime);
                saveAndSetAlert(context, false); // Save changes. // Set alarm for next trigger time.
            }
        }
    }

    public void dismissByApp(final Context context) {
        if (isReminder()) {

            if (getReminderModel().getTime() != null) {
                missedTimes.add(getReminderModel().getTime()); // Register as missed.
            }

            getReminderModel().setCompleted(false);
            getReminderModel().setTime(getTimeModel().getTime());

            final Date nextTime = getRepeatModel().schedule(getTimeModel());
            if (nextTime == null) {
                saveAsExpired();
            } else {
                getTimeModel().setScheduledTime(nextTime);
                saveAndSetAlert(context, false); // Save changes. // Set alarm for next trigger time.
            }
        } else {
            missedTimes.add(getTimeModel().getTime()); // Register as missed. Because its missed and expired altogether.

            final Date nextTime = getRepeatModel().schedule(getTimeModel());
            if (nextTime == null) { // EOF situation
                saveAsExpired();
            } else {
                getTimeModel().setScheduledTime(nextTime);
                saveAndSetAlert(context, false); // Save changes. // Set alarm for next trigger time.
            }
        }
    }

    public void snoozeByUser(final Context context) {
        snooze(context, true);
    }

    public void snoozeByApp(final Context context) {
        snooze(context, false);
    }

    private void snooze(final Context context, boolean isByUser) {
        if (!isReminder()) {
            if (canSnooze()) {
                //nextSnoozeOffTime = getNextSnoozeTime(originalTime);
                getSnoozeModel().addCount();
                saveAndSetAlert(context, false);
            } else {
                if (isByUser) {
                    // dismissByUser will calculate next schedule again and set to next time
                    ToastHelper.showShort(context, "Overriding snooze for next schedule");
                    dismissByUser(context);
                } else {
                    ToastHelper.showShort(context, "MISSED ... Overriding snooze for next schedule");
                    // This will set nextScheduledTime as calculated time internally and then call saveAndSetAlert to set for next schedule
                    //saveAsMissed(context, nextScheduledTime);
                    dismissByApp(context);
                }
            }
        }
    }

    public boolean canSnooze() {

        if (!getSnoozeModel().isEnable())
            return false;

        final Calendar currentTime = Calendar.getInstance();

        if (currentTime.getTime().after(getTimeModel().getAlertTime(true))) { // Set snooze only if current time is past alarm time or previous snooze time.

            final Date nextSnoozeTime = getSnoozeModel().getNextSnoozeTime(getTimeModel().getTime());

            if (nextSnoozeTime == null) { // Next snooze time null means there is no more alarms and it has reached its EOF:

                return false;

            } else { // Check next snooze comes after next schedule or not.

                final Date nextScheduledTime = getRepeatModel().schedule(getTimeModel());

                if (nextScheduledTime == null) {
                    // No next schedule found so proceed with snoozing only if its not passed current time
                    return currentTime.getTime().before(nextSnoozeTime);
                }

                // If next schedule comes first then it will take priority. IN this case snoozing further isn't required.
                return nextScheduledTime.after(nextSnoozeTime);

            }
        } else { // Else dismiss the alarm

            return false;
        }

    }

    public static List<AlertModel> getActiveReminders(final String name) {
        final Realm realm = Realm.getDefaultInstance();
        if (StringHelper.isNullOrEmpty(name)) {
            final List<Alert> dataList = realm.where(Alert.class).equalTo("isExpired", false).sort("time").findAll();
            final ArrayList<AlertModel> reminderList = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                reminderList.add(AlertModel.getInstance(dataList.get(i)));
            }
            realm.close();
            return reminderList;
        } else {
            final List<Alert> dataList = realm.where(Alert.class).equalTo("isExpired", false).beginsWith("name", name).sort("time").findAll();
            final ArrayList<AlertModel> reminderList = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                reminderList.add(AlertModel.getInstance(dataList.get(i)));
            }
            realm.close();
            return reminderList;
        }
    }

    public static List<AlertModel> getDismissedReminders(final String name) {
        final Realm realm = Realm.getDefaultInstance();
        if (StringHelper.isNullOrEmpty(name)) {
            final List<Alert> dataList = realm.where(Alert.class).equalTo("isExpired", true).sort("time").findAll();
            final ArrayList<AlertModel> reminderList = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                reminderList.add(AlertModel.getInstance(dataList.get(i)));
            }
            realm.close();
            return reminderList;
        } else {
            final List<Alert> dataList = realm.where(Alert.class).equalTo("isExpired", true).beginsWith("name", name).sort("time").findAll();
            final ArrayList<AlertModel> reminderList = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                reminderList.add(AlertModel.getInstance(dataList.get(i)));
            }
            realm.close();
            return reminderList;
        }
    }

    public boolean isNew() {
        return id == null;
    }

    public void saveAndSetAlert(final Context context, boolean isShowElapseTimeToast) {

        if (isNew()) { // New reminder. First save

            UUID uuid = UUID.randomUUID();
            id = uuid.toString();
            intId = (int) uuid.getMostSignificantBits();

        } else { // Edit reminder

            cancelPendingIntent(context);

            // If user entered a different time than what exists already.
            // Or, it has calculated a different time than what exists.
            // For both cases snooze no longer required as the time on which it was calculated is already modified.
            // Both isOriginalTimeChanged() and isHasDifferentTimeCalculated() only works if original time changed after it was loaded from database e.g editing
            // and are not valid for new reminders e.g not saved yet.
            if (getTimeModel().isTimeChanged() || getTimeModel().isHasScheduledTime()) {
                //resetSnooze();
                getSnoozeModel().clearCount();
            }
        }

        isExpired = false;

        save();

        if (isEnabled && !AppSettingsHelper.getInstance().isDisableAllReminders()) {
            setPendingIntent(context, getTimeModel().getAlertTime(true), isShowElapseTimeToast);
        }

    }

    public void deleteAndCancelAlert(final Context context) {

        if (!isExpired) {
            cancelPendingIntent(context);
        }

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                final Alert alert = realm.where(Alert.class).equalTo("id", id).findFirst();
                if (alert != null) {
                    alert.deleteFromRealm();
                }
            }
        }, realm::close);
    }

    private void saveAsExpired() {
        isExpired = true;
        isEnabled = false;
        getSnoozeModel().clearCount();
        save();
    }

    private void save() {

        final Alert entity = new Alert();

        entity.id = getId();
        entity.isEnabled = isEnabled();
        entity.isExpired = isExpired();
        entity.alarmIntentId = getIntId();
        entity.name = getName();
        entity.note = getNote();
        entity.time = getTimeModel().getAlertTime(false);
        entity.timeListMode = TimeModel.getIntegerOfTimeListMode(getTimeModel().getTimeListMode());
        if (getTimeModel().getTimeListMode() != TimeModel.TimeListModes.NONE) {
            entity.multipleTimeDetails = new MultipleTimeDetails();
            entity.multipleTimeDetails.customTimes.addAll(getTimeModel().getTimeListTimes());
            entity.multipleTimeDetails.hourlyTimes.addAll(getTimeModel().getTimeListHours());
        }

        entity.missedTimes.addAll(missedTimes);

        if (getRepeatModel().isEnabled()) {
            entity.repeat = new ReminderRepeat();
            entity.repeat.isRepeatEnabled = getRepeatModel().isEnabled();
            entity.repeat.repeatOption = RepeatModel.getIntegerOfRepeatOption(getRepeatModel().getRepeatOption());
            entity.repeat.repeatDays.addAll(getRepeatModel().getCustomDays());
            entity.repeat.repeatWeeks.addAll(getRepeatModel().getCustomWeeks());
            entity.repeat.repeatMonths.addAll(getRepeatModel().getCustomMonths());
            entity.repeat.repeatCustomTimeUnit = RepeatModel.getIntegerFromTimeUnit(repeatModel.getCustomTimeUnit());
            entity.repeat.repeatCustomTimeValue = getRepeatModel().getCustomTimeValue();
            entity.repeat.isHasRepeatEnd = getRepeatModel().isHasRepeatEnd();
            entity.repeat.repeatEndDate = getRepeatModel().getRepeatEndDate();
        } else {
            entity.repeat = null;
        }

        if (isReminder()) {
            entity.reminderDetails = new ReminderDetails();
            entity.reminderDetails.time = getReminderModel().getTime();
            entity.reminderDetails.isCompleted = getReminderModel().isCompleted();
        } else {
            entity.alarmDetails = new AlarmDetails();
            if (getRingingModel().getRingToneUri() != null) {
                entity.alarmDetails.selectedAlarmTone = getRingingModel().getRingToneUri().toString();
            }

            entity.alarmDetails.isToneEnabled = getRingingModel().isToneEnabled();
            entity.alarmDetails.alarmVolume = getRingingModel().getAlarmVolumePercentage();
            entity.alarmDetails.isIncreaseVolumeGradually = getRingingModel().isIncreaseVolumeGradually();
            entity.alarmDetails.ringDurationInMin = RingingModel.convertToAlarmRingDuration(getRingingModel().getAlarmRingDuration());

            entity.alarmDetails.isVibrate = getRingingModel().isVibrationEnabled();
            entity.alarmDetails.vibratePattern = RingingModel.convertToVibratePattern(getRingingModel().getVibratePattern());

            if (getSnoozeModel().isEnable()) {
                entity.alarmDetails.snooze = new ReminderSnooze();
                entity.alarmDetails.snooze.snoozeCount = getSnoozeModel().getCount();
                entity.alarmDetails.snooze.snoozeInterval = SnoozeModel.getIntegerOfSnoozeInterval(getSnoozeModel().getInterval());
                entity.alarmDetails.snooze.snoozeLimit = SnoozeModel.getIntegerOfSnoozeLimit(getSnoozeModel().getLimit());
            }
        }

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(entity);
            }
        }, realm::close);

    }

    private void setInstance(final Alert from) {

        id = from.id;
        isEnabled = from.isEnabled;
        isExpired = from.isExpired;
        intId = from.alarmIntentId;
        setName(from.name);
        setNote(from.note);
        getTimeModel().setTime(from.time, true);
        getTimeModel().setTimeListMode(TimeModel.getTimeListModeFromInteger(from.timeListMode));
        if (getTimeModel().getTimeListMode() != TimeModel.TimeListModes.NONE) {
            getTimeModel().setTimeListTimes(from.multipleTimeDetails.customTimes);
            getTimeModel().setTimeListHours(from.multipleTimeDetails.hourlyTimes);
        }

        missedTimes.addAll(from.missedTimes);

        if (from.repeat == null) {
            getRepeatModel().setEnable(false);
        } else {
            getRepeatModel().setEnable(true);
            getRepeatModel().setCustomDays(from.repeat.repeatDays);
            getRepeatModel().setCustomWeeks(from.repeat.repeatWeeks);
            getRepeatModel().setCustomMonths(from.repeat.repeatMonths);
            getRepeatModel().setRepeatCustom(from.repeat.repeatCustomTimeUnit, from.repeat.repeatCustomTimeValue);
            getRepeatModel().setRepeatOption(RepeatModel.getRepeatOptionFromInteger(from.repeat.repeatOption));
            getRepeatModel().setHasRepeatEnd(from.repeat.isHasRepeatEnd);
            getRepeatModel().setRepeatEndDate(from.repeat.repeatEndDate);
        }

        if (from.alarmDetails == null) {
            setReminder(true);
            getReminderModel().setTime(from.reminderDetails.time);
            getReminderModel().setCompleted(from.reminderDetails.isCompleted);
        } else {
            setReminder(false);
            getRingingModel().setToneEnabled(from.alarmDetails.isToneEnabled);
            getRingingModel().setRingToneUri(from.alarmDetails.selectedAlarmTone);
            getRingingModel().setAlarmVolumePercentage(from.alarmDetails.alarmVolume);
            getRingingModel().setIncreaseVolumeGradually(from.alarmDetails.isIncreaseVolumeGradually);
            getRingingModel().setAlarmRingDuration(RingingModel.convertToAlarmRingDuration(from.alarmDetails.ringDurationInMin));
            getRingingModel().setVibrationEnabled(from.alarmDetails.isVibrate);
            getRingingModel().setVibratePattern(RingingModel.convertToVibratePattern(from.alarmDetails.vibratePattern));

            if (from.alarmDetails.snooze == null) {
                getSnoozeModel().setEnable(false);
            } else {
                getSnoozeModel().setEnable(true);
                getSnoozeModel().setInterval(SnoozeModel.getSnoozeIntervalFromInteger(from.alarmDetails.snooze.snoozeInterval));
                getSnoozeModel().setLimit(SnoozeModel.getSnoozeLimitFromInteger(from.alarmDetails.snooze.snoozeLimit));
                getSnoozeModel().setCount(from.alarmDetails.snooze.snoozeCount);
            }
        }
    }

    public static AlertModel getInstance(final Alert from) {
        final AlertModel to = new AlertModel();
        to.setInstance(from);
        return to;
    }

    public static AlertModel getInstance(final Intent intent) {

        if (intent == null) {
            return null;
        }

        final String reminderId = intent.getStringExtra(AlertModel.REMINDER_ID_INTENT);

        if (reminderId == null || reminderId.isEmpty()) {
            return null;
        } else {
            return getInstance(reminderId);
        }

    }

    public static AlertModel getInstance(final String reminderId) {
        final Realm realm = Realm.getDefaultInstance();
        final Alert alertData = realm.where(Alert.class).equalTo("id", reminderId).findFirst();
        if (alertData == null) {
            realm.close();
            return null;
        } else {
            final AlertModel model = getInstance(alertData);
            realm.close();
            return model;
        }
    }

    public AlertModel() {
        id = null;
        timeModel = new TimeModel(this);
        repeatModel = new RepeatModel(this);
        snoozeModel = new SnoozeModel(this);
        ringingModel = new RingingModel();
        reminderModel = new ReminderModel();
    }
}
