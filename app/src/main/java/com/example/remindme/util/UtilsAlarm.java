package com.example.remindme.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.dataModels.ReminderMissed;

import java.text.ParseException;
import java.util.Calendar;
import io.realm.Realm;
import io.realm.RealmResults;

public class UtilsAlarm {

    public static void set(Context context, ReminderActive reminder) throws ParseException {

        Intent intent = new Intent(context, BroadcastReceiverAlarm.class);
        intent.putExtra("ID", reminder.id);

        int time_value = reminder.id;
        if(reminder.next_snooze_id > 0){
            time_value = reminder.next_snooze_id;
        }

        intent.putExtra("TIME", time_value);
        intent.putExtra("NAME", reminder.name);
        intent.putExtra("NOTE", reminder.note);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, reminder.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(UtilsDateTime.toDate(time_value));

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmPendingIntent);
    }

    public static void unSet(Context context, int reminder_id){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiverAlarm.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, reminder_id, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
    }

    public static void boot(Context context) {
        Calendar calendar = Calendar.getInstance();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReminderActive> reminders = realm.where(ReminderActive.class).findAll();
        for(int i=0;i<reminders.size();i++){
            final ReminderActive r = reminders.get(i);
            final int reminder_id = r.id;
            final int reminder_snooze_id = r.next_snooze_id;

            if(r.enabled){
                try {
                    if(reminder_snooze_id > 0 && calendar.getTime().after(UtilsDateTime.toDate(reminder_snooze_id))){
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                            ReminderActive from = realm.where(ReminderActive.class).equalTo("id", reminder_id).findFirst();
                            ReminderMissed to = new ReminderMissed();
                            to.id = from.id;
                            to.name = from.name;
                            to.note = from.note;
                            realm.insertOrUpdate(to);
                            from.deleteFromRealm();
                            }
                        });
                        unSet(context, reminder_id);
                    }
                    else if(calendar.getTime().after(UtilsDateTime.toDate(reminder_id))){
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                ReminderActive from = realm.where(ReminderActive.class).equalTo("id", reminder_id).findFirst();
                                ReminderMissed to = new ReminderMissed();
                                to.id = from.id;
                                to.name = from.name;
                                to.note = from.note;
                                realm.insertOrUpdate(to);
                                from.deleteFromRealm();
                            }
                        });
                        unSet(context, reminder_id);
                    }
                    else{
                        set(context, r);
                    }
                }
                catch (ParseException e){
                    Toast.makeText(context, "BOOT ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
