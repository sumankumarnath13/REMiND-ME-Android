package com.example.remindme.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.activities.ReminderView;
import com.example.remindme.ui.fragments.common.iSelectionControl;
import com.example.remindme.viewModels.ReminderModel;

import java.util.ArrayList;
import java.util.List;


public class AdapterRecyclerReminder
        extends RecyclerView.Adapter<AdapterRecyclerReminder.ReminderHolder>
        implements iSelectionControl {

    private List<ReminderModel> _data;
    private boolean isEnableCheck;

    @Override
    public boolean isSelectable() {
        return isEnableCheck;
    }

    private int countSelections() {
        int count = 0;
        for (int i = 0; i < _data.size(); i++) {
            if (_data.get(i).isSelected()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void selectAll() {
        for (int i = 0; i < _data.size(); i++) {
            _data.get(i).setSelected(true);
        }
    }

    @Override
    public boolean isAllSelected() {
        return countSelections() == _data.size();
    }

    @Override
    public void selectNone() {
        for (int i = 0; i < _data.size(); i++) {
            _data.get(i).setSelected(false);
        }
    }

    @Override
    public boolean isNoneSelected() {
        return countSelections() == 0;
    }

    @Override
    public void notifyChange() {
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChange(this);
        }
    }

    @Override
    public int size() {
        return _data.size();
    }

    @Override
    public void notifySelectedAsDeleted() {
        setSelectable(false);
        for (int i = _data.size() - 1; i >= 0; i--) {
            if (_data.get(i).isSelected()) {
                _data.remove(i);
                notifyItemRemoved(i);
            }
        }
        if (listener != null) {
            listener.onSelectionChange(this);
        }
    }

    @Override
    public void setSelectable(boolean value) {
        isEnableCheck = value;
        notifyDataSetChanged();
    }

    @Override
    public int getSelectedCount() {
        return countSelections();
    }

    @Override
    public List<ReminderModel> getSelected() {
        final ArrayList<ReminderModel> selectedReminders = new ArrayList<>();
        for (int i = 0; i < _data.size(); i++) {
            if (_data.get(i).isSelected()) {
                selectedReminders.add(_data.get(i));
            }
        }
        return selectedReminders;
    }

    public interface iDataChangeListener {
        void onSelectionChange(iSelectionControl selectionControl);
    }

    private final iDataChangeListener listener;

    public AdapterRecyclerReminder(final List<ReminderModel> data, final iDataChangeListener listener) {
        _data = data;
        isEnableCheck = false;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayoutCompat v = (LinearLayoutCompat) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_fragment_reminder, parent, false);

        return new ReminderHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReminderHolder holder, int position) {
        // create a new view
        final ReminderModel reminder = _data.get(position);

        if (reminder == null) {
            return;
        }

        final AppCompatCheckBox reminderSelectionCheck = holder.linearLayout.findViewById(R.id.reminderSelectionCheck);
        final TextView time = holder.linearLayout.findViewById(R.id.tv_reminder_time);
        final TextView date = holder.linearLayout.findViewById(R.id.tv_reminder_date);

        final TextView name = holder.linearLayout.findViewById(R.id.tv_reminder_name);
        final TextView tv_reminder_repeat_short_summary = holder.linearLayout.findViewById(R.id.tv_reminder_repeat_short_summary);
        final SwitchCompat enabled = holder.linearLayout.findViewById(R.id.sw_reminder_enabled);
        final LinearLayout lv_reminder_view_snooze = holder.linearLayout.findViewById(R.id.lv_reminder_view_snooze);
        final TextView next_snooze = holder.linearLayout.findViewById(R.id.tv_reminder_next_snooze);

        final LinearLayout lv_reminder_last_missed_time = holder.linearLayout.findViewById(R.id.lv_reminder_last_missed_time);
        final TextView tv_reminder_last_missed_time = holder.linearLayout.findViewById(R.id.tv_reminder_last_missed_time);

        holder.linearLayout.setLongClickable(true);

        holder.linearLayout.setOnLongClickListener(v -> {
            isEnableCheck = !isEnableCheck;
            if (isEnableCheck) {
                if (!reminder.isSelected()) {
                    reminder.setSelected(true);
                }
            } else {
                selectNone();
            }
            notifyDataSetChanged();
            return true;
        });

        holder.linearLayout.setOnClickListener(view -> {
            if (!isEnableCheck) {
                final Context context = view.getContext();
                if (context == null)
                    return;

                final Intent intent = new Intent(context, ReminderView.class);
                intent.putExtra(ReminderModel.REMINDER_ID_INTENT, reminder.getId());
                context.startActivity(intent);
            } else {
                reminder.setSelected(!reminder.isSelected());
                notifyDataSetChanged();
            }
        });

        reminderSelectionCheck.setChecked(reminder.isSelected());
        reminderSelectionCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {

            reminder.setSelected(isChecked);

            if (listener != null) {
                listener.onSelectionChange(this);
            }

        });

        enabled.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isRefreshing)
                return;

            if (AppSettingsHelper.getInstance().isDisableAllReminders()) {
                buttonView.setChecked(false);
                ToastHelper.showShort(buttonView.getContext(), "All reminders are disabled in settings");
                return;
            }

            final Context context = buttonView.getContext();

            if (reminder.trySetEnabled(context, isChecked)) {
                reminder.saveAndSetAlert(context, true);
                buttonView.setChecked(isChecked);
                if (isChecked) {
                    time.setText(StringHelper.toTime(reminder.getTimeModel().getTime()));
                    date.setText(StringHelper.toWeekdayDate(context, reminder.getTimeModel().getTime()));
                }
            }
        });

        isRefreshing = true;

        time.setText(StringHelper.toTime(reminder.getTimeModel().getTime()));
        date.setText(StringHelper.toWeekdayDate(holder.linearLayout.getContext(), reminder.getTimeModel().getTime()));
        tv_reminder_repeat_short_summary.setText(reminder.getRepeatSettingShortString());

        if (StringHelper.isNullOrEmpty(reminder.getName())) {
            name.setVisibility(View.GONE);
        } else {
            name.setText(reminder.getName());
            name.setVisibility(View.VISIBLE);
        }

        if (reminder.getSnoozeModel().isSnoozed()) {
            lv_reminder_view_snooze.setVisibility(View.VISIBLE);
            next_snooze.setText(StringHelper.toTime(
                    reminder.getSnoozeModel().getSnoozedTime(
                            reminder.getTimeModel().getTime()
                    )));
        } else {
            lv_reminder_view_snooze.setVisibility(View.GONE);

            if (reminder.getLastMissedTime() != null) {
                lv_reminder_last_missed_time.setVisibility(View.VISIBLE);
                tv_reminder_last_missed_time.setText(StringHelper.toTimeWeekdayDate(holder.linearLayout.getContext(), reminder.getLastMissedTime()));
            } else {
                lv_reminder_last_missed_time.setVisibility(View.GONE);
            }
        }

        if (reminder.isExpired()) {
            time.setTextColor(holder.linearLayout.getResources().getColor(R.color.colorDanger));
            enabled.setVisibility(View.GONE);
        } else {
            enabled.setChecked(reminder.isEnabled() && !AppSettingsHelper.getInstance().isDisableAllReminders());
        }

        if (isEnableCheck) {
            enabled.setVisibility(View.GONE);
            reminderSelectionCheck.setVisibility(View.VISIBLE);
        } else {
            reminderSelectionCheck.setVisibility(View.GONE);
            enabled.setVisibility(View.VISIBLE);
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
        public final LinearLayoutCompat linearLayout;
        //public final EnumReminderTypes reminderType;

        public ReminderHolder(LinearLayoutCompat v) {
            super(v);
            linearLayout = v;
        }
    }
}
