package com.example.remindme.controllers;

import androidx.fragment.app.DialogFragment;

public abstract class AbstractDialogFragmentController extends DialogFragment {
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
