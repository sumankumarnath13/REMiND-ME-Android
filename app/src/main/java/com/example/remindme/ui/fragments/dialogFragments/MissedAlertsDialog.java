package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.R;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.common.DialogFragmentBase;
import com.example.remindme.viewModels.AlertModel;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MissedAlertsDialog extends DialogFragmentBase {

    public static final String TAG = "MissedAlertsDialog";

    public static final String MODEL_CHANGE = "mc";

    private class MissedAlertRecyclerAdapter extends RecyclerView.Adapter<MissedAlertRecyclerAdapter.ViewHolder> {

        private final List<Date> items;

        private MissedAlertRecyclerAdapter(final List<Date> values) {
            items = values;
        }

        @NonNull
        @Override
        public MissedAlertRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final Context context = parent.getContext();
            final LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            final View contactView = inflater.inflate(R.layout.item_recycler_fragment_missed_alert, parent, false);

            // Return a new holder instance
            return new MissedAlertRecyclerAdapter.ViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(@NonNull MissedAlertRecyclerAdapter.ViewHolder holder, int position) {
            final Date time = items.get(position);
            // Set item views based on your views and data model
            holder.tv_rank.setText(getRank(position + 1, getItemCount()));
            holder.tv_time.setText(StringHelper.toTimeAmPm(time));
            holder.tv_date.setText(StringHelper.toWeekdayDate(getContext(), time));
        }

        private String getRank(int position, int count) {
            if (count == position) {
                return "Last";
            } else {
                switch (position) {
                    default:
                        return String.format(Locale.getDefault(), "%dth", position);
                    case 1:
                        return String.format(Locale.getDefault(), "%dst", position);
                    case 2:
                        return String.format(Locale.getDefault(), "%dnd", position);
                    case 3:
                        return String.format(Locale.getDefault(), "%drd", position);
                }
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private final AppCompatTextView tv_rank;
            private final AppCompatTextView tv_time;
            private final AppCompatTextView tv_date;

            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                tv_rank = itemView.findViewById(R.id.tv_rank);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_date = itemView.findViewById(R.id.tv_date);
            }
        }
    }

    private boolean isModelChanged;

    public interface IMissedAlertsDialogListener {

        void onChangeMissedAlertsList(AlertModel model);

        AlertModel onGetReminderModel();

    }

    private IMissedAlertsDialogListener listener;

    private IMissedAlertsDialogListener getListener() {

        if (listener == null) {
            listener = super.getListener(IMissedAlertsDialogListener.class);
        }

        return listener;

    }

    private AlertModel model;

    private RecyclerView missed_alerts_recycler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Dialog listener is not set!");
            dismiss();
            return;
        }

        model = getListener().onGetReminderModel();

        if (model.getMissedTimes().size() == 0) {
            dismiss();
            ToastHelper.showShort(getContext(), "No missed alerts found!");
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(MODEL_CHANGE, isModelChanged);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            isModelChanged = savedInstanceState.getBoolean(MODEL_CHANGE);
        }

        final FragmentActivity activity = getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (activity == null)
            return builder.create();

        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_fragment_missed_alerts, null);

        missed_alerts_recycler = view.findViewById(R.id.missed_alerts_recycler);

        builder.setView(view)
                .setTitle("Missed alerts")
                .setPositiveButton("OK", (dialog, which) -> {

                }).setNeutralButton("CLEAR ALL", (dialog, which) -> {
            model.getMissedTimes().clear();
            model.save();
            isModelChanged = true;
            refresh();
        }).setNegativeButton(getString(R.string.acton_dialog_negative), (dialog, which) -> {
        });

        refresh();

        return builder.create();

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (isModelChanged) {
            getListener().onChangeMissedAlertsList(model);
        }
        super.onDismiss(dialog);
    }

    @Override
    protected void onUIRefresh() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        final MissedAlertRecyclerAdapter adapter = new MissedAlertRecyclerAdapter(model.getMissedTimes());
        missed_alerts_recycler.setAdapter(adapter);
        missed_alerts_recycler.setLayoutManager(layoutManager);
    }

}
