package com.example.remindme.ui.fragments.dialogFragments.common;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.RepeatModel;
import com.example.remindme.viewModels.factories.RepeatViewModelFactory;

public abstract class TimeListDialogBase extends DialogFragmentBase {

    boolean isCanceled;

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (getListener() != null) {
            getListener().setTimeListDialogModel(null);
        }
        isCanceled = true;
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (!isCanceled) {
            if (getListener() != null) {
                getListener().setTimeListDialogModel(null);
            }
        }
        super.onDismiss(dialog);
    }

    private RepeatModel model;

    public interface ITimeListListener {

        RepeatModel getTimeListDialogModel();

        void setTimeListDialogModel(RepeatModel model);

    }

    private ITimeListListener listener;

    protected ITimeListListener getListener() {
        if (listener == null) {
            listener = super.getListener(ITimeListListener.class);
        }
        return listener;
    }

    protected RepeatModel getModel() {
        return model;
    }

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Dialog listener is not set!");
            dismiss();
            return;
        }

        model = new ViewModelProvider(this,
                new RepeatViewModelFactory(getListener()
                        .getTimeListDialogModel())).get(RepeatModel.class);
    }

    @Override
    protected void onUIRefresh() {

    }


}
