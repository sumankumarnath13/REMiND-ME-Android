package com.example.remindme.ui.fragments.dialogFragments.common;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.remindme.helpers.ToastHelper;

import java.util.Stack;


public abstract class DialogFragmentBase extends DialogFragment {

    private boolean isStackChanged;

    private static final Stack<String> dialogStack = new Stack<>();

    private boolean isRefreshing;

    private Object listener;

    protected <T> T getListener(Class<T> type) {
        try {
            return type.cast(listener);
        } catch (Exception ex) {
            return null;
        }
    }

    protected boolean isRefreshing() {
        return isRefreshing;
    }

    protected void refresh() {
        isRefreshing = true;
        onUIRefresh();
        isRefreshing = false;
    }

    protected abstract void onUIRefresh();

    @Override
    public final void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (dialogStack.size() == 0) {
            // Root level, Activity
            listener = context;
        } else {
            // Dialog from dialog or Activity
            String listenerTag = dialogStack.lastElement();
            listener = getParentFragmentManager().findFragmentByTag(listenerTag);
        }

        if (dialogStack.contains(getTag())) {
            ToastHelper.showError(context, "Duplicate tag in dialog chain");
            dismiss();
            return;
        }

        if (listener == null) {
            dismiss();
            return;
        }

        dialogStack.push(getTag());
        isStackChanged = true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!dialogStack.isEmpty() && isStackChanged) {
            dialogStack.pop();
        }
    }

    protected int resolveRefAttributeResourceId(int refAttributeId) {

        if (getActivity() != null) {
            final Resources.Theme theme = getActivity().getTheme();
            final TypedValue typedValue = new TypedValue();
            if (theme.resolveAttribute(refAttributeId, typedValue, true)) {
                return typedValue.resourceId;
            }
        }

        return -1;
    }

    @Override
    public void onStart() {
        super.onStart();
//        final AlertDialog alertDialog = (AlertDialog) getDialog();
//
//        if (alertDialog != null) {
//
//            final Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//            if (positive != null) {
//                positive.setTextColor(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeSoothingText)));
//                //positive.setTextSize(getResources().getDimensionPixelSize(R.dimen.font_size_medium));
//            }
//
//            final Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//            if (negative != null) {
//                negative.setTextColor(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDimText)));
//                //negative.setTextSize(getResources().getDimension(R.dimen.font_size_medium));
//            }
//
//        }
    }
}
