package com.example.remindme;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import com.example.remindme.util.IReminderRepeatListener;
import com.example.remindme.util.ReminderRepeatModel;
import com.example.remindme.util.ReminderRepeatOptions;

public class DialogReminderRepeatInput extends DialogFragment {

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
        final View view = inflater.inflate(R.layout.dialog_reminder_input_repeat, null);
        builder.setView(view);
//                .setTitle("Repeat")
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                //listener.setRepeatType();
//                //Do nothing
//
//            }
//        });
//        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });

        final AppCompatButton btn_repeat_none = view.findViewById(R.id.btn_reminder_repeat_none);
        btn_repeat_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatOptions.None;
                listener.set(model);

            }
        });
        final AppCompatButton btn_repeat_hourly = view.findViewById(R.id.btn_reminder_repeat_hourly);
        btn_repeat_hourly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatOptions.Hourly;
                listener.set(model);
            }
        });
        final AppCompatButton btn_repeat_daily = view.findViewById(R.id.btn_reminder_repeat_daily);
        btn_repeat_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatOptions.Daily;
                listener.set(model);
            }
        });
        final AppCompatButton btn_repeat_monthly = view.findViewById(R.id.btn_reminder_repeat_monthly);
        btn_repeat_monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatOptions.Monthly;
                listener.set(model);
            }
        });
        final AppCompatButton btn_repeat_yearly = view.findViewById(R.id.btn_reminder_repeat_yearly);
        btn_repeat_yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                model.repeatOption = ReminderRepeatOptions.Yearly;
                listener.set(model);
            }
        });

        return builder.create();
    }
}
