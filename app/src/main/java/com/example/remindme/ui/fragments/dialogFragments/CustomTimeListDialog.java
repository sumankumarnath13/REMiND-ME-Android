package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.viewModels.TimeModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomTimeListDialog extends TimeListDialogBase {

    public static final String TAG = "CustomTimeListDialog";

    public class CustomTimeListAdapter extends RecyclerView.Adapter<CustomTimeListAdapter.ViewHolder> {

        private List<Date> times;

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
            final View contactView = inflater.inflate(R.layout.custom_time_list_recycler_item, parent, false);

            // Return a new holder instance
            final ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Date time = times.get(position);

            // Set item views based on your views and data model
            holder.tv_reminder_time.setText(StringHelper.toTime(time));
            holder.imgBtnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getModel().removeTimeListTime(time);
                    refresh();
                }
            });
        }

        @Override
        public int getItemCount() {
            return times.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_reminder_time;
            private ImageButton imgBtnRemove;

            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                tv_reminder_time = (TextView) itemView.findViewById(R.id.tv_reminder_time);
                imgBtnRemove = (ImageButton) itemView.findViewById(R.id.imgBtnRemove);

            }
        }
    }

    private ImageButton imgBtnAddCustomTime;
    private RecyclerView timeListRecycler;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.custom_time_list_dialog, null);

        timeListRecycler = view.findViewById(R.id.timeListRecycler);

        imgBtnAddCustomTime = view.findViewById(R.id.imgBtnAddCustomTime);
        imgBtnAddCustomTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar alertTime = Calendar.getInstance();
                alertTime.setTime(getModel().getTime());
                final int mHour, mMinute;
                mHour = alertTime.get(Calendar.HOUR_OF_DAY);
                mMinute = alertTime.get(Calendar.MINUTE);

                final TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                        AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT ? R.style.TimePickerDialogLight : R.style.TimePickerDialogBlack,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                alertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                alertTime.set(Calendar.MINUTE, minute);
                                alertTime.set(Calendar.SECOND, 0);
                                alertTime.set(Calendar.MILLISECOND, 0);

                                getModel().addTimeListTime(alertTime.getTime());
                                refresh();

                            }
                        }, mHour, mMinute, AppSettingsHelper.getInstance().isUse24hourTime());
                timePickerDialog.show();
            }
        });


        builder.setView(view)
                .setTitle("Select hours to Repeat")
                .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getModel().getTimeListTimes().size() > 0) {
                            getModel().setTimeListMode(TimeModel.TimeListModes.CUSTOM);
                            getListener().setTimeListDialogModel(getModel());
                        } else {
                            getModel().setTimeListMode(TimeModel.TimeListModes.NONE);
                        }
                    }
                }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
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
