package com.example.remindme.ui.main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.ActivityReminderView;
import com.example.remindme.R;
import com.example.remindme.dataModels.ActiveReminder;
import com.example.remindme.dataModels.DismissedReminder;
import com.example.remindme.dataModels.MissedReminder;
import com.example.remindme.helpers.StringHelper;
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
            time.setText(StringHelper.toTimeDate(reminderModel.getOriginalTime()));
            if (reminderModel.getNextSnoozeOffTime() != null) {
                next_snooze.setText(StringHelper.toTime(reminderModel.getNextSnoozeOffTime()));
                img.setVisibility(View.VISIBLE);
            } else {
                img.setVisibility(View.GONE);
            }
            enabled.setChecked(reminderModel.getIsEnabled());

            if (StringHelper.isNullOrEmpty(reminderModel.name)) {
                name.setVisibility(View.GONE);
            } else {
                name.setText(reminderModel.name);
                name.setVisibility(View.VISIBLE);
            }

            if (StringHelper.isNullOrEmpty(reminderModel.note)) {
                note.setVisibility(View.GONE);
            } else {
                note.setText(reminderModel.note);
                note.setVisibility(View.VISIBLE);
            }

        } else if (holder.reminderType == EnumReminderTypes.Missed) {
            final MissedReminder missedReminder = (MissedReminder) reminder;
            time.setText(StringHelper.toTimeDate(missedReminder.time));
            time.setTextColor(holder.linearLayout.getResources().getColor(R.color.text_danger));
            enabled.setVisibility(View.GONE);

            if (StringHelper.isNullOrEmpty(missedReminder.name)) {
                name.setVisibility(View.GONE);
            } else {
                name.setText(missedReminder.name);
                name.setVisibility(View.VISIBLE);
            }

            if (StringHelper.isNullOrEmpty(missedReminder.note)) {
                note.setVisibility(View.GONE);
            } else {
                note.setText(missedReminder.note);
                note.setVisibility(View.VISIBLE);
            }
        } else {
            final DismissedReminder dismissedReminder = (DismissedReminder) reminder;
            time.setText(StringHelper.toTimeDate(dismissedReminder.time));
            time.setTextColor(holder.linearLayout.getResources().getColor(R.color.text_gray2));
            enabled.setVisibility(View.GONE);

            if (StringHelper.isNullOrEmpty(dismissedReminder.name)) {
                name.setVisibility(View.GONE);
            } else {
                name.setText(dismissedReminder.name);
                name.setVisibility(View.VISIBLE);
            }

            if (StringHelper.isNullOrEmpty(dismissedReminder.note)) {
                note.setVisibility(View.GONE);
            } else {
                note.setText(dismissedReminder.note);
                note.setVisibility(View.VISIBLE);
            }
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
