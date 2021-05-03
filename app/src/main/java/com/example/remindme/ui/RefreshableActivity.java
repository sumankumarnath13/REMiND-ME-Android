package com.example.remindme.ui;

import androidx.appcompat.app.AppCompatActivity;

public abstract class RefreshableActivity extends AppCompatActivity {

    private boolean isUserInteracted;

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        isUserInteracted = true;
    }

    public boolean isUserInteracted() {
        return isUserInteracted;
    }

    public void setUserInteracted(boolean value) {
        isUserInteracted = value;
    }

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
