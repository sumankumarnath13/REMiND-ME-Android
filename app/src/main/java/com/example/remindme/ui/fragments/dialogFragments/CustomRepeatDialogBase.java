package com.example.remindme.ui.fragments.dialogFragments;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.remindme.viewModels.RepeatModel;

public class CustomRepeatDialogBase extends DialogFragment {

    private RepeatModel model;

    protected RepeatModel getModel() {
        return model;
    }

    private CustomRepeatDialogBase.ICustomRepeatDialogListener listener;

    protected CustomRepeatDialogBase.ICustomRepeatDialogListener getListener() {
        return listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CustomRepeatDialogBase.ICustomRepeatDialogListener) getParentFragmentManager().findFragmentByTag(RepeatDialog.TAG);
            model = listener.customRepeatDialogGetRepeatModel();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement IReminderRepeatListener");
        }
    }

    private boolean canceled;

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        getListener().customRepeatDialogSetRepeatModel(getModel());
        canceled = true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!canceled) {
            getListener().customRepeatDialogSetRepeatModel(getModel());
        }
    }


    public interface ICustomRepeatDialogListener {
        void customRepeatDialogSetRepeatModel(RepeatModel model);

        RepeatModel customRepeatDialogGetRepeatModel();
    }
}
