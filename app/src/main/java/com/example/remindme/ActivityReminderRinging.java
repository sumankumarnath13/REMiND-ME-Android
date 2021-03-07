package com.example.remindme;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.util.UtilsActivity;
import com.example.remindme.util.UtilsDateTime;
import com.example.remindme.viewModels.ReminderModel;

public class ActivityReminderRinging extends AppCompatActivity {

    ReminderModel reminderModel = null;
    Ringtone alarmTone = null;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_ringing);
        UtilsActivity.setTitle(this);

        // Important: have to do the following in order to show without unlocking
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        alarmTone = RingtoneManager.getRingtone(getApplicationContext(), notification);

        PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My:Tag");
        wakeLock.acquire(5 * 60 * 1000L /*5 minutes*/);

        final Button btnDismiss = findViewById(R.id.btn_reminder_ringing_dismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reminderModel != null) {
                    reminderModel.dismiss(getApplicationContext());
                }

                finish();
            }
        });

        final Button btnSnooze = findViewById(R.id.btn_reminder_ringing_snooze);
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reminderModel != null) {
                    reminderModel.snooze(getApplicationContext());
                }

                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            Intent i = getIntent();
            final String id = i.getStringExtra(ReminderModel.INTENT_ATTR_ID);
            reminderModel = ReminderModel.read(id);

            if (reminderModel == null) {
                Toast.makeText(this, "Reminder not found!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String date_str = UtilsDateTime.toTimeDateString(reminderModel.time);
                TextView t_date = findViewById(R.id.txt_reminder_ringing_date);
                Spannable spannable = new SpannableString(date_str);
                spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_success)), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new RelativeSizeSpan(1.5f), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                t_date.setText(spannable);

                ((TextView) findViewById(R.id.tv_reminder_name)).setText(reminderModel.name);
                ((TextView) findViewById(R.id.txt_reminder_note)).setText(reminderModel.note);

                if (alarmTone != null) {
                    alarmTone.play();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Well after start " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (alarmTone != null) {
            alarmTone.stop();
        }

        if (wakeLock != null) {
            wakeLock.release();
        }
    }

}