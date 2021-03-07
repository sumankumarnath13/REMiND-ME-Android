package com.example.remindme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.dataModels.ReminderDismissed;
import com.example.remindme.dataModels.ReminderMissed;
import com.example.remindme.util.UtilsActivity;
import com.example.remindme.util.UtilsDateTime;
import com.example.remindme.viewModels.ReminderModel;

import javax.annotation.ParametersAreNonnullByDefault;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityReminderView extends AppCompatActivity {
    String id;
    String alarm_time = null;
    String name = null;
    String note = null;
    String from = null;
    //private ReminderModel reminderModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_view);
        UtilsActivity.setTitle(this);

        Intent i = getIntent();
        id = i.getStringExtra(ReminderModel.INTENT_ATTR_ID);
        from = i.getStringExtra("FROM");

        final Button btnDelete = findViewById(R.id.btn_reminder_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals("ACTIVE")) {
                    ReminderModel reminderModel = ReminderModel.read(id);
                    reminderModel.cancelAlarm(getApplicationContext());
                    reminderModel.delete();
                } else if (from.equals("MISSED")) {
                    Realm r = Realm.getDefaultInstance();
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        @ParametersAreNonnullByDefault
                        public void execute(Realm realm) {
                            RealmResults<ReminderMissed> results = realm.where(ReminderMissed.class)
                                    .equalTo("id", id).findAll();
                            results.deleteAllFromRealm();
                        }
                    });
                } else {
                    Realm r = Realm.getDefaultInstance();
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        @ParametersAreNonnullByDefault
                        public void execute(Realm realm) {
                            RealmResults<ReminderDismissed> results = realm.where(ReminderDismissed.class)
                                    .equalTo("id", id).findAll();
                            results.deleteAllFromRealm();
                        }
                    });
                }
                finish();
            }
        });

        final Button btnChange = findViewById(R.id.btn_reminder_edit);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent input_i = new Intent(getApplicationContext(), ActivityReminderInput.class);
                input_i.putExtra(ReminderModel.INTENT_ATTR_ID, id);
                input_i.putExtra("FROM", from);
                startActivity(input_i);
                finish();
            }
        });

        final SwitchCompat enabled = findViewById(R.id.sw_reminder_enabled);
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ReminderModel reminderModel = ReminderModel.read(id);
                reminderModel.isEnable = b;
                //realm.insertOrUpdate(reminder);
                if (b) {
                    reminderModel.setAlarm(getApplicationContext());
                } else {
                    reminderModel.cancelAlarm(getApplicationContext());
                }
                reminderModel.insertOrUpdate();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent i = getIntent();
        id = i.getStringExtra(ReminderModel.INTENT_ATTR_ID);
        from = i.getStringExtra("FROM");

        final ImageView img = findViewById((R.id.img_snooze));
        final SwitchCompat sw_enabled = findViewById(R.id.sw_reminder_enabled);
        img.setVisibility(View.GONE);
        sw_enabled.setVisibility(View.GONE);

        if (from.equals("ACTIVE")) {
            ReminderModel reminderModel = ReminderModel.read(id);
            alarm_time = UtilsDateTime.toTimeDateString(reminderModel.time);
            if (reminderModel.nextSnoozeOffTime != null) {
                ImageView snooze_img = findViewById(R.id.img_snooze);
                TextView next_snooze = findViewById(R.id.tv_reminder_next_snooze);
                next_snooze.setText(UtilsDateTime.toTimeString(reminderModel.nextSnoozeOffTime));
                img.setVisibility(View.VISIBLE);
                snooze_img.setVisibility(View.VISIBLE);
            }
            sw_enabled.setVisibility(View.VISIBLE);
            sw_enabled.setChecked(reminderModel.isEnable);
            name = reminderModel.name;
            note = reminderModel.note;

        } else if (from.equals("MISSED")) {
            Realm r = Realm.getDefaultInstance();
            ReminderMissed reminder = r
                    .where(ReminderMissed.class)
                    .equalTo("id", id)
                    .findFirst();
            if (reminder != null) {
                alarm_time = UtilsDateTime.toTimeDateString(reminder.time);
                name = reminder.name;
                note = reminder.note;
            }
        } else {
            Realm r = Realm.getDefaultInstance();
            ReminderDismissed reminder = r
                    .where(ReminderDismissed.class)
                    .equalTo("id", id)
                    .findFirst();
            if (reminder != null) {
                alarm_time = UtilsDateTime.toTimeDateString(reminder.time);
                name = reminder.name;
                note = reminder.note;
            }
        }

        ((TextView) findViewById(R.id.tv_reminder_time)).setText(alarm_time);
        ((TextView) findViewById(R.id.tv_reminder_name)).setText(name);
        ((TextView) findViewById(R.id.txt_reminder_note)).setText(note);
    }
}