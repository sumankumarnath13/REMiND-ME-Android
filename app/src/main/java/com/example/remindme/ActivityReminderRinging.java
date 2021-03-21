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

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.util.UtilsActivity;
import com.example.remindme.util.UtilsDateTime;
import com.example.remindme.viewModels.ReminderModel;

public class ActivityReminderRinging extends AppCompatActivity {
    private int ringing_elapse_counter = 0;
    private boolean isTouched = false;
    private boolean isFirstLoad = true;
    //boolean isChangingConfig = false;
    private ReminderModel reminderModel = null;
    private CountDownTimer timer = null;
    private static final long ringTimeDuration = 60 * 1000L;
    private static final long ringTimeInterval = 1000L;

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

    private String reminderId;

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstLoad) { // Second null check if it present in the database.
            isFirstLoad = false;

            reminderId = ReminderModel.getReminderId(getIntent());
            reminderModel = ReminderModel.read(ActivityReminderRinging.this, reminderId);
            if (reminderModel == null) {
                ReminderModel.error(this, "Reminder not found!");
                finish();
                return;
            }
        }

        if (reminderModel != null) {
            String date_str = UtilsDateTime.toTimeDateString(reminderModel.time);
            TextView t_date = findViewById(R.id.txt_reminder_ringing_date);
            Spannable spannable = new SpannableString(date_str);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_success)), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new RelativeSizeSpan(1.5f), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            t_date.setText(spannable);

            ((TextView) findViewById(R.id.tv_reminder_name)).setText(reminderModel.name);
            ((TextView) findViewById(R.id.txt_reminder_note)).setText(reminderModel.note);

            if (timer != null) {
                timer = new CountDownTimer(ringTimeDuration, ringTimeInterval) {
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
//        if (!isReady) { // If not ready then only make it read. Else, just keep the state.
//            isReady = !isChangingConfigurations(); //isChangingConfigurations returns true if switching from "landscape <> portrait"
//        }

        //isChangingConfig = isChangingConfigurations();

        if (!isChangingConfigurations()) {
            if (!isTouched) {
                if (reminderModel != null) {
                    reminderModel.broadcastSnooze(false);
                    reminderModel = null;
                }
            }

            if (timer != null) {
                timer.cancel();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (isReady) {
//
//            if (!isTouched) {
//                if (reminderModel == null) {
//                    ReminderModel.error(this, "Reminder not found!");
//                } else {
//                    reminderModel.broadcastSnooze(false);
//                }
//            }
//
//            if (timer != null) {
//                timer.cancel();
//            }
//        }
    }
}