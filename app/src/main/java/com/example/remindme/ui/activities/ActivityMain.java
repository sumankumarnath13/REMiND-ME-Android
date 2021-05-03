package com.example.remindme.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.ui.main.AdapterSectionsPager;
import com.google.android.material.tabs.TabLayout;

public class ActivityMain extends AppCompatActivity {

    private boolean isThemeChangeReceiverRegistered = false;
    private final BroadcastReceiver themeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActivitySettings.THEME_CHANGE_INTENT_ACTION.equals(intent.getAction())) {
                recreate();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT) {
            setTheme(R.style.LightTheme_NoActionBar);
        } else {
            setTheme(R.style.BlackTheme_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final IntentFilter intentFilter = new IntentFilter(ActivitySettings.THEME_CHANGE_INTENT_ACTION);
        registerReceiver(themeChangeReceiver, intentFilter);
        isThemeChangeReceiverRegistered = true;

        final AdapterSectionsPager adapterSectionsPager = new AdapterSectionsPager(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapterSectionsPager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActivityHelper.setTitle(this, "");

        final Button btnNewReminder = findViewById(R.id.btn_main_new_reminder);
        btnNewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewReminderActivity = new Intent(ActivityMain.this, ActivityReminderInput.class);
                startActivity(addNewReminderActivity);
            }
        });
    }

    @Override
    protected void onDestroy() {

        if (isThemeChangeReceiverRegistered) {
            unregisterReceiver(themeChangeReceiver);
            isThemeChangeReceiverRegistered = false;
        }

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
            case R.id.action_settings:
                Intent settings_intent = new Intent(this, ActivitySettings.class);
                startActivity(settings_intent);
                break;

            case R.id.action_about_app:
                Intent aboutAppIntent = new Intent(this, ActivityAboutApp.class);
                startActivity(aboutAppIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}