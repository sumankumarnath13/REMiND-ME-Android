package com.example.remindme.ui.fragments.dialogFragments;

import androidx.fragment.app.DialogFragment;

public abstract class RefreshableDialogFragmentBase extends DialogFragment {

    private boolean isRefreshing;

    protected boolean isRefreshing() {
        return isRefreshing;
    }

    protected void refresh() {
        isRefreshing = true;
        onUIRefresh();
        isRefreshing = false;
    }

    protected abstract void onUIRefresh();

//    @Override
//    public void onStart() {
//        super.onStart();
//        final Button positive = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
//        if (positive != null) {
//            positive.setTextColor(getResources().getColor(R.color.text_success));
//        }
//
//        final Button negative = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
//        if (negative != null) {
//            negative.setTextColor(getResources().getColor(R.color.text_danger));
//        }
//    }
}
