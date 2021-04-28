package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.remindme.R;
import com.example.remindme.viewModels.ReminderRepeatModel;

public class TimeListRepeatInputDialog extends DialogFragment {

    private ReminderRepeatModel model;
    private boolean isCancel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            DialogReminderRepeatInput.IRepeatInputDialogListener listener = (DialogReminderRepeatInput.IRepeatInputDialogListener) context;
            model = listener.getRepeatModel();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderRepeatListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null) return builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.time_list_repeat_input_dialog, null);


        builder.setView(view).setTitle("Add times to Repeat").setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.timeList.clear();
//                if (chk_weekly_1.isChecked()) model.customWeeks.add(0);
//                if (chk_weekly_2.isChecked()) model.customWeeks.add(1);
//                if (chk_weekly_3.isChecked()) model.customWeeks.add(2);
//                if (chk_weekly_4.isChecked()) model.customWeeks.add(3);
//                if (chk_weekly_5.isChecked()) model.customWeeks.add(4);

                model.setRepeatOption(ReminderRepeatModel.ReminderRepeatOptions.TIME_LIST);
                commitToParent();
            }
        }).setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                commitToParent();
            }
        });

        return builder.create();
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
            commitToParent();
        }
    }

    private void commitToParent() {
        final Fragment fragment = getParentFragmentManager().findFragmentByTag("repeatInput");
        if (fragment != null) {
            final IRepeatInputChildDialogListener hostDialog = (IRepeatInputChildDialogListener) fragment;
            hostDialog.setChanges(model);
        }
    }

}
