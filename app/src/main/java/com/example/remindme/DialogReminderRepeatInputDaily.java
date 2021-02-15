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
import com.example.remindme.util.IReminderRepeatListener;
import com.example.remindme.util.ReminderRepeatModel;

public class DialogReminderRepeatInputDaily extends DialogFragment {

    private IReminderRepeatListener listener;
    private ReminderRepeatModel model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (IReminderRepeatListener) context;
            model = listener.get();
        }
        catch (ClassCastException e){
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderRepeatListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_daily, null);

        final CheckBox chk_daily_sun = view.findViewById(R.id.chk_daily_sun);
        final CheckBox chk_daily_mon = view.findViewById(R.id.chk_daily_mon);
        final CheckBox chk_daily_tue = view.findViewById(R.id.chk_daily_tue);
        final CheckBox chk_daily_wed = view.findViewById(R.id.chk_daily_wed);
        final CheckBox chk_daily_thu = view.findViewById(R.id.chk_daily_thu);
        final CheckBox chk_daily_fri = view.findViewById(R.id.chk_daily_fri);
        final CheckBox chk_daily_sat = view.findViewById(R.id.chk_daily_sat);

        chk_daily_sun.setChecked(model.dailyModel.isSun);
        chk_daily_mon.setChecked(model.dailyModel.isMon);
        chk_daily_tue.setChecked(model.dailyModel.isTue);
        chk_daily_wed.setChecked(model.dailyModel.isWed);
        chk_daily_thu.setChecked(model.dailyModel.isThu);
        chk_daily_fri.setChecked(model.dailyModel.isFri);
        chk_daily_sat.setChecked(model.dailyModel.isSat);

        builder.setView(view).setTitle("Select days to Repeat").setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.dailyModel.isSun = chk_daily_sun.isChecked();
                model.dailyModel.isMon = chk_daily_mon.isChecked();
                model.dailyModel.isTue = chk_daily_tue.isChecked();
                model.dailyModel.isWed = chk_daily_wed.isChecked();
                model.dailyModel.isThu = chk_daily_thu.isChecked();
                model.dailyModel.isFri = chk_daily_fri.isChecked();
                model.dailyModel.isSat = chk_daily_sat.isChecked();
                listener.set(model);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

}
