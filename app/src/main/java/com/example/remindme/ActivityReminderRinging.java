package com.example.remindme;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
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
    int counter = 0;
    boolean isTouched = false;
    boolean isScreenOn = false;
    ReminderModel reminderModel = null;
    Ringtone alarmTone = null;
    Vibrator vibrator = null;
    CountDownTimer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_ringing);
        UtilsActivity.setTitle(this, "ALERT!");

        // Important: have to do the following in order to show without unlocking
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        final Button btnDismiss = findViewById(R.id.btn_reminder_ringing_dismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTouched = true;

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
                isTouched = true;

                if (reminderModel != null) {
                    reminderModel.snooze(true, getApplicationContext());
                }

                finish();
            }
        });

        timer = new CountDownTimer(3 * 60 * 1000L, 60 * 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                counter++;
                if (reminderModel != null) {
                    if (reminderModel.name == null) {
                        ((TextView) findViewById(R.id.tv_reminder_name)).setText(" (" + counter + ")");
                    } else {
                        ((TextView) findViewById(R.id.tv_reminder_name)).setText(reminderModel.name + " (" + counter + ")");
                    }
                }
            }

            @Override
            public void onFinish() {
                if (reminderModel != null) {
                    isTouched = true;
                    reminderModel.snooze(false, getApplicationContext());
                }
                finish();
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Intent i = getIntent();
            final String id = i.getStringExtra(ReminderModel.INTENT_ATTR_ID);
            if (reminderModel == null) { // First null check to ensure that its first onStart hit after creating the activity. OnStart will be called multiple time if launched from locked screen.
                reminderModel = ReminderModel.read(id);
                if (reminderModel == null) { // Second null check if it present in the database.
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

                    if (reminderModel.isEnableTone) {
                        if (reminderModel.selectedAlarmToneUri == null) {
                            Uri alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                            alarmTone = RingtoneManager.getRingtone(getApplicationContext(), alarmToneUri);
                        } else {
                            alarmTone = RingtoneManager.getRingtone(getApplicationContext(), reminderModel.selectedAlarmToneUri);
                        }

                        if (alarmTone != null && !alarmTone.isPlaying()) {
                            alarmTone.play();
                        }
                    }

                    if (reminderModel.isVibrate) {
                        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (vibrator != null) {
                            long[] pattern = {0, 100, 1000};
                            vibrator.vibrate(pattern, 0);
                        }
                    }
                }
            } else {
                // Flow for activities that are allowed to show while in locked is
                // onCreate - onStart - onResume - onPause - onStop - onStart - onPause
                // https://stackoverflow.com/questions/25369909/onpause-and-onstop-called-immediately-after-starting-activity
                // And thus this else block will be hit on 2nd on start where no action is needed.
            }
        } catch (Exception e) {
            Toast.makeText(this, "Well after start " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // Disables back button
    }

    @Override
    protected void onResume() {
        super.onResume();
        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        isScreenOn = (Build.VERSION.SDK_INT < 20 ? powerManager.isScreenOn() : powerManager.isInteractive());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isScreenOn) {
            finish(); // Finish it when going in background.
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isTouched && reminderModel != null) {
            reminderModel.snooze(false, getApplicationContext());
        }

        if (alarmTone != null) {
            alarmTone.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }

        if (timer != null) {
            timer.cancel();
        }

    }
}