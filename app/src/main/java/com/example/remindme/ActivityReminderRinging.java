package com.example.remindme;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.controllers.AlertService;
import com.example.remindme.controllers.AlertServiceBinder;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.ReminderModel;

public class ActivityReminderRinging extends AppCompatActivity {

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

    private boolean mServiceBound = false;
    private AlertServiceBinder serviceBinder;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (AlertServiceBinder) service;

            if (serviceBinder.getIsIdle()) {
                serviceBinder.setActivityOpen(true);

                ReminderModel reminderModel = serviceBinder.getServingReminder();
                if (reminderModel == null) {
                    ReminderModel.showToast("Serious flow trouble!");
                    finish();
                    return;
                }

                String date_str = StringHelper.toTimeDate(reminderModel.getOriginalTime());
                TextView t_date = findViewById(R.id.txt_reminder_ringing_date);
                Spannable spannable = new SpannableString(date_str);
                spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_success)), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new RelativeSizeSpan(1.5f), 0, date_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                t_date.setText(spannable);
                ((TextView) findViewById(R.id.tv_reminder_name)).setText(reminderModel.name);
                ((TextView) findViewById(R.id.txt_reminder_note)).setText(reminderModel.note);

                btnSnooze.setVisibility(View.VISIBLE);
                btnDismiss.setVisibility(View.VISIBLE);

            } else {
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //mServiceBound = false;
        }
    };

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ReminderModel.ACTION_CLOSE_ALARM_ACTIVITY);
            registerReceiver(receiver, filter);
            isReceiverRegistered = true;
        }
    }

    private void unRegisterReceiver() {
        if (isReceiverRegistered) {
            unregisterReceiver(receiver);
            isReceiverRegistered = false;
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

    private Button btnSnooze;
    private Button btnDismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_ringing);
        ActivityHelper.setTitle(this, getResources().getString(R.string.reminder_alert_heading));

        // Important: have to do the following in order to show without unlocking
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        btnDismiss = findViewById(R.id.btn_reminder_ringing_dismiss);
        btnDismiss.setVisibility(View.INVISIBLE);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceBinder.dismiss();
                finish();
            }
        });

        btnSnooze = findViewById(R.id.btn_reminder_ringing_snooze);
        btnSnooze.setVisibility(View.INVISIBLE);
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceBinder.snooze();
                finish();
            }
        });

        bindAlarmService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindAlarmService();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (serviceBinder != null && mServiceBound) {
            serviceBinder.setActivityOpen(false);
        }
        unbindAlarmService();
        unRegisterReceiver();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // Disables back button
    }
}