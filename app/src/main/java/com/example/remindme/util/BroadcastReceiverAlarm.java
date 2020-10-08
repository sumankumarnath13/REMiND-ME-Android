package com.example.remindme.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

public class BroadcastReceiverAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "U la la la", Toast.LENGTH_SHORT).show();
/*        Integer alarmId = intent.getIntExtra("ID", 0);
        Integer time = intent.getIntExtra("TIME", 0);
        String name = intent.getStringExtra("NAME");
        String note = intent.getStringExtra("NOTE");*/

        /*if (!intent.getBooleanExtra(RECURRING, false)) {
            startAlarmService(context, intent);
        } {
            if (alarmIsToday(intent)) {
                startAlarmService(context, intent);
            }
        }*/

        startAlarmService(context, intent);
    }

    private void startAlarmService(Context context, Intent intent) {
        try {
            Intent intentService = new Intent(context, ServiceAlarm.class);
            intentService.putExtra("ID", intent.getIntExtra("ID", 0));
            intentService.putExtra("TIME", intent.getIntExtra("TIME", 0));
            intentService.putExtra("NAME", intent.getStringExtra("NAME"));
            intentService.putExtra("NOTE", intent.getStringExtra("NOTE"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentService);
            } else {
                context.startService(intentService);
            }
        }
        catch (Exception e){
            Toast.makeText(context, "ALARM SERVICE START ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
