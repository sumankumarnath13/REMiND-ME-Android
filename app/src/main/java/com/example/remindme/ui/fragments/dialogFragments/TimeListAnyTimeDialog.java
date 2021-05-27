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
import com.example.remindme.viewModels.TimeModel;

import java.util.Date;
import java.util.List;

public class TimeListAnyTimeDialog extends TimeListDialogBase {

    public static final String TAG = "CustomTimeListDialog";

    public class CustomTimeListAdapter extends RecyclerView.Adapter<CustomTimeListAdapter.ViewHolder> {

        private final List<Date> times;

        // Pass in the contact array into the constructor
        public CustomTimeListAdapter(List<Date> values) {
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
            final Date time = times.get(position);

            // Set item views based on your views and data model
            holder.tv_reminder_time.setText(StringHelper.toTimeAmPm(time));
            holder.imgBtnRemove.setOnClickListener(v -> {
                getModel().removeTimeListTime(time);
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
            timePickerDialog.setListener(new TimePickerDialogBase.ITimePickerListener() {
                @Override
                public void onSetListenerTime(Date dateTime) {
                    getModel().addTimeListTime(dateTime);
                    refresh();
                }

                @Override
                public Date onGetListenerTime() {
                    return getModel().getTime();
                }
            });
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
                    if (getModel().getTimeListTimes().size() > 0) {
                        getModel().setTimeListMode(TimeModel.TimeListModes.ANYTIME);
                        getListener().setTimeListDialogModel(getModel());
                    } else {
                        getModel().setTimeListMode(TimeModel.TimeListModes.NONE);
                    }
                }).setNegativeButton(getString(R.string.acton_dialog_negative), (dialog, which) -> {

        });

        refresh();

        return builder.create();
    }

    @Override
    protected void onUIRefresh() {
        super.onUIRefresh();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        final CustomTimeListAdapter timeListAdapter = new CustomTimeListAdapter(getModel().getTimeListTimes());
        timeListRecycler.setAdapter(timeListAdapter);
        timeListRecycler.setLayoutManager(layoutManager);
    }

}
