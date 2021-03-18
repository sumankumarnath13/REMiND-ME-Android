package com.example.remindme;

import android.os.Bundle;
import android.os.CountDownTimer;
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
    int ringing_elapse_counter = 0;
    boolean isTouched = false;
    boolean isReady = false;
    ReminderModel reminderModel = null;
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
                    reminderModel.broadcastDismiss(true);
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
                    reminderModel.broadcastSnooze(true);
                }

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReady) {
            final String id = ReminderModel.getReminderId(getIntent());
            reminderModel = ReminderModel.read(ActivityReminderRinging.this, id);
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

                timer = new CountDownTimer(3 * 60 * 1000L, 60 * 1000L) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        ringing_elapse_counter++;
                        if (reminderModel != null) {
                            if (reminderModel.name == null) {
                                ((TextView) findViewById(R.id.tv_reminder_name)).setText(" (" + ringing_elapse_counter + ")");
                            } else {
                                ((TextView) findViewById(R.id.tv_reminder_name)).setText(reminderModel.name + " (" + ringing_elapse_counter + ")");
                            }
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (reminderModel != null) {
                            isTouched = true;
                            reminderModel.broadcastSnooze(false);
                        }
                        finish();
                    }
                }.start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // Disables back button
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isReady) {
            isReady = !isChangingConfigurations();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReady) {

            if (!isTouched) {
                if (reminderModel == null) {
                    ReminderModel.error(this, "Reminder not found!");
                } else {
                    reminderModel.broadcastSnooze(false);
                }
            }

            if (timer != null) {
                timer.cancel();
            }
        }
    }
}