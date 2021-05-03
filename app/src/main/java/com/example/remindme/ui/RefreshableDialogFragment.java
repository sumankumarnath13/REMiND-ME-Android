package com.example.remindme.ui;
import androidx.fragment.app.DialogFragment;

public abstract class RefreshableDialogFragment extends DialogFragment {
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

}
