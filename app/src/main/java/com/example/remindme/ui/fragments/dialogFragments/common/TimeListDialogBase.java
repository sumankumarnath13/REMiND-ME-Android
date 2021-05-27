package com.example.remindme.ui.fragments.dialogFragments.common;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.TimeModel;
import com.example.remindme.viewModels.factories.TimeViewModelFactory;

public abstract class TimeListDialogBase extends DialogFragmentBase {

    private TimeModel model;

    public interface ITimeListListener {

        TimeModel getTimeListDialogModel();

        void setTimeListDialogModel(TimeModel model);

    }

    private ITimeListListener listener;

    protected ITimeListListener getListener() {
        return listener;
    }

    public void setListener(ITimeListListener listener) {
        this.listener = listener;
    }

    protected TimeModel getModel() {
        return model;
    }

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //listener = getListener(ITimeListListener.class);

        if (listener == null) {
            ToastHelper.showError(getContext(), "Dialog listener is not set!");
            dismiss();
            return;
        }

        model = new ViewModelProvider(this, new TimeViewModelFactory(getListener().getTimeListDialogModel().getParent())).get(TimeModel.class);
    }

    @Override
    protected void onUIRefresh() {

    }


}
