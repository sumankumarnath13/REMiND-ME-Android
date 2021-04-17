package com.example.remindme.ui.main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.R;
import com.example.remindme.dataModels.ActiveReminder;
import com.example.remindme.dataModels.DismissedReminder;
import com.example.remindme.dataModels.MissedReminder;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.ui.activities.ActivityReminderView;
import com.example.remindme.viewModels.ReminderModel;

import java.util.List;

import io.realm.RealmObject;

public class AdapterRecyclerReminder extends RecyclerView.Adapter<AdapterRecyclerReminder.ReminderHolder> {

    private final List<? extends RealmObject> _data;
    private final EnumReminderTypes reminderType;

    public AdapterRecyclerReminder(List<? extends RealmObject> data, EnumReminderTypes type) {
        this._data = data;
        this.reminderType = type;
    }

    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recycler_reminder_item, parent, false);

        return new ReminderHolder(v, reminderType);
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


        final RealmObject reminder = _data.get(position);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(holder.linearLayout.getContext(), ActivityReminderView.class);

                if (reminderType == EnumReminderTypes.Active) {

                    final ActiveReminder activeReminder = (ActiveReminder) reminder;
                    ReminderModel.setReminderId(intent, activeReminder.id);
                    intent.putExtra(ReminderModel.INTENT_ATTR_FROM, "ACTIVE");

                } else if (reminderType == EnumReminderTypes.Missed) {

                    final MissedReminder missedReminder = (MissedReminder) reminder;
                    ReminderModel.setReminderId(intent, missedReminder.id);
                    intent.putExtra(ReminderModel.INTENT_ATTR_FROM, "MISSED");

                } else {

                    final DismissedReminder dismissedReminder = (DismissedReminder) reminder;
                    ReminderModel.setReminderId(intent, dismissedReminder.id);
                    intent.putExtra(ReminderModel.INTENT_ATTR_FROM, "DISMISSED");

                }

                holder.linearLayout.getContext().startActivity(intent);
            }
        });

        enabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderModel reminderModel = new ReminderModel();
                ReminderModel.transformToModel((ActiveReminder) reminder, reminderModel);

                enabled.setChecked(reminderModel.trySetEnabled(holder.linearLayout.getContext().getApplicationContext(), enabled.isChecked()));
                if (enabled.isChecked()) {
                    time.setText(StringHelper.toTimeDate(reminderModel.getOriginalTime()));
                }
                reminderModel.trySaveAndSetAlert(holder.linearLayout.getContext().getApplicationContext(), true, true);
            }
        });

        if (reminderType == EnumReminderTypes.Active) {
            ReminderModel reminderModel = new ReminderModel();
            ReminderModel.transformToModel((ActiveReminder) reminder, reminderModel);

            time.setText(StringHelper.toTime(reminderModel.getOriginalTime()));
            date.setText(StringHelper.toWeekdayDate(reminderModel.getOriginalTime()));
            enabled.setChecked(reminderModel.getIsEnabled());

            tv_reminder_repeat_short_summary.setText(reminderModel.getRepeatSettingShortString());

            if (reminderModel.getNextSnoozeOffTime() != null) {
                lv_reminder_view_snooze.setVisibility(View.VISIBLE);
                next_snooze.setText(StringHelper.toTime(reminderModel.getNextSnoozeOffTime()));
            } else {
                lv_reminder_view_snooze.setVisibility(View.GONE);
            }

            if (reminderModel.getLastMissedTime() != null) {
                lv_reminder_last_missed_time.setVisibility(View.VISIBLE);
                tv_reminder_last_missed_time.setText(StringHelper.toTimeDate(reminderModel.getLastMissedTime()));
            } else {
                lv_reminder_last_missed_time.setVisibility(View.GONE);
            }

            if (StringHelper.isNullOrEmpty(reminderModel.getName())) {
                name.setVisibility(View.GONE);
            } else {
                name.setText(reminderModel.getName());
                name.setVisibility(View.VISIBLE);
            }


        } else if (holder.reminderType == EnumReminderTypes.Missed) {
            final MissedReminder missedReminder = (MissedReminder) reminder;

            time.setTextColor(holder.linearLayout.getResources().getColor(R.color.text_danger));

            time.setText(StringHelper.toTime(missedReminder.time));
            date.setText(StringHelper.toWeekdayDate(missedReminder.time));
            enabled.setVisibility(View.GONE);

            if (StringHelper.isNullOrEmpty(missedReminder.name)) {
                name.setVisibility(View.GONE);
            } else {
                name.setText(missedReminder.name);
                name.setVisibility(View.VISIBLE);
            }

        } else {
            final DismissedReminder dismissedReminder = (DismissedReminder) reminder;

            time.setTextColor(holder.linearLayout.getResources().getColor(R.color.text_gray2));

            time.setText(StringHelper.toTime(dismissedReminder.time));
            date.setText(StringHelper.toWeekdayDate(dismissedReminder.time));
            enabled.setVisibility(View.GONE);

            if (StringHelper.isNullOrEmpty(dismissedReminder.name)) {
                name.setVisibility(View.GONE);
            } else {
                name.setText(dismissedReminder.name);
                name.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    public static class ReminderHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final LinearLayout linearLayout;
        public final EnumReminderTypes reminderType;

        public ReminderHolder(LinearLayout v, EnumReminderTypes t) {
            super(v);
            linearLayout = v;
            reminderType = t;
        }
    }
}
