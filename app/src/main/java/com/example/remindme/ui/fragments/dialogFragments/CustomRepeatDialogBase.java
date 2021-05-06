package com.example.remindme.ui.fragments.dialogFragments;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.factories.RepeatViewModelFactory;

public class CustomRepeatDialogBase extends RefreshableDialogFragmentBase {

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
            model = new ViewModelProvider(this, new RepeatViewModelFactory(listener.customRepeatDialogGetRepeatModel())).get(RepeatModel.class);
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

    @Override
    protected void onUIRefresh() {

    }

    public interface ICustomRepeatDialogListener {
        void customRepeatDialogSetRepeatModel(RepeatModel model);

        RepeatModel customRepeatDialogGetRepeatModel();
    }
}