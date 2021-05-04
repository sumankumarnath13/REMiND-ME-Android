package com.example.remindme.ui.activities;

import android.os.Bundle;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.ui.RefreshableActivity;

public class ActivityBase extends RefreshableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.BlackTheme);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onUIRefresh() {

    }
}
