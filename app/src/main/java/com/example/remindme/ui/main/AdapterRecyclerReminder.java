package com.example.remindme.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.R;
import com.example.remindme.dataModels.Reminder;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.activities.ActivityReminderView;
import com.example.remindme.viewModels.ReminderModel;

import java.util.List;


public class AdapterRecyclerReminder extends RecyclerView.Adapter<AdapterRecyclerReminder.ReminderHolder> {

    private final List<Reminder> _data;

    public AdapterRecyclerReminder(List<Reminder> data) {
        this._data = data;
    }

    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recycler_reminder_item, parent, false);

        return new ReminderHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReminderHolder holder, int position) {
        // create a new view
        final TextView time = holder.linearLayout.findViewById(R.id.tv_reminder_time);
        final TextView date = holder.linearLayout.findViewById(R.id.tv_reminder_date);

        final TextView name = holder.linearLayout.findViewById(R.id.tv_reminder_name);
        final TextView tv_reminder_repeat_short_summary = holder.linearLayout.findViewById(R.id.tv_reminder_repeat_short_summary);
        final SwitchCompat enabled = holder.linearLayout.findViewById(R.id.sw_reminder_enabled);
        final LinearLayout lv_reminder_view_snooze = holder.linearLayout.findViewById(R.id.lv_reminder_view_snooze);
        final TextView next_snooze = holder.linearLayout.findViewById(R.id.tv_reminder_next_snooze);

        final LinearLayout lv_reminder_last_missed_time = holder.linearLayout.findViewById(R.id.lv_reminder_last_missed_time);
        final TextView tv_reminder_last_missed_time = holder.linearLayout.findViewById(R.id.tv_reminder_last_missed_time);
        final Reminder reminder = _data.get(position);


        if (reminder == null) {
            return;
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                if (context == null) return;

                final Intent intent = new Intent(context, ActivityReminderView.class);
                intent.putExtra(ReminderModel.REMINDER_ID_INTENT, reminder.id);
                context.startActivity(intent);
            }
        });

        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isRefreshing) return;

                if (AppSettingsHelper.getInstance().isDisableAllReminders()) {
                    buttonView.setChecked(false);
                    ToastHelper.showShort(buttonView.getContext(), "All reminders are disabled in settings");
                    return;
                }

                ReminderModel reminderModel = ReminderModel.getInstance(reminder);

                final Context context = buttonView.getContext();

                if (reminderModel.trySetEnabled(context, isChecked)) {
                    reminderModel.trySaveAndSetAlert(context, true, true);
                    buttonView.setChecked(isChecked);
                    if (isChecked) {
                        time.setText(StringHelper.toTime(reminderModel.getOriginalTime()));
                        date.setText(StringHelper.toWeekdayDate(reminderModel.getOriginalTime()));
                    }
                }
            }
        });


        isRefreshing = true;

        final ReminderModel reminderModel = ReminderModel.getInstance(reminder);

        time.setText(StringHelper.toTime(reminderModel.getOriginalTime()));
        date.setText(StringHelper.toWeekdayDate(reminderModel.getOriginalTime()));
        tv_reminder_repeat_short_summary.setText(reminderModel.getRepeatSettingShortString());

        if (StringHelper.isNullOrEmpty(reminderModel.getName())) {
            name.setVisibility(View.GONE);
        } else {
            name.setText(reminderModel.getName());
            name.setVisibility(View.VISIBLE);
        }

        if (reminderModel.getLastMissedTime() != null) {
            lv_reminder_last_missed_time.setVisibility(View.VISIBLE);
            tv_reminder_last_missed_time.setText(StringHelper.toTimeWeekdayDate(reminderModel.getLastMissedTime()));
        } else {
            lv_reminder_last_missed_time.setVisibility(View.GONE);
        }

        if (reminderModel.getNextSnoozeOffTime() != null) {
            lv_reminder_view_snooze.setVisibility(View.VISIBLE);
            next_snooze.setText(StringHelper.toTime(reminderModel.getNextSnoozeOffTime()));
        } else {
            lv_reminder_view_snooze.setVisibility(View.GONE);
        }

        if (reminderModel.isExpired()) {
            time.setTextColor(holder.linearLayout.getResources().getColor(R.color.text_dim));
            enabled.setVisibility(View.GONE);
        } else {
            enabled.setChecked(reminderModel.isEnabled() && !AppSettingsHelper.getInstance().isDisableAllReminders());
        }

        isRefreshing = false;

    }

    private boolean isRefreshing;

    @Override
    public int getItemCount() {
        return _data.size();
    }

    public static class ReminderHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final LinearLayout linearLayout;
        //public final EnumReminderTypes reminderType;

        public ReminderHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
            //reminderType = t;
        }
    }
}
