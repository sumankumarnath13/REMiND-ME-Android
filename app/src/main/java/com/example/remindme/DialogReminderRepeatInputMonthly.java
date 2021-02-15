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

public class DialogReminderRepeatInputMonthly extends DialogFragment {

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
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat_monthly, null);

        final CheckBox chk_monthly_jan = view.findViewById(R.id.chk_monthly_jan);
        final CheckBox chk_monthly_feb = view.findViewById(R.id.chk_monthly_feb);
        final CheckBox chk_monthly_mar = view.findViewById(R.id.chk_monthly_mar);
        final CheckBox chk_monthly_apr = view.findViewById(R.id.chk_monthly_apr);
        final CheckBox chk_monthly_may = view.findViewById(R.id.chk_monthly_may);
        final CheckBox chk_monthly_jun = view.findViewById(R.id.chk_monthly_jun);
        final CheckBox chk_monthly_jul = view.findViewById(R.id.chk_monthly_jul);
        final CheckBox chk_monthly_aug = view.findViewById(R.id.chk_monthly_aug);
        final CheckBox chk_monthly_sep = view.findViewById(R.id.chk_monthly_sep);
        final CheckBox chk_monthly_oct = view.findViewById(R.id.chk_monthly_oct);
        final CheckBox chk_monthly_nov = view.findViewById(R.id.chk_monthly_nov);
        final CheckBox chk_monthly_dec = view.findViewById(R.id.chk_monthly_dec);

        chk_monthly_jan.setChecked(model.monthlyModel.isJan);
        chk_monthly_feb.setChecked(model.monthlyModel.isFeb);
        chk_monthly_mar.setChecked(model.monthlyModel.isMar);
        chk_monthly_apr.setChecked(model.monthlyModel.isApr);
        chk_monthly_may.setChecked(model.monthlyModel.isMay);
        chk_monthly_jun.setChecked(model.monthlyModel.isJun);
        chk_monthly_jul.setChecked(model.monthlyModel.isJul);
        chk_monthly_aug.setChecked(model.monthlyModel.isAug);
        chk_monthly_sep.setChecked(model.monthlyModel.isSep);
        chk_monthly_oct.setChecked(model.monthlyModel.isOct);
        chk_monthly_nov.setChecked(model.monthlyModel.isNov);
        chk_monthly_dec.setChecked(model.monthlyModel.isDec);

        builder.setView(view).setTitle("Select months to Repeat").setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.monthlyModel.isJan = chk_monthly_jan.isChecked();
                model.monthlyModel.isFeb = chk_monthly_feb.isChecked();
                model.monthlyModel.isMar = chk_monthly_mar.isChecked();
                model.monthlyModel.isApr = chk_monthly_apr.isChecked();
                model.monthlyModel.isMay = chk_monthly_may.isChecked();
                model.monthlyModel.isJun = chk_monthly_jun.isChecked();
                model.monthlyModel.isJul = chk_monthly_jul.isChecked();
                model.monthlyModel.isAug = chk_monthly_aug.isChecked();
                model.monthlyModel.isSep = chk_monthly_sep.isChecked();
                model.monthlyModel.isOct = chk_monthly_oct.isChecked();
                model.monthlyModel.isNov = chk_monthly_nov.isChecked();
                model.monthlyModel.isDec = chk_monthly_dec.isChecked();

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
