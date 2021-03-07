package com.example.remindme.viewModels;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.dataModels.ReminderDismissed;
import com.example.remindme.dataModels.ReminderMissed;
import com.example.remindme.util.BroadcastReceiverAlarm;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReminderModel {
    public static final String INTENT_ATTR_ID = "_ID";

    public String id;
    public int alarmIntentId;
    public String name;
    public String note;
    public Date time;
    public boolean isEnable = true;
    public boolean isVibrate = false;
    public boolean isMissed = false;

    public ReminderRepeatModel repeatModel;
    public ReminderSnoozeModel snoozeModel;
    public Date nextSnoozeOffTime = null;

    private ReminderModel() {
        repeatModel = new ReminderRepeatModel();
        snoozeModel = new ReminderSnoozeModel();
    }

    public ReminderModel(String id) {
        this();
        this.id = id;
    }

    public static void reScheduleAllActive(Context context) {
        Calendar calendar = Calendar.getInstance();
        List<ReminderActive> reminders = getAll();
        for (int i = 0; i < reminders.size(); i++) {
            final ReminderActive r = reminders.get(i);
            final ReminderModel reminderModel = ReminderModel.transform(r);

            Date _time;
            if (reminderModel.nextSnoozeOffTime == null) {
                _time = reminderModel.time;
            } else {
                _time = reminderModel.nextSnoozeOffTime;
            }

            if (reminderModel.isEnable) {
                if (calendar.getTime().after(_time)) {
                    reminderModel.isMissed = true;
                    reminderModel.dismiss(context);
                } else {
                    reminderModel.setAlarm(context);
                    reminderModel.insertOrUpdate();
                }
            }
        }
    }

    public static void copy(ReminderModel from, ReminderModel to) {
        to.name = from.name;
        to.note = from.note;
        to.isEnable = from.isEnable;
        to.isVibrate = from.isVibrate;
        to.repeatModel = from.repeatModel;
        to.snoozeModel = from.snoozeModel;
    }

    public static ReminderActive transform(ReminderModel from) {
        ReminderActive to = new ReminderActive();
        to.id = from.id;
        to.alarmIntentId = from.alarmIntentId;
        to.name = from.name;
        to.note = from.note;
        to.time = from.time;
        to.isEnable = from.isEnable;
        to.isVibrate = from.isVibrate;

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

        return to;
    }

    public static ReminderModel transform(ReminderActive from) {
        ReminderModel to = new ReminderModel();
        to.id = from.id;
        to.alarmIntentId = from.alarmIntentId;
        to.name = from.name;
        to.note = from.note;
        to.time = from.time;
        to.isEnable = from.isEnable;
        to.isVibrate = from.isVibrate;

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

        return to;
    }

    public static ReminderModel read(String id) {
        Realm realm = Realm.getDefaultInstance();
        ReminderActive reminder = realm.where(ReminderActive.class).equalTo("id", id).findFirst();
        if (reminder == null) {
            return null;
        } else {
            return transform(reminder);
        }
    }

    private static RealmResults<ReminderActive> privateGetAllActive() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(ReminderActive.class).findAll();
    }

    private static RealmResults<ReminderActive> privateGetAllActive(String name) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(ReminderActive.class).beginsWith("name", name).findAll();
    }

    public static List<ReminderActive> getAll() {
//        Calendar calendar = Calendar.getInstance();
        RealmResults<ReminderActive> result = privateGetAllActive();
//        for (int i = 0; i < result.size(); i++) {
//            final ReminderActive r = result.get(i);
//            final ReminderModel reminderModel = ReminderModel.transform(r);
//
//            Date _time;
//            if (reminderModel.nextSnoozeTime == null) {
//                _time = reminderModel.time;
//            } else {
//                _time = reminderModel.nextSnoozeTime;
//            }
//
//            if (calendar.getTime().after(_time)) {
//                reminderModel.setAlarm(context);
//                reminderModel.createOrUpdate();
//            } else {
//                reminderModel.cancelAlarm(context);
//                reminderModel.moveToMissed();
//            }
//        }
        return result;
    }

    public static List<ReminderActive> getAll(String name) {
        RealmResults<ReminderActive> result = privateGetAllActive(name);
        return result;
    }

    public boolean canUpdate() {

        Date _time;

        if (nextSnoozeOffTime == null) {
            _time = time;
        } else {
            _time = nextSnoozeOffTime;
        }

        Calendar calendar = Calendar.getInstance();

        return _time.after(calendar.getTime());

    }

    public void snooze(Context context) {

        if (snoozeModel.isEnable) {

            Date _time;
            if (nextSnoozeOffTime == null) {
                _time = time;
            } else {
                _time = nextSnoozeOffTime;
            }

            nextSnoozeOffTime = null; // RESET

            Calendar calendar = Calendar.getInstance();

            calendar.setTime(_time);

            switch (snoozeModel.intervalOption) {
                default:
                case M5:
                    calendar.add(Calendar.MINUTE, 5);
                    break;

                case M10:
                    calendar.add(Calendar.MINUTE, 10);
                    break;

                case M15:
                    calendar.add(Calendar.MINUTE, 15);
                    break;
                case M30:
                    calendar.add(Calendar.MINUTE, 30);
                    break;
            }

            switch (snoozeModel.countOptions) {
                default:
                case R3:
                    if (snoozeModel.count < 3) {
                        snoozeModel.count++;
                        nextSnoozeOffTime = calendar.getTime();
                    }
                    break;
                case R5:
                    if (snoozeModel.count < 5) {
                        snoozeModel.count++;
                        nextSnoozeOffTime = calendar.getTime();
                    }
                    break;
                case RC:
                    snoozeModel.count++;
                    nextSnoozeOffTime = calendar.getTime();
                    break;
            }

            if (nextSnoozeOffTime == null) { // Next snooze time null means there is no more alarms and it has reached its EOF:
                dismiss(context);
            } else {
                insertOrUpdate();
                setAlarm(context);
            }
        }

    }

    public void dismiss(Context context) {

        boolean firstMatch = false;

        Date nextTime = null;

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(time);

        switch (repeatModel.repeatOption) {
            default:
            case None:
                // Do nothing. As result  nextTime will be null and this indicate end of life for the reminder
                break;
            case Hourly:
                calendar.add(Calendar.HOUR, 1);
                nextTime = calendar.getTime();
                break;
            case Daily:
                // Add 1 day till the next days comes for the coming/this week
                for (int i = 0; i < 7; i++) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    switch (calendar.get(calendar.DAY_OF_WEEK)) {
                        case Calendar.SUNDAY:
                            if (repeatModel.dailyModel.isSun) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.MONDAY:
                            if (repeatModel.dailyModel.isMon) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.TUESDAY:
                            if (repeatModel.dailyModel.isTue) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.WEDNESDAY:
                            if (repeatModel.dailyModel.isWed) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.THURSDAY:
                            if (repeatModel.dailyModel.isThu) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.FRIDAY:
                            if (repeatModel.dailyModel.isFri) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.SATURDAY:
                            if (repeatModel.dailyModel.isSat) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                    }
                }
                break;
            case Weekly:
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                nextTime = calendar.getTime();
                break;
            case Monthly:
                // Add 1 month till the next month comes for the coming/this year
                for (int i = 0; i < 12; i++) {
                    calendar.add(Calendar.MONTH, 1);
                    switch (calendar.get(calendar.MONTH)) {
                        case Calendar.JANUARY:
                            if (repeatModel.monthlyModel.isJan) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.FEBRUARY:
                            if (repeatModel.monthlyModel.isFeb) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.MARCH:
                            if (repeatModel.monthlyModel.isMar) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.APRIL:
                            if (repeatModel.monthlyModel.isApr) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.MAY:
                            if (repeatModel.monthlyModel.isMay) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.JUNE:
                            if (repeatModel.monthlyModel.isJun) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.JULY:
                            if (repeatModel.monthlyModel.isJul) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.AUGUST:
                            if (repeatModel.monthlyModel.isAug) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.SEPTEMBER:
                            if (repeatModel.monthlyModel.isSep) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.OCTOBER:
                            if (repeatModel.monthlyModel.isOct) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.NOVEMBER:
                            if (repeatModel.monthlyModel.isNov) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                        case Calendar.DECEMBER:
                            if (repeatModel.monthlyModel.isDec) {
                                if (!firstMatch) {
                                    nextTime = calendar.getTime();
                                    firstMatch = true;
                                } else if (nextTime.after(calendar.getTime())) {
                                    nextTime = calendar.getTime();
                                }
                            }
                            break;
                    }
                }
                break;
            case Yearly:
                calendar.add(Calendar.YEAR, 1);
                nextTime = calendar.getTime();
                break;
        }

        if (nextTime == null) { // EOF situation
            if (isMissed) {
                archiveToMissed();
            } else {
                archiveToDismissed();
            }
        } else { // Found next trigger point.
            time = nextTime; // Set next trigger time.
            insertOrUpdate(); // Save changes.
            setAlarm(context); // Set alarm for next trigger time.
        }
    }

    public void setAlarm(Context context) {
        cancelAlarm(context); // Cancel previous alarm for same ID

        if (!isEnable) {
            return;
        }

        Date _time;
        if (nextSnoozeOffTime == null) {
            _time = time;
        } else {
            _time = nextSnoozeOffTime;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(_time);

        if (alarmIntentId == 0) { // If was never set before. Else keep using the same value.
            alarmIntentId = (int) UUID.fromString(id).getMostSignificantBits();
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), BroadcastReceiverAlarm.class);
        intent.putExtra(ReminderModel.INTENT_ATTR_ID, id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), alarmIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 0, pendingIntent);
    }

    public void cancelAlarm(Context context) {
        if (alarmIntentId != 0) {
            AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) {
                Toast.makeText(context.getApplicationContext(), "Warning! No alarm manager found for the device.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(context.getApplicationContext(), BroadcastReceiverAlarm.class);
                intent.putExtra(ReminderModel.INTENT_ATTR_ID, id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), alarmIntentId, intent, PendingIntent.FLAG_NO_CREATE);
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent);
                    alarmIntentId = 0;
                }
            }
        }
    }

    public void insertOrUpdate() {
        final ReminderActive reminder = transform(this);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(reminder);
            }
        });
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        final ReminderActive reminder = realm.where(ReminderActive.class).equalTo("id", id).findFirst();
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

    private void archiveToMissed() {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                ReminderMissed to = new ReminderMissed();
                to.id = id;
                to.time = time;
                to.name = name;
                to.note = note;
                realm.insertOrUpdate(to);
            }
        });

        delete();
    }

    private void archiveToDismissed() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                ReminderDismissed to = new ReminderDismissed();
                to.id = id;
                to.time = time;
                to.name = name;
                to.note = note;
                realm.insertOrUpdate(to);
            }
        });

        delete();
    }

}
