package com.example.remindme;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindme.dataModels.ReminderDismissed;
import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.util.UtilsActivity;
import com.example.remindme.util.UtilsAlarm;
import com.example.remindme.util.ServiceAlarm;
import com.example.remindme.util.UtilsDateTime;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityReminderRinging extends AppCompatActivity {

    ReminderActive activeReminder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_ringing);
        UtilsActivity.setTitle(this);

        final Button btnDismiss = findViewById(R.id.btn_reminder_ringing_dismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            final ReminderDismissed reminderDismissed = new ReminderDismissed();
            reminderDismissed.id = activeReminder.id;
            reminderDismissed.name = activeReminder.name;
            reminderDismissed.note = activeReminder.note;

            Realm r = Realm.getDefaultInstance();

            r.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                realm.insertOrUpdate(reminderDismissed);
                RealmResults<ReminderActive> result = realm
                        .where(ReminderActive.class)
                        .equalTo("id", activeReminder.id)
                        .findAll();
                result.deleteAllFromRealm();
                }
            });

            Intent intentService = new Intent(ActivityReminderRinging.this, ServiceAlarm.class);
            getApplicationContext().stopService(intentService);

            finish();
            }
        });

        final Button btnSnooze = findViewById(R.id.btn_reminder_ringing_snooze);
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            final Calendar calendar = Calendar.getInstance();
            try {

                if(activeReminder.next_snooze_id > 0) {
                    calendar.setTime(UtilsDateTime.toDate(activeReminder.next_snooze_id));
                }else{
                    calendar.setTime(UtilsDateTime.toDate(activeReminder.id));
                }
                calendar.add(Calendar.MINUTE, 5);

                Realm r = Realm.getDefaultInstance();
                r.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                    activeReminder.next_snooze_id = UtilsDateTime.toInt(calendar.getTime());
                    realm.insertOrUpdate(activeReminder);
                    }
                });

                UtilsAlarm.set(getApplicationContext(), activeReminder);

            } catch (ParseException e) {
                Toast.makeText(ActivityReminderRinging.this, "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            Intent intentService = new Intent(ActivityReminderRinging.this, ServiceAlarm.class);
            getApplicationContext().stopService(intentService);

            finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Intent i = getIntent();
            final Integer id = i.getIntExtra("ID", 0);

            Realm r = Realm.getDefaultInstance();
            activeReminder = r.where(ReminderActive.class).equalTo("id", id).findFirst();

            final Date d;
            String date_str = null;
            try {
                d = UtilsDateTime.toDate(activeReminder.id);
                date_str = UtilsDateTime.toTimeDateString(d);
                TextView t_date = findViewById(R.id.txt_reminder_ringing_date);
                Spannable spannable = new SpannableString(date_str);
                spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_success)), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new RelativeSizeSpan(1.5f), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                t_date.setText(spannable);
            }
            catch (ParseException e) {
                Toast.makeText(this, "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            ((TextView) findViewById(R.id.txt_reminder_ringing_name)).setText(this.activeReminder.name);
            ((TextView) findViewById(R.id.txt_reminder_ringing_note)).setText(this.activeReminder.note);
        }
        catch (Exception e){
            Toast.makeText(this, "Well after resume " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}