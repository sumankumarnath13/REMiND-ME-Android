package com.example.remindme.ui.fragments.dialogFragments;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.TimeModel;
import com.example.remindme.viewModels.factories.TimeViewModelFactory;

public class TimeListDialogBase extends RefreshableDialogFragmentBase {

    private ITimeListListener listener;

    protected ITimeListListener getListener() {
        return listener;
    }

    private TimeModel model;

    protected TimeModel getModel() {
        return model;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ITimeListListener) context;
            model = new ViewModelProvider(this, new TimeViewModelFactory(listener.getTimeListDialogModel().getParent())).get(TimeModel.class);
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement ITimeListListener");
        }
    }

    private boolean canceled;

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        getListener().setTimeListDialogModel(getModel());
        canceled = true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!canceled) {
            getListener().setTimeListDialogModel(getModel());
        }
    }

    @Override
    protected void onUIRefresh() {

    }

    public interface ITimeListListener {

        TimeModel getTimeListDialogModel();

        void setTimeListDialogModel(TimeModel model);

    }
}
