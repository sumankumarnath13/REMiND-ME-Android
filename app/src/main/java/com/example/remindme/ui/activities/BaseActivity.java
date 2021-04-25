package com.example.remindme.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switch (AppSettingsHelper.getInstance().getTheme()) {
            default:
                setTheme(R.style.BlackTheme);
                break;
            case LIGHT:
                setTheme(R.style.LightTheme);
                break;
        }
        super.onCreate(savedInstanceState);
    }

}
