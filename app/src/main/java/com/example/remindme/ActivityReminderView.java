package com.example.remindme;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindme.dataModels.ReminderDismissed;
import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.dataModels.ReminderMissed;
import com.example.remindme.util.UtilsActivity;
import com.example.remindme.util.UtilsAlarm;
import com.example.remindme.util.UtilsDateTime;
import java.text.ParseException;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityReminderView extends AppCompatActivity {

    private ReminderActive reminderActive = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_view);
        UtilsActivity.setTitle(this);

        final Button btnDelete = findViewById(R.id.btn_reminder_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UtilsAlarm.unSet(getApplicationContext(), alarm_id);

                Realm r = Realm.getDefaultInstance();
                r.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        if(from.equals("ACTIVE")) {
                            RealmResults<ReminderActive> results = realm.where(ReminderActive.class)
                                    .equalTo("id", alarm_id) .findAll();
                            results.deleteAllFromRealm();
                        }
                        else if (from.equals("MISSED")){
                            RealmResults<ReminderMissed> results = realm.where(ReminderMissed.class)
                                    .equalTo("id", alarm_id) .findAll();
                            results.deleteAllFromRealm();
                        }
                        else if (from.equals("DISMISSED")){
                            RealmResults<ReminderDismissed> results = realm.where(ReminderDismissed.class)
                                    .equalTo("id", alarm_id) .findAll();
                            results.deleteAllFromRealm();
                        }
                        else{

                        }

                        finish();
                    }
                });
            }
        });

        final Button btnChange = findViewById(R.id.btn_reminder_edit);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent input_i = new Intent(getApplicationContext(), ActivityReminderInput.class);
            input_i.putExtra("ID", alarm_id);
            input_i.putExtra("TIME", alarm_time);
            input_i.putExtra("NAME", name);
            input_i.putExtra("NOTE", note);
            input_i.putExtra("FROM", from);
            startActivity(input_i);
            finish();
            }
        });

        final Switch enabled = findViewById(R.id.sw_reminder_enabled);
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                final boolean enabled = b;
                if(reminderActive !=null){
                    Realm r = Realm.getDefaultInstance();
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            reminderActive.enabled = enabled;
                            realm.insertOrUpdate(reminderActive);
                            if(enabled){
                                try {
                                    UtilsAlarm.set(getApplicationContext(), reminderActive);
                                } catch (ParseException e) {
                                    Toast.makeText(ActivityReminderView.this, "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                UtilsAlarm.unSet(getApplicationContext(), reminderActive.id);
                            }
                        }
                    });
                }
            }
        });
    }

    Integer alarm_id;
    String alarm_time = null;
    String next_snooze_time = null;
    String name = null;
    String note = null;
    String from = null;

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();

        alarm_id = i.getIntExtra("ID", 0);
        from = i.getStringExtra("FROM");

        Switch sw_enabled = findViewById(R.id.sw_reminder_enabled);
        sw_enabled.setVisibility(View.GONE);

        if(alarm_id == 0){
            return;
        }

        if(from.equals("ACTIVE")){
            Realm r = Realm.getDefaultInstance();
            reminderActive = r
                .where(ReminderActive.class)
                .equalTo("id", alarm_id)
                .findFirst();

            try {
                alarm_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminderActive.id));
                if(reminderActive.next_snooze_id  > 0){
                    next_snooze_time = UtilsDateTime.toTimeString(UtilsDateTime.toDate(reminderActive.next_snooze_id));
                }
            } catch (ParseException e) {
                Toast.makeText(this, "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            sw_enabled.setVisibility(View.VISIBLE);
            sw_enabled.setChecked(reminderActive.enabled);
            name = reminderActive.name;
            note = reminderActive.note;

        }
        else if(from.equals("MISSED")){
            Realm r = Realm.getDefaultInstance();
            ReminderMissed reminder = r
                    .where(ReminderMissed.class)
                    .equalTo("id", alarm_id)
                    .findFirst();

            try {
                alarm_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminder.id));
            } catch (ParseException e) {
                Toast.makeText(this, "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            name = reminder.name;
            note = reminder.note;
        }
        else if(from.equals("DISMISSED")){
            Realm r = Realm.getDefaultInstance();
            ReminderDismissed reminder = r
                    .where(ReminderDismissed.class)
                    .equalTo("id", alarm_id)
                    .findFirst();

            try {
                alarm_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminder.id));
            } catch (ParseException e) {
                Toast.makeText(this, "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            name = reminder.name;
            note = reminder.note;
        }
        else{

        }

        TextView t_date = findViewById(R.id.tv_reminder_time);
        ImageView snooze_img = findViewById(R.id.img_snooze);
        TextView next_snooze = findViewById(R.id.tv_reminder_next_snooze);
        t_date.setText(alarm_time);

        if(next_snooze_time != null){
            next_snooze.setText(next_snooze_time);
            snooze_img.setVisibility(View.VISIBLE);
        }else{
            snooze_img.setVisibility(View.GONE);
        }

        ((TextView)findViewById(R.id.tv_reminder_name)).setText(name);
        ((TextView)findViewById(R.id.tv_reminder_note)).setText(note);

    }
}