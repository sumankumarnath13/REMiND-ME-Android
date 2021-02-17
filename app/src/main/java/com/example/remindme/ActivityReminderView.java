package com.example.remindme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.remindme.dataModels.ReminderDismissed;
import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.dataModels.ReminderMissed;
import com.example.remindme.util.UtilsActivity;
import com.example.remindme.util.UtilsAlarm;
import com.example.remindme.util.UtilsDateTime;
import java.text.ParseException;
import javax.annotation.ParametersAreNonnullByDefault;
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
            public void onClick(View view)
            {
                UtilsAlarm.unSet(getApplicationContext(), alarm_id);
                Realm r = Realm.getDefaultInstance();
                r.executeTransaction(new Realm.Transaction() {
                    @Override
                    @ParametersAreNonnullByDefault
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
                        else{
                            RealmResults<ReminderDismissed> results = realm.where(ReminderDismissed.class)
                                    .equalTo("id", alarm_id) .findAll();
                            results.deleteAllFromRealm();
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

//        final Button btnBack = findViewById(R.id.btn_reminder_back);
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        final SwitchCompat enabled = findViewById(R.id.sw_reminder_enabled);
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                final boolean enabled = b;
                if(reminderActive != null){
                    Realm r = Realm.getDefaultInstance();
                    r.executeTransaction(new Realm.Transaction() {
                        @ParametersAreNonnullByDefault
                        @Override
                        public void execute(Realm realm) {
                            reminderActive.isEnable = enabled;
                            realm.insertOrUpdate(reminderActive);
                            if(enabled) {
                                try {
                                    UtilsAlarm.set(getApplicationContext(), reminderActive);
                                } catch (ParseException e) {
                                    Toast.makeText(ActivityReminderView.this, "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                UtilsAlarm.unSet(ActivityReminderView.this, reminderActive.id);
                            }
                        }
                    });
                }
            }
        });
    }

    Integer alarm_id;
    String alarm_time = null;
    String name = null;
    String note = null;
    String from = null;

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        alarm_id = i.getIntExtra("ID", 0);
        from = i.getStringExtra("FROM");
        final ImageView img = findViewById((R.id.img_snooze));
        final SwitchCompat sw_enabled = findViewById(R.id.sw_reminder_enabled);
        img.setVisibility(View.GONE);
        sw_enabled.setVisibility(View.GONE);

        if(alarm_id == 0){
            return;
        }

        try {
            if(from.equals("ACTIVE")){
                Realm r = Realm.getDefaultInstance();
                reminderActive = r
                        .where(ReminderActive.class)
                        .equalTo("id", alarm_id)
                        .findFirst();
                if(reminderActive != null) {
                    alarm_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminderActive.id));
                    if (reminderActive.next_snooze_id > 0) {
                        ImageView snooze_img = findViewById(R.id.img_snooze);
                        TextView next_snooze = findViewById(R.id.tv_reminder_next_snooze);
                        next_snooze.setText(UtilsDateTime.toTimeString(UtilsDateTime.toDate(reminderActive.next_snooze_id)));
                        img.setVisibility(View.VISIBLE);
                        snooze_img.setVisibility(View.VISIBLE);
                    }
                    sw_enabled.setVisibility(View.VISIBLE);
                    sw_enabled.setChecked(reminderActive.isEnable);
                    name = reminderActive.name;
                    note = reminderActive.note;
                }
            }
            else if(from.equals("MISSED")){
                Realm r = Realm.getDefaultInstance();
                ReminderMissed reminder = r
                        .where(ReminderMissed.class)
                        .equalTo("id", alarm_id)
                        .findFirst();
                if(reminder != null) {
                    alarm_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminder.id));
                    name = reminder.name;
                    note = reminder.note;
                }
            }
            else{
                Realm r = Realm.getDefaultInstance();
                ReminderDismissed reminder = r
                        .where(ReminderDismissed.class)
                        .equalTo("id", alarm_id)
                        .findFirst();
                if(reminder != null) {
                    alarm_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminder.id));
                    name = reminder.name;
                    note = reminder.note;
                }
            }
        } catch (ParseException e) {
            Toast.makeText(this, "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        ((TextView)findViewById(R.id.tv_reminder_time)).setText(alarm_time);
        ((TextView)findViewById(R.id.tv_reminder_name)).setText(name);
        ((TextView)findViewById(R.id.tv_reminder_note)).setText(note);
    }
}