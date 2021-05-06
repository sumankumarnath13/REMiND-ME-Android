package com.example.remindme.ui.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.remindme.R;
import com.example.remindme.controllers.AlertService;
import com.example.remindme.controllers.AlertServiceBinder;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.ReminderModel;

import java.util.Date;

public class ReminderRinging extends ActivityBase {

    private boolean isReceiverRegistered = false;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;

            if (ReminderModel.ACTION_CLOSE_ALARM_ACTIVITY.equals(intent.getAction())) {
                finish();
            }
        }
    };

    private boolean isScreenOffReceiverRegistered = false;
    private final BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                if (serviceBinder != null && mServiceBound) {
                    serviceBinder.snoozeByUser();
                    finish();
                }
            }
        }
    };

    private boolean mServiceBound = false;
    private AlertServiceBinder serviceBinder;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (AlertServiceBinder) service;

            serviceBinder.setActivityOpen(true);

            final ReminderModel reminderModel = serviceBinder.getServingReminder();
            if (reminderModel == null) {
                ToastHelper.showLong(ReminderRinging.this, "Serious flow trouble!");
                finish();
                return;
            }

            //tv_reminder_time.setText(StringHelper.toTime(reminderModel.getTimeModel().getTime()));
            tv_reminder_name.setText(reminderModel.getName());
            tv_reminder_note.setText(reminderModel.getNote());

            if (reminderModel.canSnooze()) {
                btnSnooze.setVisibility(View.VISIBLE);
            } else {
                btnSnooze.setVisibility(View.GONE);
            }

            currentTimeTimer.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //mServiceBound = false;
            currentTimeTimer.cancel();
        }
    };

    private final int[] colors = new int[]{R.color.text_success, R.color.text_info, R.color.text_warning, R.color.text_danger, R.color.text_soothing};
    private int colorIndex = 0;
    private final CountDownTimer currentTimeTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tv_time_now.setText(StringHelper.toAlertTime(new Date()));

            if (colorIndex >= colors.length) {
                colorIndex = 0;
            }

            String coloredTimeString = StringHelper.toTime(serviceBinder.getServingReminder().getTimeModel().getTime());
            Spannable spannable = new SpannableString(coloredTimeString);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(colors[colorIndex])), 0, coloredTimeString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //spannable.setSpan(new RelativeSizeSpan(2.7f), 0, alertStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_reminder_time.setText(spannable);

            colorIndex++;
        }

        @Override
        public void onFinish() {

        }
    };

    private void registerReceiver() {

        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ReminderModel.ACTION_CLOSE_ALARM_ACTIVITY);
            registerReceiver(receiver, filter);
            isReceiverRegistered = true;
        }

        if (!isScreenOffReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(screenOffReceiver, filter);
            isScreenOffReceiverRegistered = true;
        }
    }

    @Override
    protected void onDestroy() {
        unbindAlarmService();
        unRegisterReceiver();
        super.onDestroy();
    }

    private void unRegisterReceiver() {
        if (isReceiverRegistered) {
            unregisterReceiver(receiver);
            isReceiverRegistered = false;
        }

        if (isScreenOffReceiverRegistered) {
            unregisterReceiver(screenOffReceiver);
            isScreenOffReceiverRegistered = false;
        }
    }

    private void bindAlarmService() {
        if (!mServiceBound) {
            final Intent intent = new Intent(this, AlertService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mServiceBound = true;
        }
    }

    private void unbindAlarmService() {
        if (mServiceBound) {
            unbindService(mConnection);
            mServiceBound = false;
        }
    }

    private TextView tv_time_now;
    private TextView tv_reminder_time;
    private TextView tv_reminder_name;
    private TextView tv_reminder_note;
    private Button btnSnooze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_ringing);
        ActivityHelper.setTitle(this, null);

        // Important: have to do the following in order to show without unlocking
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (OsHelper.isOreoMr1OrLater()) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        final Button btnDismiss = findViewById(R.id.btn_reminder_dismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceBinder != null && mServiceBound) {
                    serviceBinder.dismiss();
                }
                finish();
            }
        });

        btnSnooze = findViewById(R.id.btn_reminder_snooze);
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceBinder != null && mServiceBound) {
                    serviceBinder.snoozeByUser();
                }
                finish();
            }
        });

        tv_time_now = findViewById(R.id.tv_time_now);
        tv_reminder_time = findViewById(R.id.tv_reminder_time);
        tv_reminder_name = findViewById(R.id.tv_reminder_name);
        tv_reminder_note = findViewById(R.id.tv_reminder_note);

        bindAlarmService();
        registerReceiver();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // Disables back button
    }

}