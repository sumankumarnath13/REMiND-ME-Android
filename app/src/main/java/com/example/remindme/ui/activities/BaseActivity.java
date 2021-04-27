package com.example.remindme.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.BlackTheme);
        }
        super.onCreate(savedInstanceState);
    }

}
