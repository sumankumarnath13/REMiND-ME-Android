package com.example.remindme.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.remindme.viewModels.ReminderModel;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ReminderModel.showToast(context, "Remind me: Device reboot acknowledged");
            ReminderModel.bootCompletedOnReceive(context);
        }
    }
}
