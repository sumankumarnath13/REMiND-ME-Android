package com.example.remindme.ui.main;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
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

    private static final long HIDE_ANIMATION_DURATION = 117;
    private static final long SHOW_ANIMATION_DURATION = 117;
    private final List<ReminderModel> _data;
    boolean isRefreshing;
    private boolean isAnimate;
    private int animationEndCounter;
    private boolean isEnableSelection;

    @Override
    public boolean isSelectable() {
        return isEnableSelection;
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

        notifyDataSetChanged();

        notifySelectionChange();
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

        notifyDataSetChanged();

        notifySelectionChange();
    }

    @Override
    public boolean isNoneSelected() {
        return countSelections() == 0;
    }

    @Override
    public void notifySelectionChange() {
        if (listener != null) {
            listener.onSelectionChange(this);
        }
    }

    @Override
    public int size() {
        return _data.size();
    }

    @Override
    public void removeAllSelected() {
        for (int i = _data.size() - 1; i >= 0; i--) {
            if (_data.get(i).isSelected()) {
                _data.remove(i);
            }
        }
        dismissSelectable();
    }

    private void initAnimation() {
        animationEndCounter = 0;
        isAnimate = true;
    }

    private void itemAnimationCompleted() {
        animationEndCounter++;
        if (animationEndCounter == _data.size()) {
            isAnimate = false;
            animationEndCounter = 0;
        }
    }

    @Override
    public void dismissSelectable() {
        for (int i = _data.size() - 1; i >= 0; i--) {
            _data.get(i).setSelected(false);
        }
        initAnimation();
        isEnableSelection = false;

        notifyDataSetChanged();

        notifySelectionChange();
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
        isEnableSelection = false;
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
        final AppCompatTextView time = holder.linearLayout.findViewById(R.id.tv_reminder_time);
        final AppCompatTextView amPm = holder.linearLayout.findViewById(R.id.tv_reminder_AmPm);
        final AppCompatTextView date = holder.linearLayout.findViewById(R.id.tv_reminder_date);

        final AppCompatTextView name = holder.linearLayout.findViewById(R.id.tv_reminder_name);
        final AppCompatTextView tv_reminder_repeat_short_summary = holder.linearLayout.findViewById(R.id.tv_reminder_repeat_short_summary);
        final SwitchCompat switchEnabled = holder.linearLayout.findViewById(R.id.sw_reminder_enabled);
        final LinearLayoutCompat lv_reminder_view_snooze = holder.linearLayout.findViewById(R.id.lv_reminder_view_snooze);
        final AppCompatTextView next_snooze = holder.linearLayout.findViewById(R.id.tv_reminder_next_snooze);

        final LinearLayoutCompat lv_reminder_last_missed_time = holder.linearLayout.findViewById(R.id.lv_reminder_last_missed_time);
        final AppCompatTextView tv_reminder_last_missed_time = holder.linearLayout.findViewById(R.id.tv_reminder_last_missed_time);

        isRefreshing = true;

        time.setText(StringHelper.toTime(reminder.getTimeModel().getTime()));
        amPm.setText(StringHelper.toAmPm(reminder.getTimeModel().getTime()));
        date.setText(StringHelper.toWeekdayDate(holder.linearLayout.getContext(), reminder.getTimeModel().getTime()));
        tv_reminder_repeat_short_summary.setText(reminder.getRepeatSettingShortString());

        if (isAnimate) {
            if (isEnableSelection) {
                switchEnabled.setAlpha(1);
                switchEnabled.clearAnimation();
                switchEnabled.animate().alpha(0).setDuration(HIDE_ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        switchEnabled.setVisibility(View.GONE);
                        switchEnabled.clearAnimation();

                        reminderSelectionCheck.setChecked(reminder.isSelected());
                        reminderSelectionCheck.setVisibility(View.VISIBLE);
                        reminderSelectionCheck.setAlpha(0);
                        reminderSelectionCheck.clearAnimation();
                        reminderSelectionCheck.animate().alpha(1).setDuration(SHOW_ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                reminderSelectionCheck.clearAnimation();
                                itemAnimationCompleted();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            } else {
                reminderSelectionCheck.setAlpha(1);
                reminderSelectionCheck.clearAnimation();
                reminderSelectionCheck.animate().alpha(0).setDuration(HIDE_ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reminderSelectionCheck.setVisibility(View.GONE);
                        reminderSelectionCheck.setChecked(reminder.isSelected());
                        reminderSelectionCheck.clearAnimation();

                        switchEnabled.setVisibility(View.VISIBLE);
                        switchEnabled.setAlpha(0);
                        switchEnabled.clearAnimation();
                        switchEnabled.animate().alpha(1).setDuration(SHOW_ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                switchEnabled.clearAnimation();
                                itemAnimationCompleted();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }
        } else {
            reminderSelectionCheck.setChecked(reminder.isSelected());

            if (isEnableSelection) {
                switchEnabled.setVisibility(View.GONE);
                reminderSelectionCheck.setVisibility(View.VISIBLE);
            } else {
                reminderSelectionCheck.setVisibility(View.GONE);
                switchEnabled.setVisibility(View.VISIBLE);
            }
        }

        holder.linearLayout.setLongClickable(true);

        holder.linearLayout.setOnLongClickListener(v -> {
            isEnableSelection = !isEnableSelection;
            initAnimation();
            if (isEnableSelection) {
                if (!reminder.isSelected()) {
                    reminder.setSelected(true);
                }
                notifyDataSetChanged();

                notifySelectionChange();
            } else {
                selectNone();
            }
            return true;
        });

        holder.linearLayout.setOnClickListener(view -> {
            if (!isEnableSelection) {
                final Context context = view.getContext();
                if (context == null)
                    return;

                final Intent intent = new Intent(context, ReminderView.class);
                intent.putExtra(ReminderModel.REMINDER_ID_INTENT, reminder.getId());
                context.startActivity(intent);
            } else {
                reminder.setSelected(!reminder.isSelected());
                reminderSelectionCheck.setChecked(reminder.isSelected());
                if (listener != null) {
                    listener.onSelectionChange(this);
                }
            }
        });

        reminderSelectionCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isRefreshing)
                return;

            reminder.setSelected(isChecked);
            if (listener != null) {
                listener.onSelectionChange(this);
            }
        });

        switchEnabled.setChecked(reminder.isEnabled() && !AppSettingsHelper.getInstance().isDisableAllReminders());
        switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
                time.setText(StringHelper.toTime(reminder.getTimeModel().getTime()));
                amPm.setText(StringHelper.toAmPm(reminder.getTimeModel().getTime()));
                date.setText(StringHelper.toWeekdayDate(context, reminder.getTimeModel().getTime()));
            } else {
                buttonView.setChecked(false);
            }
        });

        if (StringHelper.isNullOrEmpty(reminder.getName())) {
            name.setVisibility(View.GONE);
        } else {
            name.setText(reminder.getName());
            name.setVisibility(View.VISIBLE);
        }

        if (reminder.getSnoozeModel().isSnoozed()) {
            lv_reminder_view_snooze.setVisibility(View.VISIBLE);
            next_snooze.setText(StringHelper.toTimeAmPm(
                    reminder.getSnoozeModel().getSnoozedTime(
                            reminder.getTimeModel().getTime()
                    )));
        } else {
            lv_reminder_view_snooze.setVisibility(View.GONE);
        }

        if (reminder.getLastMissedTime() != null) {
            lv_reminder_last_missed_time.setVisibility(View.VISIBLE);
            tv_reminder_last_missed_time.setText(StringHelper.toTimeWeekdayDate(holder.linearLayout.getContext(), reminder.getLastMissedTime()));
        } else {
            lv_reminder_last_missed_time.setVisibility(View.GONE);
        }

        if (reminder.isExpired()) {
            time.setTextColor(holder.linearLayout.getResources().getColor(R.color.colorDanger));
            amPm.setTextColor(holder.linearLayout.getResources().getColor(R.color.colorDanger));
        }

        isRefreshing = false;

    }

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
