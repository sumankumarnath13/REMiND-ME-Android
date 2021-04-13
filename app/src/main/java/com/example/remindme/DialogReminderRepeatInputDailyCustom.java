package com.example.remindme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.remindme.ui.main.IReminderRepeatListener;
import com.example.remindme.ui.main.IRepeatInputDialog;
import com.example.remindme.viewModels.ReminderRepeatModel;

import java.util.Calendar;

public class DialogReminderRepeatInputDailyCustom extends DialogFragment {

    private IReminderRepeatListener listener;
    private ReminderRepeatModel model;
    private boolean isCancel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (IReminderRepeatListener) context;
            model = listener.getRepeatModel();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderRepeatListener");
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        isCancel = true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (isCancel) {
            listener.discardChanges();
        }
//        final FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//        transaction.remove(this);
//        transaction.commit();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_daily_custom, null);

        final CheckBox chk_daily_sun = view.findViewById(R.id.chk_daily_sun);
        final CheckBox chk_daily_mon = view.findViewById(R.id.chk_daily_mon);
        final CheckBox chk_daily_tue = view.findViewById(R.id.chk_daily_tue);
        final CheckBox chk_daily_wed = view.findViewById(R.id.chk_daily_wed);
        final CheckBox chk_daily_thu = view.findViewById(R.id.chk_daily_thu);
        final CheckBox chk_daily_fri = view.findViewById(R.id.chk_daily_fri);
        final CheckBox chk_daily_sat = view.findViewById(R.id.chk_daily_sat);

        for (int i = 0; i < model.customDays.size(); i++) {
            int value = model.customDays.get(i);
            switch (value) {
                default:
                case Calendar.SUNDAY:
                    chk_daily_sun.setChecked(true);
                    break;
                case Calendar.MONDAY:
                    chk_daily_mon.setChecked(true);
                    break;
                case Calendar.TUESDAY:
                    chk_daily_tue.setChecked(true);
                    break;
                case Calendar.WEDNESDAY:
                    chk_daily_wed.setChecked(true);
                    break;
                case Calendar.THURSDAY:
                    chk_daily_thu.setChecked(true);
                    break;
                case Calendar.FRIDAY:
                    chk_daily_fri.setChecked(true);
                    break;
                case Calendar.SATURDAY:
                    chk_daily_sat.setChecked(true);
                    break;
            }
        }

        builder.setView(view)
                .setTitle("Select hours to Repeat")
                .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        model.customDays.clear();
                        if (chk_daily_sun.isChecked()) model.customDays.add(Calendar.SUNDAY);
                        if (chk_daily_mon.isChecked()) model.customDays.add(Calendar.MONDAY);
                        if (chk_daily_tue.isChecked()) model.customDays.add(Calendar.TUESDAY);
                        if (chk_daily_wed.isChecked()) model.customDays.add(Calendar.WEDNESDAY);
                        if (chk_daily_thu.isChecked()) model.customDays.add(Calendar.THURSDAY);
                        if (chk_daily_fri.isChecked()) model.customDays.add(Calendar.FRIDAY);
                        if (chk_daily_sat.isChecked()) model.customDays.add(Calendar.SATURDAY);
                        //listener.setChanges(model);

                        final Fragment fragment = getParentFragmentManager().findFragmentByTag("repeatInput");
                        final IRepeatInputDialog hostDialog = (IRepeatInputDialog) fragment;
                        hostDialog.setChanges(model);


                    }
                }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //listener.set(null, true);
            }
        });

        return builder.create();
    }

}
