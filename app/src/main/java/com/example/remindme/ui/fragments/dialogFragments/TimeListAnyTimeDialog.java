package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.ui.fragments.dialogFragments.common.TimeListDialogBase;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogBase;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogBlack;
import com.example.remindme.ui.fragments.dialogFragments.common.TimePickerDialogLight;
import com.example.remindme.viewModels.TimeOfDayModel;
import com.example.remindme.viewModels.TimelyRepeatModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeListAnyTimeDialog extends TimeListDialogBase implements TimePickerDialogBase.ITimePickerListener {

    public static final String TAG = "TimeListAnyTimeDialog";

    @Override
    public void onSetListenerTime(int hourOfDay, int minute) {
        getModel().getTimelyRepeatModel().addTimeListTime(hourOfDay, minute);
        refresh();
    }

    @Override
    public Date onGetListenerTime() {
        return getModel().getParent().getTimeModel().getTime();
    }

    private class CustomTimeListAdapter extends RecyclerView.Adapter<CustomTimeListAdapter.ViewHolder> {

        private final List<TimeOfDayModel> times;

        // Pass in the contact array into the constructor
        public CustomTimeListAdapter(List<TimeOfDayModel> values) {
            times = values;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final Context context = parent.getContext();
            final LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            final View contactView = inflater.inflate(R.layout.item_recycler_fragment_time_list_random, parent, false);

            // Return a new holder instance
            return new ViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final TimeOfDayModel time = times.get(position);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, time.getHourOfDay());
            calendar.set(Calendar.MINUTE, time.getMinute());
            // Set item views based on your views and data model
            holder.tv_reminder_time.setText(StringHelper.toTimeAmPm(calendar.getTime()));
            holder.imgBtnRemove.setOnClickListener(v -> {
                getModel().getTimelyRepeatModel().removeTimeListTime(time);
                refresh();
            });
        }

        @Override
        public int getItemCount() {
            return times.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final AppCompatTextView tv_reminder_time;
            private final AppCompatImageButton imgBtnRemove;

            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                tv_reminder_time = itemView.findViewById(R.id.tv_reminder_time);
                imgBtnRemove = itemView.findViewById(R.id.imgBtnRemove);
            }
        }
    }

    private RecyclerView timeListRecycler;

    private TimePickerDialogBase timePickerDialog;

    private TimePickerDialogBase getTimePickerDialog() {
        if (timePickerDialog == null) {
            if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.BLACK) {
                timePickerDialog = new TimePickerDialogBlack();
            } else {
                timePickerDialog = new TimePickerDialogLight();
            }
        }
        return timePickerDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_fragment_input_time_list_random, null);

        timeListRecycler = view.findViewById(R.id.timeListRecycler);

        final AppCompatImageButton imgBtnAddCustomTime = view.findViewById(R.id.imgBtnAddCustomTime);
        imgBtnAddCustomTime.setOnClickListener(v -> getTimePickerDialog().show(getParentFragmentManager(), TimePickerDialogBase.TAG));

        builder.setView(view)
                .setTitle(getString(R.string.format_heading_time_list, "Select", "time"))
                .setPositiveButton(getString(R.string.acton_dialog_positive), (dialog, which) -> {
                    if (getModel().getTimelyRepeatModel().getTimeListTimes().size() > 0) {
                        getModel().getTimelyRepeatModel().setTimeListMode(TimelyRepeatModel.TimeListModes.ANYTIME);
                        getListener().setTimeListDialogModel(getModel());
                    }
                }).setNegativeButton(getString(R.string.acton_dialog_negative), (dialog, which) -> {
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        timeListRecycler.setLayoutManager(layoutManager);
        refresh();
        return builder.create();
    }

    @Override
    protected void onUIRefresh() {
        super.onUIRefresh();
        final CustomTimeListAdapter timeListAdapter = new CustomTimeListAdapter(getModel().getTimelyRepeatModel().getTimeListTimes());
        timeListRecycler.setAdapter(timeListAdapter);
    }
}
