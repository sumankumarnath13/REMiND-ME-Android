package com.example.remindme.ui.main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.ActivityReminderView;
import com.example.remindme.R;
import com.example.remindme.dataModels.ActiveReminder;
import com.example.remindme.dataModels.DismissedReminder;
import com.example.remindme.dataModels.MissedReminder;
import com.example.remindme.util.UtilsDateTime;
import com.example.remindme.viewModels.ReminderModel;

import java.util.List;

import io.realm.RealmObject;

public class AdapterRecyclerReminder extends RecyclerView.Adapter<AdapterRecyclerReminder.ReminderHolder> {

    List<? extends RealmObject> _data;
    EnumReminderTypes reminderType;

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
        final TextView name = holder.linearLayout.findViewById(R.id.tv_reminder_name);
        final TextView note = holder.linearLayout.findViewById(R.id.tv_reminder_truncated_note);
        final SwitchCompat enabled = holder.linearLayout.findViewById(R.id.sw_reminder_enabled);
        final ImageView img = holder.linearLayout.findViewById(R.id.img_snooze);
        final TextView next_snooze = holder.linearLayout.findViewById(R.id.tv_reminder_next_snooze);
        final RealmObject reminder = _data.get(position);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(holder.linearLayout.getContext(), ActivityReminderView.class);

                if (reminderType == EnumReminderTypes.Active) {
                    final ActiveReminder activeReminder = (ActiveReminder) reminder;
                    ReminderModel.setReminderId(i, activeReminder.id);
                    i.putExtra("FROM", "ACTIVE");
                } else if (reminderType == EnumReminderTypes.Missed) {
                    final MissedReminder missedReminder = (MissedReminder) reminder;
                    ReminderModel.setReminderId(i, missedReminder.id);
                    i.putExtra("FROM", "MISSED");
                } else {
                    final DismissedReminder dismissedReminder = (DismissedReminder) reminder;
                    ReminderModel.setReminderId(i, dismissedReminder.id);
                    i.putExtra("FROM", "DISMISSED");
                }

                holder.linearLayout.getContext().startActivity(i);
            }
        });

        enabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderModel reminderModel = ReminderModel.transform(holder.linearLayout.getContext(), (ActiveReminder) reminder);
                if (enabled.isChecked()) {
                    if (reminderModel.canEnable()) {
                        reminderModel.setIsEnabled(enabled.isChecked());
                        time.setText(UtilsDateTime.toTimeDateString(reminderModel.time));
                    } else {
                        Toast.makeText(holder.linearLayout.getContext(), "Cannot enable in past time.", Toast.LENGTH_SHORT).show();
                        enabled.setChecked(false);
                    }
                } else {
                    reminderModel.setIsEnabled(false);
                }
            }
        });

        if (reminderType == EnumReminderTypes.Active) {
            ReminderModel reminderModel = ReminderModel.transform(holder.linearLayout.getContext(), (ActiveReminder) reminder);
            time.setText(UtilsDateTime.toTimeDateString(reminderModel.time));
            if (reminderModel.nextSnoozeOffTime != null) {
                next_snooze.setText(UtilsDateTime.toTimeString(reminderModel.nextSnoozeOffTime));
                img.setVisibility(View.VISIBLE);
            } else {
                img.setVisibility(View.GONE);
            }
            enabled.setChecked(reminderModel.getIsEnabled());
            name.setText(reminderModel.name);
            note.setText(reminderModel.note);
        } else if (holder.reminderType == EnumReminderTypes.Missed) {
            final MissedReminder missedReminder = (MissedReminder) reminder;
            time.setText(UtilsDateTime.toTimeDateString(missedReminder.time));
            time.setTextColor(holder.linearLayout.getResources().getColor(R.color.text_danger));
            enabled.setVisibility(View.GONE);
            name.setText(missedReminder.name);
            note.setText(missedReminder.note);
        } else {
            final DismissedReminder dismissedReminder = (DismissedReminder) reminder;
            time.setText(UtilsDateTime.toTimeDateString(dismissedReminder.time));
            time.setTextColor(holder.linearLayout.getResources().getColor(R.color.text_gray2));
            enabled.setVisibility(View.GONE);
            name.setText(dismissedReminder.name);
            note.setText(dismissedReminder.note);
        }
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    public static class ReminderHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout linearLayout;
        public EnumReminderTypes reminderType;

        public ReminderHolder(LinearLayout v, EnumReminderTypes t) {
            super(v);
            linearLayout = v;
            reminderType = t;
        }
    }
}
