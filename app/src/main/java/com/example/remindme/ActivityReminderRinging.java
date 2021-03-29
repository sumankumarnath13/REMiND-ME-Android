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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.util.StringHelper;
import com.example.remindme.util.UtilsActivity;
import com.example.remindme.viewModels.ReminderModel;

public class ActivityReminderRinging extends AppCompatActivity {
    private static final long TIMER_INTERVAL = 1000L;
    private static final long TIMER_DURATION = 60 * TIMER_INTERVAL;
    private int timer_elapse;
    private static final String KEY_TIMER_ELAPSE = "¨NV/®L¹µ:G";

    private boolean isTouched;
    private boolean isRestarted;
    private static final String KEY_IS_RESTARTED = "¶tÁÑ9HÊ¶´×";
    private ReminderModel reminderModel;
    private CountDownTimer timer = null;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_TIMER_ELAPSE, timer_elapse);
        outState.putBoolean(KEY_IS_RESTARTED, isRestarted);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_ringing);
        UtilsActivity.setTitle(this, getResources().getString(R.string.reminder_alert_heading));

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            timer_elapse = savedInstanceState.getInt(KEY_TIMER_ELAPSE);
            isRestarted = savedInstanceState.getBoolean(KEY_IS_RESTARTED);
        }

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
                timer.cancel();
                reminderModel.broadcastDismiss(true);
                finish();
            }
        });

        final Button btnSnooze = findViewById(R.id.btn_reminder_ringing_snooze);
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTouched = true;
                timer.cancel();
                reminderModel.broadcastSnooze(true);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        reminderModel = new ViewModelProvider(this).get(ReminderModel.class);
        if (reminderModel.getIsEmpty()) {
            if (!reminderModel.tryReadFrom(getIntent())) {
                ReminderModel.error("Reminder not found!");
                finish();
            }
        }

        if (!reminderModel.getIsEmpty()) {
            String date_str = StringHelper.toTimeDate(reminderModel.getOriginalTime());
            TextView t_date = findViewById(R.id.txt_reminder_ringing_date);
            Spannable spannable = new SpannableString(date_str);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_success)), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new RelativeSizeSpan(1.5f), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            t_date.setText(spannable);
            ((TextView) findViewById(R.id.tv_reminder_name)).setText(reminderModel.name);
            ((TextView) findViewById(R.id.txt_reminder_note)).setText(reminderModel.note);

            timer = new CountDownTimer(TIMER_DURATION - timer_elapse * TIMER_INTERVAL, TIMER_INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timer_elapse++;
                    if (reminderModel != null) {
                        ((TextView) findViewById(R.id.tv_reminder_name)).setText(reminderModel.name + " (" + timer_elapse + ")");
                    }
                }

                @Override
                public void onFinish() {
                    reminderModel.broadcastSnooze(false);
                    finish();
                }
            }.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isRestarted) {
            isRestarted = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRestarted && !isTouched && !isChangingConfigurations()) {
            reminderModel.broadcastSnooze(false);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // Disables back button
    }
}