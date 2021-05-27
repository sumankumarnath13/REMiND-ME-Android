package com.example.remindme.ui.fragments.dialogFragments.common;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.factories.RepeatViewModelFactory;

public abstract class CustomRepeatDialogBase extends DialogFragmentBase {

    public interface ICustomRepeatDialogListener {

        void setCustomRepeatDialogModel(RepeatModel model);

        RepeatModel getCustomRepeatDialogModel();
    }

    private ICustomRepeatDialogListener listener;

    protected ICustomRepeatDialogListener getListener() {
        return listener;
    }

    public void setListener(ICustomRepeatDialogListener listener) {
        this.listener = listener;
    }

    private RepeatModel model;

    protected RepeatModel getModel() {
        return model;
    }

    boolean isCanceled;

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (getListener() != null) {
            getListener().setCustomRepeatDialogModel(null);
        }
        isCanceled = true;
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (!isCanceled) {
            if (getListener() != null) {
                getListener().setCustomRepeatDialogModel(null);
            }
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //listener = getListener(ICustomRepeatDialogListener.class);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Listener not set for dialog!");
            dismiss();
            return;
        }

        model = new ViewModelProvider(this,
                new RepeatViewModelFactory(getListener()
                        .getCustomRepeatDialogModel())).get(RepeatModel.class);
    }

    @Override
    protected void onUIRefresh() {

    }
}
