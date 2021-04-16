package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.R;
import com.example.remindme.dataModels.DismissedReminder;
import com.example.remindme.dataModels.MissedReminder;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
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
        ActivityHelper.setTitle(this, getResources().getString(R.string.view_reminder_heading));

        Intent i = getIntent();
        id = ReminderModel.getReminderId(i);
        from = i.getStringExtra("FROM");

        final Button btnDelete = findViewById(R.id.btn_reminder_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals("ACTIVE")) {
                    ReminderModel reminderModel = new ReminderModel();
                    if (reminderModel.tryReadFrom(getIntent())) {
                        reminderModel.deleteAndCancelAlert(getApplicationContext());
                    }
                } else if (from.equals("MISSED")) {
                    Realm r = Realm.getDefaultInstance();
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        @ParametersAreNonnullByDefault
                        public void execute(Realm realm) {
                            RealmResults<MissedReminder> results = realm.where(MissedReminder.class)
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
                            RealmResults<DismissedReminder> results = realm.where(DismissedReminder.class)
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
                ReminderModel.setReminderId(input_i, id);
                input_i.putExtra("FROM", from);
                startActivity(input_i);
                finish();
            }
        });

        final SwitchCompat enabled = findViewById(R.id.sw_reminder_enabled);

        enabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderModel reminderModel = new ReminderModel();
                if (reminderModel.tryReadFrom(getIntent())) {
                    enabled.setChecked(reminderModel.trySetEnabled(getApplicationContext(), enabled.isChecked()));
                    if (enabled.isChecked()) {
                        ((TextView) findViewById(R.id.tv_reminder_time)).setText(StringHelper.toTimeDate(reminderModel.getOriginalTime()));
                    }
                    reminderModel.trySaveAndSetAlert(getApplicationContext(), true, true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent i = getIntent();
        id = ReminderModel.getReminderId(i);
        from = i.getStringExtra("FROM");

        final ImageView img = findViewById((R.id.img_snooze));
        final SwitchCompat sw_enabled = findViewById(R.id.sw_reminder_enabled);
        img.setVisibility(View.GONE);
        sw_enabled.setVisibility(View.GONE);

        if (from.equals("ACTIVE")) {
            ReminderModel reminderModel = new ReminderModel();
            if (reminderModel.tryReadFrom(getIntent())) {
                alarm_time = StringHelper.toTimeDate(reminderModel.getOriginalTime());
                if (reminderModel.getNextSnoozeOffTime() != null) {
                    ImageView snooze_img = findViewById(R.id.img_snooze);
                    TextView next_snooze = findViewById(R.id.tv_reminder_next_snooze);
                    next_snooze.setText(StringHelper.toTime(reminderModel.getNextSnoozeOffTime()));
                    img.setVisibility(View.VISIBLE);
                    snooze_img.setVisibility(View.VISIBLE);
                }
                sw_enabled.setVisibility(View.VISIBLE);
                sw_enabled.setChecked(reminderModel.getIsEnabled());
                name = reminderModel.name;
                note = reminderModel.note;
            } else {
                ToastHelper.showLong(ActivityReminderView.this, "Reminder not found!");
                finish();
            }
        } else if (from.equals("MISSED")) {
            Realm r = Realm.getDefaultInstance();
            MissedReminder reminder = r
                    .where(MissedReminder.class)
                    .equalTo("id", id)
                    .findFirst();
            if (reminder != null) {
                alarm_time = StringHelper.toTimeDate(reminder.time);
                name = reminder.name;
                note = reminder.note;
            } else {
                Toast.makeText(ActivityReminderView.this, "Reminder not found!", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Realm r = Realm.getDefaultInstance();
            DismissedReminder reminder = r
                    .where(DismissedReminder.class)
                    .equalTo("id", id)
                    .findFirst();
            if (reminder != null) {
                alarm_time = StringHelper.toTimeDate(reminder.time);
                name = reminder.name;
                note = reminder.note;
            } else {
                Toast.makeText(ActivityReminderView.this, "Reminder not found!", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        ((TextView) findViewById(R.id.tv_reminder_time)).setText(alarm_time);
        ((TextView) findViewById(R.id.tv_reminder_name)).setText(name);
        ((TextView) findViewById(R.id.txt_reminder_note)).setText(note);
    }
}