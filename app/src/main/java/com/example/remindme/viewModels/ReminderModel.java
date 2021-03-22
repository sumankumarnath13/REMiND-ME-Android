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
    private static Ringtone playingRingtone = null;
    private static boolean isRinging = false;
    private static Vibrator vibrator;
    private static AlarmManager alarmManager;
    private static Application application;

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
                    _time = reminderModel.time;
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
                    reminderModel.setAlarm(false);
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
        to.time = from.time;
        if (from.ringToneUri != null) {
            to.selectedAlarmTone = from.ringToneUri.toString();
        }
        to.isEnableTone = from.isEnableTone;
        to.isEnable = from.isEnable;
        to.isVibrate = from.isEnableVibration;

        switch (from.repeatModel.repeatOption) {
            default:
            case None:
                to.repeatOption = 0;
                break;
            case Hourly:
                to.repeatOption = 1;
                break;
            case Daily:
                to.repeatOption = 2;

                to.isRepeatOn_Sun = from.repeatModel.dailyModel.isSun;
                to.isRepeatOn_Mon = from.repeatModel.dailyModel.isMon;
                to.isRepeatOn_Tue = from.repeatModel.dailyModel.isTue;
                to.isRepeatOn_Wed = from.repeatModel.dailyModel.isWed;
                to.isRepeatOn_Thu = from.repeatModel.dailyModel.isThu;
                to.isRepeatOn_Fri = from.repeatModel.dailyModel.isFri;
                to.isRepeatOn_Sat = from.repeatModel.dailyModel.isSat;

                break;
            case Weekly:
                to.repeatOption = 3;
                break;
            case Monthly:
                to.repeatOption = 4;

                to.isRepeatOn_Jan = from.repeatModel.monthlyModel.isJan;
                to.isRepeatOn_Feb = from.repeatModel.monthlyModel.isFeb;
                to.isRepeatOn_Mar = from.repeatModel.monthlyModel.isMar;
                to.isRepeatOn_Apr = from.repeatModel.monthlyModel.isApr;
                to.isRepeatOn_May = from.repeatModel.monthlyModel.isMay;
                to.isRepeatOn_Jun = from.repeatModel.monthlyModel.isJun;
                to.isRepeatOn_Jul = from.repeatModel.monthlyModel.isJul;
                to.isRepeatOn_Aug = from.repeatModel.monthlyModel.isAug;
                to.isRepeatOn_Sep = from.repeatModel.monthlyModel.isSep;
                to.isRepeatOn_Oct = from.repeatModel.monthlyModel.isOct;
                to.isRepeatOn_Nov = from.repeatModel.monthlyModel.isNov;
                to.isRepeatOn_Dec = from.repeatModel.monthlyModel.isDec;

                break;
            case Yearly:
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
        to.time = from.time;
        if (from.selectedAlarmTone != null) {
            to.ringToneUri = Uri.parse(from.selectedAlarmTone);
        }
        to.isEnableTone = from.isEnableTone;
        to.isEnable = from.isEnable;
        to.isEnableVibration = from.isVibrate;

        switch (from.repeatOption) {
            default:
            case 0:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.None;
                break;
            case 1:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Hourly;
                break;
            case 2:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Daily;

                to.repeatModel.dailyModel.isSun = from.isRepeatOn_Sun;
                to.repeatModel.dailyModel.isMon = from.isRepeatOn_Mon;
                to.repeatModel.dailyModel.isTue = from.isRepeatOn_Tue;
                to.repeatModel.dailyModel.isWed = from.isRepeatOn_Wed;
                to.repeatModel.dailyModel.isThu = from.isRepeatOn_Thu;
                to.repeatModel.dailyModel.isFri = from.isRepeatOn_Fri;
                to.repeatModel.dailyModel.isSat = from.isRepeatOn_Sat;

                break;
            case 3:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Weekly;
                break;
            case 4:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Monthly;

                to.repeatModel.monthlyModel.isJan = from.isRepeatOn_Jan;
                to.repeatModel.monthlyModel.isFeb = from.isRepeatOn_Feb;
                to.repeatModel.monthlyModel.isMar = from.isRepeatOn_Mar;
                to.repeatModel.monthlyModel.isApr = from.isRepeatOn_Apr;
                to.repeatModel.monthlyModel.isMay = from.isRepeatOn_May;
                to.repeatModel.monthlyModel.isJun = from.isRepeatOn_Jun;
                to.repeatModel.monthlyModel.isJul = from.isRepeatOn_Jul;
                to.repeatModel.monthlyModel.isAug = from.isRepeatOn_Aug;
                to.repeatModel.monthlyModel.isSep = from.isRepeatOn_Sep;
                to.repeatModel.monthlyModel.isOct = from.isRepeatOn_Oct;
                to.repeatModel.monthlyModel.isNov = from.isRepeatOn_Nov;
                to.repeatModel.monthlyModel.isDec = from.isRepeatOn_Dec;

                break;
            case 5:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Yearly;
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
            time = nextTime; // Set next trigger time.
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
            time = nextTime; // Set next trigger time.
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

    private void setAlarm(boolean isShowElapseTimeToast) {
        if (!isEnable) {
            return;
        }

        Date _time;
        if (nextSnoozeOffTime == null) {
            _time = time;
        } else {
            _time = nextSnoozeOffTime;
        }

        String x = UtilsDateTime.toTimeString(_time);

        Calendar calendar = Calendar.getInstance();

        long different = _time.getTime() - calendar.getTime().getTime();

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

        calendar.setTime(_time);

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
                to.time = time;
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
                to.time = time;
                to.name = name;
                to.note = note;
                realm.insertOrUpdate(to);
            }
        });
    }

    private void snooze(boolean isByUser) {
        Date _time;
        if (nextSnoozeOffTime == null) {
            _time = time;
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
                    timeStamp = UtilsDateTime.toTimeString(time) + " " + intId;
                } else {
                    timeStamp = UtilsDateTime.toTimeString(time) + " & was snoozed for " + snoozeModel.count + " times" + " " + intId;
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

        Date _time;
        if (nextSnoozeOffTime == null) {
            _time = time;
        } else {
            _time = nextSnoozeOffTime;
        }

        Calendar calendar = Calendar.getInstance();

        if (_time.after(calendar.getTime())) {
            if (isResetSnooze) {
                nextSnoozeOffTime = null;
                snoozeModel.count = 0;
            }

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
                setAlarm(isShowElapseTimeToast);
            }

            return true;

        } else {
            showToast("Alarm cannot be set in past.");
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
                    notificationManager.cancel(ALARM_NOTIFICATION_ID);
                    notify(DEFAULT_NOTIFICATION_ID, "Error!", "Reminder not found!", null);
                } else {

                    boolean isUser = intent.getBooleanExtra(ALERT_INTENT_IS_USER, false);
                    stopAlarm(); // STOP RINGING
                    notificationManager.cancel(ALARM_NOTIFICATION_ID);

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
    public Date time;
    public Uri ringToneUri = null;
    public boolean isEnableTone = true;
    public boolean isEnableVibration = true;
    public ReminderRepeatModel repeatModel;
    public ReminderSnoozeModel snoozeModel;
    public Date nextSnoozeOffTime = null;
    //endregion

    //region Public instance functions

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

            if (currentTime.getTime().after(time)) { //If the time is in past then find if next schedule exists
                // SET NEW TRIGGER TIME
                Date nextTime = getNextScheduleTime(currentTime);
                if (nextTime == null) { // EOF situation. No next schedule possible
                    //archiveToFinished();
                    //deleteAndCancelAlert();
                    showToast("Alarm cannot be scheduled further. Please set time into future to enable.");
                } else { // Found next trigger point.
                    time = nextTime; // Set next trigger time.
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
        Date nextTime = null;

//        if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.None) {
//            // Do nothing. As result  nextTime will be null and this indicate end of life for the reminder
//        } else

        if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.Hourly) {
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(time);
            // Take the VALUES from alarm time
            int alarmMin = alarmTime.get(Calendar.MINUTE);

            // Set current time to alarm time
            alarmTime.setTime(currentTime.getTime());
            // Set alarm values to current time onwards
            alarmTime.set(Calendar.MINUTE, alarmMin);
            alarmTime.set(Calendar.SECOND, 0);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (currentTime.after(alarmTime)) {
                alarmTime.add(Calendar.HOUR_OF_DAY, 1);
            }
            nextTime = alarmTime.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.Daily) {
            boolean firstMatch = false;
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(time);
            // Take the VALUES from alarm time
            int alarmHour = alarmTime.get(Calendar.HOUR_OF_DAY);
            int alarmMin = alarmTime.get(Calendar.MINUTE);

            // Set current time to alarm time
            alarmTime.setTime(currentTime.getTime());
            // Set alarm values to current time onwards
            alarmTime.set(Calendar.HOUR_OF_DAY, alarmHour);
            alarmTime.set(Calendar.MINUTE, alarmMin);
            alarmTime.set(Calendar.SECOND, 0);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (currentTime.after(alarmTime)) {
                alarmTime.add(Calendar.DAY_OF_YEAR, 1);
            }
            // Add 1 day till the next days comes for the coming/this week
            for (int i = 0; i < 7; i++) {
                switch (alarmTime.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.SUNDAY:
                        if (repeatModel.dailyModel.isSun) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.MONDAY:
                        if (repeatModel.dailyModel.isMon) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.TUESDAY:
                        if (repeatModel.dailyModel.isTue) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.WEDNESDAY:
                        if (repeatModel.dailyModel.isWed) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.THURSDAY:
                        if (repeatModel.dailyModel.isThu) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.FRIDAY:
                        if (repeatModel.dailyModel.isFri) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.SATURDAY:
                        if (repeatModel.dailyModel.isSat) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                }
                alarmTime.add(Calendar.DAY_OF_YEAR, 1); // Increase a day to scan other days of week
            }
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.Weekly) {
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(time);

            // Merge current time to alarm time
            alarmTime.set(Calendar.WEEK_OF_YEAR, currentTime.get(Calendar.WEEK_OF_YEAR));
            alarmTime.set(Calendar.SECOND, 0);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (currentTime.after(alarmTime)) {
                alarmTime.add(Calendar.WEEK_OF_YEAR, 1);
            }

            alarmTime.add(Calendar.WEEK_OF_YEAR, 1);
            nextTime = alarmTime.getTime();
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.Monthly) {
            boolean firstMatch = false;
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(time);
            // Take the VALUES from alarm time
            int alarmMonth = alarmTime.get(Calendar.MONTH);
            int alarmDay = alarmTime.get(Calendar.DAY_OF_MONTH);
            int alarmHour = alarmTime.get(Calendar.HOUR_OF_DAY);
            int alarmMin = alarmTime.get(Calendar.MINUTE);

            // Set current time to alarm time
            alarmTime.setTime(currentTime.getTime());
            // Set alarm values to current time onwards
            alarmTime.set(Calendar.MONTH, alarmMonth);
            alarmTime.set(Calendar.DAY_OF_MONTH, alarmDay);
            alarmTime.set(Calendar.HOUR_OF_DAY, alarmHour);
            alarmTime.set(Calendar.MINUTE, alarmMin);
            alarmTime.set(Calendar.SECOND, 0);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (currentTime.after(alarmTime)) {
                alarmTime.add(Calendar.MONTH, 1);
            }

            // Add 1 month till the next month comes for the coming/this year
            for (int i = 0; i < 12; i++) {
                switch (alarmTime.get(Calendar.MONTH)) {
                    case Calendar.JANUARY:
                        if (repeatModel.monthlyModel.isJan) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.FEBRUARY:
                        if (repeatModel.monthlyModel.isFeb) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.MARCH:
                        if (repeatModel.monthlyModel.isMar) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.APRIL:
                        if (repeatModel.monthlyModel.isApr) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.MAY:
                        if (repeatModel.monthlyModel.isMay) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.JUNE:
                        if (repeatModel.monthlyModel.isJun) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.JULY:
                        if (repeatModel.monthlyModel.isJul) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.AUGUST:
                        if (repeatModel.monthlyModel.isAug) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.SEPTEMBER:
                        if (repeatModel.monthlyModel.isSep) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.OCTOBER:
                        if (repeatModel.monthlyModel.isOct) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.NOVEMBER:
                        if (repeatModel.monthlyModel.isNov) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                    case Calendar.DECEMBER:
                        if (repeatModel.monthlyModel.isDec) {
                            if (!firstMatch) {
                                nextTime = alarmTime.getTime();
                                firstMatch = true;
                            } else if (nextTime.after(alarmTime.getTime())) {
                                nextTime = alarmTime.getTime();
                            }
                        }
                        break;
                }
                alarmTime.add(Calendar.MONTH, 1);
            }
        } else if (repeatModel.repeatOption == ReminderRepeatModel.ReminderRepeatOptions.Yearly) {
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(time);

            // Merge current time to alarm time
            alarmTime.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
            alarmTime.set(Calendar.SECOND, 0);
            // Then check if its in past or in future. If in past then increase an unit. Else, keep the time.
            if (currentTime.after(alarmTime)) {
                alarmTime.add(Calendar.YEAR, 1);
            }

            alarmTime.add(Calendar.YEAR, 1);
            nextTime = alarmTime.getTime();
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
