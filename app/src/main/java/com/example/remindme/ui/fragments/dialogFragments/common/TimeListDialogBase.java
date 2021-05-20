package com.example.remindme.ui.fragments.dialogFragments.common;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.TimeModel;
import com.example.remindme.viewModels.factories.TimeViewModelFactory;

public abstract class TimeListDialogBase extends DialogFragmentBase {

    private TimeModel model;

    protected TimeModel getModel() {
        return model;
    }

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((ITimeListListener) getListener() == null) {
            ToastHelper.showError(getContext(), "Listener incompatible!");
            dismiss();
            return;
        }

        model = new ViewModelProvider(this, new TimeViewModelFactory(((ITimeListListener) getListener()).getTimeListDialogModel().getParent())).get(TimeModel.class);
    }

    @Override
    protected void onUIRefresh() {

    }

    public interface ITimeListListener {

        TimeModel getTimeListDialogModel();

        void setTimeListDialogModel(TimeModel model);

    }
}
