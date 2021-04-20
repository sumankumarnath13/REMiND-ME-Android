package com.example.remindme.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;

public class ActivityAboutApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ActivityHelper.setTitle(this, getResources().getString(R.string.activityAboutAppTitle));
    }
}