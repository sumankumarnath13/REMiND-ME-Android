package com.example.remindme.ui.fragments.dialogFragments;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.remindme.viewModels.TimeViewModel;

public class TimeListDialogBase extends DialogFragment {

    private ITimeListListener listener;

    protected ITimeListListener getListener() {
        return listener;
    }


    private TimeViewModel model;

    protected TimeViewModel getModel() {
        return model;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ITimeListListener) context;
            model = listener.timeListDialogGetTimeViewModel();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " : " + context.toString() + " must implement ITimeListListener");
        }
    }

    private boolean canceled;

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        getListener().timeListDialogSetTimeViewModel(getModel());
        canceled = true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!canceled) {
            getListener().timeListDialogSetTimeViewModel(getModel());
        }
    }


    public interface ITimeListListener {
        TimeViewModel timeListDialogGetTimeViewModel();

        void timeListDialogSetTimeViewModel(TimeViewModel model);
    }
}
