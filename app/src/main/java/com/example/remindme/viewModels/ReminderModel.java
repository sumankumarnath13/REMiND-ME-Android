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

import com.example.remindme.dataModels.Reminder;
import com.example.remindme.helpers.AppSettingsHelper;
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
import io.realm.RealmList;
import io.realm.exceptions.RealmMigrationNeededException;

public class ReminderModel extends ViewModel {

    public static final String DEFAULT_NOTIFICATION_GROUP_KEY = "ÆjËèúÒ+·_²";
    private static final String DEFAULT_NOTIFICATION_GROUP_NAME = "Default notification group";
    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "RxLwKNdHEL";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "Other notifications";

    public static final int MINIMUM_INPUT_VOLUME_PERCENTAGE = 10;
    public static final int MAX_RING_DURATION = 1000 * 60 * convertToAlarmRingDuration(AlarmRingDurations.THREE_MINUTE) + 1000; // 1 sec extra if it takes some more time to close and clean everything.
    private static final long[] VIBRATE_PATTERN_BASIC = {500, 500};
    private static final long[] VIBRATE_PATTERN_HEART_BREAK = {500, 500, 117};
    private static final long[] VIBRATE_PATTERN_TICKTOCK = {500, 300, 411};
    private static final long[] VIBRATE_PATTERN_WALTZ = {500, 500, 300, 300, 300, 117};
    private static final long[] VIBRATE_PATTERN_ZIG_ZIG_ZIG = {500, 300, 300, 300};
    public static final int ALARM_NOTIFICATION_ID = 117;
    public static final String ACTION_SNOOZE_ALARM = "com.example.remindme.SNOOZE.ALARM";
    public static final String ACTION_DISMISS_ALARM = "com.example.remindme.DISMISS.ALARM";
    public static final String ACTION_ALERT_NOTIFICATION_CONTENT_FULLSCREEN = "com.example.remindme.DISMISS.ALARM";
    public static final String ACTION_ALERT_NOTIFICATION_CONTENT = "com.example.remindme.£fcEB]¬B9æ";
    public static final String ACTION_ALERT_FULLSCREEN = "com.example.remindme.ALERT.FULLSCREEN";
    public static final String ACTION_CLOSE_ALARM_ACTIVITY = "com.example.remindme.CLOSE.ALARM.ACTIVITY";
    public static final String ALARM_NOTIFICATION_CHANNEL_ID = "z_0EdcKpGP";
    public static final String ALARM_NOTIFICATION_CHANNEL_NAME = "Alarm notifications";

    public static final int DEFAULT_NOTIFICATION_GROUP_ID = 13;
    public static final String ACTION_RECEIVE_ALARM = "com.example.remindme.2eXXCW2ZrH.RECEIVE.ALARM";
    public static final String REMINDER_ID_INTENT = "uNX¯3Á×MòP";

    private static Class<? extends BroadcastReceiver> externalBroadcastReceiverClass;

    private boolean expired;

    public boolean isExpired() {
        return expired;
    }

    private int alarmVolumePercentage;

    public int getAlarmVolumePercentage() {
        return alarmVolumePercentage;
    }

    public void setAlarmVolumePercentage(int value) {
        alarmVolumePercentage = Math.min(Math.max(value, 0), 100);
    }


    private AlarmRingDurations ringDuration = AlarmRingDurations.ONE_MINUTE;

    public AlarmRingDurations getAlarmRingDuration() {
        return ringDuration;
    }

    public static AlarmRingDurations convertToAlarmRingDuration(int minute) {
        switch (minute) {
            default:
            case 0:
                return AlarmRingDurations.ONE_MINUTE;
            case 1:
                return AlarmRingDurations.TWO_MINUTE;
            case 2:
                return AlarmRingDurations.THREE_MINUTE;
        }
    }

    public static int convertToAlarmRingDuration(AlarmRingDurations duration) {
        switch (duration) {
            default:
            case ONE_MINUTE:
                return 0;
            case TWO_MINUTE:
                return 1;
            case THREE_MINUTE:
                return 2;
        }
    }

    public void setAlarmRingDuration(AlarmRingDurations value) {
        ringDuration = value;
    }


    private VibratePatterns vibratePattern = VibratePatterns.LONG;

    public VibratePatterns getVibratePattern() {
        return vibratePattern;
    }

    public static VibratePatterns convertToVibratePattern(int value) {
        switch (value) {
            default:
            case 0:
                return VibratePatterns.LONG;
            case 1:
                return VibratePatterns.HEARTBEAT;
            case 2:
                return VibratePatterns.TICKTOCK;
            case 3:
                return VibratePatterns.WALTZ;
            case 4:
                return VibratePatterns.ZIG_ZIG_ZIG;
        }
    }

    public static int convertToVibratePattern(VibratePatterns pattern) {
        switch (pattern) {
            default:
            case LONG:
                return 0;
            case HEARTBEAT:
                return 1;
            case TICKTOCK:
                return 2;
            case WALTZ:
                return 3;
            case ZIG_ZIG_ZIG:
                return 4;
        }
    }

    public static long[] convertToVibrateFrequency(VibratePatterns pattern) {
        switch (pattern) {
            default:
            case LONG:
                return VIBRATE_PATTERN_BASIC;
            case HEARTBEAT:
                return VIBRATE_PATTERN_HEART_BREAK;
            case TICKTOCK:
                return VIBRATE_PATTERN_TICKTOCK;
            case WALTZ:
                return VIBRATE_PATTERN_WALTZ;
            case ZIG_ZIG_ZIG:
                return VIBRATE_PATTERN_ZIG_ZIG_ZIG;
        }
    }

    public void setVibratePattern(VibratePatterns value) {
        vibratePattern = value;
    }


    public static String getReminderIdFromIntent(Intent intent) {
        return intent.getStringExtra(ReminderModel.REMINDER_ID_INTENT);
    }

    public static void setReminderIdInIntent(Intent intent, String reminderId) {
        intent.putExtra(ReminderModel.REMINDER_ID_INTENT, reminderId);
    }

    public boolean isNew() {
        return id == null;
    }

    private int intId;

    public int getIntId() {
        return intId;
    }

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    private final ReminderRepeatModel repeatModel;

    private final ReminderSnoozeModel snoozeModel;

    public ReminderSnoozeModel getSnoozeModel() {
        return snoozeModel;
    }

    private Date nextSnoozeOffTime = null;

    public Date getNextSnoozeOffTime() {
        return nextSnoozeOffTime;
    }

    private ReminderRepeatModel repeatValueChangeBuffer;

    private Date originalTime;

    private boolean originalTimeChanged;

    public boolean isOriginalTimeChanged() {
        return originalTimeChanged;
    }

    public void setOriginalTime(Date userTime) {

        if (userTime == null) return;

        Calendar userTimeCl = Calendar.getInstance();
        userTimeCl.setTime(userTime);
        userTimeCl.set(Calendar.SECOND, 0);
        userTimeCl.set(Calendar.MILLISECOND, 0);

        if (originalTime != null && !originalTime.equals(userTimeCl.getTime())) {
            originalTimeChanged = true;
        }

        originalTime = userTimeCl.getTime();
        //isOriginalTimeChanged = true;
        repeatModel.setReminderTime(userTimeCl); // Repeat model has complex calculations for which it needs a reference to the original time.

        if (Calendar.getInstance().compareTo(userTimeCl) >= 0) { // If the user value "effectively" is in past then calculate next schedule.

            // calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime); // Given_time will be used if its not null.
            //            switch (repeatModel.getRepeatOption()) {
            //                default:
            //                    if (Calendar.getInstance().compareTo(userTimeCl) >= 0) {
            //                        //If the user value "effectively" is in past then calculate next schedule.
            //
            //                    } else {
            //                        // If not then no need of calculated time. The given time will be used.
            //                        calculatedTime = null;
            //                    }
            //                    break;
            //                case HOURLY_CUSTOM:
            //                case DAILY_CUSTOM:
            //                case WEEKLY_CUSTOM:
            //                case MONTHLY_CUSTOM:
            //                case OTHER:
            //                    calculatedTime = getNextScheduleTime(Calendar.getInstance(), originalTime); // Given_time will be used if its not null.
            //                    break;
            //            }


            calculatedTime = getNextScheduleTime(Calendar.getInstance()); // Given_time will be used if its not null.

        } else {
            // If not then no need of calculated time. The given time will be used.
            calculatedTime = null;
        }
    }

    public Date getOriginalTime() {
        return originalTime;
    }

    private Date calculatedTime;

    public Date getCalculatedTime() {
        return calculatedTime;
    }

    public boolean isHasDifferentTimeCalculated() {
        return calculatedTime != null && !calculatedTime.equals(originalTime);
    }

    private Date lastMissedTime;

    public Date getLastMissedTime() {
        return lastMissedTime;
    }

    private final RealmList<Date> missedTimes = new RealmList<>();

    public List<Date> getMissedTimes() {
        return missedTimes;
    }

    private boolean increaseVolumeGradually;

    public boolean isIncreaseVolumeGradually() {
        return this.increaseVolumeGradually;
    }

    public void setIncreaseVolumeGradually(boolean value) {
        increaseVolumeGradually = value;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String value) {
        note = value;
    }

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

    private boolean enableTone = true;

    public boolean isEnableTone() {
        return enableTone;
    }

    public void setEnableTone(boolean value) {
        enableTone = value;
    }

    private boolean enableVibration = true;

    public boolean isEnableVibration() {
        return enableVibration;
    }

    public void setEnableVibration(boolean value) {
        enableVibration = value;
    }

    public static void reScheduleAllActive(Context context, boolean isDeviceRebooted) {

        if (AppSettingsHelper.getInstance().isDisableAllReminders())
            return; // Ignore if all reminders are disabled from settings

        final Calendar calendar = Calendar.getInstance();
        List<Reminder> reminders = getActiveReminders(null);
        boolean isNewAlertFound = false;
        for (int i = 0; i < reminders.size(); i++) {
            final Reminder r = reminders.get(i);
            final ReminderModel reminderModel = ReminderModel.getInstance(r);
            if (reminderModel.isEnabled()) {
                if (calendar.getTime().after(reminderModel.getAlertTime()) && isDeviceRebooted) {
                    // App getting killed after a while. But Broadcast receiver recreating app which leads to rescheduling.
                    // And for the logic below it getting dismissed before it could be snoozed from alerts.
                    // isDeviceRebooted will prevent this from happening.
                    reminderModel.dismissByApp(context, calendar);
                } else if (!reminderModel.isPendingIntentExists(context)) {
                    isNewAlertFound = true;

                    if (calendar.getTime().after(reminderModel.getAlertTime())) {
                        NotificationHelper.notify(context, reminderModel.getIntId(), "Dismissing reminder!", reminderModel.getSignatureName(), reminderModel.note);
                        reminderModel.dismissByApp(context, calendar);
                    } else {
                        NotificationHelper.notify(context, reminderModel.getIntId(), "New reminder!", reminderModel.getSignatureName(), reminderModel.note);
                        reminderModel.setPendingIntent(context, reminderModel.getAlertTime(), false);
                    }
                }
            }
        }
        if (isNewAlertFound) {
            NotificationHelper.notifySummary(context, "Rescheduling reminders", null, null);
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

    public Date getAlertTime() {
        final Date _time;

        if (isHasDifferentTimeCalculated()) {
            _time = calculatedTime;
        } else if (nextSnoozeOffTime == null) {
            _time = originalTime;
        } else {
            _time = nextSnoozeOffTime;
        }

        return _time;
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

        Date nextTime = getNextScheduleTime(Calendar.getInstance());

        if (nextTime == null) { // EOF situation
            archiveToFinished();
        } else {
            // User's dismiss will erase missed alert history:
            lastMissedTime = null;
            missedTimes.clear();
            calculatedTime = nextTime; // Set next trigger time.
            saveAndSetAlert(context, false); // Save changes. // Set alarm for next trigger time.
        }
    }

    public void dismissByApp(Context context, final Calendar currentTime) {

        Date nextTime = getNextScheduleTime(currentTime);

        if (nextTime == null) { // EOF situation

            archiveToFinished();

        } else {

            missedTimes.add(originalTime);

            lastMissedTime = originalTime;

            calculatedTime = nextTime; // Set next trigger time.

            saveAndSetAlert(context, false); // Save changes. // Set alarm for next trigger time.

        }
    }

    public void snoozeByUser(Context context) {
        snooze(context, true);
    }

    public void snoozeByApp(Context context) {
        snooze(context, false);
    }

    private void snooze(Context context, boolean isByUser) {

        if (!snoozeModel.isEnable) { // If snooze isn't enable then dismiss it

            if (isByUser) {
                dismissByUser(context);
            } else {
                dismissByApp(context, Calendar.getInstance());
            }

            return;
        }

        Calendar currentTime = Calendar.getInstance();

        if (currentTime.getTime().after(getAlertTime())) { // Set snooze only if current time is past alarm time or previous snooze time.

            Calendar nextSnoozeOff = Calendar.getInstance();

            nextSnoozeOff.setTime(getAlertTime());

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
                //ToastHelper.showLong(context, "Dismissing from snooze! " + getIntId());
                if (isByUser) {
                    dismissByUser(context);
                } else {
                    dismissByApp(context, Calendar.getInstance());
                }
            } else if (currentTime.getTime().after(nextSnoozeOffTime)) { // Snooze makes no sense if its in past!
                //ToastHelper.showLong(context, "Dismissing from snooze! " + getIntId());
                if (isByUser) {
                    dismissByUser(context);
                } else {
                    dismissByApp(context, Calendar.getInstance());
                }
            } else {
                //ToastHelper.showLong(context, "Snoozing! " + getIntId());
                saveAndSetAlert(context, false);
            }
        } else { // Else dismiss the alarm
            cancelPendingIntent(context);

            resetSnooze();

            ToastHelper.showLong(context, "Dismissing from snooze! " + getIntId());
            if (isByUser) {
                dismissByUser(context);
            } else {
                dismissByApp(context, Calendar.getInstance());
            }
        }
    }

    public boolean trySetEnabled(Context context, boolean value) {

        if (value && !AppSettingsHelper.getInstance().isDisableAllReminders()) {

            Calendar currentTime = Calendar.getInstance();

            if (currentTime.getTime().after(originalTime)) { //If the time is in past then find if next schedule exists
                // SET NEW TRIGGER TIME
                Date nextTime = getNextScheduleTime(currentTime);
                if (nextTime == null) { // EOF situation. No next schedule possible
                    enabled = false;
                    ToastHelper.showLong(context, "Reminder time expired. Please edit with a future time to enable.");
                    return false;
                } else { // Found next trigger point.
                    calculatedTime = nextTime; // Set next trigger time.
                    enabled = true;
                    return true;
                }
            } else {
                enabled = true;
                return true;
            }
        } else {
            cancelPendingIntent(context);
            enabled = false;
            return true;
        }
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
                // Calculate only if its not snoozed already. This calculations will occur when snooze get off or when it will be dismissed
                if (nextSnoozeOffTime == null || isOriginalTimeChanged()) {
                    calculatedTime = getNextScheduleTime(Calendar.getInstance());
                }
                discardRepeatSettingChanges();
                return true;
            case HOURLY_CUSTOM:
                if (repeatValueChangeBuffer.customHours.size() > 0) {
                    resetRepeatOptions();
                    this.repeatModel.setRepeatOption(repeatValueChangeBuffer.getRepeatOption());
                    this.repeatModel.customHours.addAll(repeatValueChangeBuffer.customHours);
                    repeatModel.setRepeatEndDate(repeatValueChangeBuffer.getRepeatEndDate());
                    repeatModel.setHasRepeatEnd(repeatValueChangeBuffer.isHasRepeatEnd());
                    // Calculate only if its not snoozed already. This calculations will occur when snooze get off or when it will be dismissed
                    if (nextSnoozeOffTime == null || isOriginalTimeChanged()) {
                        calculatedTime = getNextScheduleTime(Calendar.getInstance());
                    }
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
                    // Calculate only if its not snoozed already. This calculations will occur when snooze get off or when it will be dismissed
                    if (nextSnoozeOffTime == null || isOriginalTimeChanged()) {
                        calculatedTime = getNextScheduleTime(Calendar.getInstance());
                    }
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
                    // Calculate only if its not snoozed already. This calculations will occur when snooze get off or when it will be dismissed
                    if (nextSnoozeOffTime == null || isOriginalTimeChanged()) {
                        calculatedTime = getNextScheduleTime(Calendar.getInstance());
                    }
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
                    // Calculate only if its not snoozed already. This calculations will occur when snooze get off or when it will be dismissed
                    if (nextSnoozeOffTime == null || isOriginalTimeChanged()) {
                        calculatedTime = getNextScheduleTime(Calendar.getInstance());
                    }
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
                    // Calculate only if its not snoozed already. This calculations will occur when snooze get off or when it will be dismissed
                    if (nextSnoozeOffTime == null || isOriginalTimeChanged()) {
                        calculatedTime = getNextScheduleTime(Calendar.getInstance());
                    }
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
    }

    public void discardRepeatSettingChanges() {
        repeatValueChangeBuffer = null;
    }

    public String getRepeatSettingString() {
        return repeatModel.toString();
    }

    public String getRepeatSettingShortString() {
        return repeatModel.toShortString();
    }


    private boolean isPendingIntentExists(Context context) {
        if (getIntId() == 0) {
            return false;
        } else {
            PendingIntent pendingIntent = getReminderPendingIntent(context, false);
            return pendingIntent != null;
        }
    }

    private void cancelPendingIntent(Context context) {
        if (getIntId() != 0) {
            PendingIntent pendingIntent = getReminderPendingIntent(context, false);
            if (pendingIntent != null) {
                OsHelper.getAlarmManager(context).cancel(pendingIntent);
            }
        }
    }

    private void setPendingIntent(Context context, Date atTime, boolean isShowElapseTimeToast) {
        if (!enabled) {
            return;
        }

        Calendar calendar = Calendar.getInstance();

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

    private Intent createReminderIntent(Context context) {
        return new Intent(context, externalBroadcastReceiverClass)
                .setAction(ACTION_RECEIVE_ALARM)
                .putExtra(REMINDER_ID_INTENT, id);
    }

    private PendingIntent getReminderPendingIntent(Context context, boolean isCreateNew) {
        Intent intent = createReminderIntent(context);
        PendingIntent pendingIntent;
        if (isCreateNew) {
            pendingIntent = PendingIntent.getBroadcast(context, getIntId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, getIntId(), intent, PendingIntent.FLAG_NO_CREATE);
        }
        return pendingIntent;
    }

    private Date getNextScheduleTime(final Calendar currentTime) {
        /*
         * This method will look for next closest date and time to repeat from reminder set time.
         * If the time is in past then it will bring the DAY of YEAR to present and then will look for next possible schedule based on repeat settings.
         * This method will return a non null value only if there is a dat can reached in future.
         * */

        if (currentTime == null) return null;

        if (originalTime == null) return null;

        Date nextScheduleTime = null;
        final Calendar reminderCal = Calendar.getInstance();
        //Calculation to find next schedule will begin from current time
        reminderCal.setTime(originalTime);

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
            baseCl.setTime(originalTime);
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
            nextScheduleTime = newScheduleCl.getTime();
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.DAILY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.DAY_OF_YEAR, 1);
            }
            nextScheduleTime = newScheduleCl.getTime();
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.WEEKLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.WEEK_OF_YEAR, 1);
            }
            nextScheduleTime = newScheduleCl.getTime();
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.MONTHLY) {
            // Set alarm values to current time onwards
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.MONTH, 1);
            }
            nextScheduleTime = newScheduleCl.getTime();
        } else if (repeatModel.getRepeatOption() == ReminderRepeatModel.ReminderRepeatOptions.YEARLY) {
            newScheduleCl.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
            newScheduleCl.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
            newScheduleCl.set(Calendar.MINUTE, MINUTE);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (newScheduleCl.compareTo(baseCl) < 0) {
                newScheduleCl.add(Calendar.YEAR, 1);
            }
            nextScheduleTime = newScheduleCl.getTime();
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
                    nextScheduleTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextScheduleTime == null) {
                //Reset and Find next schedule tomorrow :
                newScheduleCl.set(Calendar.HOUR_OF_DAY, 0);
                newScheduleCl.add(Calendar.DATE, 1);
                for (int i = 0; i < repeatModel.customHours.size(); i++) {
                    newScheduleCl.set(Calendar.HOUR_OF_DAY, repeatModel.customHours.get(i));
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextScheduleTime = newScheduleCl.getTime();
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
                    nextScheduleTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextScheduleTime == null) {
                //Find next schedule next week :
                newScheduleCl.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                newScheduleCl.add(Calendar.WEEK_OF_YEAR, 1);
                for (int i = 0; i < repeatModel.customDays.size(); i++) {
                    newScheduleCl.set(Calendar.DAY_OF_WEEK, repeatModel.customDays.get(i));
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextScheduleTime = newScheduleCl.getTime();
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
                    nextScheduleTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextScheduleTime == null) {
                //Find next schedule next month :
                newScheduleCl.set(Calendar.DAY_OF_MONTH, 1);
                newScheduleCl.add(Calendar.MONTH, 1);
                for (int i = 0; i < repeatModel.customWeeks.size(); i++) {
                    newScheduleCl.set(Calendar.WEEK_OF_MONTH, repeatModel.customWeeks.get(i) + 1);
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextScheduleTime = newScheduleCl.getTime();
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
                    nextScheduleTime = newScheduleCl.getTime();
                    break;
                }
            }
            if (nextScheduleTime == null) {
                //Find next schedule next year :
                newScheduleCl.set(Calendar.MONTH, Calendar.JANUARY);
                newScheduleCl.add(Calendar.YEAR, 1);
                for (int i = 0; i < repeatModel.customMonths.size(); i++) {
                    newScheduleCl.set(Calendar.MONTH, repeatModel.customMonths.get(i));
                    if (newScheduleCl.compareTo(baseCl) >= 0) {
                        nextScheduleTime = newScheduleCl.getTime();
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
            nextScheduleTime = newScheduleCl.getTime();
        }

        if (nextScheduleTime != null && repeatModel.getRepeatEndDate() != null) {
            return nextScheduleTime.after(repeatModel.getRepeatEndDate()) ? null : nextScheduleTime;
        }

        return nextScheduleTime;
    }

    public static List<Reminder> getActiveReminders(String name) {
        final Realm realm = Realm.getDefaultInstance();
        if (StringHelper.isNullOrEmpty(name)) {
            return realm.where(Reminder.class).equalTo("expired", false).sort("time").findAll();
        } else {
            return realm.where(Reminder.class).equalTo("expired", false).beginsWith("name", name).sort("time").findAll();
        }
    }

    public static List<Reminder> getDismissedReminders(String name) {
        final Realm realm = Realm.getDefaultInstance();
        if (StringHelper.isNullOrEmpty(name)) {
            return realm.where(Reminder.class).equalTo("expired", true).findAll();
        } else {
            return realm.where(Reminder.class).equalTo("expired", true).beginsWith("name", name).findAll();
        }
    }

    private String id;

    public String getId() {
        return id;
    }

    public void saveAndSetAlert(Context context, boolean isShowElapseTimeToast) {

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
            if (isOriginalTimeChanged() || isHasDifferentTimeCalculated()) {
                resetSnooze();
            }

        }

        expired = false;

        setDb();

        if (enabled && !AppSettingsHelper.getInstance().isDisableAllReminders()) {
            setPendingIntent(context, getAlertTime(), isShowElapseTimeToast);
        }

    }

    private void resetSnooze() {
        nextSnoozeOffTime = null;
        snoozeModel.count = 0;
    }

    public void deleteAndCancelAlert(Context context) {

        if (!expired) {
            cancelPendingIntent(context);
        }

        Realm realm = Realm.getDefaultInstance();
        final Reminder reminder = realm.where(Reminder.class).equalTo("id", id).findFirst();
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

    private void archiveToFinished() {
        expired = true;
        nextSnoozeOffTime = null;
        snoozeModel.count = 0;
        setDb();
    }

    private void setDb() {

        final Reminder entity = new Reminder();

        entity.id = id;
        entity.expired = expired;
        entity.alarmIntentId = getIntId();
        entity.name = name;
        entity.note = note;
        entity.time = calculatedTime == null ? originalTime : calculatedTime;

        entity.lastMissedTime = lastMissedTime;

        if (entity.missedTimes == null) {
            entity.missedTimes = new RealmList<>();
        } else {
            entity.missedTimes.clear();
        }

        entity.missedTimes.addAll(missedTimes);

        if (ringToneUri != null) {
            entity.selectedAlarmTone = ringToneUri.toString();
        }

        entity.isEnableTone = enableTone;
        entity.enabled = enabled;
        entity.vibrate = enableVibration;

        entity.increaseVolumeGradually = increaseVolumeGradually;
        entity.alarmVolume = alarmVolumePercentage;
        entity.ringDurationInMin = convertToAlarmRingDuration(ringDuration);
        entity.vibratePattern = convertToVibratePattern(vibratePattern);

        entity.repeatHours.clear();
        entity.repeatDays.clear();
        entity.repeatWeeks.clear();
        entity.repeatMonths.clear();

        switch (repeatModel.getRepeatOption()) {
            default:
            case OFF:
                entity.repeatOption = 0;
                break;
            case HOURLY:
                entity.repeatOption = 1;
                break;
            case HOURLY_CUSTOM:
                entity.repeatOption = 11;
                entity.repeatHours.addAll(repeatModel.customHours);
                break;
            case DAILY:
                entity.repeatOption = 2;
                break;
            case DAILY_CUSTOM:
                entity.repeatOption = 21;
                entity.repeatDays.addAll(repeatModel.customDays);
                break;
            case WEEKLY:
                entity.repeatOption = 3;
                break;
            case WEEKLY_CUSTOM:
                entity.repeatOption = 31;
                entity.repeatWeeks.addAll(repeatModel.customWeeks);
                break;
            case MONTHLY:
                entity.repeatOption = 4;
                break;
            case MONTHLY_CUSTOM:
                entity.repeatOption = 41;
                entity.repeatMonths.addAll(repeatModel.customMonths);
                break;
            case YEARLY:
                entity.repeatOption = 5;
                break;
            case OTHER:
                entity.repeatOption = 6;
                entity.customTimeUnit = ReminderRepeatModel.transform(repeatModel.getCustomTimeUnit());
                entity.customTimeValue = repeatModel.getCustomTimeValue();
                break;
        }

        entity.hasRepeatEnd = repeatModel.isHasRepeatEnd();
        entity.repeatEndDate = repeatModel.getRepeatEndDate();

        entity.snoozeEnabled = snoozeModel.isEnable;
        entity.nextSnoozeTime = nextSnoozeOffTime;
        entity.snoozeCount = snoozeModel.count;

        if (snoozeModel.isEnable) {

            switch (snoozeModel.countOptions) {
                default:
                case R3:
                    entity.snoozeLength = 3;
                    break;
                case R5:
                    entity.snoozeLength = 5;
                    break;
                case RC:
                    entity.snoozeLength = -1;
                    break;
            }

            switch (snoozeModel.intervalOption) {
                default:
                case M5:
                    entity.snoozeInterval = 5;
                    break;
                case M10:
                    entity.snoozeInterval = 10;
                    break;
                case M15:
                    entity.snoozeInterval = 15;
                    break;
                case M30:
                    entity.snoozeInterval = 30;
                    break;
            }
        } else {
            entity.snoozeLength = 0;
            entity.snoozeInterval = 0;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(entity);
            }
        });
    }

    private void setInstance(Reminder from) {
        id = from.id;
        expired = from.expired;
        intId = from.alarmIntentId;
        name = from.name;
        note = from.note;
        originalTime = from.time;

        lastMissedTime = from.lastMissedTime;

        missedTimes.clear();
        missedTimes.addAll(from.missedTimes);

        if (from.selectedAlarmTone != null) {
            ringToneUri = Uri.parse(from.selectedAlarmTone);
        }
        enableTone = from.isEnableTone;
        enabled = from.enabled;
        enableVibration = from.vibrate;

        increaseVolumeGradually = from.increaseVolumeGradually;
        alarmVolumePercentage = from.alarmVolume;
        ringDuration = convertToAlarmRingDuration(from.ringDurationInMin);
        vibratePattern = convertToVibratePattern(from.vibratePattern);

        repeatModel.setReminderTime(from.time);
        repeatModel.customHours.clear();
        repeatModel.customDays.clear();
        repeatModel.customWeeks.clear();
        repeatModel.customMonths.clear();

        switch (from.repeatOption) {
            default:
            case 0:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.OFF);
                break;
            case 1:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.HOURLY);
                break;
            case 11:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.HOURLY_CUSTOM);
                repeatModel.customHours.addAll(from.repeatHours);
                break;
            case 2:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.DAILY);
                break;
            case 21:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.DAILY_CUSTOM);
                repeatModel.customDays.addAll(from.repeatDays);
                break;
            case 3:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.WEEKLY);
                break;
            case 31:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.WEEKLY_CUSTOM);
                repeatModel.customWeeks.addAll(from.repeatWeeks);
                break;
            case 4:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.MONTHLY);
                break;
            case 41:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.MONTHLY_CUSTOM);
                repeatModel.customMonths.addAll(from.repeatMonths);
                break;
            case 5:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.YEARLY);
                break;
            case 6:
                repeatModel.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.OTHER);
                repeatModel.setRepeatCustom(from.customTimeUnit, from.customTimeValue);
                break;
        }
        repeatModel.setRepeatEndDate(from.repeatEndDate);
        repeatModel.setHasRepeatEnd(from.hasRepeatEnd);

        snoozeModel.isEnable = from.snoozeEnabled;
        nextSnoozeOffTime = from.nextSnoozeTime;
        snoozeModel.count = from.snoozeCount;

        if (from.snoozeEnabled) {

            switch (from.snoozeLength) {
                default:
                case 3:
                    snoozeModel.countOptions = ReminderSnoozeModel.SnoozeCountOptions.R3;
                    break;
                case 5:
                    snoozeModel.countOptions = ReminderSnoozeModel.SnoozeCountOptions.R5;
                    break;
                case -1:
                    snoozeModel.countOptions = ReminderSnoozeModel.SnoozeCountOptions.RC;
                    break;
            }

            switch (from.snoozeInterval) {
                default:
                case 5:
                    snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M5;
                    break;
                case 10:
                    snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M10;
                    break;
                case 15:
                    snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M15;
                    break;
                case 30:
                    snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M30;
                    break;
            }
        }
    }

    public static ReminderModel getInstance(Reminder from) {
        ReminderModel to = new ReminderModel();
        to.setInstance(from);
        return to;
    }

    public static ReminderModel getInstance(Intent intent) {

        if (intent == null) {
            return null;
        }

        String reminderId = intent.getStringExtra(ReminderModel.REMINDER_ID_INTENT);

        if (reminderId == null || reminderId.isEmpty()) {
            return null;
        } else {
            return getInstance(reminderId);
        }

    }

    public static ReminderModel getInstance(String reminderId) {
        Realm realm = Realm.getDefaultInstance();
        Reminder reminderData = realm.where(Reminder.class).equalTo("id", reminderId).findFirst();
        if (reminderData == null) {
            return null;
        } else {
            return getInstance(reminderData);
        }
    }

    public ReminderModel() {
        id = null;
        repeatModel = new ReminderRepeatModel();
        snoozeModel = new ReminderSnoozeModel();
    }

    public enum AlarmRingDurations {
        ONE_MINUTE,
        TWO_MINUTE,
        THREE_MINUTE,
    }

    public enum VibratePatterns {
        LONG,
        HEARTBEAT,
        TICKTOCK,
        WALTZ,
        ZIG_ZIG_ZIG
    }
}
