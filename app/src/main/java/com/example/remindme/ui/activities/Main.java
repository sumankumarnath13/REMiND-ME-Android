package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.remindme.R;
import com.example.remindme.ui.main.AdapterSectionsPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class Main extends ActivityBase {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        setActivitySubTitle("");

//        final AppCompatButton btnNewReminder = findViewById(R.id.btn_main_new_reminder);
//        btnNewReminder.setOnClickListener(view -> {
//            Intent addNewReminderActivity = new Intent(Main.this, ReminderInput.class);
//            startActivity(addNewReminderActivity);
//        });

//        final AppCompatImageButton imgBtnAddReminder = findViewById(R.id.imgBtnAddReminder);
//        imgBtnAddReminder.setOnClickListener(view -> {
//            Intent addNewReminderActivity = new Intent(Main.this, ReminderInput.class);
//            startActivity(addNewReminderActivity);
//        });

        final FloatingActionButton imgBtnAddReminder = findViewById(R.id.imgBtnAddReminder);
        imgBtnAddReminder.setOnClickListener(view -> {
            Intent addNewReminderActivity = new Intent(Main.this, ReminderInput.class);
            startActivity(addNewReminderActivity);
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
            case R.id.action_settings:
                Intent settings_intent = new Intent(this, Settings.class);
                startActivity(settings_intent);
                break;

            case R.id.action_about_app:
                Intent aboutAppIntent = new Intent(this, AboutApp.class);
                startActivity(aboutAppIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}